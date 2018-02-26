package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import aksenchyk.englishgrow.R;



public class MeFragment extends Fragment {

    private FirebaseFirestore mFirestore;

    public MeFragment() {
        // Required empty public constructor
    }

    private Button buttonMe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirestore = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        buttonMe = (Button) rootView.findViewById(R.id.buttonMe);

        buttonMe.setText("123");
        buttonMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth =  FirebaseAuth.getInstance();
                auth.signOut();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

}
