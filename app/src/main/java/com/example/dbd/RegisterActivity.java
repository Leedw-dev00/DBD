package com.example.dbd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_Pw, et_Pw_Ck;
    private Button btn_Register;
    private RadioButton M;
    private RadioButton WM;
    private RadioButton Y;
    private RadioButton N;
    ImageView iv_Exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //회원가입 정보 값 받아오기
        final EditText id_Et = (EditText) findViewById(R.id.et_Id);
        final EditText password_Et = (EditText) findViewById(R.id.et_Pw);
        final EditText name_Et = (EditText) findViewById(R.id.et_Name);
        final EditText birth_Et = (EditText) findViewById(R.id.et_Birth);
        TextView message = (TextView) findViewById(R.id.message);

        et_Pw = (EditText) findViewById(R.id.et_Pw);
        et_Pw_Ck = (EditText) findViewById(R.id.et_Pw_Ck);

        //Exit버튼 클릭 시
        iv_Exit = (ImageView)findViewById(R.id.iv_Exit);
        iv_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //비밀번호 일치 여부
        et_Pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_Pw_Ck.getText().toString().equals(et_Pw.getText().toString())) {
                    message.setTextColor(Color.parseColor("#1741ff"));
                    message.setText("비밀번호가 일치합니다.");

                }else{
                    message.setTextColor(Color.parseColor("#fa1414"));
                    message.setText("비밀번호를 확인해주세요.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //비밀번호 일치 여부
        et_Pw_Ck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_Pw_Ck.getText().toString().equals(et_Pw.getText().toString())) {
                    message.setTextColor(Color.parseColor("#1741ff"));
                    message.setText("비밀번호가 일치합니다.");

                }else{
                    message.setTextColor(Color.parseColor("#fa1414"));
                    message.setText("비밀번호를 확인해주세요.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        RadioGroup rg_Sex = (RadioGroup) findViewById(R.id.rg_Sex);
        RadioGroup rg_Marriage = (RadioGroup) findViewById(R.id.rg_Marriage);

        btn_Register = (Button) findViewById(R.id.btn_Register);
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioButton rd_Sex = (RadioButton) findViewById(rg_Sex.getCheckedRadioButtonId());
                String userSex = rd_Sex.getText().toString();
                //Toast.makeText(RegisterActivity.this, sex, Toast.LENGTH_SHORT).show();

                RadioButton rd_Marriage = (RadioButton) findViewById(rg_Marriage.getCheckedRadioButtonId());
                String userMarriage = rd_Marriage.getText().toString();
                //Toast.makeText(RegisterActivity.this, marriage, Toast.LENGTH_SHORT).show();

                String userID = id_Et.getText().toString();
                String userPassword = password_Et.getText().toString();
                String userName = name_Et.getText().toString();
                String userBirth = birth_Et.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("회원 등록에 성공했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create()
                                        .show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("회원 등록에 실패했습니다.")
                                        .setNegativeButton("다시 시도", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userBirth, userSex, userMarriage, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });

    }

}