package org.techtown.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    ArrayList<Day> items = new ArrayList<Day>();
    OnDayItemClickListener listener;

    public void addItem(Day item){
        items.add(item);
    }
    public void setItems(ArrayList<Day> items){
        this.items = items;
    }
    public Day getItem(int position){
        return items.get(position);
    }
    public void setItem(int position, Day item){
        items.set(position,item);
    }

    public void setOnItemClickListener(OnDayItemClickListener listener){
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDate;
        public ViewHolder(View itemView,final OnDayItemClickListener listener){
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtSdate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //뷰 클릭했을 때 리스너에 정보전달
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this,v,position);
                    }
                }
            });
        }

        public void setItem(Day item,int count){
            txtDate.setText("Day"+(count)+" "+item.getMonth()+"/"+item.getDate());
            count++;
        }
    }
    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.day_item,viewGroup,false);

        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.ViewHolder holder, int position) {

        Day item = items.get(position);
        holder.setItem(item,(position+1));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
