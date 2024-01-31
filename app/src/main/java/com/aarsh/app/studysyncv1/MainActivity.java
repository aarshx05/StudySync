package com.aarsh.app.studysyncv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       SplashScreen screen= SplashScreen.installSplashScreen(this);


        Button l_login=findViewById(R.id.login_button);
        l_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth authProfile = FirebaseAuth.getInstance();
                if(authProfile.getCurrentUser()!=null) {
                    Intent n_intent = new Intent(MainActivity.this, home_screen.class);
                    startActivity(n_intent);
                }
                else {
                    Intent l_intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(l_intent);
                }
            }
        });
        Button r_login=findViewById(R.id.register_button);
        r_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent r_intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(r_intent);
            }
        });
    }
}

