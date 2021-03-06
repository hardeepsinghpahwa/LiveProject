package com.example.pahwa;

import androidx.appcompat.app.AppCompatActivity;
import static maes.tech.intentanim.CustomIntent.customType;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Intent i = new Intent(SplashScreen.this,
                            Home.class);
                    startActivity(i);

                    finish();
                } else {

                    Intent i = new Intent(SplashScreen.this,
                            MainActivity.class);
                    startActivity(i);

                    customType(SplashScreen.this,"fadein-to-fadeout");

                    finish();
                }
            }
        }, 2000);


    }
}
