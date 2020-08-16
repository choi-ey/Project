package org.techtown.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TourApiAdapter extends RecyclerView.Adapter<TourApiAdapter.ViewHolder> {

    ArrayList<TourApi> items = new ArrayList<TourApi>();
    Context mContext;
    OnTourApiItemClickListener listener;

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
                Toast.makeText(mContext,mapx+", "+mapy,Toast.LENGTH_SHORT).show();
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
        CheckBox heart_check;

        public ViewHolder(@NonNull View itemView,final OnTourApiItemClickListener listener) {
            super(itemView);

            apiTitle = itemView.findViewById(R.id.apiTitle);
            apiAddr1 = itemView.findViewById(R.id.apiAddr1);
            imageView = itemView.findViewById(R.id.imageView);
            mapbtn = itemView.findViewById(R.id.mapbtn);
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
