package com.example.dbd;

import android.widget.BaseAdapter;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DelButtonRequest extends StringRequest {

    final static private String URL = "https://akftmalffk.cafe24.com/delBtn.php";
    private Map<String, String> parameters;

    public DelButtonRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams(){ return parameters; }

}
