package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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


public class BlogFragment extends Fragment {


    @BindView(R.id.floatingActionButtonAddNewPost)
    FloatingActionButton floatingActionButtonAddNewPost;

    @BindView(R.id.recyclerViewBlogPosts)
    RecyclerView recyclerViewBlogPosts;



    private Animation animFabShow;
    private Animation animFabHide;



    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private BlogRecyclerAdapter mAdapter;

    public BlogFragment() {
        // Required empty public constructor
    }

    public static BlogFragment newInstance() {
        return new BlogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.menu_down_moments));

        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated
        mQuery = mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);


        mAdapter = new BlogRecyclerAdapter(mQuery/*, this*/) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    Log.w("err","blogCount = null");
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {

            }
        };

        recyclerViewBlogPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBlogPosts.setAdapter(mAdapter);





        animFabShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_show);
        animFabHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide);

        floatingActionButtonAddNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                floatingActionButtonAddNewPost.startAnimation(animFabHide);
                Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        floatingActionButtonAddNewPost.startAnimation(animFabShow);

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
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
