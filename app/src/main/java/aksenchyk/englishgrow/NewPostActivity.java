package aksenchyk.englishgrow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;


public class NewPostActivity extends AppCompatActivity {


    private Toolbar toolbarAddNewPost;
    private ImageView imageViewNewPost;
    private EditText editTextDescriptionNewPost;
    private Button buttonAddNewPost;
    private ProgressBar progressBarAddNewPost;


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String userID;

    private Uri postImageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbarAddNewPost = (Toolbar) findViewById(R.id.toolbarAddNewPost);
        imageViewNewPost = (ImageView) findViewById(R.id.imageViewNewPost);
        editTextDescriptionNewPost = (EditText) findViewById(R.id.editTextDescriptionNewPost);
        buttonAddNewPost = (Button) findViewById(R.id.buttonAddNewPost);
        progressBarAddNewPost = (ProgressBar) findViewById(R.id.progressBarAddNewPost);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = firebaseAuth.getCurrentUser().getUid();

        setSupportActionBar(toolbarAddNewPost);
        getSupportActionBar().setTitle(getString(R.string.toolbar_new_post));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarAddNewPost.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarAddNewPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });

        imageViewNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });


        buttonAddNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String msgNewPost = editTextDescriptionNewPost.getText().toString();


                if(!TextUtils.isEmpty(msgNewPost) && postImageURI != null) {

                    progressBarAddNewPost.setVisibility(View.VISIBLE);

                    String randomName = FieldValue.serverTimestamp().toString();

                    StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");

                    filePath.putFile(postImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {

                                String downloadURL =  task.getResult().getDownloadUrl().toString();
                                Map<String,Object> postMap = new HashMap<>();
                                postMap.put("user_id", userID);
                                postMap.put("image_url", downloadURL);
                                postMap.put("msg", msgNewPost);


                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(NewPostActivity.this,getText(R.string.new_post_successful),Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {

                                        }

                                        progressBarAddNewPost.setVisibility(View.INVISIBLE);

                                    }
                                });



                            } else {

                                progressBarAddNewPost.setVisibility(View.INVISIBLE);


                            }

                        }
                    });

                }


            }
        });


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageURI = result.getUri();
                imageViewNewPost.setImageURI(postImageURI);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }




}
