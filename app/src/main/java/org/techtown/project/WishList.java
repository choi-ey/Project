package org.techtown.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class WishList extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar toolbar;

    RecyclerView wishRecycler;
    TourApiAdapter adapter;
    ArrayList<TourApi> list = null;
    TourApi tour = null;
    String email;

    // 추가한 것
    ArrayList wishList = null;
    int size;
    ArrayList<String> addr1s = null;
    ArrayList<String> titles = null;
    ArrayList<String> firstImages = null;
    ArrayList<String> mapxs = null;
    ArrayList<String> mapys = null;
    ArrayList<Boolean> checks = null;

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
        email = intent.getExtras().getString("user");

        /* for문으로 DB에 저장된 내용 불러오기
        Intent intent = getIntent();
        list = (ArrayList<TourApi>)intent.getSerializableExtra("tList");
        System.out.println(list); //지워도 됨
         */

        //DB에 저장된 WishList 목록 불러오기
        list = new ArrayList<TourApi>();
        addr1s = new ArrayList<String>();
        titles = new ArrayList<String>();
        firstImages = new ArrayList<String>();
        mapxs = new ArrayList<String>();
        mapys = new ArrayList<String>();
        checks = new ArrayList<Boolean>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("User").document(email);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) { //User -> 해당 email 문서가 있으면
                        //wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                        wishList= (ArrayList)document.getData().get("WishList");
                        size = wishList.size();
                        for (int i = 0; i< size; i++){
                            HashMap map = (HashMap) wishList.get(i);
                            addr1s.add(map.get("addr1").toString());
                            titles.add(map.get("title").toString());
                            firstImages.add(map.get("firstImage").toString());
                            mapxs.add(map.get("mapx").toString());
                            mapys.add(map.get("mapy").toString());
                            tour = new TourApi();
                            tour.setAddr1(addr1s.get(i));
                            tour.setFirstImage(firstImages.get(i));
                            tour.setMapx(mapxs.get(i));
                            tour.setMapy(mapys.get(i));
                            tour.setTitle(titles.get(i));
                            tour.setSelected(checks.get(i));

                            list.add(tour);
                        }
                        adapter = new TourApiAdapter(WishList.this,list);
                        wishRecycler.setAdapter(adapter);

                    } else { }
                } else { } }
        });


        /*for (int i = 0; i <size; i++){
            tour = new TourApi();
            tour.setAddr1(addr1s.get(i));
            tour.setFirstImage(firstImages.get(i));
            tour.setMapx(mapxs.get(i));
            tour.setMapy(mapys.get(i));
            tour.setTitle(titles.get(i));
            list.add(tour);
        }

        adapter = new TourApiAdapter(WishList.this,list);
        wishRecycler.setAdapter(adapter);*/
    }
}