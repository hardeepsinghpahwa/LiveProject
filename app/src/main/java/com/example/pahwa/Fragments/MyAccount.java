package com.example.pahwa.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pahwa.R;
import com.example.pahwa.chatinfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyAccount extends Fragment {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<chatinfo, ChatViewHolder> firebaseRecyclerAdapter;
    TextView sendmoney, requestmoney, addproof, attachtext, nofile;
    ConstraintLayout constraintLayout;
    Button cancel, proceed;
    Dialog myDialog;
    EditText amount, message;
    CheckBox alreadypaid;
    Uri file;

    public MyAccount() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_account, container, false);

        constraintLayout = v.findViewById(R.id.cons);
        sendmoney = v.findViewById(R.id.sendmoney);
        requestmoney = v.findViewById(R.id.requestmoney);

        Log.i("time", String.valueOf(ServerValue.TIMESTAMP));

        Toast.makeText(getActivity(), "ho", Toast.LENGTH_SHORT).show();

        recyclerView = v.findViewById(R.id.chatrecyclerview);

        Query query = FirebaseDatabase.getInstance().getReference().child("Chats").child("id").orderByChild("timestamp");
        FirebaseRecyclerOptions<chatinfo> options = new FirebaseRecyclerOptions.Builder<chatinfo>()
                .setQuery(query, new SnapshotParser<chatinfo>() {
                    @NonNull
                    @Override
                    public chatinfo parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new chatinfo(snapshot.child("message").getValue(String.class), snapshot.child("who").getValue(String.class), snapshot.child("timestamp").getValue(Long.class), snapshot.child("alreadypaid").getValue(String.class), snapshot.child("file").getValue(String.class), snapshot.child("amount").getValue(String.class), snapshot.child("type").getValue(String.class), snapshot.child("cleared").getValue(String.class));
                    }
                }).build();


        requestmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog = new Dialog(getActivity());
                myDialog.setContentView(R.layout.requestmoneypopup);
                myDialog.setCanceledOnTouchOutside(false);
                Window window = myDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                myDialog.show();

                cancel = myDialog.findViewById(R.id.recievemoneycancel);
                amount = myDialog.findViewById(R.id.recievemoneyamount);
                message = myDialog.findViewById(R.id.receivemoneymessage);
                addproof = myDialog.findViewById(R.id.recievemoneyproof);
                proceed = myDialog.findViewById(R.id.recievemoneyproceed);
                attachtext = myDialog.findViewById(R.id.attachbilltext);
                nofile = myDialog.findViewById(R.id.nobill);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                addproof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(getFileChooserIntent(), 5);
                    }
                });

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String random = UUID.randomUUID().toString();

                        if (file != null) {
                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setTitle("Uploading File");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Sender").child("Proofs").child(random);
                            UploadTask uploadTask;

                            uploadTask = storageReference.putFile(file);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return storageReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        progressDialog.dismiss();
                                        Map map = new HashMap<>();

                                        map.put("message", message.getText().toString());
                                        map.put("timestamp", ServerValue.TIMESTAMP);
                                        map.put("who", "sender");
                                        map.put("amount", amount.getText().toString());
                                        map.put("file", downloadUri.toString());
                                        map.put("cleared", "no");
                                        map.put("type", "requested");
                                        map.put("alreadypaid", "");

                                        FirebaseDatabase.getInstance().getReference().child("Chats").child("id").child(random).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                                                myDialog.dismiss();
                                            }
                                        });

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            nofile.setVisibility(View.VISIBLE);

                        }
                    }
                });


            }
        });


        sendmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDialog = new Dialog(getActivity());
                myDialog.setContentView(R.layout.sendmoneypopup);
                myDialog.setCanceledOnTouchOutside(false);
                Window window = myDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                myDialog.show();

                cancel = myDialog.findViewById(R.id.sendmoneycancel);
                alreadypaid = myDialog.findViewById(R.id.sendmoneypaidcheckbox);
                amount = myDialog.findViewById(R.id.sendmoneyamount);
                message = myDialog.findViewById(R.id.sendmoneymessage);
                addproof = myDialog.findViewById(R.id.sendmoneyproof);
                proceed = myDialog.findViewById(R.id.sendmoneyproceed);
                attachtext = myDialog.findViewById(R.id.attachprooftext);
                nofile = myDialog.findViewById(R.id.nofilesend);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });


                alreadypaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            addproof.setVisibility(View.VISIBLE);

                            attachtext.setVisibility(View.VISIBLE);
                        } else {
                            addproof.setVisibility(View.GONE);
                            attachtext.setVisibility(View.GONE);

                        }
                    }
                });

                addproof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(getFileChooserIntent(), 5);
                    }
                });

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String random = UUID.randomUUID().toString();

                        final String paid;
                        if (alreadypaid.isChecked()) {
                            paid = "yes";

                            if (file != null) {
                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setTitle("Uploading File");
                                progressDialog.show();

                                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Sender").child("Proofs").child(random);
                                UploadTask uploadTask;

                                uploadTask = storageReference.putFile(file);

                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }

                                        // Continue with the task to get the download URL
                                        return storageReference.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            progressDialog.dismiss();
                                            Map map = new HashMap<>();

                                            map.put("message", message.getText().toString());
                                            map.put("timestamp", ServerValue.TIMESTAMP);
                                            map.put("who", "sender");
                                            map.put("amount", amount.getText().toString());
                                            map.put("file", downloadUri.toString());
                                            map.put("alreadypaid", paid);
                                            map.put("cleared", "no");
                                            map.put("type", "paid");
                                            ;
                                            FirebaseDatabase.getInstance().getReference().child("Chats").child("id").child(random).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                                                    myDialog.dismiss();
                                                }
                                            });

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                if (nofile.getVisibility() == View.VISIBLE) {
                                    Map map = new HashMap<>();

                                    map.put("message", message.getText().toString());
                                    map.put("timestamp", ServerValue.TIMESTAMP);
                                    map.put("who", "sender");
                                    map.put("amount", amount.getText().toString());
                                    map.put("file", "");
                                    map.put("alreadypaid", paid);
                                    map.put("cleared", "no");
                                    map.put("type", "paid");

                                    FirebaseDatabase.getInstance().getReference().child("Chats").child("id").child(random).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                                            myDialog.dismiss();
                                        }
                                    });

                                } else {
                                    nofile.setVisibility(View.VISIBLE);
                                }
                            }

                        } else {
                            paid = "no";


                        }
                    }
                });
            }
        });

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chatinfo, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, final int position, @NonNull chatinfo model) {


                holder.sendermessage.setVisibility(View.GONE);
                holder.recievermessage.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.GONE);
                holder.senderproof.setVisibility(View.GONE);
                holder.recieverproof.setVisibility(View.GONE);
                holder.recieveramount.setVisibility(View.GONE);
                holder.senderamount.setVisibility(View.GONE);
                holder.sendstatus.setVisibility(View.GONE);
                holder.recievestatus.setVisibility(View.GONE);

                if (model.getWho().equals("sender")) {
                    holder.sendermessage.setVisibility(View.VISIBLE);
                    holder.senderamount.setVisibility(View.VISIBLE);
                    holder.sendermessage.setText(model.getMessage());
                    if (model.getType().equals("paid")) {
                        holder.senderamount.setText("PAID ₹ " + model.getAmount());

                        holder.sendstatus.setVisibility(View.VISIBLE);
                        if (model.getCleared().equals("no")) {
                            holder.sendstatus.setText("Pending");
                            holder.sendstatus.setCompoundDrawablePadding(5);
                            holder.sendstatus.setTextColor(Color.parseColor("#FFC107"));
                            holder.sendstatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pending, 0, 0, 0);
                        } else if (model.getCleared().equals("yes")) {
                            holder.sendstatus.setText("Approved");
                            holder.sendstatus.setCompoundDrawablePadding(5);
                            holder.sendstatus.setTextColor(Color.parseColor("#45F402"));
                            holder.sendstatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.approved, 0, 0, 0);
                        } else if (model.getCleared().equals("cancelled")) {
                            holder.sendstatus.setText("Rejected");
                            holder.sendstatus.setCompoundDrawablePadding(5);
                            holder.sendstatus.setTextColor(Color.parseColor("#E90600"));
                            holder.sendstatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cancelled, 0, 0, 0);
                        }

                    } else if (model.getType().equals("requested")) {
                        holder.senderamount.setText("REQUESTED ₹ " + model.getAmount());

                        holder.sendstatus.setVisibility(View.VISIBLE);
                        if (model.getCleared().equals("yes")) {
                            holder.sendstatus.setText("Approved");
                            holder.sendstatus.setCompoundDrawablePadding(5);
                            holder.sendstatus.setTextColor(Color.parseColor("#4EC321"));
                            holder.sendstatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.approved, 0, 0, 0);
                        } else if (model.getCleared().equals("no")) {
                            holder.sendstatus.setText("Cancel Request");
                            holder.sendstatus.setTextColor(Color.parseColor("#E90600"));
                            holder.sendstatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                            holder.sendstatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Request Removed", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }


                    if (!model.getFile().equals("")) {
                        holder.senderproof.setVisibility(View.VISIBLE);
                        Picasso.get().load(model.getFile()).into(holder.senderproof);
                    }

                } else if (model.getWho().equals("reciever")) {
                    holder.recievermessage.setVisibility(View.VISIBLE);
                    holder.recievermessage.setText(model.getMessage());
                    holder.recieveramount.setVisibility(View.VISIBLE);
                    if (model.getType().equals("paid")) {
                        holder.recieveramount.setText("PAID ₹ " + model.getAmount());
                    } else {
                        holder.recieveramount.setText("REQUESTED ₹ " + model.getAmount());

                    }
                    if (model.getFile().equals("")) {
                        holder.recieverproof.setVisibility(View.VISIBLE);
                        Picasso.get().load(model.getFile()).into(holder.recieverproof);
                    } else {

                    }
                    holder.cardView.setVisibility(View.VISIBLE);
                }
            }


            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, null);

                return new ChatViewHolder(view);
            }
        };


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return v;
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView sendermessage, recievermessage, senderamount, recieveramount, sendstatus, recievestatus;
        ImageView senderproof, recieverproof;
        CardView cardView;
        ImageView propic;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            sendermessage = itemView.findViewById(R.id.sendtext);
            recievermessage = itemView.findViewById(R.id.recievetext);
            propic = itemView.findViewById(R.id.chatpropic);
            cardView = itemView.findViewById(R.id.chatpropiccard);
            senderamount = itemView.findViewById(R.id.senderamount);
            recieveramount = itemView.findViewById(R.id.recieveramounte);
            senderproof = itemView.findViewById(R.id.senderproof);
            recieverproof = itemView.findViewById(R.id.receiverproof);
            sendstatus = itemView.findViewById(R.id.sendstatus);
            recievestatus = itemView.findViewById(R.id.recievestatus);
        }
    }

    @Override
    public void onStart() {
        firebaseRecyclerAdapter.startListening();
        super.onStart();
    }

    @Override
    public void onStop() {
        firebaseRecyclerAdapter.stopListening();
        super.onStop();
    }

    private Intent getFileChooserIntent() {
        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5) {

            if (file != null) {
                file = data.getData();
                addproof.setText(getFileName(file));
                if (nofile != null && nofile.getVisibility() == View.VISIBLE) {
                    nofile.setVisibility(View.GONE);
                }
            }
        }


    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
