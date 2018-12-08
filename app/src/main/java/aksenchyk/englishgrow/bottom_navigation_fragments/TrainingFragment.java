package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aksenchyk.englishgrow.AddTrainingActivity;
import aksenchyk.englishgrow.GrammarActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.VocabularyActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TrainingFragment extends Fragment {

    @BindView(R.id.cvAddTrain) CardView cvAddTrain;
    @BindView(R.id.cvWordTransl) CardView cvWordTransl;
    @BindView(R.id.cvTranslWord) CardView cvTranslWord;



    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment newInstance() {
        return new TrainingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_training, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.menu_down_training));

        ButterKnife.bind(this, rootView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();




        // Inflate the layout for this fragment
        return rootView;

    }


    @OnClick(R.id.cvAddTrain)
    void onCvAddTrainClick(View view) {
        Intent intent = new Intent(getActivity(), AddTrainingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cvWordTransl)
    void onCvWordTranslClick(View view) {

    }

    @OnClick(R.id.cvTranslWord)
    void onCvTranslWordClick(View view) {

    }





}
