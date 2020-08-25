package org.techtown.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TourApiAdapter extends RecyclerView.Adapter<TourApiAdapter.ViewHolder> {

    ArrayList<TourApi> items = new ArrayList<TourApi>();
    Context mContext;
    OnTourApiItemClickListener listener;
    FirebaseAuth firebaseAuth;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.tourapi_item,viewGroup,false);

        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
        //지도 보여주는 버튼 현재는 좌표만 받아옴
        holder.mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* 마커 설정하는건 아래와 같은데 이걸,,,MainFragment에서 어떻게 MapActivity로 끌고가지..............?
               Marker mapMarker=null;
               MarkerOptions markOp = new MarkerOptions();
                markOp.position(new LatLng( Double.parseDouble(mapx),Double.parseDouble(mapy)));
                markOp.title(title).snippet(addr1); //마커 내용
                markOp.draggable(true);
                mapMarker= mMap.addMarker(markOp); */

                Toast.makeText(mContext,mapx+", "+mapy,Toast.LENGTH_SHORT).show();
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
                    ArrayList<TourApi> tList = new ArrayList<TourApi>();
                    TourApi tourApi = new TourApi();
                    tourApi.setTitle(title);
                    tourApi.setAddr1(addr1);
                    tourApi.setFirstImage(imgURL);
                    tourApi.setMapx(mapx);
                    tourApi.setMapy(mapy);
                    tList.add(tourApi);

                    //DB에 추가...!
                    /*FirebaseFirestore db = FirebaseFirestore.getInstance();

                    DocumentReference docRef = db.collection("User").document("현재사용자");

                    ArrayList<Object> wishlist = new ArrayList<>();
                    wishlist.add(title); wishlist.add(addr1); wishlist.add(imgURL); wishlist.add(mapx); wishlist.add(mapy);
                    docRef.update("WishList",wishlist); */

                    Intent wishIntent = new Intent(v.getContext(),WishList.class); //v.getContext()

                    wishIntent.putExtra("tList",tList);
                    v.getContext().startActivity(wishIntent);
                }else{
                    System.out.println(items.get(position).title+"체크X");
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

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
}
