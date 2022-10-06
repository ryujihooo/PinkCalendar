package com.example.termproject_8;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RecordActivity  extends AppCompatActivity {

    int period, term = 0;       //평균 주기, 생리기간
    String str;     //파일 입출력 시 사용하는 String
    //평균 기간 계산을 위해 생리기간을 저장하는 ArrayList
    ArrayList<Integer> days = new ArrayList<>();
    //위젯에 대응하는 변수
    TextView tvPeriod, tvTerm;

    //날짜 형식 (2020년 1월 1일->2020년 01월 01일로 변환시 사용)
    DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        //record.xml의 위젯을 변수에 대입
        tvPeriod = (TextView)findViewById(R.id.tvPeriod);
        tvTerm = (TextView)findViewById(R.id.tvTerm);
        //최근 생리 기간은 배열로 생성해 대입
        TextView[] terms = {(TextView)findViewById(R.id.term1), (TextView)findViewById(R.id.term2),
                            (TextView)findViewById(R.id.term3), (TextView)findViewById(R.id.term4),
                            (TextView)findViewById(R.id.term5)};

        //주기를 period.txt파일로부터 입력받음
        readPeriod();

        //생리 시작일, 종료일을 선택한 적이 없으면 생리기간은 0일로 표시함
        tvTerm.setText("생리기간\n" + term + "일");

        //실행 시 날짜를 선택한 적이 있으면 해당 txt파일을 읽음
        //path 지정
        File file = new File("/data/data/com.example.termproject_8/files/");
        //path에 파일이 있는지 확인
        if (file.exists()) {
            //path에 있는 파일을 파일 배열로 return
            File[] fileList = file.listFiles();

            //저장된 파일 수만큼 이름을 담는 fileNames 생성
            ArrayList<String> fileNames = new ArrayList<>();
            //저장된 파일의 내용을 담는 fileContents 생성
            ArrayList<String> fileContents = new ArrayList<>();

            //주기, 일기 파일을 제외한 파일의 제목, 내용을 읽어 배열에 저장
            for (int i = 0; i < fileList.length; i++) {
                if (((fileList[i] != null)) && (fileList[i].getName().contains("월"))) {
                    //제목, 내용 저장
                    fileNames.add(fileList[i].getName());
                    fileContents.add(readText(fileList[i].getName()));
                }
            }

            //최신순으로 표시하기 위해 저장된 순서를 역순으로 만듦
            Collections.reverse(fileContents);

            //저장된 파일이 있다면 파일의 내용을 텍스트뷰에 표시함
            if (!(fileNames.isEmpty())) {
                //저장된 파일이 5개 이상이면
                if((fileContents.size() > terms.length) ||
                        (fileContents.size() > terms.length)){
                    for(int i=0;i<terms.length;i++){
                        //텍스트뷰에 텍스트뷰 개수만큼 파일내용을 저장
                        terms[i].setText(fileContents.get(i));
                    }
                }
                //저장된 파일이 5개 이하면
                else{
                    for(int i=0;i<fileContents.size();i++){
                        //텍스트뷰에 저장된 파일개수만큼 파일내용을 저장
                        terms[i].setText(fileContents.get(i));
                    }
                }
                //평균 기간의 결과값
                int result = 0;
                //평균기간을 계산해 텍스트뷰에 표시함
                for(Integer i : days){
                    //days에 저장된 날짜를 result에 누적해서 더함
                    result += i;
                }
                //더한 값을 생리기간을 저장한 횟수로 나눔
                result /= days.size();
                tvTerm.setText("생리기간\n" + result + "일");
            }
        }
    }


    //선택한 날짜(텍스트파일)를 읽는 메소드
    public String readText(String fileName){
        //입력 파일 스트림 inFs,
        FileInputStream inFs;
        Date startDate, endDate;    //시작일, 종료일(Date형)
        try{
            //전달받은 파일명(시작일.txt)의 파일을 읽음
            inFs = openFileInput(fileName);
            //byte[]형 변수 txt에 입력 파일에서 데이터를 읽음
            byte[] txt = new byte[50];
            inFs.read(txt);
            inFs.close();       //파일을 닫음
            str = (new String(txt)).trim();
            //파일 내용(시작일 ~ 종료일)을 분리함
            String[] result = str.split(" ~ ");

            //String->date로 형변환. result[0]은 시작일
            startDate = dateFormat.parse(result[0]);

            //String->date로 형변환. result[1]은 종료일
            endDate = dateFormat.parse(result[1]);

            //생리기간을 계산
            calTerm(startDate, endDate);
        }
        //파일이 없는 경우(생리기간을 지정한 적이 없음)
        catch (IOException | ParseException e){
            Toast.makeText(getApplicationContext(),"생리 시작일, 종료일을 선택해주세요.",
                    Toast.LENGTH_SHORT).show();
        }
        return str; //읽은 파일의 내용(시작일~종료일)을 반환
    }

   //두 날짜의 차를 구하는 메소드
    public void calTerm(Date startDate, Date endDate){
        //두날짜 차 = 종료일-시작일
        long diffSec = endDate.getTime()-startDate.getTime();
        //millisec->일로 변경
        long diffDays = diffSec/(24*60*60*1000);
        //생리기간은 시작일, 종료일을 모두 포함하므로 +1일 함
        days.add((int)diffDays+1);
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
            inFs.close();       //파일을 닫음
            //파일내용을 integer형으로 변환해 주기로 저장함
            period = Integer.parseInt((new String(txt)).trim());
            //텍스트뷰에 저장한 주기를 표시
            tvPeriod.setText("생리주기\n" + period + "일");
        }
        //파일이 없는 경우(주기파일은 미리 저장하므로 무조건 존재함)
        catch (IOException e){
        }
    }
}
