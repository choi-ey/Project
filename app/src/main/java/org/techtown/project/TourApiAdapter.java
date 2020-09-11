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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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

        final TourApi tourApi = items.get(position);
        holder.heart_check.setChecked(tourApi.isSelected());
        holder.heart_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tourApi.setSelected(isChecked);
                System.out.println("변경: "+isChecked);
            }
        });

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

        //좋아요 체크박스 버튼 누르면 하트 채워지고 wishList로 넘어가도록
        holder.heart_check.setOnClickListener(new View.OnClickListener() {
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

                }else{ //체크박스해제하면?

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
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

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

            /*SharedPreferences Preference = mContext.getSharedPreferences("preference", Context.MODE_PRIVATE);

            //Creating editor to store values to shared preferences
            //SharedPreferences.Editor editor = Preference.edit();

            //Adding values to editor
            editor.putBoolean("preference", heart_check.isChecked()); */

            appData = mContext.getSharedPreferences("appData", Context.MODE_PRIVATE);
            load();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null){
                        listener.OnItemClick(ViewHolder.this,v,position);
                    }
                }
            });

        }

    }

    // 설정값을 저장하는 함수
    private void save(CheckBox cb) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();
        CheckBox checkBox=cb;

        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private void load() {
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA",false);

        System.out.println(saveLoginData+" +load");
        System.out.println(saveLoginData+" +load22");
        //회의중임다!

    }
}
