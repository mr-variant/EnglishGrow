package aksenchyk.englishgrow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.regex.Pattern;

public class EmailPasswordActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //GOOGLE
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGooglesignInButton;
    private static final int RC_SIGN_IN = 9001;

    //UI
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
        setContentView(R.layout.activity_email_password);

        // Views


        // Buttons
        mGooglesignInButton = (SignInButton) findViewById (R.id. sign_in_google_button);
        mGooglesignInButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        findViewById(R.id.logInButton).setOnClickListener(this);
        findViewById(R.id.singUpButton).setOnClickListener(this);

        //TextViews
        mLoginErrorTextView = (TextView) findViewById(R.id.loginErrorTextView);
        mPasswordErrorTextView = (TextView) findViewById(R.id.passwordErrorTextView);

        mLoginErrorTextView.setVisibility(View.INVISIBLE);
        mPasswordErrorTextView.setVisibility(View.INVISIBLE);

        findViewById(R.id.forgetPasswordTextView).setOnClickListener(this);

        // EditTexts
        mLoginEditText = (EditText) findViewById(R.id.loginEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);


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
                    Intent intent = new Intent(EmailPasswordActivity.this,MainActivity.class);
                    startActivity(intent);
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
            startActivity(intent);
        }


            updateUI(user);
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
                        EmailPasswordActivity.this);

                regWindows.setPositiveButton(getString(R.string.alertRegOk), null);
                regWindows.setNegativeButton(getString(R.string.cancel), null);

                LayoutInflater inflater = EmailPasswordActivity.this.getLayoutInflater();
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
                                final CheckBox rulesCheckBox = (CheckBox) ((AlertDialog) mRegAlertDialog).findViewById(R.id.rulesCheckBox);

                                String newEmail = newEmailEditText.getText().toString();
                                String newPass = newPasswordEditText.getText().toString();
                                String newConfPass = newConfirmPasswordEditText.getText().toString();


                                final TextView emailError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newEmailErrorTextView);
                                final TextView passError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newPassErrorTextView);
                                final TextView confPassError = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.newPassConfErrorTextView);
                                final TextView rulesErrorTextView = (TextView) ((AlertDialog) mRegAlertDialog).findViewById(R.id.rulesErrorTextView);


                                emailError.setVisibility(View.GONE);
                                passError.setVisibility(View.GONE);
                                confPassError.setVisibility(View.GONE);
                                rulesErrorTextView.setVisibility(View.GONE);


                                if(newEmail.isEmpty() || newPass.isEmpty() || newConfPass.isEmpty()) {
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
                                    createAccount(newEmail,newPass);
                                }

                            }
                        });
                    }
                });

                mRegAlertDialog.show();

                break;


            case R.id.forgetPasswordTextView:

                AlertDialog.Builder restoreWindows = new AlertDialog.Builder(
                        EmailPasswordActivity.this);

                restoreWindows.setPositiveButton(getString(R.string.forgotPassButt), null);
                restoreWindows.setNegativeButton(getString(R.string.cancel), null);

                LayoutInflater inflaterRestore = EmailPasswordActivity.this.getLayoutInflater();
                restoreWindows.setView(inflaterRestore.inflate(R.layout.alert_dialog_forgot_password, null));

                mRestoreAlertDialog = restoreWindows.create();
                mRestoreAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = mRestoreAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                final EditText restoreEmailEditText = (EditText) ((AlertDialog) mRestoreAlertDialog).findViewById(R.id.restoreEmailEditText);
                                String email = restoreEmailEditText.getText().toString();
                                final TextView emailError = (TextView) ((AlertDialog) mRestoreAlertDialog).findViewById(R.id.restoreEmailErrorTextView);

                                emailError.setVisibility(View.GONE);

                                if(email.isEmpty()) {
                                    emailError.setText(getString(R.string.loginIsNull));
                                    emailError.setVisibility(View.VISIBLE);
                                } else if(!isValidEmail(email)){
                                    emailError.setText(getString(R.string.loginIsNotValid));
                                    emailError.setVisibility(View.VISIBLE);
                                } else {
                                    sendPasswordResetEmail(email);
                                }

                            }
                        });
                    }
                });

                mRestoreAlertDialog.show();

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
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(EmailPasswordActivity.this,  task.getException().getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    //EmailAndLogin
    private void createAccount(String email, String password) {

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            hideProgressDialog();
                            mRegAlertDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast toast = Toast.makeText(EmailPasswordActivity.this, task.getException().getMessage().toString() ,
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
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

                            Intent intent = new Intent(EmailPasswordActivity.this,MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.

                            updateUI(null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(EmailPasswordActivity.this);
                            builder.setTitle(getString(R.string.loginPassIncorrectTitle))
                                    .setMessage(getString(R.string.loginPassIncorrect))
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

    private void sendPasswordResetEmail(String email) {
        // Отправляем email с паролем
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mRestoreAlertDialog.dismiss();
                    Toast toast = Toast.makeText(EmailPasswordActivity.this, getString(R.string.forgotPassMessage) ,
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(EmailPasswordActivity.this, task.getException().getMessage().toString() ,
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        mLoginErrorTextView.setVisibility(View.GONE);
        mPasswordErrorTextView.setVisibility(View.GONE);
        hideProgressDialog();
        if (user != null) {
            Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
            startActivity(intent);
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

