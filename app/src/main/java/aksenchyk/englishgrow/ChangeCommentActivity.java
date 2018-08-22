package aksenchyk.englishgrow;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import aksenchyk.englishgrow.models.Comment;
import aksenchyk.englishgrow.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeCommentActivity extends AppCompatActivity {


    @BindView(R.id.toolbarChangeComment) Toolbar toolbarChangeComment;
    @BindView(R.id.profileImageChangeComment) CircleImageView profileImageChangeComment;

    @BindView(R.id.textViewCommentChangeUsername) TextView textViewCommentChangeUsername;
    @BindView(R.id.editTextChangeComment) EditText editTextChangeComment;

    public static final String KEY_COMMENT_PATH = "commentRef";
    public static final String KEY_COMMENT_ID = "commentID";

    private FirebaseFirestore firebaseFirestore;
    private String commentPath;
    private String commentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_comment);

        ButterKnife.bind(this);

        // Initialize Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Get blog ID from extras
        commentPath = getIntent().getStringExtra(KEY_COMMENT_PATH);
        commentID = getIntent().getStringExtra(KEY_COMMENT_ID);

        if (commentPath == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_COMMENT_PATH);
        }
        if (commentID == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_COMMENT_ID);
        }



        firebaseFirestore.collection(commentPath).document(commentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Comment comment = documentSnapshot.toObject(Comment.class);
                String msg = comment.getMessage();
                String userId = comment.getUser_id();

                editTextChangeComment.setText(msg);
                setUserData(userId);
            }
        });


        //toolbar
        setSupportActionBar(toolbarChangeComment);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarChangeComment.setNavigationIcon(R.drawable.ic_clear);
        //toolbarChangeComment.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarChangeComment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });

    }


    private void setUserData(String userId) {
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String name = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    textViewCommentChangeUsername.setText(name);

                    Uri mainImageURI = Uri.parse(image);
                    RequestOptions placeholderRequest = new RequestOptions();
                    placeholderRequest.placeholder(R.drawable.default_image);
                    Glide.with(ChangeCommentActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImageChangeComment);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ChangeCommentActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment_change, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_comment_change_ok) {
            String changeMsg = editTextChangeComment.getText().toString();

            if(changeMsg.isEmpty()) {
                Toast.makeText(ChangeCommentActivity.this,getString(R.string.comment_edit_err),Toast.LENGTH_LONG).show();
            } else {

                Map<String,Object> commentMap = new HashMap<>();
                commentMap.put("message", changeMsg);

                //update
                firebaseFirestore.collection(commentPath).document(commentID).update(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            finish();
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ChangeCommentActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
        return true;
    }





}
