package com.example.carlo.amst5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
        initializeComponents();

    }

    public void Salir(View v) {
        this.deleteDatabase("db");
        this.finish();
        System.exit(0);
    }

    public void initializeComponents(){
        ProgressBar bateria = (ProgressBar) findViewById(R.id.progressBar2);
        ProgressBar bateria_circulo = (ProgressBar) findViewById(R.id.progressBar3);
        TextView porcentaje = findViewById(R.id.porcentaje_bateria);
        bateria_circulo.setProgress(50);bateria.setProgress(50);porcentaje.setText("50%");
    }

    public String viewdata()
    {
        String data = helper.getData();
        System.out.println("!!!!!!!AQUI DEBERIA ESTAR EL TOKEEEEEEEEN" +data);
        return data;
    }

    public void revisarEstadoTanque(View v) {
        Intent menu_tanques = new Intent(getBaseContext(),
                EstadoTanque.class);
        menu_tanques.putExtra("token", token);
        startActivity(menu_tanques);
    }

    public void ver_estadisticas(View v) {
        Intent ventana = new Intent(getBaseContext(),
                Estadisticas.class);
        ventana.putExtra("token", token);
        startActivity(ventana);
    }

    public void ver_historico(View v) {
        Intent ventana = new Intent(getBaseContext(),
                registroHistorico.class);
        ventana.putExtra("token", token);
        startActivity(ventana);
    }
}