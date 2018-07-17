package aksenchyk.englishgrow;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import aksenchyk.englishgrow.bottom_navigation_fragments.MeFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private Toolbar toolbarSetup;

    private CircleImageView profileImage;
    private EditText editTextNameSetup;
    private ProgressBar progressBarSetup;

    private Uri mainImageURI = null;


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = firebaseAuth.getCurrentUser().getUid();


        toolbarSetup = (Toolbar) findViewById(R.id.toolbarSetup);
        editTextNameSetup = (EditText) findViewById(R.id.editTextNameSetup);
        progressBarSetup = (ProgressBar)findViewById(R.id.progressBarSetup);
        profileImage = (CircleImageView) findViewById(R.id.profileImageSetup);




        progressBarSetup.setVisibility(View.VISIBLE);


        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        editTextNameSetup.setText(name);
                        RequestOptions placeholderRequest = new RequestOptions();

                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);
                        Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve ) :!!! "  , Toast.LENGTH_LONG).show();
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }

                progressBarSetup.setVisibility(View.INVISIBLE);
            }
        });


        //toolbar
        setSupportActionBar(toolbarSetup);
        getSupportActionBar().setTitle(R.string.title_setup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarSetup.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarSetup.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(ContextCompat.checkSelfPermission(SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(SetupActivity.this, getString(R.string.permission_denied),Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(SetupActivity.this, new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },1);

                } else {

                    // start picker to get image for cropping and then use the image in cropping activity
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(SetupActivity.this);

                }



            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_setup_ok) {

            final String userName = editTextNameSetup.getText().toString();

            if(!TextUtils.isEmpty(userName) && mainImageURI != null) {
                progressBarSetup.setVisibility(View.VISIBLE);


                StorageReference imagePath = storageReference.child("profile_images").child(userID + ".jpg");

                imagePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()) {
                            Uri downloadURI = task.getResult().getDownloadUrl();

                            Map<String,Object> userMap = new HashMap<>();
                            userMap.put("name", userName);
                            userMap.put("image", downloadURI.toString());


                            //update
                            firebaseFirestore.collection("Users").document(userID).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SetupActivity.this, getString(R.string.setup_error) + error, Toast.LENGTH_LONG).show();
                                    }

                                    progressBarSetup.setVisibility(View.INVISIBLE);
                                }
                            });


                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, getString(R.string.setup_error), Toast.LENGTH_LONG).show();
                        }

                        progressBarSetup.setVisibility(View.INVISIBLE);

                    }
                });


            }


        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                profileImage.setImageURI(mainImageURI);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
