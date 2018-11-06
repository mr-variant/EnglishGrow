package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.SetupActivity;
import aksenchyk.englishgrow.VocabularyActivity;
import aksenchyk.englishgrow.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class MeFragment extends Fragment {

    @BindView(R.id.profileImage) CircleImageView profileImage;
    @BindView(R.id.textViewUserName) TextView textViewUserName;
    @BindView(R.id.textViewUserLvl) TextView textViewUserLvl;
    @BindView(R.id.textViewNeedPoints) TextView textViewNeedPoints;
    @BindView(R.id.textViewDayUserExp) TextView textViewDayUserExp;
    @BindView(R.id.progressBarLevel) ProgressBar progressBarLevel;
    @BindView(R.id.progressBarDayUserExp) ProgressBar progressBarDayUserExp;
    @BindView(R.id.textViewUserVocabulary) TextView textViewUserVocabulary;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;




    private Uri mainImageURI = null;
    private String userID;

    public MeFragment() {
        // Required empty public constructor
    }


    public static MeFragment newInstance() {
        return new MeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.profile));

        ButterKnife.bind(this, rootView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBarDayUserExp.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.amber600)));




        if(firebaseAuth.getCurrentUser() != null) {

            userID = firebaseAuth.getCurrentUser().getUid();


            firebaseFirestore.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){

                        String userImage = documentSnapshot.get("image").toString();
                        String userName = documentSnapshot.get("name").toString();
                        int experience = documentSnapshot.getLong("experience").intValue();
                        int dayExperience = documentSnapshot.getLong("satiation").intValue();


                        final int expToNewLvl = 500;
                        int userLvl =  experience / expToNewLvl;
                        int needExp = expToNewLvl - (experience % expToNewLvl);
                        int needExpPercent =  experience % expToNewLvl * 100 / expToNewLvl;




                        RequestOptions placeholderOption = new RequestOptions();
                        placeholderOption.placeholder(R.drawable.default_image);
                        Glide.with(profileImage.getContext()).applyDefaultRequestOptions(placeholderOption).load(userImage).into(profileImage);

                        textViewUserName.setText(userName);
                        textViewUserLvl.setText(getString(R.string.userLvl) + " " +  userLvl);
                        textViewNeedPoints.setText(getString(R.string.userLvlNeed) + " " +  needExp + " " + getString(R.string.userLvlPoints));
                        textViewDayUserExp.setText(dayExperience + "%");

                        progressBarLevel.setProgress(needExpPercent);
                        progressBarDayUserExp.setProgress(dayExperience);

                    } else {
                        Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error) : " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });


            firebaseFirestore.collection("Users").document(userID).collection("Vocabulary").addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(documentSnapshots.isEmpty()){
                        textViewUserVocabulary.setText(getString(R.string.vocabularyCount, 0));

                    } else {
                        int count = documentSnapshots.size();
                        textViewUserVocabulary.setText(getString(R.string.vocabularyCount, count));
                    }
                }
            });




        }

        // Inflate the layout for this fragment
        return rootView;
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.me, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_me_setup :
                if(isOnline()) {
                    Intent setupIntent = new Intent(getActivity(), SetupActivity.class);
                    startActivity(setupIntent);
                } else {
                    Toast.makeText(getActivity(),getString(R.string.connection_bad), Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
