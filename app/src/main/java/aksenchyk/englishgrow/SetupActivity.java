package aksenchyk.englishgrow;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import aksenchyk.englishgrow.bottom_navigation_fragments.MeFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    @BindView(R.id.toolbarSetup) Toolbar toolbarSetup;
    @BindView(R.id.profileImageSetup) CircleImageView profileImageSetup;
    @BindView(R.id.editTextNameSetup) EditText editTextNameSetup;
    @BindView(R.id.progressBarSetup) ProgressBar progressBarSetup;
    @BindView(R.id.buttonLogOut) Button buttonLogOut;
    @BindView(R.id.buttonSetupChangeName) Button buttonSetupChangeName;


    private Animation animShowBtnChange;
    private Uri mainImageURI = null;


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = firebaseAuth.getCurrentUser().getUid();
        animShowBtnChange = AnimationUtils.loadAnimation(SetupActivity.this, R.anim.fab_show);

        buttonSetupChangeName.setVisibility(View.INVISIBLE);
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
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImageSetup);
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
    }


    @OnClick(R.id.buttonLogOut)
    void onButtonLogOutClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);

        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater layoutInflater = LayoutInflater.from(SetupActivity.this);
        View promptsView = layoutInflater.inflate(R.layout.alert_dialog_logout, null);

        //Настраиваем prompt.xml для нашего AlertDialog:
        builder.setView(promptsView);

        builder.setNegativeButton(R.string.alert_dialog_logout_close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.alert_dialog_logout_exit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth auth =  FirebaseAuth.getInstance();
                                auth.signOut();
                                Intent loginIntent = new Intent(SetupActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        });


        AlertDialog alert = builder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }


    @OnClick(R.id.profileImageSetup)
    void onProfileImageSetupClick(View view) {
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


    @OnClick(R.id.editTextNameSetup)
    void onEditTextNameSetupClick(View view) {
            buttonSetupChangeName.setVisibility(View.VISIBLE);
            buttonSetupChangeName.startAnimation(animShowBtnChange);
    }


    @OnClick(R.id.buttonSetupChangeName)
    void onButtonSetupChangeNameClick(View view) {
        progressBarSetup.setVisibility(View.VISIBLE);

        final String userName = editTextNameSetup.getText().toString();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("name", userName);

        //update
        firebaseFirestore.collection("Users").document(userID).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                }
                progressBarSetup.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                profileImageSetup.setImageURI(mainImageURI);

                progressBarSetup.setVisibility(View.VISIBLE);

                StorageReference imagePath = storageReference.child("profile_images").child(userID + ".jpg");
                imagePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            Uri downloadURI = task.getResult().getDownloadUrl();
                            Map<String,Object> userMap = new HashMap<>();
                            userMap.put("image", downloadURI.toString());

                            //update
                            firebaseFirestore.collection("Users").document(userID).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SetupActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                                    }
                                    progressBarSetup.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                        }
                        progressBarSetup.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
