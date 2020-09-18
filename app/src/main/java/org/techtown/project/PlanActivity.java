package org.techtown.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import java.util.Calendar;

public class PlanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText txtPlace;
    Button btnCalendar;
    Button btnGoToPlan;
    int year,monthOfYear,dayOfMonth;
    int yearEnd, monthOfYearEnd,dayOfMonthEnd;

    ActionBar actionBar;
    Toolbar toolbar;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        txtPlace = findViewById(R.id.txtPlace);
        btnCalendar = findViewById(R.id.btnCalendar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        actionBar.setTitle("Plan");
        // actionBar.setIcon(R.drawable.menu_settings);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        user = intent.getExtras().getString("user");

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        PlanActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now); // 현재 이전의 날짜 선택X
                dpd.show(getFragmentManager(),"Datepickerdialog");
            }
        });
        btnGoToPlan = findViewById(R.id.btnGoToPlan);
    }
    @Override
    protected void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null){
            dpd.setOnDateSetListener(this);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, final int year, int monthOfYear, final int dayOfMonth, int yearEnd, int monthOfYearEnd, final int dayOfMonthEnd) {
        monthOfYear = monthOfYear+1;
        monthOfYearEnd = monthOfYearEnd+1;
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.yearEnd = yearEnd;
        this.monthOfYearEnd = monthOfYearEnd;
        this.dayOfMonthEnd = dayOfMonthEnd;

        final String date = year+"/"+monthOfYear+"/"+dayOfMonth+" - "+yearEnd+"/"+monthOfYearEnd+"/"+dayOfMonthEnd;
        btnGoToPlan.setText(txtPlace.getText().toString()+"여행\n"+date+"등록하기");
        final int finalMonthOfYear = monthOfYear;
        btnGoToPlan.setVisibility(View.VISIBLE);
        btnGoToPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanActivity.this,PlanActivity2.class);
                intent.putExtra("place",txtPlace.getText().toString());
                intent.putExtra("year",year);
                intent.putExtra("date",date);
                intent.putExtra("month", finalMonthOfYear);
                intent.putExtra("sDate",dayOfMonth);
                intent.putExtra("eDate",dayOfMonthEnd);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }
}