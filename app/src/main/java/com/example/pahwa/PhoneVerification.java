package com.example.pahwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {

    String phone;
    String Verificationid;
    ProgressBar progressBar;
    EditText verifycode;
    TextView sendingcode,countdown;
    int counter=30;
    TextView proceed,autodetecting;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        phone=getIntent().getStringExtra("phn");
        sendingcode=findViewById(R.id.sendingcode);
        verifycode=findViewById(R.id.verificationcode);
        countdown=findViewById(R.id.countdown) ;
        progressBar=findViewById(R.id.progress);
        proceed=findViewById(R.id.proceed);
        autodetecting=findViewById(R.id.autodetecting);

        sendingcode.setText("Sending Code To "+phone);
        sendVerificationCode(phone);

        verifycode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==6)
                {
                    sendingcode.setText("Verifying Code");
                    progressBar.setVisibility(View.VISIBLE);
                    verifyVerificationCode(verifycode.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhoneVerification.this,SetupProfile.class);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });
    }

    private void sendVerificationCode(String mobile) {

        mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                final String code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    verifycode.setText(code);
                }


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                //verifyVerificationCode("123456");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Verificationid=s;
                resendingToken=forceResendingToken;
                sendingcode.setText("Code Sent ");
                countdown.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                new CountDownTimer(30000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countdown.setText(String.valueOf(counter));
                        counter--;
                    }
                    @Override
                    public void onFinish() {
                        countdown.setText("Send Code Again");
                        countdown.setClickable(true);
                    }
                }.start();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBacks);

    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Verificationid, code);
        signInWithPhoneAuthCredential(credential);;
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            Toast.makeText(PhoneVerification.this,"Success",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            progressBar.setVisibility(View.GONE);
                            autodetecting.setText("Verification Successfull");
                            autodetecting.setTextSize(25);

                            YoYo.with(Techniques.FlipOutX)
                                    .playOn(verifycode);

                            YoYo.with(Techniques.FlipOutX)
                                    .playOn(countdown);

                            YoYo.with(Techniques.FlipOutX)
                                    .playOn(sendingcode);

                            countdown.setVisibility(View.GONE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {





                                    proceed.setVisibility(View.VISIBLE);

                                    YoYo.with(Techniques.FlipInY)
                                            .playOn(proceed);


                                }
                            },2000);

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(PhoneVerification.this,"Failed",Toast.LENGTH_SHORT).show();
                            sendingcode.setText("Verification Failed");
                            progressBar.setVisibility(View.GONE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
