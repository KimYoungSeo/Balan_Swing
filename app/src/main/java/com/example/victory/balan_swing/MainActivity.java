package com.example.victory.balan_swing;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static final int SIZE = 70;

    public class CDay{
        public LinearLayout mLLDay;
        public int mYear, mMonth, mDay, mNum;
        public TextView mTvDay, mTvGrade;
        public boolean mCur, mSel;

        public CDay()
        {
            mYear = mMonth = mDay = mNum = 0;
            mLLDay = new LinearLayout(getApplicationContext());
            mLLDay.setLayoutParams(new ViewGroup.LayoutParams(GetPixelFromDP(SIZE), GetPixelFromDP(SIZE)));
            mLLDay.setOrientation(LinearLayout.VERTICAL);
            mLLDay.setOnClickListener(onClick);
            mLLDay.setOnLongClickListener(onLClick);
            mLLDay.setPadding(5,5,5,5);

            mTvDay = new TextView(getApplicationContext());
            mTvDay.setLayoutParams(new ViewGroup.LayoutParams(GetPixelFromDP(22), GetPixelFromDP(22)));
            mTvDay.setGravity(Gravity.CENTER);
            mTvDay.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mTvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            // imageView로 바꾸기
            mTvGrade = new TextView(getApplicationContext());
            mTvGrade.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mTvGrade.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mTvGrade.setPadding(10,10,10,10);
            mTvGrade.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

            mLLDay.addView(mTvDay);
            mLLDay.addView(mTvGrade);

            mCur = mSel = false;
        }

        public void setBackground()
        {
            if (mNum >= 35) {
                if (mNum == 41) {
                    mLLDay.setBackgroundResource(R.drawable.calendar_border4);
                } else {
                    mLLDay.setBackgroundResource(R.drawable.calendar_border3);
                }
            } else if (mNum % 7 == 6) {
                mLLDay.setBackgroundResource(R.drawable.calendar_border2);
            } else {
                mLLDay.setBackgroundResource(R.drawable.calendar_border1);
            }
            mTvDay.setBackground(null);
        }

        public void setBackground_Cur()
        {
            mTvDay.setBackgroundResource(R.drawable.today_bg);
        }

        public void setBackground_Sel()
        {
            mTvDay.setBackgroundResource(R.drawable.sel_bg);
        }

        public void setBackground_Other()
        {
            if (mNum >= 35) {
                if (mNum == 41) {
                    mLLDay.setBackgroundResource(R.drawable.other_border4);
                } else {
                    mLLDay.setBackgroundResource(R.drawable.other_border3);
                }
            } else if (mNum % 7 == 6) {
                mLLDay.setBackgroundResource(R.drawable.other_border2);
            } else {
                mLLDay.setBackgroundResource(R.drawable.other_border1);
            }
        }

        public void clearSel() { mSel = false; }
    };

    Button btnMainBack;

    LinearLayout viewCalendar;
    LinearLayout viewDayOftheWeek;
    Button btnPrevMonth;
    Button btnNextMonth;
    TextView tvCurCal;

    LinearLayout theLLWeeks[];
    CDay cDay[];

    Calendar theDate;

    int theYear;
    int theMonth;
    int theDay;
    int theDayOfWeek;

    static int curYear;
    static int curMonth;
    static int curDay;

    int index;

    DatePickerDialog dpDialog;

    View.OnClickListener onClick;
    View.OnLongClickListener onLClick;

    SharedPreferences pref;
    int lang;

    String[] main_dayofweek, main_match, main_month;
    int[] dayofweekID = {
            R.id.sun, R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        MakeCalendarView();
    }

    public int GetPixelFromDP(float aDP)
    {
        float scale = this.getResources().getDisplayMetrics().density;
        return((int)(aDP*scale));
    }

    public void init()
    {
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        lang = pref.getInt("language", 0);

        btnMainBack = (Button) findViewById(R.id.btnMainBack);

        viewCalendar = (LinearLayout) findViewById(R.id.calendarLayout);
        viewDayOftheWeek = (LinearLayout) findViewById(R.id.DayOftheWeek);
        btnPrevMonth = (Button) findViewById(R.id.btnPrevMonth);
        btnNextMonth = (Button) findViewById(R.id.btnNextMonth);
        tvCurCal = (TextView) findViewById(R.id.tvCurCal);

        theLLWeeks = new LinearLayout[6];
        cDay = new CDay[6*7];

        onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof Button)
                {
                    Button tmpBtn = (Button) v;
                    switch (tmpBtn.getId())
                    {
                        case R.id.btnPrevMonth:
                            theMonth--;
                            if (theMonth < 0)
                            {
                                theYear--;
                                theMonth = 11;
                            }
                            break;
                        case R.id.btnNextMonth:
                            theMonth++;
                            if (theMonth > 11)
                            {
                                theYear++;
                                theMonth = 0;
                            }
                            break;
                        case R.id.btnMainBack:
                            finish();
                            break;
                    }

                    MakeCalender(theYear, theMonth);
                    //setDateLabel(false);
                }
                else if (v instanceof TextView)
                {
                    TextView tmpTv = (TextView) v;
                    if (tmpTv.getId() == R.id.tvCurCal)
                    {
                        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year, int monthOfYear, int dayOfMonth) {
                                theYear = year;
                                theMonth = monthOfYear;
                                theDay = dayOfMonth;
                                MakeCalender(theYear, theMonth);
                                //setDateLabel(false);
                            }
                        };
                        dpDialog = new DatePickerDialog(getApplicationContext(), android.R.style.Theme_Holo_Light_Dialog, d, theYear, theMonth, theDay);
                        dpDialog.getDatePicker().setCalendarViewShown(false);
                        dpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dpDialog.show();
                    }
                }
                else if (v instanceof LinearLayout)
                {
                    index = v.getId();

                    if (cDay[index].mSel)    // 이미 선택된 날짜
                    {
                        cDay[index].mSel = false;
                        /*Intent intent = new Intent(this, EnterActivity.class);
                        intent.putExtra(EnterActivity.KEY_YEAR, cDay[index].mYear);
                        intent.putExtra(EnterActivity.KEY_MONTH, cDay[index].mMonth);
                        intent.putExtra(EnterActivity.KEY_DAY, cDay[index].mDay);
                        startActivityForResult(intent, 0);*/
                    }
                    MakeCalender(theYear, theMonth);
                    cDay[index].mSel = true;
                    cDay[index].setBackground_Sel();

                    //tvCurDate.setText(String.format("%04d/%02d/%02d", cDay[index].mYear, cDay[index].mMonth+1, cDay[index].mDay));
                }
            }
        };

        onLClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                index = v.getId();
                MakeCalender(theYear, theMonth);
                cDay[index].setBackground_Sel();
                cDay[index].mSel = true;

                /*tvCurDate.setText(String.format("%04d/%02d/%02d", cDay[index].mYear, cDay[index].mMonth+1, cDay[index].mDay));

                Intent intent = new Intent(TestFragment1.super.getActivity(), DiaryActivity.class);
                intent.putExtra(EnterActivity.KEY_YEAR, cDay[index].mYear);
                intent.putExtra(EnterActivity.KEY_MONTH, cDay[index].mMonth);
                intent.putExtra(EnterActivity.KEY_DAY, cDay[index].mDay);
                startActivity(intent);*/
                return true;
            }
        };

        btnMainBack.setOnClickListener(onClick);
        btnPrevMonth.setOnClickListener(onClick);
        btnNextMonth.setOnClickListener(onClick);
        tvCurCal.setOnClickListener(onClick);

        main_dayofweek = getResources().getStringArray(R.array.main_dayofweek);
        main_match = getResources().getStringArray(R.array.main_match);
        main_month = getResources().getStringArray(R.array.main_month);

        TextView[] tvDayofweek = new TextView[7];
        for (int i = 0; i < 7; i++) {
            tvDayofweek[i] = (TextView) findViewById(dayofweekID[i]);
            tvDayofweek[i].setText(main_dayofweek[i+(lang*7)]);
        }
        TextView tvMatch = (TextView) findViewById(R.id.main_match);
        tvMatch.setText(main_match[lang]);
    }

    public void MakeCalendarView() {
        int i,j;
        for (i = 0; i < 6; i++)
        {
            theLLWeeks[i] = new LinearLayout(getApplicationContext());
            theLLWeeks[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            theLLWeeks[i].setOrientation(LinearLayout.HORIZONTAL);
            theLLWeeks[i].setGravity(Gravity.CENTER);
            for (j = 0; j < 7; j++)
            {
                cDay[i * 7 + j] = new CDay();
                cDay[i * 7 + j].mLLDay.setId(i * 7 + j);
                cDay[i * 7 + j].mNum = i * 7 + j;
                cDay[i * 7 + j].setBackground();
                theLLWeeks[i].addView(cDay[i * 7 + j].mLLDay);
            }
            viewCalendar.addView(theLLWeeks[i]);
        }
        theDate = Calendar.getInstance();
        curYear = theYear = theDate.get(Calendar.YEAR);
        curMonth = theMonth = theDate.get(Calendar.MONTH);
        curDay = theDay = theDate.get(Calendar.DAY_OF_MONTH);
        theDayOfWeek = theDate.get(Calendar.DAY_OF_WEEK);

        MakeCalender(theYear, theMonth);
    }

    public void MakeCalender(int aYear, int aMonth) {
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.set(aYear, aMonth, 1);
        int tmpDayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK)-1;
        int tmpMaxDay = tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int i;
        int tmpYear = tmpCal.get(Calendar.YEAR);
        int tmpMonth = tmpCal.get(Calendar.MONTH);
        int tmpDay = 0;

        int tmpPrevYear = tmpYear;
        int tmpPrevMonth = tmpMonth - 1;
        if (tmpPrevMonth < 0)
        {
            tmpPrevYear--;
            tmpPrevMonth = 11;
        }
        Calendar tmpPrevCal = Calendar.getInstance();
        tmpPrevCal.set(tmpPrevYear, tmpPrevMonth, 1);
        int tmpPrevMaxDay = tmpPrevCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int tmpNextYear = tmpYear;
        int tmpNextMonth = tmpMonth + 1;
        if (tmpNextMonth > 11)
        {
            tmpNextYear++;
            tmpNextMonth = 0;
        }
        int tmpNextDay = 1;

        for (i = 0; i < 6*7; i++)
        {
            cDay[i].clearSel();
            cDay[i].setBackground();
            if (i>=tmpDayOfWeek && i<tmpMaxDay+tmpDayOfWeek)
            {
                if(i == tmpDayOfWeek)   // 이전 달
                {
                    for (int j = i - 1; j >= 0; j--) {
                        cDay[j].mYear = tmpPrevYear;
                        cDay[j].mMonth = tmpPrevMonth;
                        cDay[j].mDay = tmpPrevMaxDay;
                        cDay[j].mTvDay.setTextColor(Color.LTGRAY);
                        cDay[j].mTvDay.setText(String.format("%d", tmpPrevMaxDay--));
                        cDay[j].setBackground_Other();
                    }
                }
                // 현재 달
                tmpDay++;
                if (tmpDay == curDay && tmpMonth == curMonth && tmpYear == curYear) // curday
                {
                    cDay[i].setBackground_Cur();
                }
                if (i%7 == 0)
                    cDay[i].mTvDay.setTextColor(getResources().getColor(R.color.mildRed));
                else if (i%7 == 6)
                    cDay[i].mTvDay.setTextColor(getResources().getColor(R.color.mildBlue));
                else
                    cDay[i].mTvDay.setTextColor(Color.DKGRAY);

                cDay[i].mYear = tmpYear;
                cDay[i].mMonth = tmpMonth;
                cDay[i].mDay = tmpDay;
                cDay[i].mTvDay.setText(String.format("%d", tmpDay));
            }
            else if(i>=tmpMaxDay+tmpDayOfWeek)   // 다음 달
            {
                cDay[i].mYear = tmpNextYear;
                cDay[i].mMonth = tmpNextMonth;
                cDay[i].mDay = tmpNextDay;
                cDay[i].mTvDay.setText(String.format("%d", tmpNextDay++));
                cDay[i].mTvDay.setTextColor(Color.LTGRAY);
                cDay[i].setBackground_Other();
            }
        }
        setCurDay();
    }

    public void setCurDay() {
        switch (lang) {
            case 0:
                tvCurCal.setText(String.format("%04d년 %02d월", theYear, theMonth+1));
                break;
            case 1:
                String month = main_month[theMonth];
                tvCurCal.setText(String.format(month+", %04d", theYear));
                break;
            case 2:
            case 3:
                tvCurCal.setText(String.format("%04d年 %02d月", theYear, theMonth+1));
                break;
        }
    }

}
