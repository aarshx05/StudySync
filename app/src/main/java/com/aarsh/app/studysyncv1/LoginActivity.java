package com.aarsh.app.studysyncv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText et_l_email, et_l_pwd;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_l_email = findViewById(R.id.email_lv_input);
        et_l_pwd = findViewById(R.id.pwd_lv_input);

        authProfile = FirebaseAuth.getInstance();
        //Eye Button
        ImageView eyebtn = findViewById(R.id.eye_icon_lv);

        eyebtn.setImageResource(R.drawable.eye_pwd_icon);
        et_l_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //Note I have Done Wrong Icon Naming Hide Is Open Icon and Open Is Hide Icon

        eyebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_l_pwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    //If Visible Then Hide
                    et_l_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyebtn.setImageResource(R.drawable.eye_pwd_icon);

                } else {
                    et_l_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyebtn.setImageResource(R.drawable.eye_hide_pwd_icon);
                }
            }
        });
        //Login
        Button l_button = findViewById(R.id.login_button);
        l_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String l_email = et_l_email.getText().toString();
                String l_pwd = et_l_pwd.getText().toString();

                if (TextUtils.isEmpty(l_email)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    et_l_email.setError("Email Is Required");
                    et_l_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(l_email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                    et_l_email.setError("Valid Email Is Required");
                    et_l_email.requestFocus();
                } else if (TextUtils.isEmpty(l_pwd)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    et_l_pwd.setError("Password Is Required");
                    et_l_pwd.requestFocus();
                } else {
                    loginUser(l_email, l_pwd);
                }
            }
        });

    }

    private void loginUser(String lEmail, String lPwd) {
        authProfile.signInWithEmailAndPassword(lEmail, lPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //Getting Current Instance Of The User
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, home_screen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        et_l_email.setError("User Does Not Exist");
                        et_l_email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        et_l_email.setError("Wrong Credentials!");
                        et_l_email.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please Verify Your Email. You cannot login without verification");

        //Opening The Gmail App If User Clicks Continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent e_intent = new Intent(Intent.ACTION_MAIN);
                e_intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                e_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(e_intent);
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}
