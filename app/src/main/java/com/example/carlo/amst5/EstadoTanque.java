package com.example.carlo.amst5;

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

public class EstadoTanque extends AppCompatActivity {

    private RequestQueue mQueue;
    private String token = "";
    private Activity actividad = this;
    //private ArrayList<Category> hp = new ArrayList<Category>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_tanque);
        mQueue = Volley.newRequestQueue(this);
        Intent login = getIntent();
        this.token = (String)login.getExtras().get("token");

        Obtener_estado_de_tanques();
        /*ListView lv = (ListView) findViewById(R.id.listView);


        AdapterItem adapter = new AdapterItem(this,);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                //Intent tanque = new Intent(getBaseContext(),Tanque.class);
                //startActivity(tanque); //AQUI ALGO COMO PUT EXTRAS PARA PASAR INFO DE QUE TANQUE SE REQUIERE PARAMETROS
            }
        });*/

    }

    private void Obtener_estado_de_tanques() {

        String url1 = " https://amstdb.herokuapp.com/db/registroEstadoTanque";

        JsonArrayRequest request = new JsonArrayRequest(

                Request.Method.GET, url1, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            ArrayList<Category> lista_tanques= new ArrayList<>();
                            int len = response.length();
                            ArrayList<String> tanques = ResponseUtils.obtenerListaTanques(response);

                            for (String x : tanques){
                                JSONObject x1 = (JSONObject)ResponseUtils.obtenerUltimoRegistro(x,response);
                                // OJO

                                Drawable imagen = obtener_imagen_estado_del_tanque(85);
                                //por el momento es el unico double que tengo de info en la API
                                Category elemento_tanque = new Category(x1.getString("estado"),x1.getString("fechaRegistro"),
                                        x1.getString("tanque"),imagen);
                                lista_tanques.add(elemento_tanque);

                            }

//                            for (int i = 0 ; i<len; i++){
//                                JSONObject x1 = (JSONObject) response.get(i);
//                                // OJO
//                                Drawable imagen = obtener_imagen_estado_del_tanque(85);
//                                //por el momento es el unico double que tengo de info en la API
//                                Category elemento_tanque = new Category(x1.getString("id"),
//                                        x1.getString("tipo"),x1.getString("peso"),
//                                        x1.getString("ubicacion"),imagen);
//                                lista_tanques.add(elemento_tanque);
//                                // (x1.getString("temperatura\n"));
//                                //IDtanque.append("Id: "+x1.getString("id"));
//                                //Estado_tanque.append(x1.getString("temperatura"));
//                                //Ubicacion_tanque.append(x1.getString("fecha_registro"));
//                                //cambiar_imagen_tanque(imagen_tanque);
//                            }

                            ListView lv = (ListView) findViewById(R.id.listView);

                            AdapterItem adapter = new AdapterItem(actividad,lista_tanques);

                            lv.setAdapter(adapter);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final int pos = position;
                                    //Intent tanque = new Intent(getBaseContext(),Tanque.class);
                                    //startActivity(tanque); //AQUI ALGO COMO PUT EXTRAS PARA PASAR INFO DE QUE TANQUE SE REQUIERE PARAMETROS
                                }
                            });


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

    private Drawable obtener_imagen_estado_del_tanque(double porcentaje_tanque){
        Resources res = getResources();
            if(porcentaje_tanque>=75){
                return res.getDrawable(R.drawable.high);
            }else if(porcentaje_tanque>=50){
                return res.getDrawable(R.drawable.medium);
            }
            return res.getDrawable(R.drawable.low);

    }
}