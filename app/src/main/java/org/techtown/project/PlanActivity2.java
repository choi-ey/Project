package org.techtown.project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlanActivity2 extends AppCompatActivity {

    Toolbar toolbar;
    private TextView txtDate;
    private TextView txtPlace;
    //장소추가 버튼
    private Button plusPlace;
    private int sDate;
    private int eDate;
    private int month;
    //8/19 추가
    DayAdapter adapter;
    ArrayList<Day> list = null;
    Day day = null;
    ArrayList<Memo> sublist = null;
    //8/25 추가
    String title;
    int aPosition;
    LinearLayout container;
    List<String> placeLists = new ArrayList<String>(); //한번에 저장되는 거 수정하기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //List<String> placeLists = new ArrayList<String>();

        container = adapter.getContainer(aPosition);
        if(requestCode == 1){
            if(resultCode == 2){
                title = data.getStringExtra("title");
                adapter.onActivityResult(requestCode, resultCode, data);
                final TextView txtPlace = new TextView(PlanActivity2.this);
                txtPlace.setText(title);

                txtPlace.setPadding(10,10,10,10);
                txtPlace.setBackgroundResource(R.drawable.txt_custom);

                placeLists.add(txtPlace.getText().toString());

                txtPlace.setTextSize(20);
                txtPlace.setId(aPosition);
                txtPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.isClickable()){
                            //aPosition 을 1로 인식
                            Toast.makeText(getApplicationContext(),aPosition+" " +txtPlace.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                lp.setMargins(10,10,10,10);
                txtPlace.setLayoutParams(lp);
                container.addView(txtPlace);
                System.out.println("places: "+placeLists);
                //Toast.makeText(PlanActivity2.this,title+"가져옴2",Toast.LENGTH_SHORT).show();//확인 OK
            }else{
                Toast.makeText(PlanActivity2.this,"실패",Toast.LENGTH_SHORT).show();
            }
        }
    }

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

        list = new ArrayList<Day>();
        sublist = new ArrayList<Memo>();
        for(int i = sDate; i <= eDate; i++){
            day = new Day();
            day.setMonth(month);
            day.setDate(i);
            list.add(day);
        }
        adapter = new DayAdapter(PlanActivity2.this,list); //,sublist => Memo가 필요없어짐
        recyclerView.setAdapter(adapter);

        //8/24 수정
        //final List<String> placeLists = new ArrayList<String>();
        adapter.setOnItemClickListener(new OnDayItemClickListener() {
            @Override
            public void onItemClick(DayAdapter.ViewHolder holder, View view, int position) {
                //지우기//Day item = adapter.getItem(position);
                //지우기//첫번째 position 0으로 인식
                //지우기//Toast.makeText(PlanActivity2.this,position+"아이템"+item.getDate(),Toast.LENGTH_LONG ).show();
                //지우기//Toast.makeText(PlanActivity2.this,adapter.getButton().getText().toString(),Toast.LENGTH_LONG ).show();

                Intent subIntent = new Intent(PlanActivity2.this,MainActivity.class);
                startActivityForResult(subIntent,1);

                aPosition = position;

            }
        });
        //지우기
       /* Button btn = adapter.getButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlanActivity2.this,"버튼클릭!!!",Toast.LENGTH_LONG ).show();
            }
        });*/


    }
}