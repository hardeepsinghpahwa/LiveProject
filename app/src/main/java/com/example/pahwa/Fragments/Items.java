package com.example.pahwa.Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pahwa.CategoryItems;
import com.example.pahwa.R;
import com.example.pahwa.SliderItem;
import com.example.pahwa.categorydetails;
import com.example.pahwa.chatinfo;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Items extends Fragment {

    ShimmerFrameLayout imgslidershimmer,categoriesshimmer;
    private List<SliderItem> mSliderItems = new ArrayList<>();
    SliderView sliderView;
    RecyclerView categories;
    FirebaseRecyclerAdapter<categorydetails,CategoriesViewHolder> firebaseRecyclerAdapter;

    public Items() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_items, container, false);

        sliderView=v.findViewById(R.id.imageSlider);
        imgslidershimmer=v.findViewById(R.id.imageslidershimmer);

        categories=v.findViewById(R.id.categoriesrecyclerview);
        categoriesshimmer=v.findViewById(R.id.categoriesshimmer);

        imgslidershimmer.startShimmer();
        categoriesshimmer.startShimmer();

        sliderView.startAutoCycle();
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);

        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mSliderItems.clear();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    SliderItem item=dataSnapshot1.getValue(SliderItem.class);

                    mSliderItems.add(item);
                }
                imgslidershimmer.stopShimmer();
                imgslidershimmer.setVisibility(View.GONE);
                sliderView.setSliderAdapter(new SliderAdapterExample(getActivity()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query=FirebaseDatabase.getInstance().getReference().child("Categories").orderByChild("name");


        FirebaseRecyclerOptions<categorydetails> options = new FirebaseRecyclerOptions.Builder<categorydetails>()
                .setQuery(query, new SnapshotParser<categorydetails>() {
                    @NonNull
                    @Override
                    public categorydetails parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new categorydetails(snapshot.child("image").getValue(String.class),snapshot.child("name").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<categorydetails, CategoriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, final int position, @NonNull categorydetails model) {

                holder.name.setText(model.getName());

                Picasso.get().load(model.getImage()).resize(250,250).into(holder.img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), CategoryItems.class);

                        intent.putExtra("ref",firebaseRecyclerAdapter.getRef(position).toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.categoriesitem,null);
                return new CategoriesViewHolder(view);
            }

            @Override
            public void onViewAttachedToWindow(@NonNull CategoriesViewHolder holder) {
                super.onViewAttachedToWindow(holder);

                categoriesshimmer.setVisibility(View.GONE);
                categoriesshimmer.stopShimmer();
            }
        };



        categories.setLayoutManager(new GridLayoutManager(getActivity(),2));
        categories.setAdapter(firebaseRecyclerAdapter);

        return v;
    }

    public class SliderAdapterExample extends
            SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

        private Context context;

        public SliderAdapterExample(Context context) {
            this.context = context;
        }

        public void renewItems(List<SliderItem> sliderItems) {
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            mSliderItems.remove(position);
            notifyDataSetChanged();
        }

        public void addItem(SliderItem sliderItem) {
            mSliderItems.add(sliderItem);
            notifyDataSetChanged();
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslideriteme, null);
            return new SliderAdapterVH(inflate);
        }


        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

            SliderItem sliderItem = mSliderItems.get(position);

            Picasso.get().load(sliderItem.getImage()).resize(450,250).into(viewHolder.img);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            return mSliderItems.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            ImageView img;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.itemimage);
            }
        }

    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView img;
        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.categoryname);
            img=itemView.findViewById(R.id.categoryimage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
