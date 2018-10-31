package aksenchyk.englishgrow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class NewPostActivity extends AppCompatActivity {

    private Toolbar toolbarAddNewPost;
    private ImageView imageViewNewPost;
    private EditText editTextDescriptionNewPost;




    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String userID;

    private Uri postImageURI = null;

    private Bitmap compressedImageFile;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        //Views
        toolbarAddNewPost = (Toolbar) findViewById(R.id.toolbarAddNewPost);
        imageViewNewPost = (ImageView) findViewById(R.id.imageViewNewPost);
        editTextDescriptionNewPost = (EditText) findViewById(R.id.editTextDescriptionNewPost);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.sending));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);



        setSupportActionBar(toolbarAddNewPost);
        getSupportActionBar().setTitle(getString(R.string.toolbar_new_post));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);
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
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_blog_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_blog_post) {
            final String desc = editTextDescriptionNewPost.getText().toString();


            if(TextUtils.isEmpty(desc)) {
                Toast toast = Toast.makeText(NewPostActivity.this, getString(R.string.err_moment_text), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (postImageURI == null) {
                mProgressDialog.show();
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("image_url", "not_img");
                postMap.put("image_thumb", "not_img");
                postMap.put("desc", desc);
                postMap.put("user_id", userID);
                postMap.put("timestamp", FieldValue.serverTimestamp());


                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {


                        }

                        mProgressDialog.hide();
                    }
                });


            } else if(!TextUtils.isEmpty(desc) && postImageURI != null) {

                mProgressDialog.show();

                final String randomName = UUID.randomUUID().toString();

                // PHOTO UPLOAD
                File newImageFile = new File(postImageURI.getPath());

                try {
                    compressedImageFile = new Compressor(NewPostActivity.this)
                            .setMaxHeight(720)
                            .setMaxWidth(720)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }



                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] imageData = baos.toByteArray();


                // PHOTO UPLOAD
                UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
                filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                        final String downloadUri = task.getResult().getDownloadUrl().toString();

                        if(task.isSuccessful()){

                            File newThumbFile = new File(postImageURI.getPath());

                            try {
                                compressedImageFile = new Compressor(NewPostActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(1)
                                        .compressToBitmap(newThumbFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                            byte[] thumbData = baos.toByteArray();

                            UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                    .child(randomName + ".jpg").putBytes(thumbData);


                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_url", downloadUri);
                                    postMap.put("image_thumb", downloadthumbUri);
                                    postMap.put("desc", desc);
                                    postMap.put("user_id", userID);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());


                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){

                                                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            } else {


                                            }

                                            mProgressDialog.hide();
                                        }
                                    });
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override

                                public void onFailure(@NonNull Exception e) {

                                    //Error handling

                                }

                            });

                        } else {
                            mProgressDialog.hide();
                        }

                    }

                });


            }


        }
        return true;
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_up);
    }
}
