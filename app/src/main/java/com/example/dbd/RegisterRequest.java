package com.example.dbd;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    final static private String URL = "https://akftmalffk.cafe24.com/Register_dbd.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userName, String userBirth, String userSex, String userMarriage, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("userBirth", userBirth);
        parameters.put("userSex", userSex);
        parameters.put("userMarriage", userMarriage);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
