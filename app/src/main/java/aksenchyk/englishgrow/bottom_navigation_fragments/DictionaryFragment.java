package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aksenchyk.englishgrow.R;
import butterknife.ButterKnife;


public class DictionaryFragment extends Fragment {

    public DictionaryFragment() {
        // Required empty public constructor
    }

    public static DictionaryFragment newInstance() {
        return new DictionaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dictionary, container, false);
        getActivity().setTitle(getString(R.string.menu_down_dictionary));

        ButterKnife.bind(this, rootView);


        return rootView;
    }

}
