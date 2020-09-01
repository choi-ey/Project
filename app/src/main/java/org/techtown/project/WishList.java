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

public class WishList extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar toolbar;

    RecyclerView wishRecycler;
    TourApiAdapter adapter;
    ArrayList<TourApi> list = null;
    TourApi tour = null;
    String email;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("User").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) { //User -> 해당 email 문서가 있으면
                        ArrayList wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                        //list.addAll(wishList);
                        //System.out.println(list);

                    } else { }
                } else { } }
        });

        adapter = new TourApiAdapter(WishList.this,list);
        wishRecycler.setAdapter(adapter);
    }
}