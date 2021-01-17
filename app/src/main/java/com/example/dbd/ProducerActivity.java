package com.example.dbd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ProducerActivity extends AppCompatActivity {

    private ListView listView;
    private ProducerListAdapter adapter;
    private List<Producer> producerList;
    private Button btn_Search;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer);

        Intent intent = getIntent();

        listView = (ListView) findViewById(R.id.listView);
        producerList = new ArrayList<Producer>();
        adapter = new ProducerListAdapter(getApplicationContext(), producerList);
        listView.setAdapter(adapter);


        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("producerList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int i = 0;
            String item_Code, item_Name, count, warehouse_Code;
            while(i < jsonArray.length()){
                JSONObject object = jsonArray.getJSONObject(i);
                item_Code = object.getString("item_Code");
                item_Name = object.getString("item_Name");
                count = object.getString("warehouse_Code");
                warehouse_Code = object.getString("count");
                Producer producer = new Producer(item_Code, item_Name, count, warehouse_Code);
                producerList.add(producer);

                i++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //뒤로가기 버튼 클릭 시 종료 여부
    @Override
    public void onBackPressed(){
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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