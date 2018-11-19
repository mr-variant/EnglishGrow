package aksenchyk.englishgrow;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import aksenchyk.englishgrow.adapters.VocabularyRecyclerAdapter;
import aksenchyk.englishgrow.adapters.WordSetRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WordsListActivity  extends AppCompatActivity {

    @BindView(R.id.toolbarVocabulary)  Toolbar toolbarVocabulary;
    @BindView(R.id.rvVocabulary)     RecyclerView rvVocabulary;


    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private WordSetRecyclerAdapter mAdapter;
    private FirebaseAuth mFirebaseAuth;

    private String userID;

    public static final String KEY_VOCABULARY_CATEGORY = "key_vocabulary_category";

    private String vocabularyCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();

        // Get restaurant ID from extras
        vocabularyCategory = getIntent().getExtras().getString(KEY_VOCABULARY_CATEGORY);
        if (vocabularyCategory == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_VOCABULARY_CATEGORY);
        }




        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("Dictionary").whereArrayContains("set_words", vocabularyCategory);


//.whereArrayContains("regions", "west_coast")

        // RecyclerView
        mAdapter = new WordSetRecyclerAdapter(mQuery) {
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


        rvVocabulary.setLayoutManager(new LinearLayoutManager(this));
        rvVocabulary.setAdapter(mAdapter);


        //toolbar
        setSupportActionBar(toolbarVocabulary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.titleWordSet));
        toolbarVocabulary.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarVocabulary.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });




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



}
