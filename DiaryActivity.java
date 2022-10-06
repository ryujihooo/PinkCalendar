package com.example.termproject_8;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity {
    DatePicker dp;
    EditText edtDiary;
    Button btnSave, btnDelete;
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);

        //변수에 diary.xml의 위젯을 대입
        dp = (DatePicker) findViewById(R.id.datePicker1);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnSave = (Button) findViewById(R.id.btnSave);

        //현재의 연, 월, 일 구하기
        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);

        //데이터피커를 init() 메소드로 초기화
        dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //파일 이름 지정(연_월_일 일기.txt)
                fileName = Integer.toString(year) + "_" + Integer.toString(monthOfYear + 1) + "_"
                        + Integer.toString(dayOfMonth) + " 일기" + ".txt";

                //현재 날짜의 일기 파일을 읽음
                String str = readDiary(fileName);

                //읽어온 일기 내용일 에디트텍스트에 출력
                edtDiary.setText(str);
                btnSave.setEnabled(true);
            }
        });

        //저장하기 버튼을 눌렀을 때 이벤트 처리
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream outFs = openFileOutput(fileName, Context.MODE_PRIVATE);
                    String str = edtDiary.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(getApplicationContext(), fileName + "이 저장되었습니다", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {

                }
            }
        });

    }

    //파일 이름을(연_월_일 일기.txt) 파라미터로 받음
    String readDiary(String fName){

        //읽어온 일기를 저장할 문자열 변수와 입력 파일 스트림 변수를 선언
        String diaryStr = null;
        FileInputStream inFs;

        //
        try{
            //일기 파일을 열어 입력 파일 스트림에 저장
            inFs = openFileInput(fName);

            //byte[] 형 변수를 선언
            byte[] txt = new byte[500];

            //파일 내용을 txt에 읽어들임
            inFs.read(txt);

            //파일을 닫음
            inFs.close();

            //읽어온 txt를 문자열로 변경한 후 trim() 메소드로 앞뒤의 공백을 제거
            diaryStr = (new String(txt)).trim();
            btnSave.setText("수정하기");

        }catch (IOException e){
            edtDiary.setHint("일기 없음");  //파일이 없어 오류가 발생하면 일기 없음이 나타나게 함
            btnSave.setText("새로 저장");       //일기가 없을 시 새로 저장으로 변경
        }
        //일기 파일이 있다면 일기의 내용이 반환, 일기 파일이 없다면 null값이 반환
        return diaryStr;
    }
}
