package aksenchyk.englishgrow;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



import aksenchyk.englishgrow.bottom_navigation_fragments.ChatFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.DictionaryFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.GrammarFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.MeFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.TrainingFragment;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar main_toolbar;


    private MeFragment meFragment;
    private GrammarFragment grammarFragment;
    private TrainingFragment trainingFragment;
    private DictionaryFragment dictionaryFragment;
    private ChatFragment chatFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);


        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);

        meFragment = new MeFragment();
        grammarFragment = new GrammarFragment();
        trainingFragment = new TrainingFragment();
        dictionaryFragment = new DictionaryFragment();
        chatFragment = new ChatFragment();

        setFragment(meFragment);
        //setTitle(getString(R.string.profile));

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_user:
                     //   setTitle(getString(R.string.profile));
                        setFragment(meFragment);
                        return true;
                    case R.id.navigation_grammar:
                     //   setTitle(getString(R.string.menu_down_grammar));
                        setFragment(grammarFragment);
                        return true;
                    case R.id.navigation_training:
                     //   setTitle(getString(R.string.menu_down_training));
                        setFragment(trainingFragment);
                        return true;
                    case R.id.navigation_dictionary:
                    //    setTitle(getString(R.string.menu_down_dictionary));
                        setFragment(dictionaryFragment);
                        return true;
                    case R.id.navigation_chat:
                    //    setTitle(getString(R.string.menu_down_chat));
                        setFragment(chatFragment);
                        return true;

                    default:

                        return false;
                }

            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }




}
