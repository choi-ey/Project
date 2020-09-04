package org.techtown.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TagActivity extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar toolbar;
    EditText Tagarea, Tagmeal,Tagetc;
    FirebaseFirestore db;
    DocumentReference docRef;

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

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("User").document(user);

        //9.4 수정!
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) { //User 컬렉션에 해당 email 문서가 있으면
                        String tag_ar= (String) document.get("Tag_area");
                        String tag_me= (String) document.get("Tag_meal");
                        String tag_et= (String) document.get("Tag_Etc");

                        if(tag_ar!= null){ //태그 관련 필드가 이미 생성&저장 된경우
                            Tagarea.setText(tag_ar);
                            Tagmeal.setText(tag_me);
                            Tagetc.setText(tag_et);
                        }
                        else{//태그관련 필드가 없는경우
                        }
                    } else { }
                } else { } }
        });


        Button buttonSubmit = (Button) findViewById(R.id.pre_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tagarea=Tagarea.getText().toString();
                String tagmeal=Tagmeal.getText().toString();
                String tagetc=Tagetc.getText().toString();

                // Update one field
                docRef.update("Tag_area",tagarea,"Tag_meal",tagmeal,"Tag_Etc",tagetc);

                Intent intent = new Intent(TagActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }
}