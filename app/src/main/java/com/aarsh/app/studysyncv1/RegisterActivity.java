    package com.aarsh.app.studysyncv1;



    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
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
    import com.google.firebase.auth.FirebaseAuthUserCollisionException;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.UserProfileChangeRequest;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    public class RegisterActivity extends AppCompatActivity {

        private EditText et_name, et_email, et_mob, et_pwd, et_chkpwd, et_userid;
        private static final String TAG = "RegisterActivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            Toast.makeText(RegisterActivity.this, "Register Now!!", Toast.LENGTH_SHORT).show();

            et_name = findViewById(R.id.name_input);
            et_email = findViewById(R.id.email_input);
            et_mob = findViewById(R.id.mobile_input);
            et_pwd = findViewById(R.id.pwd_input);
            et_chkpwd = findViewById(R.id.chkpwd_input);
            et_userid = findViewById(R.id.userid_input);


            Button s_button = findViewById(R.id.signup_button);

            ImageView b_button = findViewById(R.id.backtxt_button);


            b_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click
                    Intent btxt_intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(btxt_intent);
                }
            });


            s_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = et_name.getText().toString();
                    String email = et_email.getText().toString();
                    String mob = et_mob.getText().toString();
                    String pwd = et_pwd.getText().toString();
                    String chkpwd = et_chkpwd.getText().toString();
                    String userid = et_userid.getText().toString();
                    //Validate Mobile Number Using Regex

                    Pattern p = Pattern.compile("[6-9][0-9]{9}");
                    Matcher m = p.matcher(mob);


                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                        et_name.setError("Name Is Required");
                        et_name.requestFocus();
                    } else if (TextUtils.isEmpty(email)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                        et_email.setError("Email Is Required");
                        et_email.requestFocus();
                    } else if (TextUtils.isEmpty(userid)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                        et_userid.setError("Username Is Required");
                        et_userid.requestFocus();
                    }
                    // Using Pattern Matcher To Check Email
                    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                        et_email.setError("Valid Email Is Required");
                        et_email.requestFocus();
                    } else if (TextUtils.isEmpty(mob)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                        et_mob.setError("Mobile Number Is Required");
                        et_mob.requestFocus();
                    } else if (mob.length() != 10) {
                        Toast.makeText(RegisterActivity.this, "Please Re-Enter Mobile Number", Toast.LENGTH_SHORT).show();
                        et_mob.setError("Valid Mobile Number Is Required");
                        et_mob.requestFocus();
                    } else if (!m.find()) {
                        Toast.makeText(RegisterActivity.this, "Please Re-Enter Mobile Number", Toast.LENGTH_SHORT).show();
                        et_mob.setError("Valid Mobile Number Is Required");
                        et_mob.requestFocus();
                    } else if (TextUtils.isEmpty(pwd)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        et_pwd.setError("Password Is Required");
                        et_pwd.requestFocus();
                    } else if (TextUtils.isEmpty(chkpwd)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Re-Password", Toast.LENGTH_SHORT).show();
                        et_chkpwd.setError("Re-Password Is Required");
                        et_chkpwd.requestFocus();
                    } else if (!pwd.equals(chkpwd)) {
                        Toast.makeText(RegisterActivity.this, "Please Enter Same Password", Toast.LENGTH_SHORT).show();
                        et_chkpwd.setError("Same Password Is Required");
                        et_chkpwd.requestFocus();
                        et_pwd.clearComposingText();
                        et_chkpwd.clearComposingText();
                    } else {
                        registerUser(name, email, mob, pwd, chkpwd, userid);
                    }

                }
            });
        }


        //Simple Reg User
        private void registerUser(String name, String email, String mob, String pwd, String chkpwd, String userid) {
            DatabaseReference dbrefer = FirebaseDatabase.getInstance().getReference("Registered Users");

            // Check if the userid already exists in the Realtime Database
            dbrefer.orderByChild("username").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Userid already exists, show an error message
                        Toast.makeText(RegisterActivity.this, "User ID already exists", Toast.LENGTH_SHORT).show();
                        et_userid.setError("Already Taken");
                        et_userid.requestFocus();
                    } else {
                        // Userid does not exist, proceed with user registration
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Rest of your registration logic remains unchanged
                                    // ...

                                    Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();

                                    //Updating The Name Using updateProfile Directly
                                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    firebaseUser.updateProfile(request);
                                    //Entering All Data Into DB after User Creation
                                    UserDetails writeDetails = new UserDetails(email, mob, userid);

                                    DatabaseReference dbrefer = FirebaseDatabase.getInstance().getReference("Registered Users");

                                    dbrefer.child(firebaseUser.getUid()).setValue(writeDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            //Mail Should Be sent only if db entry is success
                                            if (task.isSuccessful()) {
                                                firebaseUser.sendEmailVerification();

                                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Failed-Retry", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    // Handle registration failure
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        et_email.setError("Already Registered");
                                        et_email.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error if needed
                    Log.e(TAG, "Error: " + error.getMessage());
                }
            });
        }
    }
