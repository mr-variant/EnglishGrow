package aksenchyk.englishgrow;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import aksenchyk.englishgrow.bottom_navigation_fragments.ChatFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.DictionaryFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.GrammarFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.MeFragment;
import aksenchyk.englishgrow.bottom_navigation_fragments.TrainingFragment;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference dataBaseRefence;

    private List<String> discr;
    private ListView listUserTasks;


    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

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

        meFragment = new MeFragment();
        grammarFragment = new GrammarFragment();
        trainingFragment = new TrainingFragment();
        dictionaryFragment = new DictionaryFragment();
        chatFragment = new ChatFragment();

        setFragment(meFragment);
        setTitle(getString(R.string.profile));

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_user:
                        setTitle(getString(R.string.profile));
                        setFragment(meFragment);
                        return true;
                    case R.id.navigation_grammar:
                        setTitle(getString(R.string.menu_down_grammar));
                        setFragment(grammarFragment);
                        return true;
                    case R.id.navigation_training:
                        setTitle(getString(R.string.menu_down_training));
                        setFragment(trainingFragment);
                        return true;
                    case R.id.navigation_dictionary:
                        setTitle(getString(R.string.menu_down_dictionary));
                        setFragment(dictionaryFragment);
                        return true;
                    case R.id.navigation_chat:
                        setTitle(getString(R.string.menu_down_chat));
                        setFragment(chatFragment);
                        return true;

                    default:

                        return false;
                }

            }
        });



        dataBaseRefence = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        dataBaseRefence.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                discr = dataSnapshot.child("Tasks").getValue(t);
                Toast.makeText(MainActivity.this, "--- " /*+ discr.get(1).toString()*/,Toast.LENGTH_LONG).show();
                updateUI();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Filed read value! ",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private void updateUI() {


    }


    @Override public void onBackPressed() {

    }

}
