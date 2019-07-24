package com.example.carlo.amst5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

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
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Historico extends AppCompatActivity {
    BarChart barChart;
    String token;
    String urlRegistoPulsos = "https://amstdb.herokuapp.com/db/registroDePulsos";
    String urlAmbulancia = "https://amstdb.herokuapp.com/db/ambulancia";
    String urlPulsos = "https://amstdb.herokuapp.com/db/pulsos";
    RequestQueue queue;
    TableLayout table;
    //ArrayList<RegistroPulso> registroPulsos;
    //ArrayList<Ambulancia> ambulancias;
    //ArrayList<Pulso> pulsos;
    Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        token = getIntent().getExtras().getString("token");
        queue = Volley.newRequestQueue(this);

        params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + token);

        barChart = (BarChart)findViewById(R.id.barchart);
        //table = (TableLayout) findViewById(R.id.table1);
        //registroPulsos = new ArrayList<>();
        //ambulancias = new ArrayList<>();
        //pulsos = new ArrayList<>();


        init_Barchart(barChart);
        //init_values_Barchart(barChart);

        obtenerRegistros();
        String[] row = new String[]{"Tanque: "+"id", "fecha", "hora"};
        addRow(row);
        //obtenerAmbulancias();
        //obtenerPulsos();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //addAmbulaciaAndPulso(registroPulsos);
                //Map<String, Integer> data = contarPulsos(registroPulsos);
                //String[] labels = getLabels(data);
                //add_labels_Barchart(barChart, labels);
                //crearTablaRegistros(registroPulsos);
                //init_Barchart(barChart);
                //setDataBarchart(barChart, data);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                //init_values_Barchart(barChart);

            }
        }, 2000);
    }
    public void setDataBarchart(BarChart barChart, Map<String, Integer> map){
        ArrayList<String> labels = new ArrayList<>();
        String[] l = new String[map.keySet().size()];
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int maxValue = 0;
        Iterator <Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        int counter = 0;
        while (iterator.hasNext()){
            Map.Entry<String, Integer> i = iterator.next();
            String key = i.getKey();
            Integer value = i.getValue();
            labels.add(key);
            barEntries.add(new BarEntry(counter, value));

            l[counter] = key;
            counter ++;
            if (value > maxValue)
                maxValue = value;

            System.out.println(key);
            System.out.println(value);
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Pulsos2");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        BarData barData = new BarData((List<String>) barDataSet);
        //barData.setBarWidth(0.9f);

        barChart.setData(barData);
        System.out.println(l.length);

        add_labels_Barchart(barChart, l);

    }

    public String[] getLabels(Map<String,Integer> map){

        Object[] array = map.keySet().toArray();
        String[] labels = new String[array.length];
        for (int i = 0; i<array.length; i++){
            String label = (String)array[i];
            labels[i] = label;
            //System.out.println(label);

        }

        return  labels;
    }


    public void add_labels_Barchart(BarChart barChart, String[] labels){
        IndexAxisValueFormatter indexFormatter = new IndexAxisValueFormatter();
        indexFormatter.setValues(labels);

        XAxis xAxis = barChart.getXAxis();
        //xAxis.setValueFormatter(indexFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setGranularity();
        //xAxis.sesetCenterAxisLabels(true);
        //xAxis.setAxisMinimum(0);
        //xAxis.setXOffset(2);

    }
    public void init_Barchart(BarChart barChart){
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        //barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
    }
    public void init_values_Barchart(BarChart barChart) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, (int) 40f));
        barEntries.add(new BarEntry(2, (int) 30f));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Pulsos");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        BarData barData = new BarData((List<String>) barDataSet);
       // barData.setBarWidth(0.9f);

        barChart.setData(barData);


    }

    public void obtenerRegistros(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://amstdb.herokuapp.com/db/registroEstadoTanque", null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //addRow(table,new String[]{"Señal", "Fecha", "Hora"});
                            for(int i = 0; i<response.length(); i++){
                                JSONObject registro = response.getJSONObject(i);

                                String[] cadenaSplit = registro.getString("fechaRegistro").split("T");
                                String fecha = cadenaSplit[0];
                                String hora = cadenaSplit[1].split("\\.")[0];

                                int pulso = registro.getInt("tanque");
                                String ambulancia = (String) registro.get("estado");
                                String id = registro.getString("tanque");

                                //RegistroPulso registroPulso = new RegistroPulso(id,fecha,hora,pulso,ambulancia);
                                //registroPulsos.add(registroPulso);
                            }
                            /*String[] row = new String[]{"Tanque: "+"id", "fecha", "hora"};
                            addRow(row);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }

    public void obtenerAmbulancias(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlAmbulancia , null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i<response.length(); i++){
                            try {
                                JSONObject ambulanciaJ = response.getJSONObject(i);
                                int id = ambulanciaJ.getInt("id");
                                String placa = ambulanciaJ.getString("placa");
                                boolean ocuapdo = ambulanciaJ.getBoolean("ocupado");
                                int conductor = ambulanciaJ.getInt("conductor");
                                //Ambulancia ambulancia = new Ambulancia(id,placa, ocuapdo,conductor);
                                //ambulancias.add(ambulancia);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Erooor respuesta");
                System.out.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }


    public void obtenerPulsos(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlPulsos, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i < response.length(); i++){
                            try {
                                JSONObject pulsoJ = response.getJSONObject(i);
                                int id = pulsoJ.getInt("id");
                                String nombre = pulsoJ.getString("nombre");
                                int  numero_pulsos = pulsoJ.getInt("numero_pulsos");
                                String descripcion = pulsoJ.getString("descripcion");
                                //Pulso pulso = new Pulso(id,nombre,numero_pulsos,descripcion);
                               // pulsos.add(pulso);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Erooor respuesta");
                System.out.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }

    /*public Map<String, Integer> contarPulsos(ArrayList<RegistroPulso> registroPulsos){
        Map<String, Integer> contador = new HashMap<>();
        for (int i = 0; i<registroPulsos.size(); i++){
            RegistroPulso registroPulso = registroPulsos.get(i);
            Pulso pulso = registroPulso.getPulso();
            String nombre = pulso.nombre;
            try{
                Integer value = contador.get(nombre) + 1;
                //contador.remove(nombre);
                contador.put(nombre,value);
            }
            catch (Exception e){
                contador.put(nombre,1);
            }
        }
        return contador;
    }
*/
    public void addRow(String[] cells){

        final ListView lista = (ListView)findViewById(R.id.lista_historico);

        String[] leadsNames = {
                "Alexander Pierrot",
                "Carlos Lopez",
                "Sara Bonz",
                "Liliana Clarence",
                "Benito Peralta",
                "Juan Jaramillo",
                "Christian Steps",
                "Alexa Giraldo",
                "Linda Murillo",
                "Lizeth Astrada"
        };

        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                leadsNames);

        lista.setAdapter(adapter);
        /*TableRow row = new TableRow(getApplicationContext());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        row.setClickable(false);
        for (int i = 0; i< cells.length; i++){
            String value = cells[i];
            TextView textView = new TextView(getApplicationContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(value+"\t\t");
            textView.setTextColor(Color.parseColor("#303f9f"));

            textView.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(textView);
        }
        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
*/
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int item = position;
                String itemval = (String) lista.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Position: "+ item+" – Valor: "+itemval, Toast.LENGTH_LONG).show();
            }
        });
    }

   /* public Ambulancia getAmbulancia(ArrayList<Ambulancia> array, int id ){
        for (int i = 0; i<array.size(); i++){
            Ambulancia ambulancia= array.get(i);
            if(ambulancia.id == id)
                return ambulancia;
        }
        return new Ambulancia();
    }
    public Pulso getPulso (ArrayList<Pulso> array, int id ){
        for (int i = 0; i<array.size(); i++){
            Pulso pulso= array.get(i);
            if(pulso.id == id)
                return pulso;
        }
        return new Pulso();
    }

    public void addAmbulaciaAndPulso(ArrayList<RegistroPulso> registroPulsos){
        for (int i = 0; i <registroPulsos.size(); i++){
            RegistroPulso registroPulso = registroPulsos.get(i);
            Pulso pulso =  getPulso(pulsos, registroPulso.pulsoID);
            Ambulancia ambulancia = getAmbulancia(ambulancias, registroPulso.ambulanciaID);
            registroPulso.setPulso(pulso);
            registroPulso.setAmbulancia(ambulancia);
        }
    }
*/
    public void crearTablaRegistros(ArrayList<Category> registroPulsos){
        addRow(new String[]{"Señal", "Fecha", "Hora"});
        for (int i = 0; i<registroPulsos.size(); i ++){
            Category registroPulso = registroPulsos.get(i);
            //String fecha = registroPulso.getFecah();
            //String hora = registroPulso.getHora();
            //String nombre = registroPulso.getPulso().getNombre();
            //addRow(table, new String[]{nombre, fecha, hora});
        }
    }

    public void presentar_info(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("asd")
                .setTitle("asddd");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();
    }

}