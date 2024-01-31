package com.aarsh.app.studysyncv1;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView name_tv, email_tv, userid_tv, mob_tv, reg_date;
    private String name, email, userid, mob;

    private ImageView profile_pic_main, profile_pic_bg;
    private ImageView name_edit, mob_edit;
    ImageView pro_btn;

    private FirebaseAuth authProfile;

    private StorageReference storageReference;
    private EditText userid_r;
    private FirebaseUser firebaseUser;
    //Drawer
    DrawerLayout drawerLayout;
    AlertDialog dialog;
    NavigationView navigationView;
    Toolbar toolbar;

    private final static String TAG = "ResetPasswordDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
//Bindings Start
        name_tv = findViewById(R.id.name_pv_out);
        email_tv = findViewById(R.id.email_pv_out);
        userid_tv = findViewById(R.id.user_pv_out);
        mob_tv = findViewById(R.id.mobile_pv_out);
        reg_date = findViewById(R.id.date_reg_tv);

        profile_pic_main = findViewById(R.id.main_pp);
        profile_pic_bg = findViewById(R.id.profile_back);
        pro_btn = findViewById(R.id.edit_picture_icon);

        name_edit = findViewById(R.id.nameEdit_icon_pv);
        mob_edit = findViewById(R.id.mobileEdit_icon_pv);

        authProfile = FirebaseAuth.getInstance();
//Drawer Start
        drawerLayout = findViewById(R.id.nav_drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_id);        //End Bindings
//End
        pro_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(UserProfileActivity.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(2050)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(2160, 2160)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
//Start
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_draw_open, R.string.nav_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
//Stop After All Override Function Left

        //OnClick Listener For ImageView
        profile_pic_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });
        //OnClick for NameEdit
        name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);

                View view1 = getLayoutInflater().inflate(R.layout.custom_edit_name_layout, null);
                builder.setView(view1);

                dialog = builder.create();
                dialog.show();

                EditText r_name = view1.findViewById(R.id.name_reset_input);
                Button n_button = view1.findViewById(R.id.submit1_button);
                n_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        String name_reset = r_name.getText().toString();
                        if (TextUtils.isEmpty(name_reset)) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                            r_name.setError("Email is Required");
                            r_name.requestFocus();
                        }
                        //Write Email Pattern Matcher
                        else {
                            dialog.dismiss();
                            updateName(name_reset);

                        }

                    }
                });
            }
        });


        //OnClick For EmailEdit(With Authentication)
        ImageView edit_email = findViewById(R.id.emailEdit_icon_pv);

        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);

                View view2 = getLayoutInflater().inflate(R.layout.custom_edit_email_dialog, null);
                builder.setView(view2);

                dialog = builder.create();
                dialog.show();
                EditText email_r = view2.findViewById(R.id.email_reset_input);
                EditText p_vl = view2.findViewById(R.id.pwd_vl_input);
                Button e_button = view2.findViewById(R.id.submit2_button);
                e_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        String ori_email = email_tv.getText().toString();
                        String email_reset = email_r.getText().toString();
                        String pwd = p_vl.getText().toString();
                        if (TextUtils.isEmpty(email_reset)) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                            email_r.setError("Email is Required");
                            email_r.requestFocus();
                        } else if (TextUtils.isEmpty(pwd)) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                            p_vl.setError("Email is Required");
                            p_vl.requestFocus();
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_reset).matches()) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                            email_r.setError("Email is Required");
                            email_r.requestFocus();
                        } else if (ori_email.matches(email_reset)) {
                            Toast.makeText(UserProfileActivity.this, "Please New Email", Toast.LENGTH_SHORT).show();
                            email_r.setError("Email is Required");
                            email_r.requestFocus();
                        } else {
                            dialog.dismiss();
                            authProfile = FirebaseAuth.getInstance();
                            firebaseUser = authProfile.getCurrentUser();

                            String old_email = firebaseUser.getEmail();
                            if (firebaseUser.equals("")) {
                                Toast.makeText(UserProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            } else {
                                reAuthenticate(firebaseUser, pwd, old_email, email_reset);
                            }
                        }


                    }
                });

            }
        });

        //OnClick For Mobile Number Update
        ImageView edit_mob = findViewById(R.id.mobileEdit_icon_pv);
        edit_mob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);

                View view3 = getLayoutInflater().inflate(R.layout.custom_edit_mobile_dialog, null);
                builder.setView(view3);

                dialog = builder.create();
                dialog.show();
                EditText email_r = view3.findViewById(R.id.name_reset_input);
                EditText p_vl = view3.findViewById(R.id.pwd_vl_input1);
                Button e_button = view3.findViewById(R.id.submit3_button);
                e_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        String mob_reset = email_r.getText().toString();
                        String pwd = p_vl.getText().toString();
                        if (TextUtils.isEmpty(mob_reset)) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                            email_r.setError("Email is Required");
                            email_r.requestFocus();
                        } else if (TextUtils.isEmpty(pwd)) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                            p_vl.setError("Email is Required");
                            p_vl.requestFocus();
                        } else {
                            dialog.dismiss();
                            authProfile = FirebaseAuth.getInstance();
                            firebaseUser = authProfile.getCurrentUser();

                            String old_email = firebaseUser.getEmail();
                            if (firebaseUser.equals("")) {
                                Toast.makeText(UserProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            } else {
                                mobAuthenticate(firebaseUser, pwd, old_email, mob_reset);
                            }
                        }


                    }
                });

            }
        });


