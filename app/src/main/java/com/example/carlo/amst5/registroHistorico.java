package com.example.carlo.amst5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registroHistorico extends AppCompatActivity {
    private static RequestQueue mQueue;
    private static String token = "";
    private String id_tanque = "";
    private LinearLayout contenedorTemperaturas;
    private Map<String, TextView> Dic_Estados;
    private Map<String, TextView> Dic_fechas;
    private boolean primera_instancia = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_historico);

        mQueue = Volley.newRequestQueue(this);
        Intent tanque = getIntent();
        this.id_tanque = (String) tanque.getExtras().get("id_tanque");
        this.token = (String)tanque.getExtras().get("token");
        Dic_Estados =  new HashMap<String,TextView>();
        Dic_fechas =  new HashMap<String,TextView>();
        Obtener_estado_de_tanque();
    }

    public void Obtener_estado_de_tanque() {

        String url1 = " https://amstdb.herokuapp.com/db/registroEstadoTanque";

        JsonArrayRequest request = new JsonArrayRequest(

                Request.Method.GET, url1, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        presentar_grafico(response);
                        presentar_datos(response);

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

    public void presentar_grafico(JSONArray response) {

        ArrayList<BarEntry> entradas = new ArrayList<>();
        ArrayList<String> etiquetas = new ArrayList<>();
        int contador = 0;
//        ArrayList<BarEntry> dato_temp = new ArrayList<>();
        try {
            JSONArray respuesta = (JSONArray) ResponseUtils.obtenerRegistrosTanque(id_tanque, response);
            int len = respuesta.length();
            for (int i = len - 5; i < len; i++) {
                JSONObject p = (JSONObject) respuesta.get(i);
                String temp1 = (String) p.get("estado").toString();
                String[] fecha = p.get("fechaRegistro").toString().split("T");
                String temp2 = (String) fecha[0]+"\n"+fecha[1].split("\\.")[0];
                if (temp1.equals("ES")) {
                    entradas.add(new BarEntry(1, contador));
                    etiquetas.add(temp2);
                    contador++;

                }else if(temp1.equals("ME")){
                    entradas.add(new BarEntry(0.5f, contador));
                    etiquetas.add(temp2);
                    contador++;
                }
                else {
                    entradas.add(new BarEntry(0, contador));
                    etiquetas.add(temp2);
                    contador++;

                }
            }
            iniciargrafico();
            llenargrafico(entradas,etiquetas);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void llenargrafico(ArrayList<BarEntry> entradas,ArrayList<String> etiquetas) {
        BarChart graficoBarras = findViewById(R.id.barChart);
        BarDataSet dataset;
        if ( graficoBarras.getData() == null) {
            graficoBarras.animateY(1500);
        }
            dataset = new BarDataSet(entradas, "Estados: (1) ESTABLE, (0.5) MEDIO, (0) VACIO");
            BarData datos = new BarData(etiquetas, dataset);
            graficoBarras.setData(datos);


        graficoBarras.invalidate();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Obtener_estado_de_tanque();
            }
        };
        handler.postDelayed(runnable, 3000);

    }

    private void iniciargrafico() {
        BarChart graficoBarras = findViewById(R.id.barChart);
        graficoBarras.setDescription("Estado del Tanque vs Tiempo");
        graficoBarras.setPinchZoom(false);
        graficoBarras.setDrawBarShadow(false);
        graficoBarras.setDrawGridBackground(false);
        XAxis xAxis = graficoBarras.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        graficoBarras.getAxisLeft().setDrawGridLines(false);
    }


    public void presentar_datos(JSONArray response){

        LinearLayout nuevoRegistro;
        TextView fechaRegistro;
        TextView valorRegistro;
        contenedorTemperaturas = findViewById(R.id.cont_temperaturas);
        LinearLayout.LayoutParams parametrosLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, (float) 1);
        try {
            JSONArray respuesta = (JSONArray) ResponseUtils.obtenerRegistrosTanque(id_tanque, response);
            int len =respuesta.length();

            for (int i = 1; i <= len; i++) {
                JSONObject registroTemp = (JSONObject) respuesta.get(len-i);
                String registroId = registroTemp.getString("id");
                if( !this.Dic_Estados.containsKey(registroId) && !this.Dic_fechas.containsKey(registroId) ){

                nuevoRegistro = new LinearLayout(this);
                nuevoRegistro.setOrientation(LinearLayout.HORIZONTAL);
                valorRegistro = new TextView(this);
                valorRegistro.setLayoutParams(parametrosLayout);
                String estado = registroTemp.getString("estado");
                String imprime_estado = "";
                if (estado.equals("ES")){
                    imprime_estado = "Estable";
                }else if(estado.equals("ME")){
                    imprime_estado = " Medio";
                }else imprime_estado = " Vacio";
                valorRegistro.setText(imprime_estado+"\t\t");
                valorRegistro.setGravity(Gravity.CENTER);
                if (estado.equals("VA")){
                    valorRegistro.setTextColor(Color.RED);
                }else if(estado.equals("ME")){
                    valorRegistro.setTextColor(Color.parseColor("#FF8E96C8"));
                }
                nuevoRegistro.addView(valorRegistro);
                        fechaRegistro = new TextView(this);
                        fechaRegistro.setLayoutParams(parametrosLayout);
                String[] fecha = registroTemp.getString("fechaRegistro").split("T");
                String cadenafechayhora = (String) fecha[0]+"\t\t"+fecha[1].split("\\.")[0];
                        fechaRegistro.setText(cadenafechayhora);
                        fechaRegistro.setGravity(Gravity.CENTER);
                        nuevoRegistro.addView(fechaRegistro);
                        if (primera_instancia){
                        contenedorTemperaturas.addView(nuevoRegistro);}
                        else contenedorTemperaturas.addView(nuevoRegistro,0);
                        this.Dic_fechas.put(registroId, fechaRegistro);
                        this.Dic_Estados.put(registroId, valorRegistro);
                    }
            }
            primera_instancia=false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   /* private void actualizarGrafico(JSONArray response){
        JSONObject registro_temp;
        String temp;
        String date;
        int count = 0;
        float temp_val;
        ArrayList<BarEntry> dato_temp = new ArrayList<>();
        try {
            JSONArray respuesta = (JSONArray) ResponseUtils.obtenerRegistrosTanque(id_tanque, response);
            for (int i = 0; i < respuesta.length(); i++) {
                registro_temp = (JSONObject) respuesta.get(i);
                if( registro_temp.getString("key").equals("temperatura")){
                    temp = registro_temp.getString("value");
                    date = registro_temp.getString("date_created");
                    temp_val = Float.parseFloat(temp);
                    dato_temp.add(new BarEntry(count, temp_val));
                    count++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("error");
        }
        System.out.println(dato_temp);
        llenarGrafico(dato_temp);
    }
*/

}
