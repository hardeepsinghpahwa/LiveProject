package com.example.pahwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

public class SetupProfile extends AppCompatActivity {

    TextView name, phone, alternatephone, shopname, shoplocation, cancel, proceed, error;
    ImageView profilepicture;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    public static final int PICK_IMAGE = 1;
    Uri image;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        alternatephone = findViewById(R.id.alternatephone);
        shoplocation = findViewById(R.id.shoplocation);
        shopname = findViewById(R.id.shopname);
        cancel = findViewById(R.id.cancelbutton);
        proceed = findViewById(R.id.setupproceedbutton);
        error = findViewById(R.id.errorsetupprofile);
        firebaseAuth = FirebaseAuth.getInstance();
        profilepicture = findViewById(R.id.profilepicture);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });



        firebaseUser = firebaseAuth.getCurrentUser();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().length() == 0) {
                    error.setText("Name Empty");
                    error.setVisibility(View.VISIBLE);
                } else if (shopname.getText().toString().length() == 0) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Shop Name Empty");
                } else if (shoplocation.getText().toString().length() == 0) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Shop Location Empty");
                } else if (alternatephone.getText().toString().length() != 0 && alternatephone.getText().toString().length() != 10) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Incorrect Alternate Number");
                } else if (image==null) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("No Image Selected");}
                else {


                    UploadTask uploadTask = storageReference.child("ProfilePictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(image);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return storageReference.child("ProfilePictures").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                profilesetupdetails details = new profilesetupdetails(name.getText().toString(), shopname.getText().toString(), shoplocation.getText().toString(), phone.getText().toString(), alternatephone.getText().toString(), downloadUri.toString());

                                databaseReference.child("Profiles").child(firebaseUser.getUid()).setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Alerter.create(SetupProfile.this).setTitle("Details Saved").setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient2)).show();
                                        } else {
                                            Alerter.create(SetupProfile.this).setTitle("Some Problem Occured").setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient2)).show();
                                        }
                                    }
                                });
                            } else {
                                profilesetupdetails details = new profilesetupdetails(name.getText().toString(), shopname.getText().toString(), shoplocation.getText().toString(), phone.getText().toString(), alternatephone.getText().toString(), "https://firebasestorage.googleapis.com/v0/b/pahwa-5e4f7.appspot.com/o/pp.png?alt=media&token=39c9e2c9-6cea-4b96-a4c0-31c5ca7908ae");

                                databaseReference.child("Profiles").child(firebaseUser.getUid()).setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Alerter.create(SetupProfile.this).setTitle("Details Saved").setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient2)).show();
                                            startActivity(new Intent(SetupProfile.this,Home.class));
                                        } else {
                                            Alerter.create(SetupProfile.this).setTitle("Some Problem Occured").setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient2)).show();
                                        }
                                    }
                                });
                            }
                        }
                    });


                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {

            if(data!=null) {

                profilepicture.setImageURI(data.getData());
                image = data.getData();
            }
        }
    }

}
