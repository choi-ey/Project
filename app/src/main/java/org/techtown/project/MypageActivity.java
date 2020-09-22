package org.techtown.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MypageActivity extends AppCompatActivity {

    public ExpandableListView listView; //ExpandableListView 변수선언
    public ExpandableListViewAdapter adapter;
    public ArrayList<Parent> listParent;

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;
    Toolbar toolbar;
    //설정버튼 누를시
    private DrawerLayout mDrawerLayout;
    TextView name;
    TextView email;
    //네비게이션뷰
    TextView navName;
    TextView navEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        actionBar.setTitle("My page");
        // actionBar.setIcon(R.drawable.menu_settings);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //설정메뉴 선택시
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //헤더뷰의 이름을 바꾸는 방법
        final View view = navigationView.getHeaderView(0);
        navName = view.findViewById(R.id.name);
        navEmail = view.findViewById(R.id.email);

        //이름 받아오기
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        Intent intent = getIntent();
        String mName = intent.getExtras().getString("name");
        final String mEmail = intent.getExtras().getString("email");
        name.setText(mName);
        email.setText(mEmail);
        //네비게이션
        navName.setText(mName);
        navEmail.setText(mEmail);
        //이메일 받아오기
        //사진 받아오기
        //설정메뉴
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                int id = item.getItemId();
                switch (id){
                    case R.id.mypage:
                        Toast.makeText(MypageActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
                        //마이페이지의 설정에서 마이페이지 누르면 네비게이션뷰 나가기
                        mDrawerLayout.closeDrawer(GravityCompat.END);
                        break;
                    case R.id.alert:
                        Toast.makeText(MypageActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.gps:
                        Toast.makeText(MypageActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.logout:
                        ((MainActivity) MainActivity.mContext).logout();
                        break;
                    case R.id.out:
                        deleteuser();
                        break;
                }
                return true;
            }
        });
        //Expadable
        listView = (ExpandableListView)findViewById(R.id.expandableList);
        listParent = new ArrayList<Parent>();

        //load parent
        loadParentData();

        adapter = new ExpandableListViewAdapter(this,listView,listParent);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //Toast.makeText(MypageActivity.this, "parent_list: "+groupPosition, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        //차일드 클릭 시 이벤트
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if(groupPosition == 0){
                    switch(childPosition){
                        case 0:
                            //일정계획
                            //Toast.makeText(getApplicationContext(),"child_list: "+childPosition,Toast.LENGTH_LONG).show();
                            Intent planIntent = new Intent(getApplicationContext(), PlanActivity.class);
                            planIntent.putExtra("user",mEmail);
                            startActivity(planIntent);
                            break;
                        case 1:
                            //지난계획
                            //Toast.makeText(getApplicationContext(),"child_list: "+childPosition,Toast.LENGTH_LONG).show();
                            Intent listIntent = new Intent(getApplicationContext(), PlanList.class);
                            listIntent.putExtra("user",mEmail); //9/20추가
                            startActivity(listIntent);
                            break;
                    }
                }else if(groupPosition == 1){
                    switch(childPosition){
                        case 0:
                            Toast.makeText(getApplicationContext(),"child_list: "+childPosition,Toast.LENGTH_LONG).show();
                            //이렇게하면 툴바 이름 못바꿈...그럼 프래그먼트 이용..?
                            //아니면 Planlistactivity 정보 intent 이용해서 가져오기..?
                            //Intent listIntent = new Intent(getApplicationContext(), PlanlistActivity.class);
                            //startActivity(listIntent);
                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(),"child_list: "+childPosition,Toast.LENGTH_LONG).show();
                            // Intent PlistIntent = new Intent(getApplicationContext(), PlanlistActivity.class);
                            // startActivity(PlistIntent);
                            break;
                    }
                }else{
                    switch (childPosition ){
                        case 0:
                            //위시리스트
                            Toast.makeText(getApplicationContext(),"child_list: "+childPosition,Toast.LENGTH_LONG).show();
                            Intent mywishIntent = new Intent(getApplicationContext(), WishList.class);
                            mywishIntent.putExtra("user",mEmail);
                            startActivity(mywishIntent);
                            break;
                    }
                }
                return true;



            }
        });
        //그룹이 열릴 경우 이벤트
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(),groupPosition+" 그룹이 열림",Toast.LENGTH_LONG).show();
                int groupCount = adapter.getGroupCount();

                //한 그룹을 클릭하면 나머지 그룹들은 닫힌다
                for(int i = 0; i < groupCount; i++){
                    if(!(i == groupPosition))
                        listView.collapseGroup(i);
                }
            }
        });
    }
    private void loadParentData(){
        Parent parent = new Parent();

        List<Child> listChild = new ArrayList<Child>();
        Child child = new Child();

        //1
        parent.setData("   나의여행계획");
        child.setChildData("여행계획세우기");
        listChild.add(child);

        child = new Child();
        child.setChildData("나의 지난 계획");
        listChild.add(child);

        parent.setItems(listChild);
        listParent.add(parent);

        //2
        parent = new Parent();
        listChild = new ArrayList<Child>();
        child = new Child();

        parent.setData("   나의여행기록");
        child.setChildData("여행기록하기");
        listChild.add(child);

        child = new Child();
        child.setChildData("나의 지난 기록");
        listChild.add(child);

        parent.setItems(listChild);
        listParent.add(parent);

        //3
        parent = new Parent();
        listChild = new ArrayList<Child>();
        child = new Child();

        parent.setData("   나의위시리스트");
        child.setChildData("나의위시리스트");
        listChild.add(child);


        parent.setItems(listChild);
        listParent.add(parent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        //설정 네비게이션 화면 Main이랑 동일
        MenuItem item = menu.findItem(R.id.action_setting2);
        item.setVisible(true);

        return true; //메뉴아이템 화면에 표시
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId){
            case R.id.home: //백버튼 누르면 메인페이지로
                finish();
                //   return true;
            case R.id.action_setting2:
                //Intent settingIntent = new Intent(this,SettingActivity.class);
                //startActivity(settingIntent);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //회원탈퇴하고 로그인페이지로 이동하는 함수
    public void deleteuser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);
        builder.setTitle("회원탈퇴");
        builder.setMessage("정말 회원탈퇴를 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.getCurrentUser().delete();
                        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(MypageActivity.this, "회원탈퇴가 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
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