//OnClick For UserName Edit
        ImageView edit__uname = findViewById(R.id.useridEdit_icon_pv);
        edit__uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUsername();
            }
        });


        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
        } else {
            checkIfEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        }

    }

    private void updateUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);

        View view5 = getLayoutInflater().inflate(R.layout.custom_userid_edit_dialog, null);
        builder.setView(view5);

        dialog = builder.create();
        dialog.show();

        userid_r = view5.findViewById(R.id.userid_vl_input);
        Button e_button = view5.findViewById(R.id.submit4_button);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        e_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                String newUserId = userid_r.getText().toString();

                if (TextUtils.isEmpty(newUserId)) {
                    Toast.makeText(UserProfileActivity.this, "Please Enter User ID", Toast.LENGTH_SHORT).show();
                    userid_r.setError("ID is Required");
                    userid_r.requestFocus();
                } else {
                    // Check if the new user ID already exists in the database
                    checkIfUserIdExists(firebaseUser, newUserId);
                }
            }
        });
    }

    private void checkIfUserIdExists(FirebaseUser firebaseUser, String newUserId) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        String userId = firebaseUser.getUid();

        referenceProfile.orderByChild("username").equalTo(newUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User ID already exists, show an error message
                    Toast.makeText(UserProfileActivity.this, "User ID already exists! Retry", Toast.LENGTH_SHORT).show();
                    userid_r.setError("ID already exists");
                    userid_r.requestFocus();
                } else {
                    // Update the user ID in the database
                    referenceProfile.child(userId).child("username").setValue(newUserId)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Database update successful
                                    // Update the UI to reflect the changes
                                    showUserProfile(firebaseUser);
                                    Toast.makeText(UserProfileActivity.this, "User ID updated successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss(); // Dismiss the dialog upon successful update
                                } else {
                                    // If updating user ID fails, handle the error
                                    Toast.makeText(UserProfileActivity.this, "Failed to update User ID", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mobAuthenticate(FirebaseUser firebaseUser, String pwd, String oldEmail, String mobReset) {
        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, pwd);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Password Verified", Toast.LENGTH_LONG).show();
                    updateMobInDataBase(mobReset);
                }
            }
        });

    }

    private void updateMobInDataBase(String mobReset) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String uni_code = firebaseUser.getUid();

        //Extracting Data From DB "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(uni_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails readDetails = snapshot.getValue(UserDetails.class);
                if (readDetails != null) {

                    email = readDetails.email;
                    userid = readDetails.username;

                    HashMap newUser = new HashMap();
                    newUser.put("email", email);
                    newUser.put("mob", mobReset);
                    newUser.put("username", userid);


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    databaseReference.child(uni_code).updateChildren(newUser).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                showUserProfile(firebaseUser);
                                Toast.makeText(UserProfileActivity.this, "DataBase Update Done", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reAuthenticate(FirebaseUser firebaseUser, String pwd, String old_email, String new_email) {
        AuthCredential credential = EmailAuthProvider.getCredential(old_email, pwd);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Password Verified", Toast.LENGTH_LONG).show();
                    updateEmail(firebaseUser, new_email);
                    updateEmailInDatabase(new_email);
                }
            }
        });

    }

    private void updateEmailInDatabase(String newEmail) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String uni_code = firebaseUser.getUid();

        //Extracting Data From DB "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(uni_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails readDetails = snapshot.getValue(UserDetails.class);
                if (readDetails != null) {

                    email = readDetails.email;
                    mob = readDetails.mob;
                    userid = readDetails.username;

                    HashMap newUser = new HashMap();
                    newUser.put("email", newEmail);
                    newUser.put("mob", mob);
                    newUser.put("username", userid);


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    databaseReference.child(uni_code).updateChildren(newUser).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                showUserProfile(firebaseUser);
                                Toast.makeText(UserProfileActivity.this, "DataBase Update Done", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser, String new_email) {
        firebaseUser.verifyBeforeUpdateEmail(new_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UserProfileActivity.this, "Verfiy New Mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Inside UserProfileActivity

// ... (other existing code)

    // Add this method to handle name update
    private void updateName(String newName) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && !TextUtils.isEmpty(newName)) {
            firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Display name updated successfully in Firebase Authentication
                            Toast.makeText(UserProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                            // Update the displayed name on the UI
                            name_tv.setText(newName);
                        } else {
                            // If updating fails, handle the error
                            Toast.makeText(UserProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case where newName is empty
            Toast.makeText(UserProfileActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }


    // Add this method to update the name in the Realtime Database


// ... (other existing code)


    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please Verify Your Email. You will not be able login without verification");

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

    private void showUserProfile(FirebaseUser firebaseUser) {
        String uni_code = firebaseUser.getUid();

        //Extracting Data From DB "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(uni_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails readDetails = snapshot.getValue(UserDetails.class);
                if (readDetails != null) {
                    storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

                    name = firebaseUser.getDisplayName();
                    email = readDetails.email;
                    mob = readDetails.mob;
                    userid = readDetails.username;
                    loadProfilePicture();
                    name_tv.setText(name);
                    email_tv.setText(email);
                    userid_tv.setText("@" + userid);
                    mob_tv.setText(mob);
                    if (firebaseUser != null) {
                        long creationTimestamp = firebaseUser.getMetadata().getCreationTimestamp();
                        // You can convert the timestamp to a readable date using your preferred method
                        String registrationDate = convertTimestampToDateString(creationTimestamp);

                        reg_date.setText(registrationDate);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private String convertTimestampToDateString(long timestamp) {

            // Convert timestamp to Date
            Date date = new Date(timestamp);

            // Create a SimpleDateFormat with the desired format
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            // Format the date and return as a string
            return sdf.format(date);

    }

    //Override Functions Drawer Start
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_news:
                Intent news_intent = new Intent(UserProfileActivity.this, news_screen.class);
                startActivity(news_intent);
                break;
            case R.id.nav_home:
                Intent home_intent = new Intent(UserProfileActivity.this, home_screen.class);
                startActivity(home_intent);
                break;

            case R.id.nav_question_paper:
                Intent qn_intent = new Intent(UserProfileActivity.this, PapersScreen.class);
                startActivity(qn_intent);
                break;

            case R.id.reset_pwd:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                View view = getLayoutInflater().inflate(R.layout.custom_reset_pwd_dialog, null);
                builder.setView(view);

                dialog = builder.create();
                dialog.show();
                EditText r_email = view.findViewById(R.id.email_reset_input);
                Button r_button = view.findViewById(R.id.submit_button);
                r_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email_reset = r_email.getText().toString();
                        if (TextUtils.isEmpty(email_reset)) {
                            Toast.makeText(UserProfileActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                            r_email.setError("Email is Required");
                            r_email.requestFocus();
                        }
                        //Write Email Pattern Matcher
                        else {
                            resetPassword(email_reset, r_email);
                        }

                    }
                });


                break;
            case R.id.logout:
                logout();
                break;
            case R.id.del_prof:
                deleteUser();
                break;


        }

        return true;
    }


    //End Drawer All Functions Are Part of that Interface

    public void logout() {
        try {
            // Inflate the custom dialog layout
            View view = getLayoutInflater().inflate(R.layout.custom_logout_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);

            // Find the TextViews for "Yes" and "No" in the inflated layout
            TextView tvYes = view.findViewById(R.id.tvYes);
            TextView tvNo = view.findViewById(R.id.tvNo);

            // Set click listeners for "Yes" and "No" options
            tvYes.setOnClickListener(v -> {
                // "Yes" option clicked, perform logout
                // Clear Firebase user authentication
                FirebaseAuth.getInstance().signOut();

                // Navigate to the LoginActivity
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                // Dismiss the dialog
                dialog.dismiss();
            });

            tvNo.setOnClickListener(v -> {
                // "No" option clicked, dismiss the dialog
                dialog.dismiss();
            });

            // Show the dialog
            dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            // Handle exceptions
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(UserProfileActivity.this, "Failed to logout. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteUser() {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) { // Check if the user is authenticated
                // Inflate the custom dialog layout
                View view4 = getLayoutInflater().inflate(R.layout.custom_delete_user_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view4);

                // Find the EditText and TextView for password and confirm in the inflated layout
                EditText etPassword = view4.findViewById(R.id.etPassword);
                TextView tvConfirm = view4.findViewById(R.id.tvConfirm);

                // Set click listener for the "Confirm" option
                tvConfirm.setOnClickListener(v -> {
                    String password = etPassword.getText().toString();

                    // Authenticate user's password
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), password);

                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Password authenticated successfully
                                    // Delete user from Firebase authentication
                                    firebaseUser.delete()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    // User deleted successfully
                                                    // Delete user data from the "Registered Users" node
                                                    deleteFromDatabase(firebaseUser.getUid());

                                                    // Navigate to the LoginActivity
                                                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                    // Dismiss the dialog
                                                    dialog.dismiss();
                                                } else {
                                                    // If deleting user fails, handle the error
                                                    Toast.makeText(UserProfileActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // If re-authentication fails, handle the error
                                    Toast.makeText(UserProfileActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            });
                });

                // Show the dialog
                dialog = builder.create();
                dialog.show();
            } else {
                // Handle the case where the user is not authenticated
                Toast.makeText(UserProfileActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle exceptions
            Log.e(TAG, "Error during delete user: " + e.getMessage());
            Toast.makeText(UserProfileActivity.this, "Failed to delete user. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFromDatabase(String userId) {
        try {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            // Delete user data from the Realtime Database
            referenceProfile.child(userId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User data deleted successfully from the database
                            Toast.makeText(UserProfileActivity.this, "User data deleted successfully from the database", Toast.LENGTH_SHORT).show();
                        } else {
                            // If deleting user data fails, handle the error
                            Toast.makeText(UserProfileActivity.this, "Failed to delete user data from the database", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "deleteFromDatabase: Failed: " + task.getException().getMessage());
                        }
                    });
        } catch (Exception e) {
            // Handle exceptions
            Log.e(TAG, "Error during delete user data from database: " + e.getMessage());
            Toast.makeText(UserProfileActivity.this, "Failed to delete user data from the database. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void resetPassword(String email_reset, EditText r_email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email_reset).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        r_email.setError("User Does Not Exist! Please Register");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Toast.makeText(UserProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        profile_pic_main.setImageURI(uri);
        profile_pic_bg.setImageURI(uri);
        uploadProfilePicture(uri);

        loadProfilePicture();


    }

    private void loadProfilePicture() {
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference profilePicRef = storageReference.child(userId + ".jpg");

        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the profile picture using an image loading library (e.g., Picasso, Glide)
            // For simplicity, assuming you have an image loading library initialized
            // Here, I'm using Picasso as an example (you can add the library to your project)
            Picasso.get().load(uri).into(profile_pic_main);
            Picasso.get().load(uri).into(profile_pic_bg);
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(UserProfileActivity.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadProfilePicture(Uri uri) {
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference profilePicRef = storageReference.child(userId + ".jpg");

        profilePicRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(UserProfileActivity.this, "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Toast.makeText(UserProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }

}