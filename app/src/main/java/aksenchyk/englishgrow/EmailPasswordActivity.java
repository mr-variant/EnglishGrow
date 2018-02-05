package aksenchyk.englishgrow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

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
        findViewById(R.id.logInButton).setOnClickListener(this);
        findViewById(R.id.singUpButton).setOnClickListener(this);

        findViewById(R.id.googleImageButton).setOnClickListener(this);
        findViewById(R.id.facebookImageButton).setOnClickListener(this);
        findViewById(R.id.twitterImageButton).setOnClickListener(this);

        //TextViews
        mLoginErrorTextView = (TextView) findViewById(R.id.loginErrorTextView);
        mPasswordErrorTextView = (TextView) findViewById(R.id.passwordErrorTextView);

        mLoginErrorTextView.setVisibility(View.INVISIBLE);
        mPasswordErrorTextView.setVisibility(View.INVISIBLE);

        findViewById(R.id.forgetPasswordTextView).setOnClickListener(this);

        // EditTexts
        mLoginEditText = (EditText) findViewById(R.id.loginEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);


        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void createAccount(String email, String password) {

        //showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signIn(String email, String password) {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(EmailPasswordActivity.this, "Успешная авторизация!",
                                    Toast.LENGTH_SHORT).show();
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




    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        mLoginErrorTextView.setVisibility(View.GONE);
        mPasswordErrorTextView.setVisibility(View.GONE);
        hideProgressDialog();
    }


    @Override
    public void onClick(View view) {

        updateUI(null);

        switch (view.getId()) {
            case R.id.logInButton:
                String login = mLoginEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if(login.isEmpty() && password.isEmpty()) {
                    mLoginErrorTextView.setText(getString(R.string.loginIsNull));
                    mPasswordErrorTextView.setText(getString(R.string.passwordIsNull));

                    mLoginErrorTextView.setVisibility(View.VISIBLE);
                    mPasswordErrorTextView.setVisibility(View.VISIBLE);

                } else if(login.isEmpty()) {
                    mLoginErrorTextView.setText(getString(R.string.loginIsNull));
                    mLoginErrorTextView.setVisibility(View.VISIBLE);

                } else if(!isValidEmail(login)) {
                    mLoginErrorTextView.setText(getString(R.string.loginIsNotValid));
                    mLoginErrorTextView.setVisibility(View.VISIBLE);

                } else if(password.isEmpty()) {
                    mPasswordErrorTextView.setText(getString(R.string.passwordIsNull));
                    mPasswordErrorTextView.setVisibility(View.VISIBLE);

                }  else if(password.length() < 6) {
                    mPasswordErrorTextView.setText(getString(R.string.passwordLessSix));
                    mPasswordErrorTextView.setVisibility(View.VISIBLE);

                } else {
                    signIn(login,password);

                }

                break;


            case R.id.singUpButton:
                Toast.makeText(EmailPasswordActivity.this, "singUpButton .",
                        Toast.LENGTH_SHORT).show();
                break;


            case R.id.forgetPasswordTextView:
                Toast.makeText(EmailPasswordActivity.this, "forgetPasswordTextView .",
                        Toast.LENGTH_SHORT).show();
                break;


            case R.id.googleImageButton:
                Toast.makeText(EmailPasswordActivity.this, "googleImageButton .",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.facebookImageButton:
                Toast.makeText(EmailPasswordActivity.this, "facebookImageButton .",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.twitterImageButton:
                Toast.makeText(EmailPasswordActivity.this, "twitterImageButton .",
                        Toast.LENGTH_SHORT).show();
                break;

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

