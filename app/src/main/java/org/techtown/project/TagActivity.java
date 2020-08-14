package org.techtown.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

public class TagActivity extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar toolbar;
    EditText Tagarea, Tagmeal,Tagetc;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        actionBar.setTitle("Regist Tag");
        // actionBar.setIcon(R.drawable.menu_settings);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Tagarea = (EditText) findViewById(R.id.pre_area);
        Tagmeal = (EditText) findViewById(R.id.pre_meal);
        Tagetc = (EditText) findViewById(R.id.pre_etc);

        final Intent intent = getIntent();
        final String user = intent.getExtras().getString("user");


        Button buttonSubmit = (Button) findViewById(R.id.pre_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //FirebaseUser user = firebaseAuth.getCurrentUser();
                //String currentuser= user.getEmail(); //현재 로그인한 사용자의 이메일 가져오기

                String tagarea=Tagarea.getText().toString();
                String tagmeal=Tagmeal.getText().toString();
                String tagetc=Tagetc.getText().toString();

                // Update an existing document
                DocumentReference docRef = db.collection("User").document(user);

                // (async) Update one field
                docRef.update("Tag_area",tagarea,"Tag_meal",tagmeal,"Tag_Etc",tagetc);

                Intent intent = new Intent(TagActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });



    }
}