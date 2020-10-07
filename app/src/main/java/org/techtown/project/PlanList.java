package org.techtown.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PlanList extends AppCompatActivity {
    ActionBar actionBar;
    Toolbar toolbar;

    RecyclerView planlist_re;

    PlanListAdapter adapter;
    ArrayList<Plan> list = null;
    Plan plan = null;

    ArrayList cPlace = null;

    String mEmail;

    private int year;
    private int sDate;
    private int eDate;
    private int month;
    private String place;
    ArrayList<String> day1 = new ArrayList<>();

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

        Intent intent = getIntent();
        mEmail = intent.getExtras().getString("user");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Plan").document(mEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) { //Plan -> 해당 email 문서가 있으면
                        //hashPlan 키값 찾기
                        HashMap hashs = (HashMap) document.getData();
                        Iterator<String> keys = hashs.keySet().iterator();

                        while (keys.hasNext()){
                            String key = keys.next();
                            System.out.println("KEY : " + key);
                            HashMap hashPlan = (HashMap) document.getData().get(key);
                            //Object month =  ((HashMap) document.getData().get("마포")).get("month");
                            place = key;
                            year  = ((Long) hashPlan.get("year")).intValue();
                            month  = ((Long) hashPlan.get("month")).intValue();
                            sDate  =  ((Long) hashPlan.get("sDate")).intValue();
                            eDate  = ((Long) hashPlan.get("eDate")).intValue();
                            //이런식으로 day1,2,3 ,,, 하나씩 받아와야함
                            day1 = (ArrayList<String>) hashPlan.get("Day1");
                            System.out.println("day1"+day1);

                            plan = new Plan();
                            plan.setPlace(place);
                            plan.setYear(year);
                            plan.setMonth(month);
                            plan.setsDate(sDate);
                            plan.seteDate(eDate);
                            plan.setDay1(day1);
                            plan.setmEmail(mEmail); //10/4추가
                            list.add(plan);

                            System.out.println("planlist: "+hashPlan);
                        }


                        adapter = new PlanListAdapter(PlanList.this,list);
                        //adapter.setInfo(mEmail,place); //사용 10/4 user=>mEmail 없어도 될듯
                        planlist_re.setAdapter(adapter);

                        //아이템 클릭 리스너
                        adapter.setOnItemClickListener(new OnPlanItemClickListener() {
                            @Override
                            public void onItemClick(PlanListAdapter.ViewHolder holder, View view, int position) {
                                //Toast.makeText(PlanList.this,position,Toast.LENGTH_SHORT).show();//얜왜안돼

                                Plan plan = list.get(position);
                                //System.out.println("포지션은: "+position+"장소는: "+plan.place);//확인OK
                                String date = plan.year+"/"+plan.month+"/"+plan.sDate+" - "+plan.year+"/"+plan.month+"/"+plan.eDate;
                                Intent intent = new Intent(PlanList.this,PlanActivity2.class);
                                intent.putExtra("place",plan.place);
                                intent.putExtra("year",plan.year);
                                intent.putExtra("date",date);
                                intent.putExtra("month", plan.month);
                                intent.putExtra("sDate",plan.sDate);
                                intent.putExtra("eDate",plan.eDate);
                                intent.putExtra("user",mEmail);
                                if(day1!=null){
                                    intent.putStringArrayListExtra("Day1",plan.day1);
                                }
                                startActivity(intent);
                            }
                        }); //아이템 클릭리스너

                    } else { }
                } else { }
            }
        });

        /*plan = new Plan();
        plan.setPlace("place");
        plan.setYear(year);
        plan.setMonth(month);
        plan.setsDate(sDate);
        plan.setsDate(eDate);
        list.add(plan);*/

        //test
       /* plan = new Plan();
        plan.setPlace("place");
        plan.setYear(2020);
        plan.setMonth(9);
        plan.setsDate(18);
        plan.seteDate(30);
        list.add(plan);*/
        //test
/*
        adapter = new PlanListAdapter(PlanList.this,list);
        //adapter.setInfo(user,place);
        planlist_re.setAdapter(adapter);

        //어댑터리스너
        adapter.setOnItemClickListener(new OnPlanItemClickListener() {
            @Override
            public void onItemClick(PlanListAdapter.ViewHolder holder, View view, int position) {
                //Toast.makeText(PlanList.this,position,Toast.LENGTH_SHORT).show();//얜왜안돼
                System.out.println("포지션은: "+position);
            }
        });*/
    }
}