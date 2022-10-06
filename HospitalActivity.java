package com.example.termproject_8;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HospitalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital);

        //각 이미지버튼은 배열로 생성해 대입
        ImageButton[] btn_call = {(ImageButton) findViewById(R.id.btn1_call), (ImageButton) findViewById(R.id.btn2_call), findViewById(R.id.btn3_call),
                findViewById(R.id.btn4_call), findViewById(R.id.btn5_call), findViewById(R.id.btn6_call),
                findViewById(R.id.btn7_call), findViewById(R.id.btn8_call), findViewById(R.id.btn9_call), findViewById(R.id.btn10_call)};

        ImageButton[] btn_home = {(ImageButton) findViewById(R.id.btn1_home), (ImageButton) findViewById(R.id.btn2_home), (ImageButton) findViewById(R.id.btn3_home),
                (ImageButton) findViewById(R.id.btn4_home), (ImageButton) findViewById(R.id.btn5_home), (ImageButton) findViewById(R.id.btn6_home),
                (ImageButton) findViewById(R.id.btn7_home), (ImageButton) findViewById(R.id.btn8_home),(ImageButton) findViewById(R.id.btn9_home),(ImageButton) findViewById(R.id.btn10_home)};

        //전화번호, 홈페이지 주소도 배열로 사용
        final String[] call = {"tel:/031-431-1134", "tel:/02-530-8500", "tel:/02-461-5677", "tel:/02-534-1131",
                "tel:/02-936-0400", "tel:/02-2066-3588", "tel:/02-3453-5712","tel:/032-343-2773",
                "tel:/02-514-1454", "tel:/031-915-9243"};
        final String[] uri = {"https://yj-clinic.co.kr", "http://applewoman.co.kr/", "http://jennyclinic.com",
                "http://alynn.co.kr/", "http://avenueclinic.co.kr/", "http://www.is-mom.co.kr/",
                "http://www.sinsoeclinic.com","http://pysobgy.com", "http://flocewoman.com","http://ladydoctor.kr/"};

        //(전화기 이미지를 눌렀을 때)전화 연결
        for(int i=0;i<call.length;i++){
            //인덱스는 한 번 설정시 값이 바뀌지 않도록 num 사용
            final int num = i;
            btn_call[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mintent = new Intent(Intent.ACTION_VIEW, Uri.parse(call[num]));
                    startActivity(mintent);
                }
            });
        }

        //(홈페이지 이미지를 눌렀을 때)산부인과 홈페이지로 이동
        for(int i=0;i<uri.length;i++){
            //인덱스는 한 번 설정시 값이 바뀌지 않도록 num 사용
            final int num = i;
            btn_home[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mintent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri[num]));
                    startActivity(mintent);
                }
            });
        }
    }
}
