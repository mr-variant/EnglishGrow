package aksenchyk.englishgrow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VocabularyActivity extends AppCompatActivity {

    @BindView(R.id.toolbarVocabulary) Toolbar toolbarVocabulary;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        ButterKnife.bind(this);


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


    //action_add_new_word

}
