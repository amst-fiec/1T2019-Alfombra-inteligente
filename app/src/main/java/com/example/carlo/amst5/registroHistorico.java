package com.example.carlo.amst5;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class registroHistorico extends AppCompatActivity {
    private static RequestQueue mQueue;
    private static String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_historico);

        mQueue = Volley.newRequestQueue(this);
        Intent login = getIntent();
        this.token = (String)login.getExtras().get("token");

    }


    public void Obtener_estado_de_tanques(View v) {

        String url1 = " https://amstdb.herokuapp.com/db/registroEstadoTanque";



        JsonArrayRequest request = new JsonArrayRequest(

                Request.Method.GET, url1, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<BarEntry> entradas = new ArrayList<>();
                        ArrayList<String> etiquetas = new ArrayList<>();
                        final EditText id_txt = (EditText) findViewById(R.id.txt_id_consultar_historico);
                        String id = id_txt.getText().toString();
                        int contador = 0;
                        try {
                            JSONArray respuesta = (JSONArray) ResponseUtils.obtenerRegistrosTanque(id,response);
                            for (int i = 0 ; i<respuesta.length(); i++) {
                                JSONObject p = (JSONObject) respuesta.get(i);
                                String temp1  = (String) p.get("estado").toString();
                                String temp2  = (String) p.get("fechaRegistro").toString();
                                if (temp1.equals("ES")){
                                    entradas.add(new BarEntry(1, contador));
                                    etiquetas.add(temp2);
                                    contador++;

                                }else{
                                    entradas.add(new BarEntry(0, contador));
                                    etiquetas.add(temp2);
                                    contador++;

                                }
                            }
                            BarDataSet dataset = new BarDataSet(entradas, "Estado (1) ES, (2) VA");
                            BarChart grafica;
                            grafica = (BarChart) findViewById(R.id.bar);
                            BarData datos = new BarData(etiquetas, dataset);
                            grafica.setData(datos);
                            grafica.setDescription("");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "JWT " + token);
                //System.out.println(token);
                return params;
            }
        };
        mQueue.add(request);

    }





}
