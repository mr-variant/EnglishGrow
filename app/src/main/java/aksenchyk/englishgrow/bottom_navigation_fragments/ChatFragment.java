package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.NewPostActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.SetupActivity;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    private FloatingActionButton addPostFAB;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.toolbar_blog));


        addPostFAB = (FloatingActionButton) rootView.findViewById(R.id.addPostFAB);

        addPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class);
                startActivity(newPostIntent);


            }
        });


        // Inflate the layout for this fragment
        return rootView;
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
