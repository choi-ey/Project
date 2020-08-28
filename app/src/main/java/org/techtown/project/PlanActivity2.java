package org.techtown.project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PlanActivity2 extends AppCompatActivity {

    Toolbar toolbar;
    private TextView txtDate;
    private TextView txtPlace;
    //장소추가 버튼
    private Button plusPlace;
    private int sDate;
    private int eDate;
    private int month;
    //8/19 추가
    DayAdapter adapter;
    ArrayList<Day> list = null;
    Day day = null;
    ArrayList<Memo> sublist = null;
    //8/25 추가
    String title;
    int aPosition;
    LinearLayout container;
    List<String> placeLists =new ArrayList<String>(); //한번에 저장되는 거 수정하기
    //8/27 네이버 검색기능
    String naverSearch;
    String str;
    @Override  // 일정에 장소추가할 때 MainActivity 에서 장소 이름 받아오는 함수
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //List<String> placeLists = new ArrayList<String>(); //이러면 하나하나 저장됨
        final int pos = aPosition;
        container = adapter.getContainer(pos);

        if(requestCode == 1){
            if(resultCode == 2){
                title = data.getStringExtra("title");
                //adapter.onActivityResult(requestCode, resultCode, data);
                //여기서도 DayAdapter 에서도 똑같이 작동 => 여기서 해보기
                final TextView txtPlace = new TextView(PlanActivity2.this);
                txtPlace.setText(title);

                txtPlace.setPadding(10,10,10,10);
                txtPlace.setBackgroundResource(R.drawable.txt_custom);

                placeLists.add(txtPlace.getText().toString());

                txtPlace.setTextSize(20);
                txtPlace.setId(pos);
                // 장소 txt 눌렀을 때 네이버 블로그 검색 API
                txtPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.isClickable()){
                            Toast.makeText(getApplicationContext(),pos+" " +txtPlace.getText().toString(),Toast.LENGTH_SHORT).show();
                            naverSearch = txtPlace.getText().toString();
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        str = getNaverSearch(naverSearch);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                System.out.println(str);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                                                startActivity(intent);
                                            }
                                        });
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        System.out.println("네이버에러");
                                    }
                                }
                            }); thread.start(); //네이버 검색
                        }
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                lp.setMargins(10,10,10,10);
                txtPlace.setLayoutParams(lp);
                container.addView(txtPlace);
                System.out.println("places: "+placeLists);
                //Toast.makeText(PlanActivity2.this,title+"가져옴2",Toast.LENGTH_SHORT).show();//확인 OK
            }else{
                Toast.makeText(PlanActivity2.this,"실패",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan2);

        //툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("여행계획");

        txtDate = findViewById(R.id.txtDate);
        txtPlace = findViewById(R.id.txtPlace);

        Intent incomingIntent = getIntent();
        //여행일정 모아 놓는 페이지에서 보여줄 내용 이곳에서 가져가기
        //장소, 시작 월, 일
        //전체 날짜 받아오기 20xx/0x/xx TO 20xx/0x/xx
        String date = incomingIntent.getStringExtra("date");
        txtDate.setText(date);

        //여행지 받아오기
        String place = incomingIntent.getStringExtra("place");
        txtPlace.setText(place+" 여행");
        //시작과 끝 날짜 받아오기
        month = incomingIntent.getIntExtra("month",0);
        sDate = incomingIntent.getIntExtra("sDate",0);
        //Toast.makeText(this,sDate,Toast.LENGTH_LONG).show();
        eDate = incomingIntent.getIntExtra("eDate",0);

        //리싸이클러뷰
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<Day>();
        sublist = new ArrayList<Memo>();
        for(int i = sDate; i <= eDate; i++){
            day = new Day();
            day.setMonth(month);
            day.setDate(i);
            list.add(day);
        }
        adapter = new DayAdapter(PlanActivity2.this,list); //,sublist => Memo가 필요없어짐
        recyclerView.setAdapter(adapter);

        //8/24 수정
        //final List<String> placeLists = new ArrayList<String>();
        adapter.setOnItemClickListener(new OnDayItemClickListener() {
            @Override
            public void onItemClick(DayAdapter.ViewHolder holder, View view, int position) {
                //지우기//Day item = adapter.getItem(position);
                //지우기//첫번째 position 0으로 인식
                //지우기//Toast.makeText(PlanActivity2.this,position+"아이템"+item.getDate(),Toast.LENGTH_LONG ).show();
                //지우기//Toast.makeText(PlanActivity2.this,adapter.getButton().getText().toString(),Toast.LENGTH_LONG ).show();

                Intent subIntent = new Intent(PlanActivity2.this,MainActivity.class);
                startActivityForResult(subIntent,1);

                //aPosition = holder.getAdapterPosition(); //position
                aPosition = position;
            }
        });
        //지우기
       /* Button btn = adapter.getButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlanActivity2.this,"버튼클릭!!!",Toast.LENGTH_LONG ).show();
            }
        });*/


    }
    //
    public String getNaverSearch(String naverSearch){
        String clientId = getString(R.string.Naver_ID);
        String clientSecret = getString(R.string.Naver_PW);
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
                        /*else if (tag.equals("title")){
                            sb.append("제목: ");
                            xpp.next();
                            sb.append(xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\\\\\s[a-zA-Z]*=[^>]*)?(\\\\\\\\s)*(/)?>",""));
                            sb.append("\n");
                        }*/
                        else if (tag.equals("link")){
                            //sb.append("링크: ");
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
    }
}
