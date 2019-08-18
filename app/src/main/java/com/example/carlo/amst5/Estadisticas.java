package com.example.carlo.amst5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Estadisticas extends AppCompatActivity {

    private static RequestQueue mQueue;
    private static String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);
        mQueue = Volley.newRequestQueue(this);
        Intent login = getIntent();
        this.token = (String)login.getExtras().get("token");
        Obtener_estado_de_tanques();
    }

    public void Obtener_estado_de_tanques() {
        String url1 = " https://amstdb.herokuapp.com/db/registroEstadoTanque";
        final JSONArray[] responseR = new JSONArray[1];

        JsonArrayRequest request = new JsonArrayRequest(

                Request.Method.GET, url1, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            int uno = ResponseUtils.obtenerEstadisticas(response)[0];
                            int dos = ResponseUtils.obtenerEstadisticas(response)[1];
                            PieChart pieChart;
                            pieChart = (PieChart) findViewById(R.id.pieChart);
                            if(pieChart.getData()==null){
                                pieChart.animateXY(1500, 1500);
                            }
                            /*definimos algunos atributos*/
                            pieChart.setHoleRadius(35f);
                            /*creamos una lista para los valores Y*/
                            ArrayList<Entry> valsY = new ArrayList<Entry>();
                            valsY.add(new Entry((uno*100)/(uno+dos),0));
                            valsY.add(new Entry((dos*100)/(uno+dos),1));
                            /*creamos una lista para los valores X*/
                            ArrayList<String> valsX = new ArrayList<String>();
                            valsX.add("% Estables");
                            valsX.add("% Vacios");
                            /*creamos una lista de colores*/
                            ArrayList<Integer> colors = new ArrayList<Integer>();
                            colors.add(Color.parseColor("#FFA0B1A6"));
                            colors.add(Color.parseColor("#FFA8857E"));
                            /*seteamos los valores de Y y los colores*/
                            PieDataSet set1 = new PieDataSet(valsY,"");
                            set1.setColors(colors);
                            /*seteamos los valores de X*/
                            PieData data = new PieData(valsX, set1);
                            data.setValueTextSize(16f);
                            data.setValueTextColor(Color.WHITE);
                            pieChart.setData(data);
                            ArrayList<String> tanques = ResponseUtils.obtenerListaTanques(response);
                            pieChart.setDescription("Las estadisticas se obtuvieron de los "+tanques.size()+" tanques registrados");
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
                return params;
            }
        };
        mQueue.add(request);

        //Se actualiza el grafico cada 3 segundos
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Obtener_estado_de_tanques();
            }
        };
        handler.postDelayed(runnable, 3000);
    }

}