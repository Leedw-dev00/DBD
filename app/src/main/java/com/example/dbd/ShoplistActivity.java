package com.example.dbd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoplistActivity extends AppCompatActivity {

    private ListView listView;
    private ShoplistListAdapter adapter;
    private List<Shoplist> shoplistList;
    private List<Shoplist> saveList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        //Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();

        listView = (ListView)findViewById(R.id.listView);
        shoplistList = new ArrayList<Shoplist>();
        saveList = new ArrayList<Shoplist>();
        adapter = new ShoplistListAdapter(getApplicationContext(), shoplistList);
        listView.setAdapter(adapter);
        TextView recommendation = (TextView)findViewById(R.id.recommendation);


        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("shoplistList"));
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String userID, item_Name, price, store_Code;
            while(count < jsonArray.length()){
                JSONObject object = jsonArray.getJSONObject(count);
                userID = object.getString("userID");
                item_Name = object.getString("item_Name");
                price = object.getString("price");
                store_Code = object.getString("store_Code");
                Shoplist shoplist = new Shoplist(userID, item_Name, price, store_Code);
                shoplistList.add(shoplist);
                saveList.add(shoplist);

                if(count > 1){
                    if(userId.equals("customer1")){
                        recommendation.setText("육아 생활이 생각보다 많이 힘들죠? 이럴 때일수록 건강한 부모가 되기 위해 영양제 챙겨드세요! **제약 비타민C는 생활건강 코너에 있습니다. 지금 바로 장바구니에 담아보세요.");
                    }else if(userId.equals("customer2")){
                        recommendation.setText("바쁜 일상에 혹시 간단한 아침식사를 원하시나요? 그렇다면 지금 바로 식품코너G에 가셔서 '영양 식빵'을 구매해보세요. 다양한 재료와 영양소로 하루 한 끼 식사를 책임질 수 있습니다.");
                    }
                }else{
                    recommendation.setText("");
                }
                count++;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //아이디 별 장바구니 내역 조회
        searchID(userId);


        Button btn_pay = (Button)findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveList.size() > 0){
                    //결제 페이지로 이동
                    Intent intent = new Intent(ShoplistActivity.this, PayActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(ShoplistActivity.this, "장바구니가 비었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btn_Del = (Button)findViewById(R.id.btn_Del);
        btn_Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //스캔 된 값을 MY-SQL SHOP DB에서 삭제
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(ShoplistActivity.this, "장바구니에서 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ShoplistActivity.this, HomeActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(ShoplistActivity.this, "선택한 상품이 없습니다. 다시 한번 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                DelButtonRequest delButtonRequest = new DelButtonRequest(userId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ShoplistActivity.this);
                queue.add(delButtonRequest);
            }
        });


    }


    //아이디별 장바구니 내역 조회
    public void searchID(String search){
        shoplistList.clear();
        for(int i = 0; i < saveList.size(); i++){
            if(saveList.get(i).getUserID().contains(search)){
                shoplistList.add(saveList.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }


}