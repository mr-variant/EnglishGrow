package aksenchyk.englishgrow;

import android.content.Context;
import android.graphics.PorterDuff;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import aksenchyk.englishgrow.models.Comment;
import aksenchyk.englishgrow.models.Dictionary;
import aksenchyk.englishgrow.models.User;
import aksenchyk.englishgrow.models.Vocabulary;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class AddNewWordsActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    @BindView(R.id.toolbarAddNewWords) Toolbar toolbarAddNewWords;
    @BindView(R.id.editTextSearchNewWord) EditText editTextSearchNewWord;
    @BindView(R.id.imageViewClearSearchNewWord) ImageView imageViewClearSearchNewWord;


    @BindView(R.id.cardViewNewWordNotFound) CardView cardViewNewWordNotFound;
    @BindView(R.id.imageViewNewWordImg) ImageView imageViewNewWordImg;
    @BindView(R.id.lvAtherTransl) ListView lvAtherTransl;


    @BindView(R.id.textViewNewWord) TextView textViewNewWord;
    @BindView(R.id.textViewNewWordTranscr) TextView textViewNewWordTranscr;
    @BindView(R.id.textViewNewWordTranslate) TextView textViewNewWordTranslate;
    @BindView(R.id.textViewAtherWords) TextView textViewAtherWords;

    @BindView(R.id.imageViewAddNewWord) ImageView imageViewAddNewWord;
    @BindView(R.id.imageViewSpeech) ImageView imageViewSpeech;

    private TextToSpeech mTTS;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String userID;

    private Animation animBtnShow;
    private Animation animBtnHide;


    private String example;
    private String imageUrl;
    private String transcription;
    private String translation;
    private ArrayList<String> other_translations;
    private ArrayList<String> set_words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_words);

        ButterKnife.bind(this);

        mTTS = new TextToSpeech(this, this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        imageViewAddNewWord.setClickable(false);
        cardViewNewWordNotFound.setVisibility(View.INVISIBLE);


        imageViewSpeech.setClickable(false);
        imageViewSpeech.setVisibility(View.INVISIBLE);

        imageViewNewWordImg.setVisibility(View.INVISIBLE);
        lvAtherTransl.setVisibility(View.INVISIBLE);
        textViewNewWord.setVisibility(View.INVISIBLE);
        textViewNewWordTranscr.setVisibility(View.INVISIBLE);
        textViewNewWordTranslate.setVisibility(View.INVISIBLE);
        imageViewAddNewWord.setVisibility(View.INVISIBLE);
        textViewAtherWords.setVisibility(View.INVISIBLE);


        animBtnShow = AnimationUtils.loadAnimation(AddNewWordsActivity.this, R.anim.fab_show);
        animBtnHide = AnimationUtils.loadAnimation(AddNewWordsActivity.this, R.anim.fab_hide);

        //toolbar
        setSupportActionBar(toolbarAddNewWords);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.addNewWordsTitle));
        toolbarAddNewWords.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarAddNewWords.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });

        imageViewClearSearchNewWord.setClickable(false);
        imageViewClearSearchNewWord.setVisibility(View.INVISIBLE);

        editTextSearchNewWord.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                int msgLength = s.toString().length();

                if(msgLength == 0) {
                    imageViewClearSearchNewWord.startAnimation(animBtnHide);
                    imageViewClearSearchNewWord.setClickable(false);
                    imageViewClearSearchNewWord.setVisibility(View.INVISIBLE);
                }

                if(msgLength > 0 && !(imageViewClearSearchNewWord.isClickable()) ) {
                    imageViewClearSearchNewWord.setVisibility(View.VISIBLE);
                    imageViewClearSearchNewWord.startAnimation(animBtnShow);
                    imageViewClearSearchNewWord.setClickable(true);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });



        editTextSearchNewWord.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String searchedWord = editTextSearchNewWord.getText().toString();

                if (actionId == EditorInfo.IME_ACTION_SEARCH && searchedWord.length() != 0) {
                    imageViewAddNewWord.setClickable(false);
                    cardViewNewWordNotFound.setVisibility(View.INVISIBLE);
                    imageViewNewWordImg.setVisibility(View.INVISIBLE);
                    lvAtherTransl.setVisibility(View.INVISIBLE);
                    textViewNewWord.setVisibility(View.INVISIBLE);
                    textViewNewWordTranscr.setVisibility(View.INVISIBLE);
                    textViewNewWordTranslate.setVisibility(View.INVISIBLE);
                    imageViewAddNewWord.setVisibility(View.INVISIBLE);
                    textViewAtherWords.setVisibility(View.INVISIBLE);

                    imageViewSpeech.setClickable(false);
                    imageViewSpeech.setVisibility(View.INVISIBLE);

                    //hide keyboard
                    View view = getCurrentFocus();
                    if (view != null) {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    firebaseFirestore.collection("Dictionary").document(searchedWord).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()) {


                                firebaseFirestore.collection("Users").document(userID).collection("Vocabulary").document(searchedWord).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()) {
                                            imageViewAddNewWord.setClickable(false);
                                            imageViewAddNewWord.setImageDrawable(getDrawable(R.drawable.ic_check_yellow));
                                        } else  {
                                            imageViewAddNewWord.setClickable(true);
                                            imageViewAddNewWord.setImageDrawable(getDrawable(R.drawable.ic_add_green));
                                        }
                                        imageViewAddNewWord.setVisibility(View.VISIBLE);
                                    }
                                });



                                example = task.getResult().get("example").toString();
                                imageUrl = task.getResult().get("image").toString();
                                transcription = task.getResult().get("transcription").toString();
                                translation = task.getResult().get("translation").toString();
                                other_translations = (ArrayList<String>) task.getResult().get("other_translations");
                                set_words = (ArrayList<String>) task.getResult().get("set_words");

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.placeholder(R.drawable.default_word_img);
                                Glide.with(imageViewNewWordImg.getContext()).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(imageViewNewWordImg);

                                textViewNewWord.setText(searchedWord);
                                textViewNewWordTranscr.setText(transcription);
                                textViewNewWordTranslate.setText(translation);


                                // используем адаптер данных
                                ArrayAdapter<String> adapter = new ArrayAdapter<> (AddNewWordsActivity.this,
                                        android.R.layout.simple_list_item_1, other_translations);

                                lvAtherTransl.setAdapter(adapter);


                                cardViewNewWordNotFound.setVisibility(View.INVISIBLE);
                                imageViewNewWordImg.setVisibility(View.VISIBLE);
                                lvAtherTransl.setVisibility(View.VISIBLE);
                                textViewNewWord.setVisibility(View.VISIBLE);
                                textViewNewWordTranscr.setVisibility(View.VISIBLE);
                                textViewNewWordTranslate.setVisibility(View.VISIBLE);
                                textViewAtherWords.setVisibility(View.VISIBLE);

                                imageViewSpeech.setClickable(true);
                                imageViewSpeech.setVisibility(View.VISIBLE);
                            } else  {
                                cardViewNewWordNotFound.setVisibility(View.VISIBLE);
                            }
                        }
                    });



                    return true;
                }
                return false;
            }
        });





    }




    @OnClick(R.id.imageViewClearSearchNewWord)
    public void onClearSearchClicked() {
        editTextSearchNewWord.getText().clear();
    }


    @OnClick(R.id.imageViewAddNewWord)
    public void onAddNewWordClicked() {


        Vocabulary vocabulary = new Vocabulary(example,imageUrl,transcription, translation,
                other_translations, set_words,
                new Date(), "again", false, false);



        final String searchedWord = textViewNewWord.getText().toString();
        firebaseFirestore.collection("Users").document(userID).collection("Vocabulary").document(searchedWord)
                .set(vocabulary)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        imageViewAddNewWord.setClickable(false);
                        imageViewAddNewWord.setImageDrawable(getDrawable(R.drawable.ic_check_yellow));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewWordsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });



    }


    @OnClick(R.id.imageViewSpeech)
    public void onSpeechClicked() {
        String text = textViewNewWord.getText().toString();
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
            Toast.makeText(AddNewWordsActivity.this,getText(R.string.speechErr),Toast.LENGTH_SHORT).show();
        }

    }



}
