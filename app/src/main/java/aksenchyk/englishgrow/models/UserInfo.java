package aksenchyk.englishgrow.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

/**
 * Created by ixvar on 2/27/2018.
 */

public  class UserInfo { //Singleton

    private static  String nickname;
    private static Date dateCreatedAccount;
    private static int experience;
    private static int satiation;

    private static String userUID;


    private static UserInfo uniqueInstance;


    protected UserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userUID = user.getUid();

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (DocumentSnapshot document : task.getResult()) {
                                String docID = document.getId();
                                if(docID.equals(userUID)) {

                                    if (document != null) {
                                        nickname = document.get("nickname").toString();
                                        dateCreatedAccount = (Date) document.get("dateCreatedAccount");
                                        experience = ((Number) document.get("experience")).intValue();
                                        satiation = ((Number) document.get("satiation")).intValue();
                                    } else {
                                        Log.d("!", "No such document");
                                    }

                                }
                            }
                        }
                    }
                });


    }

    public static UserInfo instance() {
        if(uniqueInstance == null) {
            uniqueInstance = new UserInfo();
        }

        return uniqueInstance;
    }



    public static String getUserUID() {
        return userUID;
    }

    public static String getNickname() {
        return nickname;
    }

    public static Date getDateCreatedAccount() {
        return dateCreatedAccount;
    }

    public static int getExperience() {
        return experience;
    }

    public static int getSatiation() {
        return satiation;
    }

}
