package aksenchyk.englishgrow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import aksenchyk.englishgrow.adapters.CommentsRecyclerAdapter;
import aksenchyk.englishgrow.adapters.VocabularyRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VocabularyActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener,
        VocabularyRecyclerAdapter.OnVocabularySelectedListener{

    @BindView(R.id.toolbarVocabulary) Toolbar toolbarVocabulary;
    @BindView(R.id.rvVocabulary) RecyclerView rvVocabulary;

    private TextToSpeech mTTS;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private VocabularyRecyclerAdapter mAdapter;
    private FirebaseAuth mFirebaseAuth;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        ButterKnife.bind(this);

        mTTS = new TextToSpeech(this, this);

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("Users").document(userID).collection("Vocabulary")
                .orderBy("timestamp", Query.Direction.DESCENDING);



        // RecyclerView
        mAdapter = new VocabularyRecyclerAdapter(mQuery, mTTS,this) {
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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(VocabularyActivity.this, LinearLayoutManager.VERTICAL);
        rvVocabulary.addItemDecoration(dividerItemDecoration);

        rvVocabulary.setLayoutManager(new LinearLayoutManager(this));
        rvVocabulary.setAdapter(mAdapter);


        //toolbar
        setSupportActionBar(toolbarVocabulary);
        getSupportActionBar().setTitle(getString(R.string.vocabularyActivityTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public void onDestroy() {
        // Don't forget to shutdown mTTS!
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("en");

            int result = mTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else {

            }

        } else {
            Toast.makeText(VocabularyActivity.this,getText(R.string.speechErr),Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vocabulary, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new_word :
                Intent intentAddNewWords = new Intent(VocabularyActivity.this, AddNewWordsActivity.class);
                startActivity(intentAddNewWords);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVocabularySelected(DocumentSnapshot word) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, WordInfoActivity.class);
        intent.putExtra(WordInfoActivity.KEY_VOCABULARY_ID, word.getId());

        startActivity(intent);
    }


    //action_add_new_word

}
