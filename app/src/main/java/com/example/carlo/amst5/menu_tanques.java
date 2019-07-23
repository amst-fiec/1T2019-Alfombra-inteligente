package com.example.carlo.amst5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class menu_tanques extends AppCompatActivity {

    String token = "";

    private RequestQueue mQueue;
    dbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tanques);
        Intent login = getIntent();
        helper = new dbAdapter(this);

        this.token = (String) login.getExtras().get("token");

    }

    public void revisarTanques(View v) {
        Intent estado_tanques = new Intent(getBaseContext(),
                EstadoTanque.class);
        estado_tanques.putExtra("token", token);
        startActivity(estado_tanques);
    }
}
