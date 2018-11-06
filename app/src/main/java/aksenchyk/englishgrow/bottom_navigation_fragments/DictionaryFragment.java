package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.VocabularyActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DictionaryFragment extends Fragment {


    @BindView(R.id.textViewCountAllWords) TextView textViewCountAllWords;
    @BindView(R.id.textViewCountAgain) TextView textViewCountAgain;
    @BindView(R.id.textViewCountGood) TextView textViewCountGood;
    @BindView(R.id.textViewCountEasy) TextView textViewCountEasy;
    @BindView(R.id.textViewCountHard) TextView textViewCountHard;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;




    private String userID;

    public DictionaryFragment() {
        // Required empty public constructor
    }

    public static DictionaryFragment newInstance() {
        return new DictionaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dictionary, container, false);
        getActivity().setTitle(getString(R.string.menu_down_dictionary));

        ButterKnife.bind(this, rootView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Users").document(userID).collection("Vocabulary").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(documentSnapshots.isEmpty()){
                    textViewCountAllWords.setText(getString(R.string.vocabularyCount, 0));

                } else {
                    int count = documentSnapshots.size();
                    textViewCountAllWords.setText(getString(R.string.vocabularyCount, count));
                }
            }
        });




        return rootView;
    }


    @OnClick(R.id.cardViewMyDictionary)
    void onCardViewDictionaryClick(View view) {
        Intent intentVocabularyActivity = new Intent(getActivity(), VocabularyActivity.class);
        startActivity(intentVocabularyActivity);
    }



}
