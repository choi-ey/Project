package org.techtown.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class RecoActivity extends AppCompatActivity {
    ActionBar actionBar;
    Toolbar toolbar;

    RecyclerView recoRecycler;
    RecoAdapter adapter;
    ArrayList<TourApi> list = null;
    TourApi tour = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);

        //툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        actionBar.setTitle("Recommendation");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //리사이클러뷰
        recoRecycler = findViewById(R.id.recoRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recoRecycler.setLayoutManager(layoutManager);

        //임시데이터
        list = new ArrayList<TourApi>();
        tour = new TourApi();
        tour.setAddr1("서울특별시 강남구 압구정로 161");
        tour.setFirstImage("http://tong.visitkorea.or.kr/cms/resource/75/1258175_image2_1.jpg");
        tour.setMapx("127.0264344408");
        tour.setMapy("37.5269874797");
        tour.setTitle("강남 시티투어 - 트롤리버스");
        list.add(tour);


        adapter = new RecoAdapter(RecoActivity.this,list);
        recoRecycler.setAdapter(adapter);
    }
}