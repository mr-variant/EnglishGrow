package aksenchyk.englishgrow;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import aksenchyk.englishgrow.bottom_navigation_fragments.BlogFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.DictionaryFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.GrammarFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.MeFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.TrainingFragment;

public class MainActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;


    //Views
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar toolbarMain;


    //Fragments
    private  MeFragment meFragment;
    private  GrammarFragment grammarFragment;
    private  TrainingFragment trainingFragment;
    private  DictionaryFragment dictionaryFragment;
    private  BlogFragment blogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        toolbarMain = (Toolbar) findViewById(R.id.toolbarMain);


        setSupportActionBar(toolbarMain);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null) {

            meFragment = new MeFragment();
            grammarFragment = new GrammarFragment();
            trainingFragment = new TrainingFragment();
            dictionaryFragment = new DictionaryFragment();
            blogFragment = new BlogFragment();

            initializeFragment();

            mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);

                    switch (item.getItemId()) {
                        case R.id.navigation_user:
                            replaceFragment(meFragment, currentFragment);
                            return true;
                        case R.id.navigation_grammar:
                            replaceFragment(grammarFragment, currentFragment);
                            return true;
                        case R.id.navigation_training:
                            replaceFragment(trainingFragment, currentFragment);
                            return true;
                        case R.id.navigation_dictionary:
                            replaceFragment(dictionaryFragment, currentFragment);
                            return true;
                        case R.id.navigation_blog:
                            replaceFragment(blogFragment, currentFragment);
                            return true;
                        default:
                            return false;
                    }

                }
            });


        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getInstance().getCurrentUser();


      //  mMainNav.setSelectedItemId(R.id.navigation_chat);

        if(currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }


    }




    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_frame, meFragment);
        fragmentTransaction.add(R.id.main_frame, grammarFragment);
        fragmentTransaction.add(R.id.main_frame, trainingFragment);
        fragmentTransaction.add(R.id.main_frame, dictionaryFragment);
        fragmentTransaction.add(R.id.main_frame, blogFragment);


        fragmentTransaction.hide(grammarFragment);
        fragmentTransaction.hide(trainingFragment);
        fragmentTransaction.hide(dictionaryFragment);
        //fragmentTransaction.hide(blogFragment);
        fragmentTransaction.hide(meFragment);

        fragmentTransaction.commit();

        mMainNav.setSelectedItemId(R.id.navigation_blog);
    }


    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if(fragment == meFragment){
            fragmentTransaction.hide(grammarFragment);
            fragmentTransaction.hide(trainingFragment);
            fragmentTransaction.hide(dictionaryFragment);
            fragmentTransaction.hide(blogFragment);
        }


        if(fragment == grammarFragment){
            fragmentTransaction.hide(meFragment);
            fragmentTransaction.hide(trainingFragment);
            fragmentTransaction.hide(dictionaryFragment);
            fragmentTransaction.hide(blogFragment);
        }


        if(fragment == trainingFragment){
            fragmentTransaction.hide(grammarFragment);
            fragmentTransaction.hide(meFragment);
            fragmentTransaction.hide(dictionaryFragment);
            fragmentTransaction.hide(blogFragment);
        }

        if(fragment == dictionaryFragment){
            fragmentTransaction.hide(grammarFragment);
            fragmentTransaction.hide(trainingFragment);
            fragmentTransaction.hide(meFragment);
            fragmentTransaction.hide(blogFragment);
        }

        if(fragment == blogFragment){
            fragmentTransaction.hide(grammarFragment);
            fragmentTransaction.hide(trainingFragment);
            fragmentTransaction.hide(dictionaryFragment);
            fragmentTransaction.hide(meFragment);
            blogFragment.setAnimFabShow();
        }



        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();

    }




}
