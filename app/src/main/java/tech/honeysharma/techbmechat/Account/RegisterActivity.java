package tech.honeysharma.techbmechat.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import java.util.regex.Pattern;
import tech.honeysharma.techbmechat.R;
import tech.honeysharma.techbmechat.onboarding.BoardingActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private View parentLayout;
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn, verifyEmailButton;


    private Toolbar mToolbar;

    private DatabaseReference mDatabase;

    //ProgressDialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar Set
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRegProgress = new ProgressDialog(this);



        // Firebase Auth

        mAuth = FirebaseAuth.getInstance();


        // Android Fields
        parentLayout = findViewById(R.id.parent_layout);
        mDisplayName = findViewById(R.id.register_display_name);
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.reg_create_btn);




        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name)
                        && !TextUtils.isEmpty(email)
                        && validatePassword(password.trim())) {

                    SharedPreferences sharedPreferences = getSharedPreferences("APP_PREF", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", display_name);
                    editor.apply();
                    register_user(display_name, email, password);
                }
            }
        });
    }

    private boolean validatePassword(final String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            showPasswordError("Password length should be greater at least 6 symbols");
            return false;
        }

        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        boolean isValid = pattern.matcher(password).matches();
        if (!isValid) {
            showPasswordError("Password should contain: \n"
                    + "at least 1 special character, \n"
                    + "at least 1 capital letter, \n"
                    + "at least 1 number,\n"
                    + "at least 1 symbol");
        }
        return isValid;
    }

    private void showPasswordError(String error) {
        Snackbar snack = Snackbar.make(parentLayout, error, Snackbar.LENGTH_LONG);
        View snackbarView = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        snackbarView.setLayoutParams(params);
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);
        snack.show();
    }

    private void register_user(final String display_name, final String email, String password) {

        mRegProgress.setTitle("Verifying User");
        mRegProgress.setMessage("Please wait while we verify your account !");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        mAuth.createUserWithEmailAndPassword(mEmail.getEditText().getText().toString(), mPassword.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {



                if (task.isSuccessful()) {

                    mRegProgress.dismiss();
                   user = FirebaseAuth.getInstance().getCurrentUser();



                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                             mRegProgress.dismiss();
                             Toast.makeText(RegisterActivity.this, "Verification link has been sent to " + mEmail.getEditText().getText().toString() , Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(RegisterActivity.this, BoardingActivity.class);
                             startActivity(intent);
                             finish();
                        }
                    }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mRegProgress.dismiss();
                            Log.e("RegisterActivity", "onFailure: " + e.getMessage());
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Register", "onFailure: " + e.getMessage());
            }
        });

    }
}
