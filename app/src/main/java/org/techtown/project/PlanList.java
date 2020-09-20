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

        Intent intent = getIntent();
        final String mEmail = intent.getExtras().getString("user");

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

                            plan = new Plan();
                            plan.setPlace(place);
                            plan.setYear(year);
                            plan.setMonth(month);
                            plan.setsDate(sDate);
                            plan.seteDate(eDate);
                            list.add(plan);
                        }


                        adapter = new PlanListAdapter(PlanList.this,list);
                        //adapter.setInfo(user,place);
                        planlist_re.setAdapter(adapter);

                        adapter.setOnItemClickListener(new OnPlanItemClickListener() {
                            @Override
                            public void onItemClick(PlanListAdapter.ViewHolder holder, View view, int position) {
                                //Toast.makeText(PlanList.this,position,Toast.LENGTH_SHORT).show();//얜왜안돼
                                System.out.println("포지션은: "+position);
                            }
                        });



                       // System.out.println(hashPlan);


                        //wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                       /* wishList= (ArrayList)document.getData().get("WishList");
                        size = wishList.size();
                        for (int i = 0; i< size; i++){
                            HashMap map = (HashMap) wishList.get(i);
                            addr1s.add(map.get("addr1").toString());
                            titles.add(map.get("title").toString());
                            firstImages.add(map.get("firstImage").toString());
                            mapxs.add(map.get("mapx").toString());
                            mapys.add(map.get("mapy").toString());
                            checks.add((Boolean) map.get("selected"));
                            tour = new TourApi();
                            tour.setAddr1(addr1s.get(i));
                            tour.setFirstImage(firstImages.get(i));
                            tour.setMapx(mapxs.get(i));
                            tour.setMapy(mapys.get(i));
                            tour.setTitle(titles.get(i));
                            tour.setSelected(checks.get(i));
                            list.add(tour);
                        }
                        System.out.println(tour.isSelected()+"wishList");
                        adapter = new TourApiAdapter(WishList.this,list);
                        wishRecycler.setAdapter(adapter);
                        adapter.setOnItemClickListener(new OnTourApiItemClickListener() {
                            @Override
                            public void OnItemClick(TourApiAdapter.ViewHolder holder, View view, int position) {
                                System.out.println(position+"  wish");
                                holder.heart_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        tour.setSelected(isChecked);
                                        System.out.println("위시에서 바뀜");
                                    }
                                });
                            }
                        });*/

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
        });
    }
}