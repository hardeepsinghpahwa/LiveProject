package com.example.pahwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CategoryItems extends AppCompatActivity {

    RecyclerView recyclerView;
    String ref;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<itemdetails,ItemViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        ref=getIntent().getStringExtra("ref");

        databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl(ref);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getSupportActionBar().setTitle(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView=findViewById(R.id.itemsrecyclerview);

        Query query=databaseReference.child("items");


        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new itemdetails(snapshot.child("image").getValue(String.class),snapshot.child("name").getValue(String.class),snapshot.child("brand").getValue(String.class),snapshot.child("moreinfo").getValue(String.class));
                    }
                }).build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull itemdetails model) {

                holder.brand.setText(model.getBrand().toUpperCase());
                holder.name.setText(model.getName());
                Picasso.get().load(model.getImage()).resize(300,300).into(holder.img);
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout,parent,false);
                return new ItemViewHolder(v);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryItems.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name,brand;
        ImageView img;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.itemname);
            brand=itemView.findViewById(R.id.itembrand);
            img=itemView.findViewById(R.id.itemimage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
