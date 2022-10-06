package com.example.termproject_8;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;


@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        TabHost.TabSpec spec;
        Intent intent;

        super.onCreate(savedInstanceState);
        //반드시 setContentView()가 getTabHost()보다 먼저 호출되어야 함
        setContentView(R.layout.activity_main);

        //탭호스트 생성
        TabHost  tabHost = getTabHost();
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        //달력 탭
        intent = new Intent().setClass(this, CalendarActivity.class);
        spec = tabHost.newTabSpec("tab1").setIndicator("달력").setContent(intent);
        tabHost.addTab(spec);

        //생리기록 탭
        intent = new Intent().setClass(this, RecordActivity.class);
        spec = tabHost.newTabSpec("tab2").setIndicator("생리" + "기록").setContent(intent);
        tabHost.addTab(spec);

        //일기장 탭
        intent = new Intent().setClass(this, DiaryActivity.class);
        spec = tabHost.newTabSpec("tab3").setIndicator("일기장").setContent(intent);
        tabHost.addTab(spec);

        //산부인과 탭
        intent = new Intent().setClass(this, HospitalActivity.class);
        spec = tabHost.newTabSpec("tab4").setIndicator("산부인과").setContent(intent);
        tabHost.addTab(spec);

        // 앱이 실행될때 초기에 선택되어질 탭(달력 탭)
        tabHost.setCurrentTab(0);
    }
}