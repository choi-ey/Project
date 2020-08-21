package org.techtown.project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    ArrayList<Day> items = new ArrayList<Day>();
    OnDayItemClickListener listener;
    Context mContext;
    //ArrayList<Memo> subItems = null;
    //8/20
    String title;

    public DayAdapter(Context context,ArrayList<Day> items){
        this.mContext = context;
        this.items = items;
        //this.subItems = subItems;
    }
    //8/20
    public void addTitle(String title){
        this.title = title;
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
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){
            if(resultCode == 2){
                String val = data.getExtras().toString();
                //TextView txtPlace = new TextView(mContext);
                //txtPlace.setText(val);
                Toast.makeText(mContext,val+"dayAdapter",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final DayAdapter.ViewHolder holder, final int position) {

        holder.txtDate.setText("Day"+(position+1)+" "+items.get(position).month+"/"+items.get(position).date);
        holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
        final List<String> placeLists = new ArrayList<String>();
        holder.plusPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(mContext,(position)+"장소추가 버튼 클릭",Toast.LENGTH_LONG).show();
                MainFragment mainFragment = new MainFragment();
                FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                mainFragment = (MainFragment)manager.findFragmentById(R.id.mainfragment);*/
                Intent mainIntent = new Intent(mContext,MainActivity.class);
                v.getContext().startActivity(mainIntent);
                ((Activity)mContext).startActivityForResult(mainIntent,1);

            }


        });
        /*holder.plusPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != RecyclerView.NO_POSITION){
                    //데이터 리스트로부터 아이템 데이터 참조
                    Day item = items.get(position);
                    Toast.makeText(mContext,(position)+"장소추가 버튼 클릭",Toast.LENGTH_LONG).show();
                    //8/19 추가
                    AlertDialog.Builder txtad = new AlertDialog.Builder(mContext);
                    txtad.setTitle("장소");
                    final EditText editText = new EditText(mContext);
                    txtad.setView(editText);
                    //다이얼로그 취소버튼
                    txtad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    txtad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String val = editText.getText().toString();

                            final TextView txtPlace = new TextView(mContext);
                            txtPlace.setText(val);

                            txtPlace.setPadding(10,10,10,10);
                            txtPlace.setBackgroundResource(R.drawable.txt_custom);

                            placeLists.add(txtPlace.getText().toString());
                            txtPlace.setTextSize(20);
                            txtPlace.setId(position);
                            txtPlace.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(v.isClickable()){
                                        Toast.makeText(mContext,position+txtPlace.getText().toString(),Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
                                        ad.setTitle("장소수정");
                                        final EditText editText = new EditText(mContext);
                                        ad.setView(editText);
                                        String val = placeLists.get(placeLists.indexOf(txtPlace.getText().toString()));//txtPlace.getText().toString();
                                        editText.setText(val);
                                        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        ad.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(mContext,"수정",Toast.LENGTH_SHORT).show();
                                                int index = placeLists.indexOf(txtPlace.getText().toString());
                                                String val = editText.getText().toString();
                                                txtPlace.setText(val);
                                                //position을 수정해야함 맨앞에것을 수정함
                                                placeLists.set(index,txtPlace.getText().toString());
                                                System.out.println(placeLists);
                                                dialog.dismiss();
                                            }
                                        });
                                        ad.show();
                                    }

                                }
                            });
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                            lp.setMargins(10,10,10,10);
                            //lp.gravity = Gravity.CENTER;
                            txtPlace.setLayoutParams(lp);
                            holder.container.addView(txtPlace);
                            System.out.println("places: "+placeLists);

                            Toast.makeText(mContext,position+"목록추가",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                    txtad.show();
                }
            }
        });*///장소추가 버튼 (임시-다이얼로그)

        //메모추가 버튼
        final List<String> memoLists = new ArrayList<String>();
        holder.enterMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
                ad.setTitle("메모");
                final EditText editText = new EditText(mContext);
                ad.setView(editText);
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

                        final TextView txtMemo = new TextView(mContext);
                        txtMemo.setText(val);

                        txtMemo.setPadding(10,10,10,10);
                        txtMemo.setBackgroundResource(R.drawable.txt_custom);

                        memoLists.add(txtMemo.getText().toString());
                        txtMemo.setTextSize(20);
                        txtMemo.setId(position);
                        txtMemo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext,position+txtMemo.getText().toString(),Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
                                ad.setTitle("메모수정");
                                final EditText editText = new EditText(mContext);
                                ad.setView(editText);
                                String val = memoLists.get(memoLists.indexOf(txtMemo.getText().toString()));
                                editText.setText(val);
                                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                ad.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext,"수정",Toast.LENGTH_SHORT).show();
                                        int index = memoLists.indexOf(txtMemo.getText().toString());
                                        String val = editText.getText().toString();
                                        txtMemo.setText(val);
                                        memoLists.set(index,txtMemo.getText().toString());
                                        System.out.println(memoLists);
                                        dialog.dismiss();
                                    }
                                });
                                ad.show();
                            }
                        });
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                        lp.setMargins(10,10,10,10);
                        //lp.gravity = Gravity.CENTER;
                        txtMemo.setLayoutParams(lp);
                        holder.container.addView(txtMemo);
                        System.out.println("txt: "+memoLists);

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
