package com.example.dbd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //id, pw 값 갖고오기
        final EditText user_ID = (EditText) findViewById(R.id.id);
        final EditText user_Password = (EditText) findViewById(R.id.password);


        //sign in 클릭 시
        //Home 화면으로 이동(userID 값을 포함)
        final Button btn_Login = (Button) findViewById(R.id.btn_Login);

        btn_Login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String userID = user_ID.getText().toString();
                final String userPassword = user_Password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response){
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                String userID = jsonResponse.getString("userID");
                                String userPassword = jsonResponse.getString("userPassword");
                                if(userID.equals("admin")){
                                    new BackgroundTask_Admin().execute();
                                }else if(userID.equals("producer")){
                                    new BackgroundTask_warehouse().execute();
                                }else{
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);  //homeActivity#################################
                                    intent.putExtra("userId", userID);
                                    MainActivity.this.startActivity(intent);
                                    finish();
                                }

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("로그인에 실패하였습니다. 아이디 또는 비밀번호를 확인해주세요")
                                        .setNegativeButton("다시 시도", null)
                                        .create()
                                        .show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });


        //Register 클릭 시
        //Register 화면으로 이동
        TextView txt_Register = (TextView) findViewById(R.id.txt_Register);
        txt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    class BackgroundTask_warehouse extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute(){target = "https://akftmalffk.cafe24.com/warehouse.php";}

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
        public void onProgressUpdate(Void... values){super.onProgressUpdate(values);}

        @Override
        public void onPostExecute(String result){
            Intent intent = new Intent(MainActivity.this, ProducerActivity.class);  //ProducerActivity#################################
            intent.putExtra("producerList", result);
            MainActivity.this.startActivity(intent);
        }

    }


    class BackgroundTask_Admin extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute(){target = "https://akftmalffk.cafe24.com/admin.php";}

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
        public void onProgressUpdate(Void... values){super.onProgressUpdate(values);}

        @Override
        public void onPostExecute(String result){
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);  //ProducerActivity#################################
            intent.putExtra("adminList", result);
            MainActivity.this.startActivity(intent);
        }

    }

}