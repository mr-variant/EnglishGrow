package aksenchyk.englishgrow;

import android.graphics.PorterDuff;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Locale;

import aksenchyk.englishgrow.adapters.VocabularyRecyclerAdapter;
import aksenchyk.englishgrow.models.Vocabulary;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WordInfoActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    @BindView(R.id.toolbarAddNewWordsW) Toolbar toolbarAddNewWordsW;
    @BindView(R.id.imageViewNewWordImgW) ImageView imageViewNewWordImgW;
    @BindView(R.id.lvAtherTranslW) ListView lvAtherTranslW;
    @BindView(R.id.textViewNewWordW) TextView textViewNewWordW;
    @BindView(R.id.textViewNewWordTranscrW) TextView textViewNewWordTranscrW;
    @BindView(R.id.textViewNewWordTranslateW) TextView textViewNewWordTranslateW;
    @BindView(R.id.textViewAtherWordsW) TextView textViewAtherWordsW;
    @BindView(R.id.imageViewAddNewWordW) ImageView imageViewAddNewWordW;
    @BindView(R.id.imageViewSpeechW) ImageView imageViewSpeechW;
    @BindView(R.id.textViewExempleW) TextView textViewExempleW;

    private TextToSpeech mTTS;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;

    private String userID;
    private String vocabularyId;
    private Boolean wordStatus = true;

    private Vocabulary vocabulary;

    public static final String KEY_VOCABULARY_ID = "key_vocabulary_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_info);

        ButterKnife.bind(this);


        mTTS = new TextToSpeech(this, this);

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();

        // Get restaurant ID from extras
        vocabularyId = getIntent().getExtras().getString(KEY_VOCABULARY_ID);
        if (vocabularyId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_VOCABULARY_ID);
        }

        //toolbar
        setSupportActionBar(toolbarAddNewWordsW);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(vocabularyId);
        toolbarAddNewWordsW.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarAddNewWordsW.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });



        mFirestore.collection("Users").document(userID).collection("Vocabulary").document(vocabularyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {

                    vocabulary = task.getResult().toObject(Vocabulary.class);

                    /*
                    String example = task.getResult().get("example").toString();
                    String imageUrl = task.getResult().get("image").toString();
                    String transcription = task.getResult().get("transcription").toString();
                    String translation = task.getResult().get("translation").toString();
                    ArrayList<String> other_translations = (ArrayList<String>) task.getResult().get("other_translations");
                    ArrayList<String> set_words = (ArrayList<String>) task.getResult().get("set_words");
                    */

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_word_img);
                    Glide.with(imageViewNewWordImgW.getContext()).applyDefaultRequestOptions(requestOptions).load(vocabulary.getImage()).into(imageViewNewWordImgW);

                    textViewNewWordW.setText(vocabularyId);
                    textViewNewWordTranscrW.setText(vocabulary.getTranscription());
                    textViewNewWordTranslateW.setText(vocabulary.getTranslation());
                    textViewExempleW.setText(vocabulary.getExample());

                    // используем адаптер данных
                    ArrayAdapter<String> adapter = new ArrayAdapter<> (WordInfoActivity.this,
                            android.R.layout.simple_list_item_1, vocabulary.getOther_translations() );

                    lvAtherTranslW.setAdapter(adapter);
                }
            }
        });







    }






    @OnClick(R.id.imageViewAddNewWordW)
    public void onDeleteAddWordClicked() {
        if(wordStatus == true) {
            mFirestore.collection("Users").document(userID).collection("Vocabulary").document(vocabularyId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imageViewAddNewWordW.setImageDrawable(getDrawable(R.drawable.ic_add_green));
                    wordStatus = false;
                }
            });
        }

        if(wordStatus == false) {
            mFirestore.collection("Users").document(userID).collection("Vocabulary").document(vocabularyId)
                    .set(vocabulary)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            imageViewAddNewWordW.setImageDrawable(getDrawable(R.drawable.ic_delete));
                            wordStatus = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WordInfoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }

    }



    @OnClick(R.id.imageViewSpeechW)
    public void onSpeechClicked() {
        String text = textViewNewWordW.getText().toString();
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
            Toast.makeText(WordInfoActivity.this,getText(R.string.speechErr),Toast.LENGTH_SHORT).show();
        }

    }

}
