package org.techtown.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import android.app.Activity;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;

    Toolbar toolbar;
    //햄버거 메뉴
    private DrawerLayout mDrawerLayout;
    //이름 불러와서 저장하기 위함
    TextView name; //drawer_header.xml의 TextView
    TextView email;

    FirebaseAuth firebaseAuth;

    Fragment fragmentmap;
    Fragment fragmentmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        mContext = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentmap= new Fragment(R.layout.fragment_gps);
        fragmentmain=new Fragment();

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
                                getSupportFragmentManager().beginTransaction().replace(R.id.container,fragmentmain).commit();
                                //프래그먼트 사용시 교재 333p
                                return true;
                            case R.id.tab_search:
                                Toast.makeText(getApplicationContext(),"Search",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_favorite:
                                Toast.makeText(getApplicationContext(),"Favorite",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_plan:
                                Toast.makeText(getApplicationContext(),"Plan",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_map:
                                Toast.makeText(getApplicationContext(),"map",Toast.LENGTH_LONG).show();
                                //getSupportFragmentManager().beginTransaction().replace(R.id.container,fragmentmap).commit();
                                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(intent);
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
        //name.setText("이민지");

        //이메일도 같은 방식으로 바꾸기
        email = view.findViewById(R.id.email);
        String Email=getIntent().getStringExtra("email");
        email.setText(Email);

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