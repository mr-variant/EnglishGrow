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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import aksenchyk.englishgrow.models.Vocabulary;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTrainingActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    @BindView(R.id.toolbarAddTrain) Toolbar toolbarAddTraining;


    @BindView(R.id.imageViewTrainWordInfo) ImageView imageViewTrainWordInfo;
    @BindView(R.id.imageViewTrainWord)  ImageView imageViewTrainWord;
    @BindView(R.id.imageViewTrainSpeech)  ImageView imageViewTrainSpeech;


    @BindView(R.id.textViewTrainWord) TextView textViewTrainWord;
    @BindView(R.id.textViewTrainTransc) TextView textViewTrainTransc;

    @BindView(R.id.cardViewMainTraining) CardView cardViewMainTraining;

    @BindView(R.id.cardViewTrainAgain) CardView cardViewTrainAgain;
    @BindView(R.id.cardViewTrainGood) CardView cardViewTrainGood;
    @BindView(R.id.cardViewTrainEasy) CardView cardViewTrainEasy;
    @BindView(R.id.cardViewTrainHard) CardView cardViewTrainHard;

    @BindView(R.id.cvNotWords) CardView cvNotWords;


    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private String userID;



    private Iterator iteratorVocabulary;
    private String currentWord;
    private Vocabulary currentVocabulary;
    HashMap<String, Vocabulary> vocabulary = new HashMap<String, Vocabulary>();

    private TextToSpeech mTTS;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training);

        ButterKnife.bind(this);

        mTTS = new TextToSpeech(this, this);

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();


        //toolbar
        setSupportActionBar(toolbarAddTraining);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.add_train));
        toolbarAddTraining.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarAddTraining.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });



        mFirestore.collection("Users").document(userID).collection("Vocabulary").whereLessThan("dateRepeat",new Date())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String word = document.getId();
                                Vocabulary vocabularyDoc = document.toObject(Vocabulary.class);

                                if( !vocabularyDoc.isTrainingTranslationWord() && !vocabularyDoc.isTrainingWordTranslation() ) {
                                    vocabulary.put(word,vocabularyDoc);
                                }

                               // vocabulary.put(word,vocabularyDoc);
                            }

                            if (vocabulary.size() != 0) {
                                Set set = vocabulary.entrySet();
                                iteratorVocabulary = set.iterator();

                                nextWord(); // TYT
                                showUI();
                            } else {
                                hideUI();
                            }

                        } else {
                            Log.d("Err", "Error getting documents: ", task.getException());
                        }
                    }
                });





    }






    void nextWord() {

        if(iteratorVocabulary.hasNext()) {
            Map.Entry mentry = (Map.Entry)iteratorVocabulary.next();
            currentWord = mentry.getKey().toString();
            currentVocabulary = (Vocabulary)  mentry.getValue();



            switch (currentVocabulary.getStatus()) {

                case Vocabulary.FIELD_STATUS_AGAIN:
                    imageViewTrainWordInfo.setImageResource(R.drawable.cyrcle_again);
                    break;

                case Vocabulary.FIELD_STATUS_GOOD:
                    imageViewTrainWordInfo.setImageResource(R.drawable.cyrcle_good);
                    break;

                case Vocabulary.FIELD_STATUS_EASY:
                    imageViewTrainWordInfo.setImageResource(R.drawable.cyrcle_easy);
                    break;

                case Vocabulary.FIELD_STATUS_HARD:
                    imageViewTrainWordInfo.setImageResource(R.drawable.cyrcle_hard);
                    break;
            }




            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_word_img);
            Glide.with(imageViewTrainWord.getContext()).applyDefaultRequestOptions(requestOptions).load(currentVocabulary.getImage()).into(imageViewTrainWord);


            textViewTrainWord.setText(currentWord);
            textViewTrainTransc.setText(currentVocabulary.getTranscription());



        } else {
            hideUI();
        }

    }



    @OnClick(R.id.cardViewTrainAgain)
    void onCardVievTrainAgainClick(View view) {
       Date date = new Date();

        Map<String,Object> userMap = new HashMap<>();
        userMap.put("dateRepeat", date);
        userMap.put("status", Vocabulary.FIELD_STATUS_AGAIN);
        userMap.put("trainingWordTranslation", true);
        userMap.put("trainingTranslationWord", true);


        //update
        mFirestore.collection("Users").document(userID).collection("Vocabulary").document(currentWord)
                .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    nextWord();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AddTrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @OnClick(R.id.imageViewTrainSpeech)
    void onImageViewTrainSpeechClick(View view) {
        String text = textViewTrainWord.getText().toString();
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    @OnClick(R.id.cardViewTrainGood)
    void onCardVievrainGoodClick(View view) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);
        date = c.getTime();

        Map<String,Object> userMap = new HashMap<>();
        userMap.put("dateRepeat", date);
        userMap.put("status", Vocabulary.FIELD_STATUS_GOOD);

        //update
        mFirestore.collection("Users").document(userID).collection("Vocabulary").document(currentWord)
                .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    nextWord();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AddTrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.cardViewTrainEasy)
    void onCardVievrainEasyClick(View view) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 14);
        date = c.getTime();

        Map<String,Object> userMap = new HashMap<>();
        userMap.put("dateRepeat", date);
        userMap.put("status", Vocabulary.FIELD_STATUS_EASY);

        //update
        mFirestore.collection("Users").document(userID).collection("Vocabulary").document(currentWord)
                .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    nextWord();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AddTrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }






    @OnClick(R.id.cardViewTrainHard)
    void onCardVievrainHardClick(View view) {

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 30);
        date = c.getTime();

        Map<String,Object> userMap = new HashMap<>();
        userMap.put("dateRepeat", date);
        userMap.put("status", Vocabulary.FIELD_STATUS_HARD);

        //update
        mFirestore.collection("Users").document(userID).collection("Vocabulary").document(currentWord)
                .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    nextWord();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AddTrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    void hideUI() {
        cardViewTrainAgain.setVisibility(View.INVISIBLE);
        cardViewTrainAgain.setClickable(false);

        cardViewTrainGood.setVisibility(View.INVISIBLE);
        cardViewTrainGood.setClickable(false);

        cardViewTrainEasy.setVisibility(View.INVISIBLE);
        cardViewTrainEasy.setClickable(false);

        cardViewTrainHard.setVisibility(View.INVISIBLE);
        cardViewTrainHard.setClickable(false);


        cardViewMainTraining.setVisibility(View.INVISIBLE);
        cardViewMainTraining.setClickable(false);

        cvNotWords.setVisibility(View.VISIBLE);
        cvNotWords.setClickable(true);
    }

    void showUI() {
        cardViewTrainAgain.setVisibility(View.VISIBLE);
        cardViewTrainAgain.setClickable(true);

        cardViewTrainGood.setVisibility(View.VISIBLE);
        cardViewTrainGood.setClickable(true);

        cardViewTrainEasy.setVisibility(View.VISIBLE);
        cardViewTrainEasy.setClickable(true);

        cardViewTrainHard.setVisibility(View.VISIBLE);
        cardViewTrainHard.setClickable(true);

        cardViewMainTraining.setVisibility(View.VISIBLE);
        cardViewMainTraining.setClickable(true);

        cvNotWords.setVisibility(View.INVISIBLE);
        cvNotWords.setClickable(false);
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
            Toast.makeText(AddTrainingActivity.this,getText(R.string.speechErr),Toast.LENGTH_SHORT).show();
        }

    }




}
