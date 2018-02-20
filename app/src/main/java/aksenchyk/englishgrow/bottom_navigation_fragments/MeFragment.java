package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import aksenchyk.englishgrow.R;



public class MeFragment extends Fragment {

    public MeFragment() {
        // Required empty public constructor
    }

    private Button buttonMe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        buttonMe = (Button) rootView.findViewById(R.id.buttonMe);

        buttonMe.setText("123");

        // Inflate the layout for this fragment
        return rootView;
    }

}
