package aksenchyk.englishgrow;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aksenchyk.englishgrow.models.GrammarUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GrammarActivity extends AppCompatActivity {

    @BindView(R.id.toolbarGrammar) Toolbar toolbarGrammar;

    @BindView(R.id.cardViewGrammar1) CardView cardViewGrammar1;
    @BindView(R.id.cardViewGrammar2) CardView cardViewGrammar2;
    @BindView(R.id.cardViewGrammar3) CardView cardViewGrammar3;
    @BindView(R.id.cardViewGrammar4) CardView cardViewGrammar4;

    @BindView(R.id.textViewUnit1) TextView textViewUnit1;
    @BindView(R.id.textViewUnit2) TextView textViewUnit2;
    @BindView(R.id.textViewUnit3) TextView textViewUnit3;
    @BindView(R.id.textViewUnit4) TextView textViewUnit4;

    @BindView(R.id.textViewSentence) TextView textViewSentence;
    @BindView(R.id.textViewPuzzleSentence) TextView textViewPuzzleSentence;

    @BindView(R.id.textViewNextSent) TextView textViewNextSent;
    @BindView(R.id.textViewErrSentace) TextView textViewErrSentace;

    @BindView(R.id.imageViewDelSentense) ImageView imageViewDelSentense;
    @BindView(R.id.imageViewBackToStart) ImageView imageViewBackToStart;

    @BindView(R.id.progressBarGramma) ProgressBar progressBarGramma;




    public static final String KEY_GRAMMAR_UNIT = "key_grammar_unit";
    public static final String KEY_GRAMMAR_NAME = "key_grammar_name";

    private String grammarId;
    private String grammarName;


    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private String userID;

    private ArrayList<GrammarUnit> grammarUnits = new ArrayList<GrammarUnit>();


    //Количество предложений
    //текущее полоение
    private int sentensesCount;
    private int wordCount;
    private int currentSentanse;
    private String sentanse;
    private int currentWord;
    private int exp;
    private int prevExpDay;
    private int prevExp;

    private GrammarUnit currentGrammarUnit = new GrammarUnit();

    private String prevText;

    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar);

        ButterKnife.bind(this);

        textViewPuzzleSentence.setText("");

        textViewNextSent.setVisibility(View.INVISIBLE);
        textViewNextSent.setClickable(false);

        textViewErrSentace.setVisibility(View.INVISIBLE);
        textViewErrSentace.setClickable(false);

        imageViewBackToStart.setVisibility(View.INVISIBLE);
        imageViewBackToStart.setClickable(false);


        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        userID = mFirebaseAuth.getCurrentUser().getUid();


        mFirestore.collection("Users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    prevExpDay = documentSnapshot.getLong("dayExp").intValue();
                    prevExp = documentSnapshot.getLong("experience").intValue();
                } else {
                    Toast.makeText(GrammarActivity.this, "(FIRESTORE Retrieve Error) : " + e, Toast.LENGTH_LONG).show();
                }
            }
        });



        // Get restaurant ID from extras
        grammarId = getIntent().getExtras().getString(KEY_GRAMMAR_UNIT);
        grammarName = getIntent().getExtras().getString(KEY_GRAMMAR_NAME);
        if (grammarId == null || grammarName == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_GRAMMAR_UNIT);
        }

        setSupportActionBar(toolbarGrammar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(grammarName);
        toolbarGrammar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarGrammar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });




        mFirestore.collection("Gramma").document(grammarId).collection("exercises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GrammarUnit grammarUnit = document.toObject(GrammarUnit.class);
                                grammarUnits.add(grammarUnit);
                            }

                            sentensesCount = grammarUnits.size();
                            //byrhtvtynbnm
                            //TODO

                            currentGrammarUnit = grammarUnits.get(currentSentanse);
                            textViewSentence.setText(currentGrammarUnit.getTranslate());
                            sentanse = currentGrammarUnit.getText();
                            sentanse += " ";

                            currentSentanse = 0;
                            currentWord = 0;
                            exp = 0;
                            wordCount = currentGrammarUnit.getAnswers().size() / 4;

                            cardViewGrammar1.setVisibility(View.VISIBLE);
                            cardViewGrammar1.setClickable(true);

                            cardViewGrammar2.setVisibility(View.VISIBLE);
                            cardViewGrammar2.setClickable(true);

                            cardViewGrammar3.setVisibility(View.VISIBLE);
                            cardViewGrammar3.setClickable(true);

                            cardViewGrammar4.setVisibility(View.VISIBLE);
                            cardViewGrammar4.setClickable(true);

                            nextWord();



                        } else {
                            Log.d("Err", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @OnClick(R.id.cardViewGrammar1)
    public void onCardViewGrammar1Clicked() {
        prevText = textViewPuzzleSentence.getText().toString();
        String currentText = prevText + textViewUnit1.getText()  + " ";
        textViewPuzzleSentence.setText(currentText);

        nextWord();
    }

    @OnClick(R.id.cardViewGrammar2)
    public void onCardViewGrammar2Clicked() {
        prevText = textViewPuzzleSentence.getText().toString();
        String currentText = prevText + textViewUnit2.getText()  + " ";
        textViewPuzzleSentence.setText(currentText);

        nextWord();
    }

    @OnClick(R.id.cardViewGrammar3)
    public void onCardViewGrammar3Clicked() {
        prevText = textViewPuzzleSentence.getText().toString();
        String currentText = prevText + textViewUnit3.getText() + " ";
        textViewPuzzleSentence.setText(currentText);

        nextWord();
    }

    @OnClick(R.id.cardViewGrammar4)
    public void onCardViewGrammar4Clicked() {
        prevText = textViewPuzzleSentence.getText().toString();
        String currentText = prevText + textViewUnit4.getText() + " ";
        textViewPuzzleSentence.setText(currentText);

        nextWord();
    }


    @OnClick(R.id.imageViewDelSentense)
    public void onImageViewDelSentenseClicked() {
        currentWord = 1;
        prevText = "";
        textViewPuzzleSentence.setText("");
        textViewUnit1.setText(currentGrammarUnit.getAnswers().get(0));
        textViewUnit2.setText(currentGrammarUnit.getAnswers().get(1));
        textViewUnit3.setText(currentGrammarUnit.getAnswers().get(2));
        textViewUnit4.setText(currentGrammarUnit.getAnswers().get(3));
    }



    @OnClick(R.id.textViewNextSent)
    public void onTextViewNextSentClicked() {
        nextSentanse();
    }


    @OnClick(R.id.imageViewBackToStart)
    public void onImageViewBackToStartClicked() {
        currentWord = 1;
        prevText = "";
        textViewPuzzleSentence.setText("");
        textViewUnit1.setText(currentGrammarUnit.getAnswers().get(0));
        textViewUnit2.setText(currentGrammarUnit.getAnswers().get(1));
        textViewUnit3.setText(currentGrammarUnit.getAnswers().get(2));
        textViewUnit4.setText(currentGrammarUnit.getAnswers().get(3));

        textViewErrSentace.setVisibility(View.INVISIBLE);
        textViewErrSentace.setClickable(false);

        imageViewBackToStart.setVisibility(View.INVISIBLE);
        imageViewBackToStart.setClickable(false);

        cardViewGrammar1.setVisibility(View.VISIBLE);
        cardViewGrammar1.setClickable(true);

        cardViewGrammar2.setVisibility(View.VISIBLE);
        cardViewGrammar2.setClickable(true);

        cardViewGrammar3.setVisibility(View.VISIBLE);
        cardViewGrammar3.setClickable(true);

        cardViewGrammar4.setVisibility(View.VISIBLE);
        cardViewGrammar4.setClickable(true);

        imageViewDelSentense.setVisibility(View.VISIBLE);
        imageViewDelSentense.setClickable(true);

    }






    void nextWord() {

        //Log.d("zxcv","nextWord() currentWord = " + currentWord + " wordCount = " + wordCount);




        if(currentWord == wordCount) {           //ТУТ ПРОВЕРКА

            String puzzleSentanse = textViewPuzzleSentence.getText().toString();



            if (sentanse.equals(puzzleSentanse)) {
                hideViews();
                textViewNextSent.setVisibility(View.VISIBLE);
                textViewNextSent.setClickable(true);
            } else {
                hideViews();
                textViewErrSentace.setVisibility(View.VISIBLE);
                textViewErrSentace.setClickable(true);

                imageViewBackToStart.setVisibility(View.VISIBLE);
                imageViewBackToStart.setClickable(true);

                String errSentanse = "Не верный ответ!\nПравильно: " + sentanse;
                textViewErrSentace.setText(errSentanse);
            }



            //nextSentanse();

        } else if( currentWord < wordCount ) {
            textViewUnit1.setText(currentGrammarUnit.getAnswers().get(4 * currentWord));
            textViewUnit2.setText(currentGrammarUnit.getAnswers().get(4 * currentWord + 1));
            textViewUnit3.setText(currentGrammarUnit.getAnswers().get(4 * currentWord + 2));
            textViewUnit4.setText(currentGrammarUnit.getAnswers().get(4 * currentWord + 3));

            currentWord++; // ТУТ ПРОБЛЕМА
        }



    }


    void nextSentanse() {

        int progress =  currentSentanse * 100 / sentensesCount;

        progressBarGramma.setProgress(progress);

        textViewNextSent.setVisibility(View.INVISIBLE);
        textViewNextSent.setClickable(false);

        cardViewGrammar1.setVisibility(View.VISIBLE);
        cardViewGrammar1.setClickable(true);

        cardViewGrammar2.setVisibility(View.VISIBLE);
        cardViewGrammar2.setClickable(true);

        cardViewGrammar3.setVisibility(View.VISIBLE);
        cardViewGrammar3.setClickable(true);

        cardViewGrammar4.setVisibility(View.VISIBLE);
        cardViewGrammar4.setClickable(true);

        imageViewDelSentense.setVisibility(View.VISIBLE);
        imageViewDelSentense.setClickable(true);

        exp += 3;


        if(currentSentanse == sentensesCount - 1) {

            AlertDialog.Builder restoreWindow = new AlertDialog.Builder(GrammarActivity.this);



            LayoutInflater inflaterRestore = GrammarActivity.this.getLayoutInflater();
            restoreWindow.setView(inflaterRestore.inflate(R.layout.alert_dialog_grammar, null));

            mAlertDialog = restoreWindow.create();
            mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {

                    final TextView textViewGrammarFinalText = (TextView) ((AlertDialog) mAlertDialog).findViewById(R.id.textViewGrammarFinalText);
                    final Button buttonGrammarFinal = (Button) ((AlertDialog) mAlertDialog).findViewById(R.id.buttonGrammarFinal);

                    textViewGrammarFinalText.setText(getString(R.string.rezult_grammar_exp, exp));

                    buttonGrammarFinal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Map<String,Object> userMap = new HashMap<>();
                            int experience = prevExp + exp;
                            userMap.put("experience", experience);
                            experience = prevExpDay  + exp;
                            userMap.put("dayExp", experience);

                            //update
                            mFirestore.collection("Users").document(userID).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        finish();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(GrammarActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });



                        }
                    });
                }
            });

            mAlertDialog.setCanceledOnTouchOutside(false);
            mAlertDialog.show();


        } else {
            currentWord = 1;
            prevText = "";
            currentSentanse++;
            currentGrammarUnit = grammarUnits.get(currentSentanse);

            textViewUnit1.setText(currentGrammarUnit.getAnswers().get(0));
            textViewUnit2.setText(currentGrammarUnit.getAnswers().get(1));
            textViewUnit3.setText(currentGrammarUnit.getAnswers().get(2));
            textViewUnit4.setText(currentGrammarUnit.getAnswers().get(3));

            textViewSentence.setText(currentGrammarUnit.getTranslate());
            sentanse = currentGrammarUnit.getText();
            sentanse += " ";

            wordCount = currentGrammarUnit.getAnswers().size() / 4;

            textViewPuzzleSentence.setText("");
            Log.d("zxcv", "currentWord =0--- " + currentWord);
        }

    }


    void hideViews() {
        cardViewGrammar1.setVisibility(View.INVISIBLE);
        cardViewGrammar1.setClickable(false);

        cardViewGrammar2.setVisibility(View.INVISIBLE);
        cardViewGrammar2.setClickable(false);

        cardViewGrammar3.setVisibility(View.INVISIBLE);
        cardViewGrammar3.setClickable(false);

        cardViewGrammar4.setVisibility(View.INVISIBLE);
        cardViewGrammar4.setClickable(false);

        imageViewDelSentense.setVisibility(View.INVISIBLE);
        imageViewDelSentense.setClickable(false);

        textViewNextSent.setVisibility(View.INVISIBLE);
        textViewNextSent.setClickable(false);

        imageViewBackToStart.setVisibility(View.INVISIBLE);
        imageViewBackToStart.setClickable(false);
    }


}