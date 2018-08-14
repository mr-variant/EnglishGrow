package aksenchyk.englishgrow;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aksenchyk.englishgrow.adapters.CommentsRecyclerAdapter;
import aksenchyk.englishgrow.models.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CommentsActivity extends AppCompatActivity {

    @BindView(R.id.toolbarComments) Toolbar toolbarComments;
    @BindView(R.id.editTextAddComment) EditText editTextAddComment;
    @BindView(R.id.imageViewAddCommentBtn) ImageView imageViewAddCommentBtn;
    @BindView(R.id.recyclerViewComments) RecyclerView recyclerViewComments;



    private String blogPostID;
    private String currentUserID;

    private List<Comment> commentsList;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();



        //RecyclerView Firebase List

        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentsRecyclerAdapter);

        recyclerViewComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        blogPostID = getIntent().getStringExtra("blogPostID");

        //toolbar
        setSupportActionBar(toolbarComments);
        getSupportActionBar().setTitle(R.string.comments_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarComments.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarComments.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });


        firebaseFirestore.collection("Posts/" + blogPostID + "/Comment")
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String commentId = doc.getDocument().getId();
                                    Comment comments = doc.getDocument().toObject(Comment.class);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });


        imageViewAddCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = editTextAddComment.getText().toString();

                if(comment.isEmpty()) {

                } else {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment);
                    commentsMap.put("user_id", currentUserID);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + blogPostID + "/Comment").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()) {
                                editTextAddComment.setText("");
                                //editTextAddComment
                            } else {
                                Toast.makeText(CommentsActivity.this, getText(R.string.error_post_comment) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

            }
        });

    }



}
