package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.SetupActivity;
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
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.profile));

        buttonLogOut = (Button) rootView.findViewById(R.id.buttonLogOut);



        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptsView = layoutInflater.inflate(R.layout.alert_dialog_logout, null);


                //Настраиваем prompt.xml для нашего AlertDialog:
                builder.setView(promptsView);


                builder.setNegativeButton(R.string.alert_dialog_logout_close,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_logout_exit,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseAuth auth =  FirebaseAuth.getInstance();
                                        auth.signOut();
                                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(loginIntent);
                                        getActivity().finish();
                                    }
                                });




                AlertDialog alert = builder.create();
                alert.show();


                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);


            }
        });

        profileImage = (CircleImageView) rootView.findViewById(R.id.profileImage);






        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.me, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_me_setup :

                if(isOnline()) {
                    Intent setupIntent = new Intent(getActivity(), SetupActivity.class);
                    startActivity(setupIntent);
                } else {
                    Toast.makeText(getActivity(),getString(R.string.connection_bad), Toast.LENGTH_LONG).show();
                }



            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
