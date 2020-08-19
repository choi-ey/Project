package org.techtown.project;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    ArrayList<Day> items = new ArrayList<Day>();
    OnDayItemClickListener listener;
    Context mContext;
    ArrayList<Memo> subItems = null;

    public DayAdapter(Context context,ArrayList<Day> items,ArrayList<Memo> subItems){
        this.mContext = context;
        this.items = items;
        this.subItems = subItems;
    }

    public void addItem(Day item){
        items.add(item);
        notifyDataSetChanged();
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
        Button plusPlace;
        Button enterMemo;
        LinearLayout container;
        public ViewHolder(View itemView,final OnDayItemClickListener listener){
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtSdate);
            plusPlace = itemView.findViewById(R.id.plusPlace); //장소
            enterMemo = itemView.findViewById(R.id.enterMemo); //메모추가
            container = itemView.findViewById(R.id.container);

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


    }
    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.day_item,viewGroup,false);

        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayAdapter.ViewHolder holder, final int position) {

        holder.txtDate.setText("Day"+(position+1)+" "+items.get(position).month+"/"+items.get(position).date);

        //메모 잘되면 나중에
        holder.plusPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != RecyclerView.NO_POSITION){
                    //데이터 리스트로부터 아이템 데이터 참조
                    Day item = items.get(position);
                    Toast.makeText(mContext,(position)+"장소추가 버튼 클릭",Toast.LENGTH_LONG).show();
                }
            }
        });
        //메모추가 버튼
        final List<String> txts = new ArrayList<String>();
        holder.enterMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
                ad.setTitle(position+"메모");
                final EditText editText = new EditText(mContext);
                ad.setView(editText);
                //memo 리싸이클러뷰
                //다이얼로그 취소버튼
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String val = editText.getText().toString();

                        final TextView txt = new TextView(mContext);
                        txt.setText(val);
                        txts.add(txt.getText().toString());
                        txt.setTextSize(20);
                        txt.setId(position);
                        txt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext,position+txt.getText().toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,100);
                        //lp.gravity = Gravity.CENTER;
                        txt.setLayoutParams(lp);
                        holder.container.addView(txt);
                        System.out.println("txt: "+txts);

                        Toast.makeText(mContext,position+"목록추가",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                ad.show();

                //memo 눌렀을 때 메세지
                //지움
               /* memoAdapter.setOnItemClickListener(new OnMemoItemClickListener() {
                    @Override
                    public void onItemClick(MemoAdapter.ItemViewHolder holder, View view, int position) {
                        Memo item = memoAdapter.getItem(position);
                        Toast.makeText(mContext,position+":메모아이템 클릭",Toast.LENGTH_SHORT).show();
                    }
                });*/
                //여까지
            }
        });

    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
