package org.techtown.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    ArrayList<Day> items = new ArrayList<Day>();
    OnDayItemClickListener listener;
    Context mContext;
    //ArrayList<Memo> subItems = null;
    //8/20
    String title;
    //8/24
    Button plusPlace;
    //LinearLayout container;
    ArrayList<LinearLayout> layouts = new ArrayList<LinearLayout>();
    //9.16 추가
    String user;
    String place;
    FirebaseFirestore db;
    ArrayList memo= new ArrayList();

    public DayAdapter(Context context,ArrayList<Day> items){
        this.mContext = context;
        this.items = items;
        //this.subItems = subItems;
    }
    //8/20
    public void addTitle(String title){
        this.title = title;
    }
    public Button getButton(){
        return plusPlace;
    }
    //8/25
    public LinearLayout getContainer(int position){
        return layouts.get(position);
    }
    //8/27
    int pos;
    public void setPosition(int position){
        this.pos = position;
    }
    public int getPosition(){
        return  pos;
    }
    //

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
    public void setInfo(String user, String place){
        this.user=user;
        this.place=place;

    }

   public class ViewHolder extends RecyclerView.ViewHolder{ //8/24 static에서 public으로
        TextView txtDate;
        //Button plusPlace;
        Button enterMemo;
        LinearLayout container;
        public ViewHolder(View itemView,final OnDayItemClickListener listener){
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtSdate);
            plusPlace = itemView.findViewById(R.id.plusPlace); //장소
            enterMemo = itemView.findViewById(R.id.enterMemo); //메모추가
            container = itemView.findViewById(R.id.container);
            layouts.add(container);

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
    //PlanActivity2에서 실행되면 지워도 될듯 =>getPosition,setPosition도 같이 지우기
    //final List<String> placeLists = new ArrayList<String>();
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == 2){
                final int pos = getPosition();
                title =data.getStringExtra("title");

                /*final TextView txtPlace = new TextView(mContext);
                txtPlace.setText(title);

                txtPlace.setPadding(10,10,10,10);
                txtPlace.setBackgroundResource(R.drawable.txt_custom);

                placeLists.add(txtPlace.getText().toString());

                txtPlace.setTextSize(20);
                txtPlace.setId(pos);
                txtPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.isClickable()){
                            Toast.makeText(mContext,pos+" " +txtPlace.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                lp.setMargins(10,10,10,10);
                txtPlace.setLayoutParams(lp);
                getContainer(pos).addView(txtPlace);
                System.out.println("places: "+placeLists);*/

                //Toast.makeText(mContext,title+"dayAdapter",Toast.LENGTH_SHORT).show(); //확인 OK
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final DayAdapter.ViewHolder holder, final int position) {

        holder.txtDate.setText("Day"+(position+1)+" "+items.get(position).month+"/"+items.get(position).date);
        holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
        //final List<String> placeLists = new ArrayList<String>();
        plusPlace.setOnClickListener(new View.OnClickListener() { //holder 뺌 8/24
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder,v,position);
                setPosition(position); //8/27 추가
                //여기부터
                /*Toast.makeText(mContext,(position)+"장소추가 버튼 클릭",Toast.LENGTH_LONG).show();
                MainFragment mainFragment = new MainFragment();
                FragmentManager manager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                mainFragment = (MainFragment)manager.findFragmentById(R.id.mainfragment);*/
                ///Intent mainIntent = new Intent(mContext,MainActivity.class);
                //v.getContext().startActivity(mainIntent);
                ///((Activity)mContext).startActivityForResult(mainIntent,1);
                //여기까지 안씀
                //8/25 //이게 다시 장소추가 눌러야만 작동함
                /*if(title != null){
                    final TextView txtPlace = new TextView(mContext);
                    txtPlace.setText(title);

                    txtPlace.setPadding(10,10,10,10);
                    txtPlace.setBackgroundResource(R.drawable.txt_custom);

                    placeLists.add(txtPlace.getText().toString());
                    txtPlace.setTextSize(20);
                    txtPlace.setId(position);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                    lp.setMargins(10,10,10,10);
                    //lp.gravity = Gravity.CENTER;
                    txtPlace.setLayoutParams(lp);
                    holder.container.addView(txtPlace);
                    System.out.println("places: "+placeLists);
                }*/

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

                        final String val = editText.getText().toString();

                        final TextView txtMemo = new TextView(mContext);
                        txtMemo.setText(val);

                        txtMemo.setPadding(10,10,10,10);
                        txtMemo.setBackgroundResource(R.drawable.memo_custom);

                        //DB에도 메모추가
                        db = FirebaseFirestore.getInstance();
                        final DocumentReference docRef=db.collection("Plan").document(user);
                        //Map<String, Object> update_memo= new HashMap<>();
                        //update_memo.put("강남.memo",val);
                        //docRef.update(update_memo);

/*
                       docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) { //User 컬렉션에 해당 email 문서가 있으면
                                        ArrayList days= (ArrayList) document.get(place);
                                        int size= days.size();
                                        HashMap map = (HashMap) days.get(size-1);

                                        //memoLists.add(val);
                                        //plan.addAll(memoLists);
                                        //docRef.update(place,plan);


                                    } else { }
                                } else { } }
                        });*/

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
                                //ad.show(); //아래와 똑같은 이유
                                AlertDialog dialog = ad.create();
                                dialog.show();
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            }
                        });
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150);
                        lp.setMargins(10,10,10,10);
                        //lp.gravity = Gravity.CENTER;
                        txtMemo.setLayoutParams(lp);
                        //이러면 또 맨아래에만 쌓임 => 해결
                        holder.container.addView(txtMemo); //holder. 8/25 PlanActivity2로 넘기려고
                        System.out.println("txtMemo: "+memoLists);

                        Toast.makeText(mContext,position+"목록추가",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                //ad.show();
                //ad.show(); 지우고 dialog 한 이유: 취소버튼과 확인버튼 색 다르게
                AlertDialog dialog = ad.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

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
