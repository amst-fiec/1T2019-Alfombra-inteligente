package com.example.carlo.amst5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class registroHistorico extends AppCompatActivity {
    private static RequestQueue mQueue;
    private static String token = "";
    //El parametro ID del tanque es obtenido de la ventana anterior
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
        //AQUI se obtiene este parametro que es enviado desde la pestaña anterior
        this.id_tanque = (String) tanque.getExtras().get("id_tanque");
        this.token = (String)tanque.getExtras().get("token");
        //Se instancian los elementos diccioanrios usados para consultar
        //los elementos ya presentados en la ventana actual al momento de actulizar
        //la informacion
        Dic_Estados =  new HashMap<String,TextView>();
        Dic_fechas =  new HashMap<String,TextView>();
        Obtener_estado_de_tanque();
    }

    //Se obtiene la informacion de todos los tanques en un principio pero
    //posteriormente se filtrara para el ID especifico en los demas metodos
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
                return params;
            }
        };
        mQueue.add(request);

    }

    public void presentar_grafico(JSONArray response) {

        ArrayList<BarEntry> entradas = new ArrayList<>();
        ArrayList<String> etiquetas = new ArrayList<>();
        int contador = 0;
        try {
            //Se obtiene todos los registros de un tanque en especifico. Esto se realiza gracias al ID
            //guardado como parametro principal de esta ventana
            JSONArray respuesta = (JSONArray) ResponseUtils.obtenerRegistrosTanque(id_tanque, response);
            int len = respuesta.length();
            //Presenta los 5 ultimos datos mas actuales en la grafica
            for (int i = len - 5; i < len; i++) {
                JSONObject p = (JSONObject) respuesta.get(i);
                String temp1 = (String) p.get("estado").toString();
                String[] fecha = p.get("fechaRegistro").toString().split("T");
                String temp2 = (String) fecha[0]+"\n"+fecha[1].split("\\.")[0];
                //Se presenta la informacion del estado de tanque en forma numerica
                /*
                    1 --- Representa estado lleno
                    0.5 --- Representa estaco medio
                    0 --- Representa estado vacio
                 */
                if (temp1.equals("ES")) {
                    entradas.add(new BarEntry(1, contador));
                    etiquetas.add(temp2);
                    contador++;
                }else if(temp1.equals("ME")){
                    entradas.add(new BarEntry(0.5f, contador));
                    etiquetas.add(temp2);
                    contador++;
                }else {
                    entradas.add(new BarEntry(0, contador));
                    etiquetas.add(temp2);
                    contador++;
                }
            }
            iniciargrafico();//Se prepara el grafico para poder instanciarlo
            llenargrafico(entradas,etiquetas);//Se llena grafico con informacion relacionada

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
        /*Se instancia el grafico con los datos enviados a traves de las listas
            Entradas --- Cantidades numericas del estado del tanque 1/0.5/0
            Etiquetas --- Informacion de fecha y hora para ese estado
        *** Las etiquetas se colocan en la parte inferior de cada barra en el grafico para mayor
        *** interactividad con el usuario
        */
        dataset = new BarDataSet(entradas, "Estados: (1) ESTABLE, (0.5) MEDIO, (0) VACIO");
        BarData datos = new BarData(etiquetas, dataset);
        graficoBarras.setData(datos);
        graficoBarras.invalidate();

        //Se mantiene actualizada la informacion cada 3 segundos una vez que los datos en el grafico han sido presentados
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
        //Se prepara el grafico para llenar informacion o datos
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
        //Se configura los parametros de un LinearLayout para agregar informacion redactada
        LinearLayout.LayoutParams parametrosLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, (float) 1);
        try {
            JSONArray respuesta = (JSONArray) ResponseUtils.obtenerRegistrosTanque(id_tanque, response);
            int len =respuesta.length();

            for (int i = 1; i <= len; i++) {
                JSONObject registroTemp = (JSONObject) respuesta.get(len-i);
                String registroId = registroTemp.getString("id");
                /*Se consulta si el ID del registro ya ha esta presentada en la ventana
                    Informacion no presentada?
                    TRUE---Se crean TextViews para presentar estado y fecha de registro y luego se los agrega al
                    LinearLayout principal de la ventana
                    FALSE---NOTHING---Ya estan presentados
                */
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
                        contenedorTemperaturas.addView(nuevoRegistro);
                    }else contenedorTemperaturas.addView(nuevoRegistro,0);

                    this.Dic_fechas.put(registroId, fechaRegistro);
                    this.Dic_Estados.put(registroId, valorRegistro);
                }
            }

            //Se utiliza este parametro debido a que cuando se corre por segundo o enesima vez la ventana
            //existen registros que no estan presentados PERO que necesitan agregarse al inicio de los demas
            // para brindarle facilidad al ususario de ver su ultimo registro mas actual de manera mas dinamica
            // y simple a la vista
            primera_instancia=false;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
