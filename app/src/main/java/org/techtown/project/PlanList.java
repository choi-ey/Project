package org.techtown.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class PlanList extends AppCompatActivity {
    ActionBar actionBar;
    Toolbar toolbar;

    RecyclerView planlist_re;

    PlanListAdapter adapter;
    ArrayList<Plan> list = null;
    Plan plan = null;

    private int year;
    private int sDate;
    private int eDate;
    private int month;
    private String place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);
        //툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        actionBar.setTitle("PlanList");
        actionBar.setDisplayHomeAsUpEnabled(true);

        //리사이클러뷰
        planlist_re = findViewById(R.id.planlist_re);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        planlist_re.setLayoutManager(layoutManager);

        list = new ArrayList<Plan>();

        /*plan = new Plan();
        plan.setPlace(place);
        plan.setYear(year);
        plan.setMonth(month);
        plan.setsDate(sDate);
        plan.setsDate(eDate);
        list.add(plan);*/
        //test
        plan = new Plan();
        plan.setPlace("place");
        plan.setYear(2020);
        plan.setMonth(9);
        plan.setsDate(18);
        plan.seteDate(19);
        list.add(plan);
        //test

        adapter = new PlanListAdapter(PlanList.this,list);
        //adapter.setInfo(user,place);
        planlist_re.setAdapter(adapter);

        //어댑터리스너
        adapter.setOnItemClickListener(new OnPlanItemClickListener() {
            @Override
            public void onItemClick(PlanListAdapter.ViewHolder holder, View view, int position) {
                //Toast.makeText(PlanList.this,position,Toast.LENGTH_SHORT).show();//얜왜안돼
                System.out.println(position);
            }
        });
    }
}