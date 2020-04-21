package com.example.pahwa.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pahwa.R;
import com.example.pahwa.SendMoneyDialog;
import com.example.pahwa.chatinfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.lang.invoke.ConstantCallSite;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyAccount extends Fragment {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<chatinfo, ChatViewHolder> firebaseRecyclerAdapter;
    TextView sendmoney,addproof;
    ConstraintLayout constraintLayout;
    Button cancel,proceed;
    Dialog myDialog;
    EditText amount,message;
    CheckBox alreadypaid;

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

        constraintLayout=v.findViewById(R.id.cons);
        sendmoney = v.findViewById(R.id.sendmoney);

        Log.i("time", String.valueOf(ServerValue.TIMESTAMP));

        Toast.makeText(getActivity(), "ho", Toast.LENGTH_SHORT).show();

        recyclerView = v.findViewById(R.id.chatrecyclerview);

        Query query = FirebaseDatabase.getInstance().getReference().child("Chats").child("id").orderByChild("timestamp");
        FirebaseRecyclerOptions<chatinfo> options = new FirebaseRecyclerOptions.Builder<chatinfo>()
                .setQuery(query, new SnapshotParser<chatinfo>() {
                    @NonNull
                    @Override
                    public chatinfo parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new chatinfo(snapshot.child("message").getValue(String.class), snapshot.child("who").getValue(String.class),snapshot.child("timestamp").getValue(String.class),snapshot.child("alreadypaid").getValue(String.class),snapshot.child("file").getValue(String.class),snapshot.child("amount").getValue(String.class));
                    }
                }).build();


        sendmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView txtclose;
                Button btnFollow;

                myDialog=new Dialog(getActivity());
                myDialog.setContentView(R.layout.sendmoneypopup);
                Window window = myDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                myDialog.show();

                cancel=myDialog.findViewById(R.id.sendmoneycancel);
                alreadypaid=myDialog.findViewById(R.id.sendmoneypaidcheckbox);
                amount=myDialog.findViewById(R.id.sendmoneyamount);
                message=myDialog.findViewById(R.id.sendmoneymessage);
                addproof=myDialog.findViewById(R.id.sendmoneyproof);
                proceed=myDialog.findViewById(R.id.sendmoneyproceed);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                addproof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String paid;
                        if(alreadypaid.isChecked())
                        {
                            paid="yes";
                        }
                        else {
                            paid="no";
                        }

                        Map map=new HashMap<>();

                        //map.put("message",message.getText());
                       // map.put("timestamp",ServerValue.TIMESTAMP);
                        map.put("who","sender");
                        //map.put("amount",amount.getText());
                        map.put("file","");
                       // map.put("alreadypaid",paid);

                        FirebaseDatabase.getInstance().getReference().child("Chats").child("id").child(UUID.randomUUID().toString()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chatinfo, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull chatinfo model) {


                holder.sendermessage.setVisibility(View.GONE);
                holder.recievermessage.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.GONE);

                if (model.getWho().equals("sender")) {
                    holder.sendermessage.setVisibility(View.VISIBLE);
                    holder.sendermessage.setText(model.getMessage());
                    holder.recievermessage.setVisibility(View.GONE);
                    holder.cardView.setVisibility(View.VISIBLE);
                } else if (model.getWho().equals("reciever")) {
                    holder.recievermessage.setVisibility(View.VISIBLE);
                    holder.recievermessage.setText(model.getMessage());
                    holder.sendermessage.setVisibility(View.GONE);

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

        TextView sendermessage, recievermessage;
        CardView cardView;
        ImageView propic;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            sendermessage = itemView.findViewById(R.id.sendtext);
            recievermessage = itemView.findViewById(R.id.recievetext);
            propic = itemView.findViewById(R.id.chatpropic);
            cardView = itemView.findViewById(R.id.chatpropiccard);
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
}
