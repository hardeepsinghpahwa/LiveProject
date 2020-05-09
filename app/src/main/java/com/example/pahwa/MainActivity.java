package com.example.pahwa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextInputEditText phone;
    TextInputLayout layout;
    TextView go,invalid,hey,signin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone=findViewById(R.id.phonenumber);
        go=findViewById(R.id.gobutton);
        invalid=findViewById(R.id.invalidphn);
        firebaseAuth=FirebaseAuth.getInstance();
        hey=findViewById(R.id.heytext);
        signin=findViewById(R.id.signintext);
        layout=findViewById(R.id.textinput);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hey.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInDown)
                        .duration(700)
                        .playOn(hey);
            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                signin.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInDown)
                        .duration(700)
                        .playOn(signin);
            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(layout);
            }
        },2500);

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length()<10 && s.toString().length()>=1)
                {
                    invalid.setVisibility(View.VISIBLE);
                }
                else {
                    invalid.setVisibility(View.INVISIBLE);
                }

                if(s.toString().length()<10 && go.getVisibility()==View.VISIBLE)
                {
                    YoYo.with(Techniques.SlideOutDown)
                            .duration(500)
                            .playOn(go);
                    go.setVisibility(View.GONE);
                }
                else  if(!s.toString().equals("")  && s.toString().length()==10 && go.getVisibility()==View.GONE)
                {
                    YoYo.with(Techniques.SlideInUp)
                            .duration(500)
                            .playOn(go);
                    go.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,PhoneVerification.class);
                i.putExtra("phn",phone.getText().toString());
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,Home.class));
        }
    }
}
