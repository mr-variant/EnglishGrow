package aksenchyk.englishgrow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WordSetActivity extends AppCompatActivity {

    @BindView(R.id.toolbarWordSet) Toolbar toolbarWordSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_set);

        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbarWordSet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.titleWordSet));
        toolbarWordSet.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbarWordSet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();// возврат на предыдущий activity
            }
        });

    }




    @OnClick(R.id.cardViewTop100)
    void onСardViewTop100Click(View view) {
        Intent intent = new Intent(this, WordsListActivity.class);
        intent.putExtra(WordsListActivity.KEY_VOCABULARY_CATEGORY,"top100");

        startActivity(intent);
    }

    @OnClick(R.id.cardViewFood)
    void onСardViewFoodClick(View view) {
        Intent intent = new Intent(this, WordsListActivity.class);
        intent.putExtra(WordsListActivity.KEY_VOCABULARY_CATEGORY, "food");

        startActivity(intent);
    }

}
