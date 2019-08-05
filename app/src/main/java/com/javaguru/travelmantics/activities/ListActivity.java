package com.javaguru.travelmantics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.javaguru.travelmantics.R;
import com.javaguru.travelmantics.adapters.DealAdapter;
import com.javaguru.travelmantics.util.FirebaseUtil;

public class ListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        /*FirebaseUtil.openFbReference("traveldeals", this);

        final DealAdapter dealAdapter = new DealAdapter();

        recyclerView = findViewById(R.id.rvDeals);
        recyclerView.setAdapter(dealAdapter);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin){
            insertMenu.setVisible(true);
        }else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.insert_menu:
                startDealActivity();
                return true;
            case R.id.logout_menu:
                logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void logOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Logout", "User Logged Out");
                        FirebaseUtil.attachListener();
                    }
                });
        FirebaseUtil.detachListener();
    }

    private void startDealActivity() {

        Intent intent = new Intent(this, DealActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("traveldeals", this);

        final DealAdapter dealAdapter = new DealAdapter();

        recyclerView = findViewById(R.id.rvDeals);
        recyclerView.setAdapter(dealAdapter);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseUtil.attachListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
