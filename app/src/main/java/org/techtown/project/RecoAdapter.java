package org.techtown.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecoAdapter extends RecyclerView.Adapter<RecoAdapter.ViewHolder> {
    ArrayList<TourApi> items = new ArrayList<TourApi>();
    static Context mContext;
    OnRecoItemClickListener listener;

    public RecoAdapter(Context context,ArrayList<TourApi> items){
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
    public void setOnItemClickListener(OnRecoItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.tourapi_item,viewGroup,false);

        return new RecoAdapter.ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String title = items.get(position).title;
        final String addr1 = items.get(position).addr1;
        final String imgURL = items.get(position).firstImage;
        holder.apiTitle.setText(title);
        holder.apiAddr1.setText(addr1);
        //이미지
        Glide.with(holder.itemView).load(imgURL).override(300,300).into(holder.imageView);
        //mapbtn
        final String mapx = items.get(position).mapx;
        final String mapy = items.get(position).mapy;

    }
    class ViewHolder extends RecyclerView.ViewHolder{

        TextView apiTitle;
        TextView apiAddr1;
        ImageView imageView;
        Button mapbtn;
        Button plusbtn;
        CheckBox heart_check;

        public ViewHolder(@NonNull View itemView,final OnRecoItemClickListener listener) {
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

    @Override
    public int getItemCount() {
        return items.size();
    }
}
