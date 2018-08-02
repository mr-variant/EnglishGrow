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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //GOOGLE
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGooglesignInButton;
    private static final int RC_SIGN_IN = 9001;

    //Views
    private ProgressDialog mProgressDialog;

    private AlertDialog mRegAlertDialog;
    private AlertDialog mRestoreAlertDialog;

    private EditText mLoginEditText;
    private EditText mPasswordEditText;

    private TextView mLoginErrorTextView;
    private TextView mPasswordErrorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Views
        mGooglesignInButton = (SignInButton) findViewById (R.id. sign_in_google_button);
        mLoginErrorTextView = (TextView) findViewById(R.id.loginErrorTextView);
        mPasswordErrorTextView = (TextView) findViewById(R.id.passwordErrorTextView);
        mLoginEditText = (EditText) findViewById(R.id.loginEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        mGooglesignInButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });




        mLoginErrorTextView.setVisibility(View.INVISIBLE);
        mPasswordErrorTextView.setVisibility(View.INVISIBLE);


        findViewById(R.id.logInButton).setOnClickListener(this);
        findViewById(R.id.singUpButton).setOnClickListener(this);
        findViewById(R.id.forgetPasswordTextView).setOnClickListener(this);




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

    @Override
    public void onClick(View view) {

        updateUI(null);

        switch (view.getId()) {

            case R.id.logInButton:
                String login = mLoginEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if(login.isEmpty() || password.isEmpty()) {
                    if(login.isEmpty()) {
                        mLoginErrorTextView.setText(getString(R.string.loginIsNull));
                        mLoginErrorTextView.setVisibility(View.VISIBLE);
                    }
                    if(password.isEmpty()) {
                        mPasswordErrorTextView.setText(getString(R.string.passwordIsNull));
                        mPasswordErrorTextView.setVisibility(View.VISIBLE);
                    }
                } else if(!isValidEmail(login)) {
                    mLoginErrorTextView.setText(getString(R.string.loginIsNotValid));
                    mLoginErrorTextView.setVisibility(View.VISIBLE);
                } else if(password.length() < 6) {
                    mPasswordErrorTextView.setText(getString(R.string.passwordLessSix));
                    mPasswordErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    signInEmailPassword(login,password);
                }

                break;


            case R.id.singUpButton:
                AlertDialog.Builder regWindows = new AlertDialog.Builder(
                        LoginActivity.this);

                regWindows.setPositiveButton(getString(R.string.alertRegOk), null);
                regWindows.setNegativeButton(getString(R.string.cancel), null);

                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                regWindows.setView(inflater.inflate(R.layout.alert_dialog_sign_up, null));

                mRegAlertDialog = regWindows.create();
                mRegAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = mRegAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                final EditText newEmailEditText = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newEmailEditText);
                                final EditText newPasswordEditText = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newPasswordEditText);
                                final EditText newConfirmPasswordEditText = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newConfirmPasswordEditText);
                                final EditText newUserNameEditText = (EditText) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newUserNameEditText);
                                final CheckBox rulesCheckBox = (CheckBox) ((AlertDialog) mRegAlertDialog).findViewById(R.id.rulesCheckBox);

                                String newEmail = newEmailEditText.getText().toString();
                                String newPass = newPasswordEditText.getText().toString();
                                String newConfPass = newConfirmPasswordEditText.getText().toString();
                                String newUserName =  newUserNameEditText.getText().toString();

                                final TextView emailError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newEmailErrorTextView);
                                final TextView passError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newPassErrorTextView);
                                final TextView confPassError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newPassConfErrorTextView);
                                final TextView newUserNameErrorTextView = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newUserNameErrorTextView);
                                final TextView rulesErrorTextView = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.rulesErrorTextView);


                                emailError.setVisibility(View.GONE);
                                passError.setVisibility(View.GONE);
                                confPassError.setVisibility(View.GONE);
                                newUserNameErrorTextView.setVisibility(View.GONE);
                                rulesErrorTextView.setVisibility(View.GONE);


                                if(newEmail.isEmpty() || newPass.isEmpty() || newConfPass.isEmpty() || newUserName.isEmpty()) {
                                    if(newEmail.isEmpty()) {
                                        emailError.setText(getString(R.string.loginIsNull));
                                        emailError.setVisibility(View.VISIBLE);
                                    }

                                    if(newPass.isEmpty()) {
                                        passError.setText(getString(R.string.passwordIsNull));
                                        passError.setVisibility(View.VISIBLE);
                                    }

                                    if(newConfPass.isEmpty()) {
                                        confPassError.setText(getString(R.string.passwordIsNull));
                                        confPassError.setVisibility(View.VISIBLE);
                                    }

                                    if(newUserName.isEmpty()) {
                                        newUserNameErrorTextView.setText(getString(R.string.nameIsNull));
                                        newUserNameErrorTextView.setVisibility(View.VISIBLE);
                                    }

                                } else if(!isValidEmail(newEmail)) {
                                    emailError.setText(getString(R.string.loginIsNotValid));
                                    emailError.setVisibility(View.VISIBLE);
                                } else if(newPass.length() < 6) {
                                    passError.setText(getString(R.string.passwordLessSix));
                                    passError.setVisibility(View.VISIBLE);
                                } else if(!newPass.equals(newConfPass)){
                                    passError.setText(getString(R.string.passDontEquals));
                                    passError.setVisibility(View.VISIBLE);
                                } else if(!rulesCheckBox.isChecked()){
                                    rulesErrorTextView.setText(getString(R.string.rulesErr));
                                    rulesErrorTextView.setVisibility(View.VISIBLE);
                                } else {
                                    createAccount(newEmail,newPass,newUserName);
                                    //signInEmailPassword(newEmail,newPass);
                                }

                            }
                        });
                    }
                });

                mRegAlertDialog.show();

                mRegAlertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                break;


            case R.id.forgetPasswordTextView:

                AlertDialog.Builder restoreWindows = new AlertDialog.Builder(
                        LoginActivity.this);

                restoreWindows.setPositiveButton(getString(R.string.forgotPassButt), null);
                restoreWindows.setNegativeButton(getString(R.string.cancel), null);

                LayoutInflater inflaterRestore = LoginActivity.this.getLayoutInflater();
                restoreWindows.setView(inflaterRestore.inflate(R.layout.alert_dialog_forgot_password, null));

                mRestoreAlertDialog = restoreWindows.create();
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
                                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                mRestoreAlertDialog.dismiss();
                                                Toast toast = Toast.makeText(LoginActivity.this, getString(R.string.forgotPassMessage) ,
                                                        Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                            }
                                            else {
                                                Toast toast = Toast.makeText(LoginActivity.this, task.getException().getMessage().toString() ,
                                                        Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
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
                break;
        }

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

                            TextView rulesErrorTextView = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.rulesErrorTextView);
                            rulesErrorTextView.setVisibility(View.VISIBLE);
                            rulesErrorTextView.setText(task.getException().getMessage().toString());
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
        mLoginErrorTextView.setVisibility(View.GONE);
        mPasswordErrorTextView.setVisibility(View.GONE);
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

