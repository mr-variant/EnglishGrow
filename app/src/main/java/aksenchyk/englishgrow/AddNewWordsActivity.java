package aksenchyk.englishgrow;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewWordsActivity extends AppCompatActivity {

    @BindView(R.id.toolbarAddNewWords) Toolbar toolbarAddNewWords;
    @BindView(R.id.editTextSearchNewWord) EditText editTextSearchNewWord;
    @BindView(R.id.imageViewClearSearchNewWord) ImageView imageViewClearSearchNewWord;



    private Animation animBtnShow;
    private Animation animBtnHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_words);

        ButterKnife.bind(this);


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
                    imageViewClearSearchNewWord.startAnimation(animBtnShow);
                    imageViewClearSearchNewWord.setClickable(true);
                    imageViewClearSearchNewWord.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });

    }





}
