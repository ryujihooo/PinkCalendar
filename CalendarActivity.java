package com.example.termproject_8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    //위젯에 대응하는 변수
    CalendarView calView;
    TextView expectDate, endDate, fertility, ovulation;

    int period;    //생리주기

    //데이트피커에서 선택하는 날짜, 예정일, 배란일, 가임기(첫날,마지막날)
    String chooseDate, calexpectDate, calFertility, calOvulation1, calOvulation2;
    String stringStart, stringEnd;    //시작일,종료일
    String fileName;    //파일명
    String str;      //파일 입출력 시 사용하는 String

    Calendar cal = Calendar.getInstance();    //캘린더(현재 날짜로 초기화)
    //날짜 형식 (2020년 1월 1일->2020년 01월 01일로 변환시 사용)
    DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        calView = (CalendarView) findViewById(R.id.calendarView);
        expectDate = (TextView) findViewById(R.id.expectDate);
        endDate = (TextView) findViewById(R.id.endDate);
        fertility = (TextView) findViewById(R.id.fertility);
        ovulation = (TextView) findViewById(R.id.ovulation);

        //주기를 period.txt파일로부터 입력받음
        readPeriod();

        //실행 시 날짜를 선택한 적이 있으면 해당 txt파일을 읽음
        //path 지정
        File file = new File("/data/data/com.example.termproject_8/files/");

        //path에 파일이 있는지 확인
        if (file.exists()) {
            //path에 있는 파일을 파일 배열로 저장
            File[] fileList = file.listFiles();

            //저장된 파일 수만큼 이름을 담는 ArrayList 생성
            ArrayList<String> fileNames = new ArrayList<>();
            //마지막 원소(불러올 파일명)를 담는 lastElement
            String lastElement = null;

            //달력에서 저장한 파일의 제목을 읽어 배열에 저장함
            for (int i = 0; i < fileList.length; i++) {
                if (((fileList[i] != null)) &&(fileList[i].getName().contains("월"))) {
                    fileNames.add(fileList[i].getName());
                    //마지막으로 저장한 파일명을 저장
                    lastElement = fileNames.get(fileNames.size() - 1);
                }
            }
            //저장된 파일이 있다면 배열에 저장된 마지막 요소(마지막으로 저장한 날짜)를 읽음
            if (null != lastElement) {
                readText(lastElement);
            }
        }

        //캘린더뷰에서 날짜 선택 시 해당날짜의 임신가능성을 토스트메시지로 표시
        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                //캘린더뷰에서 선택한 날짜
                String selectedDate = year+"년 "+ (month+1)+"월 "+dayOfMonth+"일";
                // 비교할 날짜를 얻음
                if(calOvulation1 != null){
                    try {
                        //string->date로 형변환
                        //선택한 날짜를 저장
                        Date date = dateFormat.parse(selectedDate);
                        //위험 기간(가임기 시작일, 배란일 3일전)을 Date2,date3에 저장
                        Date date2 = dateFormat.parse(calOvulation1);
                        cal.setTime(dateFormat.parse(calOvulation1));
                        cal.add(Calendar.DATE,2);
                        Date date3 = cal.getTime();
                        //가임기 종료일을 저장
                        Date date4 = dateFormat.parse(calOvulation2);

                        //가임기(높음/보통으로 나눔)
                        if( (date.equals(date2)||date.after(date2))
                                && (date.equals(date4)||date.before(date4))){
                            //배란일5일전(가임기 시작일)~3일전:높음
                            if(date.equals(date3)||date.before(date3)){
                                //내용을 토스트메시지로 표시
                                Toast.makeText(getApplicationContext(),"임신 가능성: 높음",Toast.LENGTH_SHORT).show();
                            }
                            //나머지:보통
                            else
                                Toast.makeText(getApplicationContext(),"임신 가능성: 보통", Toast.LENGTH_SHORT).show();
                        }
                        //그외:낮음
                        else
                            Toast.makeText(getApplicationContext(), "임신 가능성: 낮음", Toast.LENGTH_SHORT).show();
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //메뉴를 inflate함
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }

    //상단메뉴 선택 시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //생리 시작일 선택 시
            case R.id.itemStartDate:
                //달력을 오늘 날짜가 선택되어있도록 초기화
                cal = Calendar.getInstance();

                //데이트피커 사용, 선택한 날짜의 연,월,일로 예정일, 가임기, 배란일 계산
                new DatePickerDialog(this, startDateSet, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).show();

                return true;

            //생리 종료일 선택 시
            case R.id.itemEndDate:
                //달력을 오늘 날짜가 선택되어있도록 초기화
                cal = Calendar.getInstance();

                //데이트피커에서 선택한 날짜를 텍스트뷰에 표시
                new DatePickerDialog(this, endDateSet, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).show();
                return true;
        }
        return false;
    }

    //데이트피커 리스너(생리 종료일을 텍스트뷰에 표시)
    DatePickerDialog.OnDateSetListener endDateSet =
            new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Date date = null;
                    try{
                        //년, 월, 일을 받아 String->date로 형변환
                        date = dateFormat.parse(String.format("%d년 %d월 %d일",year,month+1,dayOfMonth));
                    }
                    catch (ParseException e){
                        e.printStackTrace();
                    }
                    //생리 종료일을 계산함
                    calEndDate(date);
                    //종료일이 지정됨을 알림
                    Toast.makeText(getApplicationContext(),"종료일이 지정되었습니다.",Toast.LENGTH_SHORT).show();
                }
            };

    //데이트피커로 예정일, 가임기, 배란일을 설정하는 리스너
    DatePickerDialog.OnDateSetListener startDateSet =
            new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Date date = null;
                    try {
                        //년, 월, 일을 받아 String->date로 형변환
                        date = dateFormat.parse(String.format("%d년 %d월 %d일",year,month+1,dayOfMonth));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //예정일, 가임기, 배란일을 계산함
                    calDate(period, date);
                    //시작일이 지정됨을 알림
                    Toast.makeText(getApplicationContext(),"시작일이 지정되었습니다.",Toast.LENGTH_SHORT).show();
                }
            };

    //예정일, 가임기, 배란일 계산 메소드
    public void calDate(int period, Date date){

        cal.setTime(date);
        //달력에서 선택된 날짜를 저장
        chooseDate = dateFormat.format(date);
        //선택한 날짜를 시작일로 저장
        stringStart = chooseDate;

        //선택한 날짜(시작일)를 파일명으로 받음
        fileName = chooseDate+".txt";

        //예정일 계산(선택한 날짜+주기)
        cal.add(Calendar.DATE, period);
        calexpectDate = dateFormat.format(cal.getTime());
        //계산한 날짜를 textView에 표시
        expectDate.setText(String.format("생리 예정일: "+calexpectDate));

        //배란일 계산(예정일-14일)
        cal.add(Calendar.DATE, -14);
        calFertility = dateFormat.format(cal.getTime());
        //계산한 날짜를 textView에 표시
        ovulation.setText(String.format("배란일: "+calFertility));

        //가임기 계산(배란일)
        //배란일의 전 5일,후 3일(배란일 포함)
        cal.add(Calendar.DATE, -5);
        calOvulation1 = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, 8);
        calOvulation2 = dateFormat.format(cal.getTime());
        //계산한 날짜를 textView에 표시
        fertility.setText(String.format("가임기: "+calOvulation1+" ~ \n\t\t\t"+calOvulation2));
    }

    //생리 종료일 계산 메소드
    private void calEndDate(Date date) {
        //선택한 날짜를 date형으로 변환
        cal.setTime(date);
        chooseDate = dateFormat.format(date);

        //datePicker에서 선택한 날짜를 textView에 표시
        endDate.setText(String.format("생리 종료일: "+chooseDate));
        stringEnd = chooseDate;

        //시작일, 종료일이 모두 지정되면 txt파일로 날짜를 저장
        // 시작날짜 ~ 종료날짜 형태
        if( !(stringStart==null)){
            try {
                //파일명(시작일.txt)에 해당하는 파일을 쓰기 모드로 연다
                FileOutputStream outFs = openFileOutput(fileName, Context.MODE_PRIVATE);
                //str에 파일 내용(시작일~종료일)을 저장
                str = stringStart+" ~ "+stringEnd;
                //str을 파일에 byte[]형으로 쓰고 파일 닫음
                outFs.write(str.getBytes());
                outFs.close();
            }
            catch (IOException e) {
            }
        }
    }

    //선택한 날짜(텍스트파일)를 읽는 메소드
    public void readText(String fileName){
        //입력 파일 스트림 inFs, date형으로 변환한 날짜를 저장할 date
        FileInputStream inFs;
        Date date;
        try{
            //전달받은 파일명의 파일을 읽음
            inFs = openFileInput(fileName);
            //byte[]형 변수 txt에 입력 파일에서 데이터를 읽음
            byte[] txt = new byte[50];
            inFs.read(txt);
            inFs.close();   //파일을 닫음
            str = (new String(txt)).trim();
            //파일 내용(시작일 ~ 종료일)을 분리함
            String[] result = str.split(" ~ ");

            //String->date로 형변환
            date = dateFormat.parse(result[0]);
            //result[0]은 시작일->예정일, 배란일, 가임기 계산
            calDate(period, date);

            //String->date로 형변환
            date = dateFormat.parse(result[1]);
            //result[1]은 종료일->종료일 계산
            calEndDate(date);
        }
        //파일이 없는 경우(시작일, 종료일을 선택한 적이 없음)
        catch (IOException | ParseException e){
            Toast.makeText(getApplicationContext(),"생리 시작일, 종료일을 선택해주세요.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //주기 파일을 읽는 메소드
    public void readPeriod(){
        //입력 파일 스트림 inFs
        FileInputStream inFs;
        try{
            //period.txt를 읽음
            inFs = openFileInput("period.txt");
            //byte[]형 변수 txt에 입력 파일에서 데이터를 읽음
            byte[] txt = new byte[10];
            inFs.read(txt);
            inFs.close();   //파일을 닫음
            //integer형으로 변환해 주기로 저장함
            period = Integer.parseInt((new String(txt)).trim());
        }
        //파일이 없는 경우(주기파일은 미리 저장하므로 무조건 존재함)
        catch (IOException e){
        }
    }
}
