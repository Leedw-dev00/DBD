package com.example.dbd;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.aqoong.lib.slidephotoviewer.MaxSizeException;
import com.aqoong.lib.slidephotoviewer.SlidePhotoViewer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lakue.lakuepopupactivity.PopupActivity;
import com.lakue.lakuepopupactivity.PopupResult;
import com.lakue.lakuepopupactivity.PopupType;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    TextView id;
    SlidePhotoViewer mSlideViewr;
    ImageView item_List;
    ImageView notice, map;


    //Bluetooth
    TextView mTvBluetoothStatus;
    TextView mTvReceiveData;
    TextView mTvSendData;
    Button mBtnBluetoothOn;
    Button mBtnBluetoothOff;
    Button mBtnConnect;
    Button mBtnSendData;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;
    Handler mBluetoothHandler;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    ConnectedBluetoothThread mThreadConnectedBluetooth;

    //Bluetooth
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Bluetooth start------------------------------------------
        mTvBluetoothStatus = (TextView)findViewById(R.id.tvBluetoothStatus);
        mTvReceiveData = (TextView)findViewById(R.id.tvReceiveData);
        mTvSendData =  (EditText) findViewById(R.id.tvSendData);
        mBtnBluetoothOn = (Button)findViewById(R.id.btnBluetoothOn);
        mBtnBluetoothOff = (Button)findViewById(R.id.btnBluetoothOff);
        mBtnConnect = (Button)findViewById(R.id.btnConnect);
        mBtnSendData = (Button)findViewById(R.id.btnSendData);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        TextView item = (TextView)findViewById(R.id.item);

