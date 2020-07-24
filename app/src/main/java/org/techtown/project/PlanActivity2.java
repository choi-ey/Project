package org.techtown.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class PlanActivity2 extends AppCompatActivity {

    Toolbar toolbar;
    private TextView txtDate;
    private TextView txtPlace;
    //장소추가 버튼
    private Button plusPlace;
    private int sDate;
    private int eDate;
    private int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan2);

        //툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("여행계획");

        txtDate = findViewById(R.id.txtDate);
        txtPlace = findViewById(R.id.txtPlace);

        Intent incomingIntent = getIntent();
        //전체 날짜 받아오기
        String date = incomingIntent.getStringExtra("date");
        txtDate.setText(date);

        //여행지 받아오기
        String place = incomingIntent.getStringExtra("place");
        txtPlace.setText(place+" 여행");
        //시작과 끝 날짜 받아오기
        month = incomingIntent.getIntExtra("month",0);
        sDate = incomingIntent.getIntExtra("sDate",0);
        //Toast.makeText(this,sDate,Toast.LENGTH_LONG).show();
        eDate = incomingIntent.getIntExtra("eDate",0);

        //리싸이클러뷰
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        DayAdapter adapter = new DayAdapter();

        for(int i = sDate; i <= eDate; i++){
            adapter.addItem(new Day(month,i));
        }

        recyclerView.setAdapter(adapter);

    }
}