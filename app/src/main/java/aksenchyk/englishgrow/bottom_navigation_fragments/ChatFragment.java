package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.NewPostActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.SetupActivity;
import aksenchyk.englishgrow.adapters.BlogRecyclerAdapter;
import aksenchyk.englishgrow.models.BlogPost;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    private FloatingActionButton fab_addPost;
    private RecyclerView rv_blog_posts;
    private List<BlogPost> blogList;

    private BlogRecyclerAdapter blogRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private Animation animFabShow,
                      animFabHide;

    private DocumentSnapshot lastVisiblePost;
    private Boolean isFirstPageFirstLoad = true;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.toolbar_blog));

        firebaseAuth = FirebaseAuth.getInstance();

        fab_addPost = (FloatingActionButton) rootView.findViewById(R.id.fab_addPost);
        rv_blog_posts = (RecyclerView) rootView.findViewById(R.id.rv_blog_posts);


        blogList = new ArrayList<>();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blogList);
        rv_blog_posts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_blog_posts.setAdapter(blogRecyclerAdapter);

        animFabShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_show);
        animFabHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide);

        if(firebaseAuth.getCurrentUser() != null) {
            rv_blog_posts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        loadMorePosts();
                    }
                }
            });

            firebaseFirestore = FirebaseFirestore.getInstance();

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    lastVisiblePost = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    if (isFirstPageFirstLoad) {
                        lastVisiblePost = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        blogList.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostID = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);

                            if (isFirstPageFirstLoad) {
                                blogList.add(blogPost);
                            } else {
                                blogList.add(0, blogPost);
                            }

                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }

                    isFirstPageFirstLoad = false;

                }
            });


            fab_addPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        fab_addPost.startAnimation(animFabShow);
    }



    public void loadMorePosts() {
        Query nextQuery =  firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisiblePost)
                .limit(3);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisiblePost = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostID = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                            blogList.add(blogPost);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        });
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
