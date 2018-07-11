package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.Manifest;
import aksenchyk.englishgrow.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class MeFragment extends Fragment {

    private FirebaseFirestore mFirestore;

    private CircleImageView profileImage;
    private Uri mainImageURI = null;

    public MeFragment() {
        // Required empty public constructor
    }

    private Button buttonLogOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirestore = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        buttonLogOut = (Button) rootView.findViewById(R.id.buttonLogOut);


        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth =  FirebaseAuth.getInstance();
                auth.signOut();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });

        profileImage = (CircleImageView) rootView.findViewById(R.id.profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(getContext(),getString(R.string.permission_denied),Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(getActivity(),new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },1);

                    } else {
/*
                        // start picker to get image for cropping and then use the image in cropping activity
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());

*/
                    }

                }



            }
        });




        // Inflate the layout for this fragment
        return rootView;
    }

/*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {

                mainImageURI = result.getUri();
                profileImage.setImageURI(mainImageURI);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }*/



}
