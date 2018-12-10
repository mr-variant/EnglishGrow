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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import aksenchyk.englishgrow.models.Dictionary;
import aksenchyk.englishgrow.models.Vocabulary;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrainingActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    @BindView(R.id.toolbarAddTrain) Toolbar toolbarAddTrain;


    @BindView(R.id.cvNotWordsTrain) CardView cvNotWordsTrain;


    @BindView(R.id.cardViewTrainWord1) CardView cardViewTrainWord1;
    @BindView(R.id.cardViewTrainWord2) CardView cardViewTrainWord2;
    @BindView(R.id.cardViewTrainWord3) CardView cardViewTrainWord3;
    @BindView(R.id.cardViewTrainWord4) CardView cardViewTrainWord4;

    @BindView(R.id.cardViewMainTrain) CardView cardViewMainTrain;


    @BindView(R.id.textViewTrainWord1) TextView textViewTrainWord1;
    @BindView(R.id.textViewTrainWord2) TextView textViewTrainWord2;
    @BindView(R.id.textViewTrainWord3) TextView textViewTrainWord3;
    @BindView(R.id.textViewTrainWord4) TextView textViewTrainWord4;


    private ArrayList<TextView> textViewTrainWordArr = new ArrayList<TextView>();
    private ArrayList<CardView> cardViewTrainWordArr = new ArrayList<CardView>();

    @BindView(R.id.imageViewNextWord) ImageView imageViewNextWord;

    @BindView(R.id.imageViewTrainWordInfo) ImageView imageViewTrainWordInfo;
    @BindView(R.id.imageViewTrainWord)  ImageView imageViewTrainWord;
    @BindView(R.id.imageViewTrainSpeech)  ImageView imageViewTrainSpeech;


    @BindView(R.id.textViewTrainWord) TextView textViewTrainWord;
    @BindView(R.id.textViewTrainTransc) TextView textViewTrainTransc;

   // cvNotWordsTrain

    private TextToSpeech mTTS;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private String userID;


    public static final String KEY_TRAIN_CATEGORY = "key_train_category";


    private String trainCategory;
    private Iterator iteratorVocabulary;
    private String currentWord;
    private Vocabulary currentVocabulary;
    private int positionWord;

    private int prevExpDay;
    private int prevExp;

    private HashMap<String, Vocabulary> vocabulary = new HashMap<String, Vocabulary>();
    private ArrayList<String> dictionariesEN = new ArrayList<String>();
    private ArrayList<String> dictionariesRU = new ArrayList<String>();
    private int dictSize;
    private boolean checkWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        ButterKnife.bind(this);

        textViewTrainWordArr.add(textViewTrainWord1);
        textViewTrainWordArr.add(textViewTrainWord2);
        textViewTrainWordArr.add(textViewTrainWord3);
        textViewTrainWordArr.add(textViewTrainWord4);

        cardViewTrainWordArr.add(cardViewTrainWord1);
        cardViewTrainWordArr.add(cardViewTrainWord2);
        cardViewTrainWordArr.add(cardViewTrainWord3);
        cardViewTrainWordArr.add(cardViewTrainWord4);

        mTTS = new TextToSpeech(this, this);
        //currentWord = 0;

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();

        // Get restaurant ID from extras
        trainCategory = getIntent().getExtras().getString(KEY_TRAIN_CATEGORY);
        if (trainCategory == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_TRAIN_CATEGORY);
        }


        mFirestore.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    prevExpDay = documentSnapshot.getLong("dayExp").intValue();
                    prevExp = documentSnapshot.getLong("experience").intValue();

                } else {
                    Toast.makeText(TrainingActivity.this, "(FIRESTORE Retrieve Error) : " + e, Toast.LENGTH_LONG).show();
                }
            }
        });

        mFirestore.collection("Dictionary").limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Dictionary dictionary = document.toObject(Dictionary.class);

                                dictionariesEN.add(dictionary.getTranslation());
                                String word = document.getId();
                                dictionariesRU.add(word);
                            }
                            dictSize = dictionariesEN.size();
                            getTrainWords();
                        } else {
                            Log.d("Err", "Error getting documents: ", task.getException());
                        }
                    }
                });





        //toolbar
        setSupportActionBar(toolbarAddTrain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.add_train));
        toolbarAddTrain.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarAddTrain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });



        if(!trainCategory.equals("trainingWordTranslation") ) {
            textViewTrainTransc.setVisibility(View.INVISIBLE);
            imageViewTrainSpeech.setVisibility(View.INVISIBLE);
            imageViewTrainSpeech.setClickable(false);
        }


    }


    void getTrainWords() {
        mFirestore.collection("Users").document(userID).collection("Vocabulary").whereEqualTo(trainCategory,true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Vocabulary vocabularyDoc = document.toObject(Vocabulary.class);
                                String word = document.getId();
                                vocabulary.put(word,vocabularyDoc);
                            }


                            if (vocabulary.size() != 0) {

                                Set set = vocabulary.entrySet();
                                iteratorVocabulary = set.iterator();
                                nextWord(); // TYT


                                cardViewTrainWord1.setVisibility(View.VISIBLE);
                                cardViewTrainWord1.setClickable(true);

                                cardViewTrainWord2.setVisibility(View.VISIBLE);
                                cardViewTrainWord2.setClickable(true);

                                cardViewTrainWord3.setVisibility(View.VISIBLE);
                                cardViewTrainWord3.setClickable(true);

                                cardViewTrainWord4.setVisibility(View.VISIBLE);
                                cardViewTrainWord4.setClickable(true);

                                cardViewMainTrain.setVisibility(View.VISIBLE);
                                cardViewMainTrain.setClickable(true);

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
            checkWord = false;
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


        positionWord = (int) (Math.random() * 4);

        if(trainCategory.equals("trainingWordTranslation") ) {
                textViewTrainWord.setText(currentWord);

                for (int i = 0; i < 4;i++) {
                    TextView textView = textViewTrainWordArr.get(i);
                    if(i == positionWord) {
                        textView.setText(currentVocabulary.getTranslation());
                    } else {
                        int rndWord = rndWord();
                        textView.setText(dictionariesEN.get(rndWord));
                    }
                }

            } else {
            textViewTrainWord.setText(currentVocabulary.getTranslation());

            for (int i = 0; i < 4; i++) {
                TextView textView = textViewTrainWordArr.get(i);
                if(i == positionWord) {
                    textView.setText(currentWord);
                } else {
                    int rndWord = rndWord();
                    textView.setText(dictionariesRU.get(rndWord));
                }
            }

        }


            textViewTrainTransc.setText(currentVocabulary.getTranscription());

            showUI();
        } else {
            hideUI();
        }
    }







    @OnClick(R.id.cardViewTrainWord1)
    void onCardVievWord1Click(View view) {
        String word =  textViewTrainWord1.getText().toString();
        checkWord(word, 0);
    }

    @OnClick(R.id.cardViewTrainWord2)
    void onCardVievWord2Click(View view) {
        String word =  textViewTrainWord2.getText().toString();
        checkWord(word, 1);
    }

    @OnClick(R.id.cardViewTrainWord3)
    void onCardVievWord3Click(View view) {
        String word =  textViewTrainWord3.getText().toString();
        checkWord(word, 2);
    }

    @OnClick(R.id.cardViewTrainWord4)
    void onCardVievWord4Click(View view) {
        String word =  textViewTrainWord4.getText().toString();
        checkWord(word, 3);
    }




    void checkWord(String word, int numWord) {
        if(!checkWord) {

            if (trainCategory.equals("trainingWordTranslation")) {
                String wordTransl = currentVocabulary.getTranslation();

                if (word.equals(wordTransl)) {
                    cardViewTrainWordArr.get(numWord).setCardBackgroundColor(getResources().getColor(R.color.bg_login_green));

                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.DATE, 7);
                    date = c.getTime();

                    Map<String,Object> userMap = new HashMap<>();
                    userMap.put("dateRepeat", date);
                    userMap.put("status", Vocabulary.FIELD_STATUS_GOOD);
                    userMap.put("trainingWordTranslation", false);
                    //userMap.put("trainingTranslationWord", true);

                    updateExpirience();
                    //update
                    mFirestore.collection("Users").document(userID).collection("Vocabulary").document(currentWord)
                            .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                nextWord();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(TrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    cardViewTrainWordArr.get(numWord).setCardBackgroundColor(getResources().getColor(R.color.deep_orange500));
                    cardViewTrainWordArr.get(positionWord).setCardBackgroundColor(getResources().getColor(R.color.bg_login_green));
                }

            } else {
                if (word.equals(currentWord)) {
                    cardViewTrainWordArr.get(numWord).setCardBackgroundColor(getResources().getColor(R.color.bg_login_green));


                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.DATE, 7);
                    date = c.getTime();

                    Map<String,Object> userMap = new HashMap<>();
                    userMap.put("dateRepeat", date);
                    userMap.put("status", Vocabulary.FIELD_STATUS_GOOD);
                    //userMap.put("trainingWordTranslation", true);
                    userMap.put("trainingTranslationWord", false);

                    updateExpirience();
                    //update
                    mFirestore.collection("Users").document(userID).collection("Vocabulary").document(currentWord)
                            .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                nextWord();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(TrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    cardViewTrainWordArr.get(numWord).setCardBackgroundColor(getResources().getColor(R.color.deep_orange500));
                    cardViewTrainWordArr.get(positionWord).setCardBackgroundColor(getResources().getColor(R.color.bg_login_green));
                }
            }

            checkWord = true;
            imageViewNextWord.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_word_img);
            Glide.with(imageViewTrainWord.getContext()).applyDefaultRequestOptions(requestOptions).load(currentVocabulary.getImage()).into(imageViewTrainWord);
        }
    }







    @OnClick(R.id.imageViewNextWord)
    void onImageViewNextWordClick(View view) {

        if(!checkWord) {
            cardViewTrainWordArr.get(positionWord).setCardBackgroundColor(getResources().getColor(R.color.bg_login_green));
            checkWord = true;
            imageViewNextWord.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_word_img);
            Glide.with(imageViewTrainWord.getContext()).applyDefaultRequestOptions(requestOptions).load(currentVocabulary.getImage()).into(imageViewTrainWord);
        } else {
            nextWord();
        }

    }

    void updateExpirience() {
        Map<String,Object> userMap = new HashMap<>();
        prevExpDay +=  5;
        prevExp +=  5;

        userMap.put("dayExp", prevExpDay);
        userMap.put("experience", prevExp);
        //update
        mFirestore.collection("Users").document(userID).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(TrainingActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void showUI() {


        imageViewNextWord.setImageDrawable(getDrawable(R.drawable.ic_help));
        cardViewTrainWord1.setCardBackgroundColor(getResources().getColor(R.color.green500));
        cardViewTrainWord2.setCardBackgroundColor(getResources().getColor(R.color.green500));
        cardViewTrainWord3.setCardBackgroundColor(getResources().getColor(R.color.green500));
        cardViewTrainWord4.setCardBackgroundColor(getResources().getColor(R.color.green500));
        imageViewTrainWord.setImageDrawable(getResources().getDrawable(R.drawable.default_word_img));
    }

    void hideUI() {
        cardViewTrainWord1.setVisibility(View.INVISIBLE);
        cardViewTrainWord1.setClickable(false);

        cardViewTrainWord2.setVisibility(View.INVISIBLE);
        cardViewTrainWord2.setClickable(false);

        cardViewTrainWord3.setVisibility(View.INVISIBLE);
        cardViewTrainWord3.setClickable(false);

        cardViewTrainWord4.setVisibility(View.INVISIBLE);
        cardViewTrainWord4.setClickable(false);


        cardViewMainTrain.setVisibility(View.INVISIBLE);
        cardViewMainTrain.setClickable(false);

        cvNotWordsTrain.setVisibility(View.VISIBLE);
        cvNotWordsTrain.setClickable(true);
    }


    @OnClick(R.id.imageViewTrainSpeech)
    void onImageViewTrainSpeechClick(View view) {
        String text = textViewTrainWord.getText().toString();
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
            Toast.makeText(TrainingActivity.this,getText(R.string.speechErr),Toast.LENGTH_SHORT).show();
        }

    }




    public  int rndWord()
    {
        return (int) (Math.random() * dictSize);
    }



}
