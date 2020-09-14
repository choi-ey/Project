package org.techtown.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout; //햄버거 메뉴

    TextView name; //이름 불러와서 저장하기 위함//drawer_header.xml의 TextView
    TextView email;

    FirebaseAuth firebaseAuth;

    MainFragment mainFragment;
    String title;
    public void setString(String title){
        this.title = title;
        //System.out.println("MainActivity: "+title); //확인 OK
        Intent intent = new Intent();
        intent.putExtra("title",title);
        setResult(2,intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager manager = getSupportFragmentManager();
        mainFragment = (MainFragment)manager.findFragmentById(R.id.mainfragment);

        //8/24
        /*Intent intent = new Intent();
        intent.putExtra("title","타이틀");
        setResult(2,intent);
        finish(); //안됨*/
        //

        firebaseAuth = FirebaseAuth.getInstance();
        mContext = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String currentuser= user.getEmail(); //현재 로그인한 사용자의 이메일 가져오기

        final Bundle bundle= new Bundle();
        bundle.putString("email",currentuser);
        mainFragment.setArguments(bundle);

        ActionBar actionBar = getSupportActionBar();
        //홈키에 햄버거 메뉴 이미지 설정
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("main");
        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.tab_home:
                                Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_LONG).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container,mainFragment).commit();
                                //프래그먼트 사용시 교재 333p
                                return true;
                            case R.id.tab_search:
                                Toast.makeText(getApplicationContext(),"Search",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_favorite:
                                Intent intentW = new Intent(MainActivity.this, WishList.class);
                                intentW.putExtra("user",currentuser);
                                startActivity(intentW);
                                Toast.makeText(getApplicationContext(),"Favorite",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_plan:
                                Intent intentP = new Intent(MainActivity.this, PlanActivity.class);
                                intentP.putExtra("user",currentuser);
                                startActivity(intentP);
                                Toast.makeText(getApplicationContext(),"Plan",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_map:
                                Toast.makeText(getApplicationContext(),"map",Toast.LENGTH_LONG).show();
                                //getSupportFragmentManager().beginTransaction().replace(R.id.container,fragmentmap).commit();
                                Intent intentM = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(intentM);
                                return true;
                        }
                        return false;
                    }
                }
        );
        //햄버거 메뉴 사용
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //헤더뷰의 이름을 바꾸는 방법
        final View view = navigationView.getHeaderView(0);
        name = view.findViewById(R.id.name);

        DocumentReference docRef=db.collection("User").document(currentuser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name11= document.getString("name");
                        name.setText(name11);
                    } else { }
                } else { }
            }
        });


        //이메일도 같은 방식으로 바꾸기
        email = view.findViewById(R.id.email);
        email.setText(currentuser);

        //이미지뷰 받아오기
        //햄버거 메뉴 누를 시
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                int id = item.getItemId();
                switch (id){
                    case R.id.mypage:
                        Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
                        Intent mypageIntent = new Intent(MainActivity.this,MypageActivity.class);
                        //이름 마이페이지에 보내기
                        mypageIntent.putExtra("name",name.getText().toString());
                        //이메일도 보내기
                        mypageIntent.putExtra("email",email.getText().toString());
                        startActivity(mypageIntent);
                        break;
                    case R.id.alert:
                        Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.gps:
                        Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.tag:
                        Intent intent = new Intent(MainActivity.this, TagActivity.class);
                        intent.putExtra("user",currentuser);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.out:
                        deleteuser();
                        break;
                }
                return true;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //기본 toolBar 설정
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //햄버거메뉴 누르면 창 띄우기
        switch (id){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //로그아웃하고 로그인 페이지로 이동하는 함수
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "로그아웃 완료", Toast.LENGTH_SHORT).show();
    }
    //회원탈퇴하고 로그인페이지로 이동하는 함수
    public void deleteuser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("회원탈퇴");
        builder.setMessage("정말 회원탈퇴를 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.getCurrentUser().delete();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "회원탈퇴가 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }



}