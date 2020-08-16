package org.techtown.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class WishList extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar toolbar;

    RecyclerView wishRecycler;
    TourApiAdapter adapter;
    ArrayList<TourApi> list = null;
    TourApi tour = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        //툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        actionBar.setTitle("WishList");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //리사이클러뷰
        wishRecycler = findViewById(R.id.wishRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        wishRecycler.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        list = (ArrayList<TourApi>)intent.getSerializableExtra("tList");
        System.out.println(list); //지워도 됨

        adapter = new TourApiAdapter(WishList.this,list);
        wishRecycler.setAdapter(adapter);
    }
}