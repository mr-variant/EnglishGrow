package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import aksenchyk.englishgrow.CommentsActivity;
import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.NewPostActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.SetupActivity;
import aksenchyk.englishgrow.adapters.BlogRecyclerAdapter;
import aksenchyk.englishgrow.models.BlogPost;
import aksenchyk.englishgrow.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {


    @BindView(R.id.floatingActionButtonAddNewPost)
    FloatingActionButton floatingActionButtonAddNewPost;

    @BindView(R.id.recyclerViewBlogPosts)
    RecyclerView recyclerViewBlogPosts;


    private List<BlogPost> blogList;
    private List<User> userList;

    private BlogRecyclerAdapter blogRecyclerAdapter;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private Animation animFabShow;
    private Animation animFabHide;

    private DocumentSnapshot lastVisiblePost;
    private Boolean isFirstPageFirstLoad = true;

    private Query mQuery;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        blogList = new ArrayList<>();
        userList = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.toolbar_blog));

        ButterKnife.bind(this, rootView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        blogRecyclerAdapter = new BlogRecyclerAdapter(blogList, userList);
        recyclerViewBlogPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewBlogPosts.setAdapter(blogRecyclerAdapter);

        animFabShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_show);
        animFabHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide);

        if(firebaseAuth.getCurrentUser() != null) {
            recyclerViewBlogPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        loadMorePosts();
                    }
                }
            });



            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {


                    int size = documentSnapshots.size() - 1;

                    if(size < 0 || e != null) {
                        return;
                    }

                    lastVisiblePost = documentSnapshots.getDocuments().get(size);

                    if (isFirstPageFirstLoad) {
                        lastVisiblePost = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        blogList.clear();
                        userList.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostID = doc.getDocument().getId();
                            final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);

                            String blogUserID = doc.getDocument().getString("user_id");
                            firebaseFirestore.collection("Users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        User user = task.getResult().toObject(User.class);


                                        if (isFirstPageFirstLoad) {
                                            userList.add(user);
                                            blogList.add(blogPost);
                                        } else {
                                            userList.add(0, user);
                                            blogList.add(0, blogPost);
                                        }

                                        blogRecyclerAdapter.notifyDataSetChanged();

                                    }
                                }
                            });




                        }
                    }

                    isFirstPageFirstLoad = false;

                }
            });


            floatingActionButtonAddNewPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    floatingActionButtonAddNewPost.startAnimation(animFabHide);
                    Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            });
        }


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        floatingActionButtonAddNewPost.startAnimation(animFabShow);
    }



    public void loadMorePosts() {
        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisiblePost)
                    .limit(3);

            nextQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisiblePost = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostID = doc.getDocument().getId();
                                final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);


                                String blogUserID = doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("Users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);

                                            userList.add(user);
                                            blogList.add(blogPost);


                                            blogRecyclerAdapter.notifyDataSetChanged();

                                        }
                                    }
                                });

                            }
                        }
                    }

                }
            });
        }
    }


    public void setAnimFabShow() {
        floatingActionButtonAddNewPost.startAnimation(animFabShow);
    }


   /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.me, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_me_setup :


            default:
                return super.onOptionsItemSelected(item);
        }
    }*/





}
