package org.techtown.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.List;

public class MainFragment extends Fragment {
    Spinner spinSigungu; //시군구
    List<String> sigungu = new ArrayList<String>();
    String sigunguCode;
    ArrayList<String> sigunguCodes = new ArrayList<String>();
    String key ;
    String data;
    Spinner spinType;
    String contentTypeId;
    ArrayList<String> contentTypeIds = new ArrayList<String>();
    RecyclerView recyclerView;
    TourApiAdapter adapter;
    ArrayList<TourApi> list = null;
    TourApi tour = null;
    //네이버 검색기능
    String naverSearch;
    String str;
    Context mContext;
    Button btn;
    String email;
    //8/24
    //지우기//String title;
    //10/17 db
    boolean same=false;
    DatabaseManager db;
    //SQLiteDatabase db;
    ArrayList<UrlList> urlLists = null;
    UrlList url = null;
    FirebaseFirestore listDB;
    FirebaseFirestore urlListDB;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    //안써도 될듯->씀
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Bundle arguments = this.getArguments();
        if (arguments != null){
            email = arguments.getString("email");
        }
        System.out.println("MainFragment: "+email); //ok 전달받음

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        key = getString(R.string.TourAPI);

        btn = rootView.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
                System.out.println("Main Search");
            }
        });


        /* //MainActivity에서 email정보 넘겨받아오긴 함
        Bundle extra = getArguments();
        if(extra != null){
            email = extra.getString("email"); //현재 유저 이메일 정보
            System.out.println(email+"출력");
        } */


        //8/9 리싸이클러뷰
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        //관광타입 스피너
        spinType = rootView.findViewById(R.id.spinType);
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
                mContext, android.R.layout.simple_spinner_item, typeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinType.setAdapter(adapter);

        spinSigungu = rootView.findViewById(R.id.spinner);
        final Spinner spinArea = rootView.findViewById(R.id.spinArea);

        //지역 스피너 클릭시 이벤트 처리
        spinArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    //Toast.makeText(MainActivity.this, area + "선택", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                data = getXmlData(1); //
                            } catch (Exception e) {//키워드 인코딩하니깐 뜸
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //text.setText(data);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                            mContext, android.R.layout.simple_spinner_item, sigungu);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Spinner spinSigungu = findViewById(R.id.spinner);
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
                    adapter1 = new TourApiAdapter(getContext(),list);
                    recyclerView.setAdapter(adapter1);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            mContext, android.R.layout.simple_spinner_item, sigungu);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinSigungu.setAdapter(adapter);
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
                //Toast.makeText(MainActivity.this,parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this,position,Toast.LENGTH_SHORT).show();//에러

                //keyword = parent.getItemAtPosition(position).toString();
                //지역

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


        return rootView;
    }
    public class MyAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            final String queryAreaUrl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="+key
                    + "&areaCode=" + 1
                    +"&sigunguCode="+sigunguCode
                    +"&contentTypeId="+contentTypeId
                    +"&numOfRows=100&pageNo=1&arrange=O&MobileOS=AND&MobileApp=AppTest";

            try {
                boolean b_title = false;
                boolean b_addr1 =false;
                boolean b_firstimage =false;
                // 8/10 추가
                boolean b_mapx = false;
                boolean b_mapy = false;

                URL url = new URL(queryAreaUrl);
                InputStream is = url.openStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is,"UTF-8"));

                String tag;
                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<TourApi>();
                            break;
                        case XmlPullParser.START_TAG:
                            tag = xpp.getName();

                            if (tag.equals("item")){
                                tour = new TourApi();
                            }
                            if(tag.equals("title")){
                                b_title = true;
                            }
                            if(tag.equals("addr1")){
                                b_addr1 = true;
                            }
                            if(tag.equals("firstimage")){
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
                            if(b_title){
                                tour.setTitle(xpp.getText());
                                b_title = false;
                            }else if(b_addr1){
                                tour.setAddr1(xpp.getText());
                                b_addr1 = false;
                            }else if(b_firstimage){
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
                            if (tag.equals("item")&& tour != null) list.add(tour);
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("에러다에러");
            }
            urlLists = new ArrayList<UrlList>();
            url = new UrlList();
            url.setUrl(queryAreaUrl);
            urlLists.add(url);
            //
            urlListDB = FirebaseFirestore.getInstance();
            final DocumentReference docRef2 = urlListDB.collection("UrlLists").document("alswl6709@gmail.com");
            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            System.out.println("U1: UrlList 불러옴");
                            ArrayList urlList= (ArrayList)document.get("UrlList");

                            if(urlList!= null){ //UrlList 필드가 생성된경우
                                System.out.println("U1: url 비교");
                                int uSize=urlList.size();
                                for (int i = 0; i<uSize; i++){
                                    HashMap map = (HashMap) urlList.get(i);
                                    if(queryAreaUrl.equals(map.get("url").toString())){
                                        same=true;
                                        break;
                                    }
                                    System.out.println("U1: url 비교후 있어서 저장X");
                                }
                                if (!same){
                                    System.out.println("U1: url 비교후 없어서 저장");
                                    urlList.addAll(urlLists);
                                    docRef2.update("UrlList",urlList);
                                    uSize++;
                                    //DataLists
                                    listDB = FirebaseFirestore.getInstance();
                                    final DocumentReference docRef2 = listDB.collection("DataLists").document("alswl6709@gmail.com");
                                    docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null) { //DataLists -> 해당 email 문서가 있으면
                                                    System.out.println("F: 됨");
                                                    ArrayList<TourApi> DataList= (ArrayList)document.get("DataList"); //DataList 필드값 가져와라
                                                    if(DataList!= null){ //DataList 필드가 생성된경우
                                                        System.out.println("F: 됨2");
                                                        DataList.addAll(list);
                                                        docRef2.update("DataList",DataList); //WishList 에 tlist 넣어서 문서 업데이트
                                                    }else{//DataList 필드가 없는경우
                                                        System.out.println("F: 안됨2"); //여긴데
                                                        docRef2.update("DataList",list);
                                                    }
                                                }else{//if (document != null)
                                                    System.out.println("F: 안됨");
                                                }
                                            }else{
                                            }
                                        }
                                    });
                                    //저장
                                }
                            }else{//UrlList 필드가 없는경우
                                System.out.println("U1: UrlList 필드가 없는경우");
                                docRef2.update("UrlList",urlLists);
                                //DataLists
                                listDB = FirebaseFirestore.getInstance();
                                final DocumentReference docRef2 = listDB.collection("DataLists").document("alswl6709@gmail.com");
                                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) { //DataLists -> 해당 email 문서가 있으면
                                                System.out.println("F1: 됨");
                                                ArrayList<TourApi> DataList= (ArrayList)document.get("DataList"); //DataList 필드값 가져와라
                                                if(DataList!= null){ //DataList 필드가 생성된경우
                                                    System.out.println("F1: 됨2");
                                                    DataList.addAll(list);
                                                    docRef2.update("DataList",DataList); //WishList 에 tlist 넣어서 문서 업데이트
                                                }else{//DataList 필드가 없는경우
                                                    System.out.println("F1: 안됨2"); //여긴데
                                                    docRef2.update("DataList",list);

                                                }
                                            }else{//if (document != null)
                                                System.out.println("F1: 안됨");
                                            }
                                        }else{//if (task.isSuccessful())
                                        }
                                    }
                                });
                                //DataLists 저장
                            }
                        }else{//if (document != null)
                            System.out.println("U1: 안됨");
                        }
                    }else{//if (task.isSuccessful())
                    }
                }
            });
            //커밋22
            return null; //원래 있었니?
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            adapter = new TourApiAdapter(getContext(),list);//250줄 list = new ArrayList<TourApi>();
            adapter.setEmail(email);
            /*
            int pos =adapter.getPosition();
            System.out.println(pos);
            CheckBox checkBox = adapter.getCheckBox(pos);
            adapter.save(checkBox);*/

            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnTourApiItemClickListener() {
                @Override
                public void OnItemClick(TourApiAdapter.ViewHolder holder, View view, int position) {
                    tour = adapter.getItem(position);
                    //확인 => OK
                    //Toast.makeText(MainActivity.this,position+"선택"+tour.getTitle(),Toast.LENGTH_SHORT).show();
                    final ArrayList<TourApi> tList = new ArrayList<TourApi>();
                    naverSearch = tour.getTitle(); //naver 검색위해
                    tour.getAddr1();
                    tour.isSelected();
                    tour.getMapx();
                    tour.getMapy();
                    tour.getFirstImage();
                    tour.setScore(1);
                    tList.add(tour);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef3 = db.collection("DATA").document(email);

                    // 10/27 firebase: 네이버검색한 내용을 DATA에 추가
                    docRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) { //User -> 해당 email 문서가 있으면
                                    ArrayList mydata= (ArrayList)document.get("data"); //data 필드값 가져와라
                                    //<TourApi> 추가 9/3
                                    if(mydata!=null) {
                                        int size = mydata.size();

                                        for (int i = 0; i<size; i++) {
                                            HashMap map = (HashMap) mydata.get(i);
                                            if (naverSearch.equals(map.get("title").toString())) {
                                                //타이틀 같으면 score꺼내와서 score update
                                                int updatesc = (Integer.parseInt(map.get("score").toString()));
                                                //System.out.println(updatesc);
                                                updatesc = updatesc + 1;

                                                mydata.remove(i);

                                                tour.setScore(updatesc);
                                                mydata.add(tour);
                                                docRef3.update("data", mydata);
                                                same = true;
                                                System.out.println("여기니..22");
                                                break;
                                            }
                                        }
                                        if(!same){ //이아이가 문제입니다,,,,추가가되지않아요,,,,
                                        tour.setScore(1);
                                        mydata.addAll(tList);
                                        System.out.println(tour+"22222222");
                                        docRef3.update("data", mydata);
                                        size++;
                                    }
                                }
                                    else{
                                        docRef3.update("data",tList);
                                    }
                                } else {
                                }
                            } else { } }
                    });


                    //System.out.println(naverSearch);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                str = getNaverSearch(naverSearch);
                                getActivity().runOnUiThread(new Runnable() {
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
    String getXmlData(int areaCode)  {
        StringBuffer buffer = new StringBuffer();

        //시군구 spinner
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
                            //buffer.append("시/군/구: ");
                            xpp.next();
                            //buffer.append(xpp.getText());
                            sigungu.add(xpp.getText());
                            //buffer.append("\n");
                        }
                        else if(tag.equals("code")){
                            //buffer.append("Code: ");
                            xpp.next();
                            //buffer.append(xpp.getText());
                            sigunguCodes.add(xpp.getText());
                            //buffer.append("\n");
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
