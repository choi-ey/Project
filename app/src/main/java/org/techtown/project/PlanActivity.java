package org.techtown.project;

import androidx.appcompat.app.AppCompatActivity;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class PlanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText txtPlace;
    Button btnCalendar;
    Button btnGoToPlan;
    int year,monthOfYear,dayOfMonth;
    int yearEnd, monthOfYearEnd,dayOfMonthEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        txtPlace = findViewById(R.id.txtPlace);
        btnCalendar = findViewById(R.id.btnCalendar);

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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, final int dayOfMonth, int yearEnd, int monthOfYearEnd, final int dayOfMonthEnd) {
        monthOfYear = monthOfYear+1;
        monthOfYearEnd = monthOfYearEnd+1;
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.yearEnd = yearEnd;
        this.monthOfYearEnd = monthOfYearEnd;
        this.dayOfMonthEnd = dayOfMonthEnd;

        final String date = year+"/"+monthOfYear+"/"+dayOfMonth+" TO "+yearEnd+"/"+monthOfYearEnd+"/"+dayOfMonthEnd;
        btnGoToPlan.setText(txtPlace.getText().toString()+"여행\n"+date+"등록하기");
        final int finalMonthOfYear = monthOfYear;
        btnGoToPlan.setVisibility(View.VISIBLE);
        btnGoToPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanActivity.this,PlanActivity2.class);
                intent.putExtra("place",txtPlace.getText().toString());
                intent.putExtra("date",date);
                intent.putExtra("month", finalMonthOfYear);
                intent.putExtra("sDate",dayOfMonth);
                intent.putExtra("eDate",dayOfMonthEnd);
                startActivity(intent);
            }
        });
    }
}