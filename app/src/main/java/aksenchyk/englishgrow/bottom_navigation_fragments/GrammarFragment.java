package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import aksenchyk.englishgrow.GrammarActivity;
import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.NewPostActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.WordInfoActivity;
import aksenchyk.englishgrow.adapters.BlogRecyclerAdapter;
import aksenchyk.englishgrow.adapters.GrammaRecyclerAdapter;
import aksenchyk.englishgrow.adapters.VocabularyRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class GrammarFragment extends Fragment implements
        GrammaRecyclerAdapter.OnGrammaSelectedListener {

    @BindView(R.id.rvGramma) RecyclerView rvGramma;


    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private GrammaRecyclerAdapter mAdapter;

    public GrammarFragment() {
        // Required empty public constructor
    }

    public static GrammarFragment newInstance() {
        return new GrammarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grammar, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.menu_down_grammar));

        ButterKnife.bind(this, rootView);

        mFirestore = FirebaseFirestore.getInstance();

        // Get the 50 highest rated
        mQuery = mFirestore.collection("Gramma")
                .orderBy("unitNum", Query.Direction.ASCENDING);



        mAdapter = new GrammaRecyclerAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    Log.w("err","GrammaCount = null");
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {

            }
        };

        rvGramma.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGramma.setAdapter(mAdapter);




        // Inflate the layout for this fragment
        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();

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

    @Override
    public void onGrammaSelected(DocumentSnapshot unit) {
        Intent intent = new Intent(getActivity(), GrammarActivity.class);
        intent.putExtra(GrammarActivity.KEY_GRAMMAR_UNIT, unit.getId());
        intent.putExtra(GrammarActivity.KEY_GRAMMAR_NAME, unit.get("unit").toString());
        startActivity(intent);

    }
}
