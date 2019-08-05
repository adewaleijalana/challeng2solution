package com.javaguru.travelmantics.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.javaguru.travelmantics.R;
import com.javaguru.travelmantics.activities.DealActivity;
import com.javaguru.travelmantics.models.TravelDeal;
import com.javaguru.travelmantics.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{

    private ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private ImageView imageDeal;

    public DealAdapter(){
        //this.deals = deals;
        //FirebaseUtil.openFbReference("traveldeals");
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        this.deals = FirebaseUtil.deals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TravelDeal  travelDeal = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal", travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size() -1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_rows, viewGroup, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int i) {

        TravelDeal travelDeal = deals.get(i);
        dealViewHolder.bind(travelDeal);

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDescription;
        public TextView tvPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageDeal = itemView.findViewById(R.id.tvImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("Click", String.valueOf(position));
                    TravelDeal td = deals.get(position);
                    Intent intent = new Intent(v.getContext(), DealActivity.class);
                    intent.putExtra("Deal", td);
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void bind(TravelDeal travelDeal){
            tvTitle.setText(travelDeal.getTitle());
            tvDescription.setText(travelDeal.getDescription());
            tvPrice.setText(travelDeal.getPrice());
            showImage(travelDeal.getImageUrl());
        }

        private void showImage(String url){
            if (url != null && !url.isEmpty()){
                Picasso.with(imageDeal.getContext())
                        .load(url)
                        .resize(80, 80)
                        .centerCrop()
                        .into(imageDeal);
            }
        }
    }
}