/*
        mBtnBluetoothOn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
            }
        });
        mBtnBluetoothOff.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOff();
            }
        });
 */

        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPairedDevices();
            }
        });
        mBtnSendData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth != null) {
                    mThreadConnectedBluetooth.write(mTvSendData.getText().toString());
                    mTvSendData.setText("");
                }
            }
        });

        mBluetoothHandler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    item.setText(readMessage);
                    //Toast.makeText(HomeActivity.this, readMessage, Toast.LENGTH_SHORT).show();

                    //스캔 된 품목 명
                    String item_Name = readMessage.substring(0,2);//아두이노에서 값이 넘어올 때 뒤에 띄어쓰기가 함께 넘어와서 앞 두 자리만 자름
                    String no = readMessage.substring(1,2);
                    //사용자 ID 불러오기
                    Intent intent = getIntent();
                    String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                    String id = "";
                    //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(item_Name.equals("+1")){
                        //스캔 된 값을 MY-SQL SHOP DB에 저장
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                        alertBuilder.setMessage("내일 아침 딸기 바나나 쉐이크 어떠세요? 식품코너A 딸기 5000원")
                                                .setNegativeButton("확인", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ShopRequest shopRequest = new ShopRequest(id, userID, "+1", responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(shopRequest);


                    }else if(item_Name.equals("+2")){
                        //스캔 된 값을 MY-SQL SHOP DB에 저장
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                        alertBuilder.setMessage("배추와 고춧가루를 함께 구매하면 30%를 할인")
                                                .setNegativeButton("확인", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ShopRequest shopRequest = new ShopRequest(id, userID, "+2", responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(shopRequest);

                    }else if(item_Name.equals("+3")){
                        //스캔 된 값을 MY-SQL SHOP DB에 저장
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                        alertBuilder.setMessage("골뱅이 소면으로 오늘 혼술안주를 해결하세요! 식품코너C")
                                                .setNegativeButton("확인", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ShopRequest shopRequest = new ShopRequest(id, userID, "+3", responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(shopRequest);

                    }else if(item_Name.equals("+4")){
                        //스캔 된 값을 MY-SQL SHOP DB에 저장
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                        alertBuilder.setMessage("맥주하면 치킨이죠! 식품코너D에 가셔서 치킨 할인 받으세요")
                                                .setNegativeButton("확인", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ShopRequest shopRequest = new ShopRequest(id, userID, "+4", responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(shopRequest);

                    }else if(item_Name.equals("+5")){
                        //스캔 된 값을 MY-SQL SHOP DB에 저장
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                        alertBuilder.setMessage("요즘 백종원의 인기 레시피 라면과 만두를 함께! 식품코너D")
                                                .setNegativeButton("확인", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ShopRequest shopRequest = new ShopRequest(id, userID, "+5", responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(shopRequest);

                    }else if(item_Name.equals("+6")){
                        //스캔 된 값을 MY-SQL SHOP DB에 저장
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                        alertBuilder.setMessage("아이의 균형있는 성장을 위해 '아기치즈'를 추천합니다")
                                                .setNegativeButton("확인", null)
                                                .create()
                                                .show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ShopRequest shopRequest = new ShopRequest(id, userID, "+6", responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(shopRequest);

                    //-no값이 넘어오면 디비에서 +no을 삭제
                    }else if(item_Name.equals("-1")){
                        //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DeleteRequest deleteRequest = new DeleteRequest("+1", userID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(deleteRequest);
                    }else if(item_Name.equals("-2")){
                        //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DeleteRequest deleteRequest = new DeleteRequest("+2", userID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(deleteRequest);
                    }else if(item_Name.equals("-3")){
                        //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DeleteRequest deleteRequest = new DeleteRequest("+3", userID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(deleteRequest);
                    }else if(item_Name.equals("-4")){
                        //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DeleteRequest deleteRequest = new DeleteRequest("+4", userID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(deleteRequest);
                    }else if(item_Name.equals("-5")){
                        //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DeleteRequest deleteRequest = new DeleteRequest("+5", userID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(deleteRequest);
                    }else if(item_Name.equals("-6")){
                        //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        Toast.makeText(HomeActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DeleteRequest deleteRequest = new DeleteRequest("+6", userID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(deleteRequest);
                    }
                }
            }
        };

        //Bluetooth end------------------------------------------



        //Pop-up 광고
        Intent intent2 = new Intent(getBaseContext(), PopupActivity.class);
        intent2.putExtra("type", PopupType.IMAGE);
        intent2.putExtra("title", "https://akftmalffk.cafe24.com/notice.png"); //Image
        intent2.putExtra("buttonLeft", "다시 보지 않기");
        intent2.putExtra("buttonRight", "닫기");
        startActivityForResult(intent2, 4);


        //SlideViewer
        mSlideViewr = findViewById(R.id.slideViewer);
        mSlideViewr.setSidePreview(true);
        try{

            //하선정 멸치액젓 장바구니 추가
            mSlideViewr.addResource(R.drawable.slide5, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Slide5", Toast.LENGTH_SHORT).show();

                    //스캔 된 품목 명
                    String item_Name = "hs";

                    //사용자 ID 불러오기
                    Intent intent = getIntent();
                    String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                    String id = "";
                    //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                    //스캔 된 값을 MY-SQL SHOP DB에 저장
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ShopRequest shopRequest = new ShopRequest(id, userID, item_Name, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                    queue.add(shopRequest);

                }
            });

            //목우촌 삼겹살 장바구니 추가
            mSlideViewr.addResource(R.drawable.slide4, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Slide4", Toast.LENGTH_SHORT).show();

                    //스캔 된 품목 명
                    String item_Name = "ms";

                    //사용자 ID 불러오기
                    Intent intent = getIntent();
                    String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                    String id = "";
                    //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                    //스캔 된 값을 MY-SQL SHOP DB에 저장
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ShopRequest shopRequest = new ShopRequest(id, userID, item_Name, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                    queue.add(shopRequest);

                }
            });

            //논산 딸기 장바구니 추가
            mSlideViewr.addResource(R.drawable.slide3, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Slide3", Toast.LENGTH_SHORT).show();

                    //스캔 된 품목 명
                    String item_Name = "nt";

                    //사용자 ID 불러오기
                    Intent intent = getIntent();
                    String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                    String id = "";
                    //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                    //스캔 된 값을 MY-SQL SHOP DB에 저장
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ShopRequest shopRequest = new ShopRequest(id, userID, item_Name, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                    queue.add(shopRequest);
                }
            });

            //인기과자세트 장바구니 추가
            mSlideViewr.addResource(R.drawable.slide2, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Slide2", Toast.LENGTH_SHORT).show();

                    //스캔 된 품목 명
                    String item_Name = "ks";

                    //사용자 ID 불러오기
                    Intent intent = getIntent();
                    String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                    String id = "";
                    //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                    //스캔 된 값을 MY-SQL SHOP DB에 저장
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ShopRequest shopRequest = new ShopRequest(id, userID, item_Name, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                    queue.add(shopRequest);
                }
            });

            //크리넥스 휴지 장바구니 추가
            mSlideViewr.addResource(R.drawable.slide1, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Slide1", Toast.LENGTH_SHORT).show();

                    //스캔 된 품목 명
                    String item_Name = "ts";

                    //사용자 ID 불러오기
                    Intent intent = getIntent();
                    String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                    String id = "";
                    //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                    //스캔 된 값을 MY-SQL SHOP DB에 저장
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ShopRequest shopRequest = new ShopRequest(id, userID, item_Name, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                    queue.add(shopRequest);
                }
            });


        } catch (MaxSizeException e) {
            e.printStackTrace();
        }


        //사용자 id

        id = (TextView) findViewById(R.id.id);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        id.setText(userId);


        //장바구니 버튼 클릭 시
        item_List = (ImageView)findViewById(R.id.item_List);
        item_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask_shopList().execute();
            }
        });

        //Map 버튼 클릭 시
        map = (ImageView)findViewById(R.id.iv_Map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });


        //Notice 버튼 클릭 시
        notice = (ImageView)findViewById(R.id.iv_Notice);
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NoticeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });


        //QR Scan 버튼 클릭 시
        Button btn_QR = (Button)findViewById(R.id.btn_QR);
        btn_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(HomeActivity.this).initiateScan();
            }
        });
    }


    //Pop-up광고 설정
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //데이터 받기
            if(requestCode == 4){
                PopupResult result = (PopupResult) data.getSerializableExtra("result");
                if(result == PopupResult.LEFT){
                    // 작성 코드
                    //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                } else if(result == PopupResult.RIGHT){
                    // 작성 코드
                    //Intent intent = new Intent(getApplicationContext(), FoodActivity.class);
                    //startActivity(intent);


                } else if(result == PopupResult.IMAGE){
                    // 작성 코드
                    //Toast.makeText(this, "IMAGE", Toast.LENGTH_SHORT).show();

                }
            }
        }


        //QR Scan 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage("제품 인식에 실패하였습니다.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // todo

                //스캔 된 품목 명
                String item_Name = result.getContents();
                String id = "";

                //사용자 ID 불러오기
                Intent intent = getIntent();
                String userID = intent.getStringExtra("userId");//id.getText().toString(); //intent2.getStringExtra("userID");
                //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();


                //스캔 된 값을 MY-SQL SHOP DB에 저장
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(HomeActivity.this, "장바구니에 선택한 상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                String item_Result = result.getContents();
                                TextView item = (TextView)findViewById(R.id.item);
                                item.setText(item_Result);
                                ImageView img_Rc = (ImageView)findViewById(R.id.img_Recomendation);


                                if(item_Result.equals("+7")){
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                    alertBuilder.setMessage("우유와 함께 즐길 수 있는 시리얼을 추천합니다.")
                                            .setNegativeButton("확인", null)
                                            .create()
                                            .show();

                                }else if(item_Result.equals("+8")){
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                    alertBuilder.setMessage("시리얼에 우유가 부족하지 않나요? 우유는 식품코너E에 있습니다.")
                                            .setNegativeButton("확인", null)
                                            .create()
                                            .show();

                                }else if(item_Result.equals("+9")){
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                    alertBuilder.setMessage("토스트에 발라 먹는 맛있는 딸기 잼을 추천합니다.")
                                            .setNegativeButton("확인", null)
                                            .create()
                                            .show();

                                }else if(item_Result.equals("+0")){
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                                    alertBuilder.setMessage("열심히 운동을 하는 당신, 고단백질 쉐이크로 근손실을 방지해보세요")
                                            .setNegativeButton("확인", null)
                                            .create()
                                            .show();
                                }

                            }else{
                                Toast.makeText(HomeActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ShopRequest shopRequest = new ShopRequest(id, userID, item_Name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                queue.add(shopRequest);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("활성화");
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("비활성화");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    //장바구니로 이동시 장바구니 품목 불러오기
    class BackgroundTask_shopList extends AsyncTask<Void, Void, String>{
        String target;

        @Override
        protected void onPreExecute(){target = "https://akftmalffk.cafe24.com/shoplist.php";}

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(target);  //해당 URL로 접속
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));  //하나씩 읽어올 수 있도록
                String temp;  //매 열마다 입력을 받도록 한다
                StringBuilder stringBuilder = new StringBuilder();  //각 결과값을 매 열마다 담겨 stringBuilder안에 넣음
                while((temp = bufferedReader.readLine()) != null)  //모든 열을 읽어옴
                {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

        @Override
        public  void onPostExecute(String result){
            String userId = id.getText().toString();
            Intent intent = new Intent(HomeActivity.this, ShoplistActivity.class);
            intent.putExtra("shoplistList", result);
            intent.putExtra("userId", userId);
            HomeActivity.this.startActivity(intent);
        }

    }


    //뒤로가기 버튼 클릭 시 종료 여부
    @Override
    public void onBackPressed(){
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(HomeActivity.this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
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



/*
    void bluetoothOn() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                mTvBluetoothStatus.setText("활성화");
            }
            else {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }
    void bluetoothOff() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            mTvBluetoothStatus.setText("비활성화");
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
*/

    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }


}