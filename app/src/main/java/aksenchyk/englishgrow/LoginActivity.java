package aksenchyk.englishgrow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //GOOGLE
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 9001;

    //Views
    private ProgressDialog mProgressDialog;

    private AlertDialog mRegAlertDialog;
    private AlertDialog mRestoreAlertDialog;

    @BindView(R.id.editTextLoginEmail) EditText editTextLoginEmail;
    @BindView(R.id.editTextLoginPassword) EditText editTextLoginPassword;

    @BindView(R.id.textViewLoginError) TextView textViewLoginError;
    @BindView(R.id.textViewLoginPasswordError) TextView textViewLoginPasswordError;
    @BindView(R.id.textViewLoginForgetPassword) TextView textViewLoginForgetPassword;

    @BindView(R.id.buttonLogin) Button buttonLogin;
    @BindView(R.id.buttonSingUp) Button buttonSingUp;
    @BindView(R.id.buttonSignInGoogle) SignInButton buttonSignInGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);



        buttonSignInGoogle.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(null);

                String login = editTextLoginEmail.getText().toString();
                String password = editTextLoginPassword.getText().toString();

                if(login.isEmpty() || password.isEmpty()) {
                    if(login.isEmpty()) {
                        textViewLoginError.setText(getString(R.string.loginIsNull));
                        textViewLoginError.setVisibility(View.VISIBLE);
                    }
                    if(password.isEmpty()) {
                        textViewLoginPasswordError.setText(getString(R.string.passwordIsNull));
                        textViewLoginPasswordError.setVisibility(View.VISIBLE);
                    }
                } else if(!isValidEmail(login)) {
                    textViewLoginError.setText(getString(R.string.loginIsNotValid));
                    textViewLoginError.setVisibility(View.VISIBLE);
                } else if(password.length() < 6) {
                    textViewLoginPasswordError.setText(getString(R.string.passwordLessSix));
                    textViewLoginPasswordError.setVisibility(View.VISIBLE);
                } else {
                    signInEmailPassword(login,password);
                }

            }
        });


        buttonSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(null);

                AlertDialog.Builder registrationWindow = new AlertDialog.Builder(LoginActivity.this);

                registrationWindow.setPositiveButton(getString(R.string.alertRegOk), null);
                registrationWindow.setNegativeButton(getString(R.string.cancel), null);

                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                registrationWindow.setView(inflater.inflate(R.layout.alert_dialog_sign_up, null));

                mRegAlertDialog = registrationWindow.create();
                mRegAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button buttoAddNewUser = mRegAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        buttoAddNewUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final EditText editTextNewEmail = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.editTextNewEmail);
                                final EditText editTextNewPassword = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.editTextNewPassword);
                                final EditText editTextNewConfirmPassword = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.editTextNewConfirmPassword);
                                final EditText editTextNewUserName = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.editTextNewUserName);
                                final CheckBox CheckBoxRulesNewUser = (CheckBox) ((AlertDialog) mRegAlertDialog).findViewById(R.id.CheckBoxRulesNewUser);

                                final TextView textViewNewEmailError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.textViewNewEmailError);
                                final TextView textViewNewPassError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.textViewNewPassError);
                                final TextView textViewNewPassConfError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.textViewNewPassConfError);
                                final TextView textViewNewUserNameError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.textViewNewUserNameError);
                                final TextView textViewRulesError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.textViewRulesError);


                                String newEmail = editTextNewEmail.getText().toString();
                                String newPass = editTextNewPassword.getText().toString();
                                String newConfPass = editTextNewConfirmPassword.getText().toString();
                                String newUserName =  editTextNewUserName.getText().toString();


                                textViewNewEmailError.setVisibility(View.GONE);
                                textViewNewPassError.setVisibility(View.GONE);
                                textViewNewPassConfError.setVisibility(View.GONE);
                                textViewNewUserNameError.setVisibility(View.GONE);
                                textViewRulesError.setVisibility(View.GONE);


                                if(newEmail.isEmpty() || newPass.isEmpty() || newConfPass.isEmpty() || newUserName.isEmpty()) {
                                    if(newEmail.isEmpty()) {
                                        textViewNewEmailError.setText(getString(R.string.loginIsNull));
                                        textViewNewEmailError.setVisibility(View.VISIBLE);
                                    }

                                    if(newPass.isEmpty()) {
                                        textViewNewPassError.setText(getString(R.string.passwordIsNull));
                                        textViewNewPassError.setVisibility(View.VISIBLE);
                                    }

                                    if(newConfPass.isEmpty()) {
                                        textViewNewPassConfError.setText(getString(R.string.passwordIsNull));
                                        textViewNewPassConfError.setVisibility(View.VISIBLE);
                                    }

                                    if(newUserName.isEmpty()) {
                                        textViewNewUserNameError.setText(getString(R.string.nameIsNull));
                                        textViewNewUserNameError.setVisibility(View.VISIBLE);
                                    }

                                } else if(!isValidEmail(newEmail)) {
                                    textViewNewEmailError.setText(getString(R.string.loginIsNotValid));
                                    textViewNewEmailError.setVisibility(View.VISIBLE);
                                } else if(newPass.length() < 6) {
                                    textViewNewPassError.setText(getString(R.string.passwordLessSix));
                                    textViewNewPassError.setVisibility(View.VISIBLE);
                                } else if(!newPass.equals(newConfPass)){
                                    textViewNewPassError.setText(getString(R.string.passDontEquals));
                                    textViewNewPassError.setVisibility(View.VISIBLE);
                                } else if(!CheckBoxRulesNewUser.isChecked()){
                                    textViewRulesError.setText(getString(R.string.rulesErr));
                                    textViewRulesError.setVisibility(View.VISIBLE);
                                } else {
                                    createAccount(newEmail,newPass,newUserName);
                                }

                            }
                        });
                    }
                });

                mRegAlertDialog.show();
                mRegAlertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });


        textViewLoginForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(null);
                AlertDialog.Builder restoreWindow = new AlertDialog.Builder(LoginActivity.this);

                restoreWindow.setPositiveButton(getString(R.string.forgotPassButt), null);
                restoreWindow.setNegativeButton(getString(R.string.cancel), null);

                LayoutInflater inflaterRestore = LoginActivity.this.getLayoutInflater();
                restoreWindow.setView(inflaterRestore.inflate(R.layout.alert_dialog_forgot_password, null));

                mRestoreAlertDialog = restoreWindow.create();
                mRestoreAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = mRestoreAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final EditText editTextRestoreEmail = (EditText) ((AlertDialog) mRestoreAlertDialog).findViewById(R.id.editTextRestoreEmail);
                                final TextView textViewRestoreEmailError = (TextView) ((AlertDialog) mRestoreAlertDialog).findViewById(R.id.textViewRestoreEmailError);

                                String email = editTextRestoreEmail.getText().toString();
                                textViewRestoreEmailError.setVisibility(View.GONE);

                                if(email.isEmpty()) {
                                    textViewRestoreEmailError.setText(getString(R.string.loginIsNull));
                                    textViewRestoreEmailError.setVisibility(View.VISIBLE);
                                } else if(!isValidEmail(email)){
                                    textViewRestoreEmailError.setText(getString(R.string.loginIsNotValid));
                                    textViewRestoreEmailError.setVisibility(View.VISIBLE);
                                } else {
                                    // Отправляем email с паролем
                                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                mRestoreAlertDialog.dismiss();
                                                Toast toast = Toast.makeText(LoginActivity.this, getString(R.string.forgotPassMessage), Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.BOTTOM, 0, 50);
                                                toast.show();
                                            }
                                            else {
                                                textViewRestoreEmailError.setText(task.getException().getMessage().toString());
                                                textViewRestoreEmailError.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }

                            }
                        });
                    }
                });

                mRestoreAlertDialog.show();
                mRestoreAlertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });


        // Google login config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    // User is signed in

                } else {
                    // User is signed out
                }

            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //GOOGLE
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Define actions after data validation (successful or not)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // See Result
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // LogIn with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
            }
        }
    }

    // Google Failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.googleConnectionFailed), Toast.LENGTH_SHORT).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();
        // Get users date
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // logIn with Firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();

                            //Add new user in DB
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                            firestore.collection("Users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                boolean userExist = false;
                                                String userUID = mAuth.getCurrentUser().getUid();

                                                for (DocumentSnapshot document : task.getResult()) {
                                                    String docID = document.getId();
                                                    if(docID.equals(userUID)) {
                                                        userExist = true;
                                                    }
                                                }

                                                if(!userExist) {
                                                    //to-do!!!!!!!!!!!

                                                    Map<String, Object> userMap = new HashMap<>();
                                                    String userName = user.getDisplayName();
                                                    String userImageURL = user.getPhotoUrl().toString();

                                                    userMap.put("name", userName);
                                                    userMap.put("image", userImageURL);
                                                    userMap.put("experience", 0);
                                                    userMap.put("satiation", 0);


                                                    FirebaseFirestore.getInstance().collection("Users").document(userUID).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            String errorMsg = e.getMessage();
                                                            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }


                                            }
                                        }
                                    });

                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this,  task.getException().getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    //EmailAndLogin
    private void createAccount(final String email,final String password, final String newUserName) {

        showProgressDialog();

        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Add new user in DB
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            String userID = user.getUid().toString();

                            Map<String, Object> userMap = new HashMap<>();

                            userMap.put("name", newUserName);
                            userMap.put("image", "https://firebasestorage.googleapis.com/v0/b/englishgrow-36226.appspot.com/o/profile_images%2Fdefault_profile.png?alt=media&token=1a8ee012-2666-4ca6-b5a5-c64b1f1c297b");
                            userMap.put("experience", 0);
                            userMap.put("satiation", 0);


                            firestore.collection("Users").document(userID).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String errorMsg = e.getMessage();
                                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            });

                            hideProgressDialog();
                            mRegAlertDialog.dismiss();
                            signInEmailPassword(email,password);
                        } else {

                            TextView textViewRulesError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.textViewRulesError);
                            textViewRulesError.setVisibility(View.VISIBLE);
                            textViewRulesError.setText(task.getException().getMessage().toString());
                            hideProgressDialog();
                        }

                    }
                });
    }

    private void signInEmailPassword(String email, String password) {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle(getString(R.string.loginPassIncorrectTitle))
                                    .setMessage(task.getException().getMessage().toString())
                                    .setCancelable(false)
                                    .setNegativeButton(getString(R.string.close),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        // ...
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        textViewLoginError.setVisibility(View.GONE);
        textViewLoginPasswordError.setVisibility(View.GONE);
        hideProgressDialog();
        if (user != null) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}

