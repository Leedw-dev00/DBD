package com.example.dbd;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ShopRequest extends StringRequest {

    final static private String URL = "https://akftmalffk.cafe24.com/shop.php";
    private Map<String, String> parameters;

    public ShopRequest(String id, String userID, String item_Name, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("userID", userID);
        parameters.put("item_Name", item_Name);
    }

    @Override
    public Map<String, String> getParams(){ return parameters; }

}
