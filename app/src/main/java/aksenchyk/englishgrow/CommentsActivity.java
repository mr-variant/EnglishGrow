package aksenchyk.englishgrow;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aksenchyk.englishgrow.adapters.CommentsRecyclerAdapter;
import aksenchyk.englishgrow.models.BlogPost;
import aksenchyk.englishgrow.models.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CommentsActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>  {

    public static final String KEY_BLOG_ID = "blogPostID";

    @BindView(R.id.toolbarComments) Toolbar toolbarComments;
    @BindView(R.id.editTextAddComment) EditText editTextAddComment;
    @BindView(R.id.imageViewAddCommentBtn) ImageView imageViewAddCommentBtn;
    @BindView(R.id.recyclerViewComments) RecyclerView recyclerViewComments;



    private String blogPostID;



    private FirebaseAuth firebaseAuth;

    private ListenerRegistration mBlogRegistration;
    private CommentsRecyclerAdapter mCommentsRecyclerAdapter;
    private FirebaseFirestore mFirestore;
    private DocumentReference mBlogRef;

    private Animation animBtnNewCommentShow;
    private Animation animBtnNewCommentHide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //toolbar
        setSupportActionBar(toolbarComments);
        getSupportActionBar().setTitle(R.string.comments_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarComments.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarComments.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed(); // возврат на предыдущий activity
            }
        });

        animBtnNewCommentShow = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.fab_show);
        animBtnNewCommentHide = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.fab_hide);


        imageViewAddCommentBtn.setVisibility(View.INVISIBLE);
        imageViewAddCommentBtn.setClickable(false);

        editTextAddComment.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                int msgLength = s.toString().length();

                if(msgLength == 0) {
                    imageViewAddCommentBtn.startAnimation(animBtnNewCommentHide);
                    imageViewAddCommentBtn.setClickable(false);
                    imageViewAddCommentBtn.setVisibility(View.INVISIBLE);
                }

                if(msgLength == 1 && !(imageViewAddCommentBtn.isClickable()) ) {
                    imageViewAddCommentBtn.startAnimation(animBtnNewCommentShow);
                    imageViewAddCommentBtn.setClickable(true);
                    imageViewAddCommentBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });



        // Get blog ID from extras
        blogPostID = getIntent().getStringExtra(KEY_BLOG_ID);
        if (blogPostID == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_BLOG_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mBlogRef = mFirestore.collection("Posts").document(blogPostID);

        // Get ratings
        Query commentsQuery = mBlogRef
                .collection("Comment")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);


        // RecyclerView
        mCommentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsQuery,blogPostID) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    //recyclerViewComments.setVisibility(View.GONE);
                    //mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    //recyclerViewComments.setVisibility(View.VISIBLE);
                   // mEmptyView.setVisibility(View.GONE);
                }
            }
        };


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(CommentsActivity.this, LinearLayoutManager.VERTICAL);
        recyclerViewComments.addItemDecoration(dividerItemDecoration);

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(mCommentsRecyclerAdapter);


        recyclerViewComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                if (reachedBottom) {



                  /*  DocumentSnapshot lastVisiblePost = mCommentsRecyclerAdapter.getLastVisiblePost();
                    Query nextQuery = mBlogRef.collection("Comment")
                            .orderBy("timestamp", Query.Direction.ASCENDING)
                            .startAfter(lastVisiblePost)
                            .limit(5);

                   mCommentsRecyclerAdapter.nextQuery(nextQuery);*/

                   // mCommentsRecyclerAdapter.nextQuery(mFirestore,5);

                    /*
                    Query commentsQuery = mBlogRef
                            .collection("Comment")
                            .orderBy("timestamp", Query.Direction.ASCENDING)
                            .limit(3);
                    mCommentsRecyclerAdapter.setQuery(commentsQuery);*/
                }
            }
        });


    }

    @OnClick(R.id.imageViewAddCommentBtn)
    public void onAddCommentClicked(View view) {
        String msg = editTextAddComment.getText().toString();

        if(msg.isEmpty()) {

        } else {

            Comment newComment = new Comment(firebaseAuth.getCurrentUser(), msg);

            mFirestore.collection("Posts/" + blogPostID + "/Comment").add(newComment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()) {
                        hideKeyboard();
                        editTextAddComment.setText("");
                    } else {
                        Toast.makeText(CommentsActivity.this, getText(R.string.error_post_comment) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mCommentsRecyclerAdapter.startListening();
        mBlogRegistration = mBlogRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mCommentsRecyclerAdapter.stopListening();

        if (mBlogRegistration != null) {
            mBlogRegistration.remove();
            mBlogRegistration = null;
        }
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            return;
        }
        //onBlogLoaded(snapshot.toObject(BlogPost.class));
    }




}
