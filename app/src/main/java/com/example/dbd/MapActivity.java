package com.example.dbd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lakue.lakuepopupactivity.PopupActivity;
import com.lakue.lakuepopupactivity.PopupGravity;
import com.lakue.lakuepopupactivity.PopupType;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //popup
        Intent intent2 = new Intent(getBaseContext(), PopupActivity.class);
        intent2.putExtra("type", PopupType.NORMAL);
        intent2.putExtra("gravity", PopupGravity.CENTER);
        intent2.putExtra("title", "안내");
        intent2.putExtra("content", "식품코너A 근처입니다. 바나나 특가 세일 할인쿠폰을 챙겨가세요. 바나나 1000원 할인 쿠폰을 받으시겠습니까?");
        intent2.putExtra("buttonCenter", "받기");
        startActivityForResult(intent2, 1);




        ImageView iv_Notice = (ImageView) findViewById(R.id.iv_Notice);
        ImageView iv_Home = (ImageView) findViewById(R.id.iv_Home);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();

        iv_Notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, NoticeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

        iv_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

    }

    //뒤로가기 버튼 클릭 시 종료 여부
    @Override
    public void onBackPressed(){
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(MapActivity.this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }


}