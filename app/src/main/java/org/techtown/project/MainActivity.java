package org.techtown.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout; //햄버거 메뉴

    TextView name; //이름 불러와서 저장하기 위함//drawer_header.xml의 TextView
    TextView email;

    FirebaseAuth firebaseAuth;

    Fragment fragmentmap;
    Fragment fragmentmain;
    //검색
    Spinner spinSigungu; //시군구
    List<String> sigungu = new ArrayList<String>();
    String sigunguCode;
    ArrayList<String> sigunguCodes = new ArrayList<String>();
    String key = "";
    String data;

    Spinner spinType; // 관광타입 스피너
    String contentTypeId;
    ArrayList<String> contentTypeIds = new ArrayList<String>();
    //8/9 리싸이클러뷰 어댑터
    RecyclerView recyclerView;
    TourApiAdapter adapter;
    ArrayList<TourApi> list = null;
    TourApi tour = null;
    //네이버 검색기능
    String naverSearch;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //리싸이클러뷰
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        //관광타입 스피너
        spinType = findViewById(R.id.spinType);
        ArrayList<String> typeNames = new ArrayList<String>();
        try{
            XmlPullParser typeList = getResources().getXml(R.xml.content_type_id);
            while(typeList.getEventType() != XmlPullParser.END_DOCUMENT){
                if (typeList.getEventType() == XmlPullParser.START_TAG){
                    if (typeList.getName().equals("item"));
                    else if(typeList.getName().equals("name")){
                        typeList.next();
                        typeNames.add(typeList.getText());
                    }
                    else if(typeList.getName().equals("code")){
                        typeList.next();
                        contentTypeIds.add(typeList.getText());
                    }
                }
                typeList.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_spinner_item, typeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinType.setAdapter(adapter);

        //시군구스피너
        spinSigungu = findViewById(R.id.spinner);
        final Spinner spinArea = findViewById(R.id.spinArea);
        //지역 스피너 클릭시 이벤트 처리
        spinArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String area = parent.getItemAtPosition(position).toString();

                if(position == 0){
                    Toast.makeText(MainActivity.this, area + "선택", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                data = getXmlData(1); //
                            } catch (Exception e) {//키워드 인코딩하니깐 뜸
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                            MainActivity.this, android.R.layout.simple_spinner_item, sigungu);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinSigungu.setAdapter(adapter);
                                }
                            });
                        }
                    }).start();
                }else{ //서울 외의 지역 선택시 결과 없음
                    sigungu.clear();
                    recyclerView.clearOnChildAttachStateChangeListeners();
                    TourApiAdapter adapter1;
                    list.clear();
                    adapter1 = new TourApiAdapter(getApplicationContext(),list);
                    recyclerView.setAdapter(adapter1);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_spinner_item, sigungu);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinSigungu.setAdapter(adapter);
                    System.out.println(sigungu);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //시군구 스피너 클릭시 이벤트 처리
        spinSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sigunguCode = sigunguCodes.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //관광타입 스피너 클릭시 이벤트 처리
        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contentTypeId = contentTypeIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                                Intent intentW = new Intent(MainActivity.this, WishList.class);
                                startActivity(intentW);
                                Toast.makeText(getApplicationContext(),"Favorite",Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.tab_plan:
                                Intent intentP = new Intent(MainActivity.this, PlanActivity.class);
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String currentuser= user.getEmail(); //현재 로그인한 사용자의 이메일 가져오기

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
    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.button:
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
        }
    }
    public class MyAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String queryAreaUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=" + key
                    + "&areaCode=" + 1
                    + "&sigunguCode=" + sigunguCode
                    + "&contentTypeId=" + contentTypeId
                    + "&numOfRows=100&pageNo=1&arrange=O&MobileOS=AND&MobileApp=AppTest";

            try {
                boolean b_title = false;
                boolean b_addr1 = false;
                boolean b_firstimage = false;
                //위도 경도
                boolean b_mapx = false;
                boolean b_mapy = false;

                URL url = new URL(queryAreaUrl);
                InputStream is = url.openStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, "UTF-8"));

                String tag;
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<TourApi>();
                            break;
                        case XmlPullParser.START_TAG:
                            tag = xpp.getName();

                            if (tag.equals("item")) {
                                tour = new TourApi();
                            }
                            if (tag.equals("title")) {
                                b_title = true;
                            }
                            if (tag.equals("addr1")) {
                                b_addr1 = true;
                            }
                            if (tag.equals("firstimage")) {
                                b_firstimage = true;
                            }
                            if(tag.equals("mapx")){
                                b_mapx = true;
                            }
                            if(tag.equals("mapy")){
                                b_mapy = true;
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (b_title) {
                                tour.setTitle(xpp.getText());
                                b_title = false;
                            } else if (b_addr1) {
                                tour.setAddr1(xpp.getText());
                                b_addr1 = false;
                            } else if (b_firstimage) {
                                tour.setFirstImage(xpp.getText());
                                b_firstimage = false;
                            }else if(b_mapx){
                                tour.setMapx(xpp.getText());
                                b_mapx = false;
                            }else if(b_mapy){
                                tour.setMapy(xpp.getText());
                                b_mapy = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            tag = xpp.getName();
                            if (tag.equals("item") && tour != null) list.add(tour);
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("에러다에러");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            adapter = new TourApiAdapter(getApplicationContext(), list);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnTourApiItemClickListener() {
                @Override
                public void OnItemClick(TourApiAdapter.ViewHolder holder, View view, int position) {
                    tour = adapter.getItem(position);

                    naverSearch = tour.getTitle(); //naver 검색위해
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                str = getNaverSearch(naverSearch);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println(str); //여기서 어댑터에 전달..?
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                                        startActivity(intent);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                                System.out.println("네이버에러");
                            }
                        }
                    }); thread.start();
                }
            });
        }
    }
    //8/11 naver검색 ~~~여기부터
    public String getNaverSearch(String naverSearch){
        String clientId = "";
        String clientSecret = "";
        StringBuffer sb = new StringBuffer();
        try {
            String text = URLEncoder.encode(naverSearch,"UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+text+ "&display=1" + "&start=1";

            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id",clientId);
            conn.setRequestProperty("X-Naver-Client-Secret",clientSecret);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;

            xpp.setInput(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item"));
                        else if (tag.equals("link")){
                            xpp.next();
                            sb.setLength(0);
                            sb.append(xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\\\\\s[a-zA-Z]*=[^>]*)?(\\\\\\\\s)*(/)?>",""));
                            sb.append("\n");
                        }
                        break;
                }
                eventType = xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("파싱못함");
        }
        return sb.toString();
    }//~~~여기까지
    String getXmlData(int areaCode)  {
        StringBuffer buffer = new StringBuffer();

        String queryUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode?ServiceKey="+key
                + "&areaCode=" + areaCode
                +"&numOfRows=25&pageNo=1&MobileOS=AND&MobileApp=AppTest";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is,"UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();
            //buffer.append("시작...\n\n");
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        //buffer.append("파싱시작...\n\n");
                        break;
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("item"));
                        else if(tag.equals("name")){
                            xpp.next();
                            sigungu.add(xpp.getText());
                        }
                        else if(tag.equals("code")){
                            xpp.next();
                            sigunguCodes.add(xpp.getText());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            buffer.append("에러\n");
        }

        return buffer.toString();
    }


}