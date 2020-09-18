package org.techtown.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    ArrayList<Plan> items = new ArrayList<Plan>();
    OnPlanItemClickListener listener;
    Context mContext;

    public PlanListAdapter(Context context,ArrayList<Plan> items){
        this.mContext = context;
        this.items = items;
    }
    public void setOnItemClickListener(OnPlanItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.planlist_item,viewGroup,false);

        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanListAdapter.ViewHolder holder, int position) {
        String place = items.get(position).place;
        int year = items.get(position).year;
        int month = items.get(position).month;
        int sDate = items.get(position).sDate;
        int eDate = items.get(position).eDate;

        holder.list_place.setText(place);
        holder.list_date.setText(year+"/"+month+"/"+sDate+"~"+eDate); // 2020/09/18~19 일단은

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView list_place;
        TextView list_date;

        public ViewHolder(@NonNull View itemView, final OnPlanItemClickListener listener) {//이거
            super(itemView);

            list_place = itemView.findViewById(R.id.list_place);
            list_date = itemView.findViewById(R.id.list_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //뷰 클릭했을 때 리스너에 정보전달
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this,v,position); //얘때매 final
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
