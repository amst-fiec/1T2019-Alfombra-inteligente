package com.example.carlo.amst5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class menu extends AppCompatActivity {
    String token = "";
    private boolean status_charging = false;
    private RequestQueue mQueue;
    dbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent login = getIntent();
        helper = new dbAdapter(this);

        this.token = (String) login.getExtras().get("token");
        this.token = viewdata();
        mQueue = Volley.newRequestQueue(this);
        presentar_estado_bateria();


    }

    public void Salir(View v) {
        this.deleteDatabase("db");
        this.finish();
        System.exit(0);
    }

    public void initializeComponents(View v){
        presentar_estado_bateria();
    }

    public String viewdata()
    {
        String data = helper.getData();
        System.out.println("!!!!!!!AQUI DEBERIA ESTAR EL TOKEEEEEEEEN" +data);
        return data;
    }

    public void presentar_estado_bateria(int porcentaje, boolean status_charging) {
        ImageView bateria = (ImageView) findViewById(R.id.Battery_View);
        if (status_charging) {
            bateria.setImageResource(R.drawable.bateria_charging);
        } else {
            if (porcentaje >=85) {
                bateria.setImageResource(R.drawable.bateria_full);
            } else if (porcentaje >= 70 & porcentaje < 85) {
                bateria.setImageResource(R.drawable.bateria_super_high);
            } else if (porcentaje >= 55 & porcentaje < 70) {
                bateria.setImageResource(R.drawable.bateria_high);
            } else if (porcentaje >= 40 & porcentaje < 55) {
                bateria.setImageResource(R.drawable.bateria_medium);
            } else if (porcentaje >= 35 & porcentaje < 40){
                bateria.setImageResource(R.drawable.bateria_low);
            }else if (porcentaje >= 20 & porcentaje < 35){
                bateria.setImageResource(R.drawable.bateria_super_low);
                Toast.makeText(this,"Bateria por agotarse. Por favor coloque una nueva",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void presentar_estado_bateria() {
        final int[] bateria = new int[1];
        String url_temp = "https://amstdb.herokuapp.com/db/logUno/56";  //VER EN BASE
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url_temp, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            presentar_estado_bateria(Integer.parseInt(response.getString("value")), status_charging);
                            ;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "JWT " + token);
                System.out.println(token);
                return params;
            }
        };
        ;
        mQueue.add(request);

    }

    public void revisarEstadoTanque(View v) {
        Intent estado_tanque = new Intent(getBaseContext(),
                EstadoTanque.class);
        estado_tanque.putExtra("token", token);
        startActivity(estado_tanque);
    }

    public void ver_estadisticas(View v) {
        Intent ventana = new Intent(getBaseContext(),
                Estadisticas.class);
        ventana.putExtra("token", token);
        startActivity(ventana);
    }
}