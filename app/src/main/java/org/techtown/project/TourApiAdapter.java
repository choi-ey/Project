package org.techtown.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TourApiAdapter extends RecyclerView.Adapter<TourApiAdapter.ViewHolder> {

    ArrayList<TourApi> items = new ArrayList<TourApi>();
    static Context mContext;
    OnTourApiItemClickListener listener;
    FirebaseAuth firebaseAuth;
    String email;
    boolean saveLoginData;
    CheckBox heart_check;
    SharedPreferences appData;
    String title;
    //10/4추가 리스트 삭제 위해
    FirebaseFirestore dbX;


    public TourApiAdapter(Context context,ArrayList<TourApi> items){
        this.items = items;
        this.mContext = context;
    }
    public void addItem(TourApi item){
        items.add(item);
        notifyDataSetChanged();
    }
    public void setItems(ArrayList<TourApi> items) {
        this.items = items;
    }
    public TourApi getItem(int position){
        return items.get(position);
    }
    public void setOnItemClickListener(OnTourApiItemClickListener listener){
        this.listener = listener;
    }
    //MainFragment 에서 호출
    public void setEmail(String email){
        this.email=email;
        System.out.println(email+"어댑터");
    }


    /*
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    private OnItemClickListener mListener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    } */


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.tourapi_item,viewGroup,false);

        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String title = items.get(position).title;
        final String addr1 = items.get(position).addr1; //"주소: "+
        final String imgURL = items.get(position).firstImage;
        holder.apiTitle.setText(title);
        holder.apiAddr1.setText(addr1);
        //이미지
        Glide.with(holder.itemView).load(imgURL).override(300,300).into(holder.imageView);
        //mapbtn
        final String mapx = items.get(position).mapx;
        final String mapy = items.get(position).mapy;

        final TourApi wTourApi = items.get(position);
        //10.4추가 콘솔
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String currentuser= user.getEmail();
        //콘솔용으로 바꾸면 이 아래 문장 오류로 나옴,,,
        final DocumentReference docRef2 = db2.collection("User").document(currentuser);

        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) { //User -> 해당 email 문서가 있으면
                        ArrayList<TourApi> wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                        //<TourApi> 추가 9/3
                        if(wishList!= null){ //wishlist필드가 생성된경우
                            ArrayList array= (ArrayList)document.getData().get("WishList");
                            int size = wishList.size();
                            for (int i = 0; i< size; i++){
                                HashMap map = (HashMap) array.get(i);

                                if (items.get(position).title.equals(map.get("title").toString())){
                                    holder.heart_check.setOnCheckedChangeListener(null);
                                    items.get(position).setSelected(true);
                                    holder.heart_check.setChecked(items.get(position).isSelected);
                                    holder.heart_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            //tourApi.setSelected(isChecked);
                                            System.out.println("변경: "+isChecked);
                                            if (isChecked){
                                                wTourApi.setSelected(isChecked);
                                                final ArrayList<TourApi> tList = new ArrayList<TourApi>();
                                                TourApi tourApi = new TourApi();
                                                tourApi.setSelected(true);
                                                tourApi.setTitle(title);
                                                tourApi.setAddr1(addr1);
                                                tourApi.setFirstImage(imgURL);
                                                tourApi.setMapx(mapx);
                                                tourApi.setMapy(mapy);
                                                tList.add(tourApi);
                                                // WishList 내용을 DB에 추가
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                final DocumentReference docRef = db.collection("User").document(currentuser);
                                                //docRef.update("WishList",tList);

                                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document != null) { //User -> 해당 email 문서가 있으면
                                                                ArrayList<TourApi> wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                                                                //<TourApi> 추가 9/3
                                                                int size = wishList.size();
                                                                if(wishList!= null){ //wishlist필드가 생성된경우

                                                                    wishList.addAll(tList);
                                                                    docRef.update("WishList",wishList); //WishList 에 tlist 넣어서 문서 업데이트
                                                                    size++;
                                                                }
                                                                else{//wishlist필드가 없는경우
                                                                    docRef.update("WishList",tList);
                                                                }
                                                            } else { }
                                                        } else { } }
                                                });

                                                //알림 다이얼로그
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                builder.setTitle("추가 완료").setMessage("위시리스트에 '"+title+"'이 추가되었습니다");
                                                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();

                                            }else{ //체크박스해제하면? //t
                                                wTourApi.setSelected(isChecked);

                                                final String wTitle = wTourApi.title;

                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                final DocumentReference docRef = db.collection("User").document(currentuser);//10/4 email에서 다바꿈

                                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document != null) { //User -> 해당 email 문서가 있으면
                                                                ArrayList wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                                                                if (wishList!= null){ //wishlist필드가 생성된경우
                                                                    //9.18에서 이부분추가
                                                                    int size = wishList.size();

                                                                    for (int i = 0; i<size; i++){
                                                                        HashMap map = (HashMap) wishList.get(i);
                                                                        if(wTitle.equals(map.get("title").toString())){
                                                                            wishList.remove(i);
                                                                            docRef.update("WishList",wishList);
                                                                            size--;
                                                                        }
                                                                    }
                                                                    //9.18에서 이부분추가
                                                                }
                                                                else{//wishlist필드가 없는경우
                                                                }
                                                                docRef.update("WishList",wishList); //WishList 에 tlist 넣어서 문서 업데이트

                                                            } else { }
                                                        } else { } }
                                                });

                                                //알림 다이얼로그 9/14
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                builder.setTitle("추가 취소").setMessage("위시리스트에 '"+title+"'이 삭제되었습니다");
                                                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();
                                            }

                                        }
                                    });
                                }
                            }
                        }
                        else{//wishlist필드가 없는경우
                        }
                    } else { }
                } else { } }
        });
        //10.4추가 콘솔 여까지
        holder.heart_check.setOnCheckedChangeListener(null); //9.14 스크롤해도 하트 남아있게
        holder.heart_check.setChecked(wTourApi.isSelected());
        holder.heart_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //tourApi.setSelected(isChecked);
                System.out.println("투어어댑터변경: "+isChecked);
                /*if (!isChecked){
                    System.out.println("취소취소 ");
                }*/
                if (isChecked){
                    wTourApi.setSelected(isChecked);
                    final ArrayList<TourApi> tList = new ArrayList<TourApi>();
                    TourApi tourApi = new TourApi();
                    tourApi.setSelected(true);
                    tourApi.setTitle(title);
                    tourApi.setAddr1(addr1);
                    tourApi.setFirstImage(imgURL);
                    tourApi.setMapx(mapx);
                    tourApi.setMapy(mapy);
                    tList.add(tourApi);
                    // WishList 내용을 DB에 추가
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("User").document(email);
                    //docRef.update("WishList",tList);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) { //User -> 해당 email 문서가 있으면
                                    ArrayList<TourApi> wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                                    //<TourApi> 추가 9/3
                                    int size = wishList.size();

                                    if(wishList!= null){ //wishlist필드가 생성된경우
                                        wishList.addAll(tList);
                                        docRef.update("WishList",wishList); //WishList 에 tlist 넣어서 문서 업데이트
                                        size++; //추가
                                    }
                                    else{//wishlist필드가 없는경우
                                        docRef.update("WishList",tList);
                                    }
                                } else { }
                            } else { } }
                    });

                    //알림 다이얼로그
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("추가 완료").setMessage("위시리스트에 '"+title+"'이 추가되었습니다");
                    builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else{ //체크박스해제하면?
                    wTourApi.setSelected(isChecked);

                    final String wTitle = wTourApi.title;

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("User").document(email);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) { //User -> 해당 email 문서가 있으면
                                    ArrayList wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                                    //wishList.remove(position); //이걸 바꿔야 할텐디

                                    int size = wishList.size();

                                    for (int i = 0; i<size; i++){
                                        HashMap map = (HashMap) wishList.get(i);
                                        if(wTitle.equals(map.get("title").toString())){
                                            wishList.remove(i);
                                            docRef.update("WishList",wishList);
                                            size--;
                                        }
                                    }
                                    //docRef.update("WishList",wishList); //WishList 에 tlist 넣어서 문서 업데이트
                                } else { }
                            } else { } }
                    });

                    //알림 다이얼로그 9/14
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("추가 취소").setMessage("위시리스트에 '"+title+"'이 삭제되었습니다");
                    builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        }); //여까지 수정 9/17

        //지도 보여주는 버튼
        holder.mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 위치만 보여주기 위한 MapActivity2 실행 기존의 MapActivity와 다름..!
                Intent map = new Intent(mContext,MapActivity2.class);
                map.putExtra("mapx",mapx);
                map.putExtra("mapy",mapy);
                map.putExtra("title",title);
                map.putExtra("addr",addr1);
                v.getContext().startActivity(map);

            }
        });

        //추가 버튼 (8/20)
        holder.plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,title,Toast.LENGTH_SHORT).show();
                ((MainActivity)mContext).setString(title);
                //지우기 여기부터
                //Intent main = new Intent(mContext,MainActivity.class);
                //main.putExtra("title",title);
                //v.getContext().startActivity(main);
                //8/24
                /*MainFragment fragment = new MainFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title",title);
                fragment.setArguments(bundle);*/
                //((Activity)mContext).startActivityForResult(plan2,1);
                //System.out.println(title+"apiadapter");
                //여기까지
            }
        });
        //추가 하트박스 이거없으면 하트 누르면 전체 누른것으로 인식
        holder.heart_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //좋아요 체크박스 버튼 누르면 하트 채워지고 wishList로 넘어가도록
        ///여기서부터 지워도 됨
      /*//  holder.heart_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
                    //System.out.println(title+"체크"); //position final로 바뀜
                    final ArrayList<TourApi> tList = new ArrayList<TourApi>();
                    TourApi tourApi = new TourApi();
                    tourApi.setSelected(true);
                    tourApi.setTitle(title);
                    tourApi.setAddr1(addr1);
                    tourApi.setFirstImage(imgURL);
                    tourApi.setMapx(mapx);
                    tourApi.setMapy(mapy);
                    tList.add(tourApi);
                    // WishList 내용을 DB에 추가
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("User").document(email);
                    //docRef.update("WishList",tList);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) { //User -> 해당 email 문서가 있으면
                                    ArrayList<TourApi> wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                                    //<TourApi> 추가 9/3
                                    if(wishList!= null){ //wishlist필드가 생성된경우
                                        wishList.addAll(tList);
                                        docRef.update("WishList",wishList); //WishList 에 tlist 넣어서 문서 업데이트
                                    }
                                    else{//wishlist필드가 없는경우
                                        docRef.update("WishList",tList);
                                    }
                                } else { }
                            } else { } }
                    });


                    //체크부분 설정저장
                    save(holder.heart_check);

                    //알림 다이얼로그
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("추가 완료").setMessage("위시리스트에 '"+title+"'이 추가되었습니다");
                    builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    /* 다이얼로그로 대체
                    Intent wishIntent = new Intent(v.getContext(),WishList.class); //v.getContext()
                    wishIntent.putExtra("tList",tList);
                    v.getContext().startActivity(wishIntent);
                    */

         /*//       }else{ //체크박스해제하면?

                    tourApi.setSelected(false);
                   // System.out.println(tourApi.isSelected());


                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection("User").document(email);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) { //User -> 해당 email 문서가 있으면
                                    ArrayList wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                                    wishList.remove(position);

                                    docRef.update("WishList",wishList); //WishList 에 tlist 넣어서 문서 업데이트
                                } else { }
                            } else { } }
                    });

                    System.out.println(items.get(position).title+"체크X");
                }

            }
        }); */ ////여까지

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
 //static으로바뀜 롱클릭
  class ViewHolder extends RecyclerView.ViewHolder{

        TextView apiTitle;
        TextView apiAddr1;
        ImageView imageView;
        Button mapbtn;
        Button plusbtn;
        CheckBox heart_check;

        public ViewHolder(@NonNull View itemView,final OnTourApiItemClickListener listener) {
            super(itemView);

            apiTitle = itemView.findViewById(R.id.apiTitle);
            apiAddr1 = itemView.findViewById(R.id.apiAddr1);
            imageView = itemView.findViewById(R.id.imageView);
            mapbtn = itemView.findViewById(R.id.mapbtn);
            plusbtn = itemView.findViewById(R.id.plusbtn);
            heart_check = itemView.findViewById(R.id.heart_check);
            heart_check.setButtonDrawable(R.drawable.heart_btn);
      /*      //9/18 콘솔하트 가져오기 시도
            //final String str = items.get(1).title;
            final ArrayList<String> wTitles = new ArrayList<String>();
            //wTitles.add(items.get(getAdapterPosition()).title);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("User").document(email);
            //docRef.update("WishList",tList);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) { //User -> 해당 email 문서가 있으면
                            ArrayList<TourApi> wishList= (ArrayList)document.get("WishList"); //WishList 필드값 가져와라
                            //<TourApi> 추가 9/3
                            if(wishList!= null){ //wishlist필드가 생성된경우
                                ArrayList array= (ArrayList)document.getData().get("WishList");
                                int size = wishList.size();
                                for (int i = 0; i< size; i++){
                                    HashMap map = (HashMap) array.get(i);
                                    wTitles.add(items.get(i).title);
                                    //wTitles.add(map.get("title").toString());
                                    if (wTitles.get(i).equals(map.get("title").toString())){
                                        heart_check.setChecked((Boolean)map.get("selected"));
                                    }

                                }
                                System.out.println(wTitles);

                            }
                            else{//wishlist필드가 없는경우
                            }
                        } else { }
                    } else { } }
            });//여까지 10.4다시 이거아냐*/


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null){
                        listener.OnItemClick(ViewHolder.this,v,position);
                    }
                }
            });

            //10/4 추가
             //여까지

        }
    }
    //위시리스트에서 삭제 위해 10/4 아직 구현안함
    public void delete(final int position){
        try{
            /*dbX = FirebaseFirestore.getInstance();
            final DocumentReference docRef = dbX.collection("User").document(email);

            Map<String,Object> updates = new HashMap<>();
            updates.put(items.get(position).place, FieldValue.delete());
            docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("PlanListAdapter 삭제 성공");
                }
            });*/

            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
            System.out.println("투어어댑터에서삭제됨");

        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }
    //여까지 아직안함
}
