package com.javaguru.travelmantics.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.javaguru.travelmantics.R;
import com.javaguru.travelmantics.models.TravelDeal;
import com.javaguru.travelmantics.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 42;

    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference  mDatabaseReference;

    EditText txtTitle;
    EditText txtPrice;
    EditText txtDescription;
    Button btnImage;
    ImageView imageView;

    TravelDeal travelDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

       // FirebaseUtil.openFbReference("traveldeals");

        mFireBaseDatabase = FirebaseUtil.firebaseDatabase;
        mDatabaseReference = FirebaseUtil.databaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);
        btnImage = findViewById(R.id.btnImage);
        imageView = findViewById(R.id.image);

        TravelDeal travelDeal = (TravelDeal) getIntent().getSerializableExtra("Deal");
        if (travelDeal == null){
            this.travelDeal = new TravelDeal();
        }else {
            this.travelDeal = travelDeal;
            txtTitle.setText(travelDeal.getTitle());
            txtPrice.setText(travelDeal.getPrice());
            txtDescription.setText(travelDeal.getDescription());
            showImage(travelDeal.getImageUrl());

        }


        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"),
                        PICTURE_RESULT);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri imageUrl = data.getData();
            StorageReference ref = FirebaseUtil.storageReference.child(imageUrl.getLastPathSegment());
            ref.putFile(imageUrl).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getDownloadUrl().toString();
                    String pictureName = taskSnapshot.getStorage().getPath();
                    travelDeal.setImageUrl(url);
                    travelDeal.setImageName(pictureName);
                    Log.d("Url", url);
                    Log.d("Name", pictureName);
                    showImage(url);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditText(true);
        }else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clearDeals();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted successfully", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                   return super.onOptionsItemSelected(item);
        }

    }

    private void deleteDeal() {
        if (travelDeal == null){
            Toast.makeText(this, "Save before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(travelDeal.getId()).removeValue();
        Log.d("imaage deal", travelDeal.getImageName());
        if (travelDeal.getImageName() != null && !travelDeal.getImageName().isEmpty()){
            StorageReference picRef =
                    FirebaseUtil.firebaseStorage.getReference().child(travelDeal.getImageName());

            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image successfully deleted");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());

                }
            });
        }

    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    private void saveDeal() {
        travelDeal.setTitle(txtTitle.getText().toString());
        travelDeal.setPrice(txtPrice.getText().toString());
        travelDeal.setDescription(txtDescription.getText().toString());

        if (travelDeal.getId() == null){
            mDatabaseReference.push().setValue(travelDeal);
        }else {
            mDatabaseReference.child(travelDeal.getId()).setValue(travelDeal);
        }

    }

    private void clearDeals() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");

        txtTitle.requestFocus();
    }

    private void enableEditText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        btnImage.setEnabled(isEnabled);
    }

    private void showImage(String url){
        if (url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
