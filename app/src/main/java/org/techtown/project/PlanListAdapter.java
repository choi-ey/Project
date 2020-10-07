package org.techtown.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder> {
    ArrayList<Plan> items = new ArrayList<Plan>();
    OnPlanItemClickListener listener;
    Context mContext;

    //10/4추가 리스트 삭제 위해
    FirebaseFirestore db;

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
            //10/4추가
            //PlanList 항목을 길게 누를시 삭제 다이얼로그
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("삭제");
                    builder.setMessage("해당 항목을 삭제하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    delete(getAdapterPosition());
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();

                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void delete(final int position){
        try{
            db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("Plan").document(items.get(position).mEmail);
            //docRef.collection("Plan").document(items.get(position).mEmail).update Data()
            Map<String,Object> updates = new HashMap<>();
            updates.put(items.get(position).place, FieldValue.delete());
            docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("PlanListAdapter 삭제 성공");
                }
            });
            /*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) { //Plan -> 해당 email 문서가 있으면
                            //hashPlan 키값 찾기
                            HashMap hashs = (HashMap) document.getData();
                            Iterator<String> keys = hashs.keySet().iterator();

                            while (keys.hasNext()){
                                String key = keys.next();
                                //System.out.println("KEY : " + key);
                                HashMap hashPlan = (HashMap) document.getData().get(key);
                                if (key.equals(items.get(position).place)){
                                    hashPlan.put(key, FieldValue.delete());
                                    items.remove(position);
                                    notifyItemRemoved(position);
                                }
                            }

                        } else { }
                    } else { }
                }
            });*/
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size()); //이거없어도 되긴했었음

        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }
}
