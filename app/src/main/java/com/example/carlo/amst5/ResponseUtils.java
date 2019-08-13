package com.example.carlo.amst5;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtils {

    public static ArrayList<String> obtenerListaTanques(JSONArray response) throws JSONException {
        ArrayList<String> tanques = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject p = (JSONObject) response.get(i);
            String temp = (String) p.get("tanque").toString();
            if (!tanques.contains(temp)) {
                tanques.add(temp);
            }
        }
        //System.out.println(tanques);
        return tanques;
    }

    public static JSONArray obtenerRegistrosTanque(String id, JSONArray response) throws JSONException {
        JSONArray registro = new JSONArray();
        for (int i = 0; i < response.length(); i++) {
            JSONObject p = (JSONObject) response.get(i);
            String temp = (String) p.get("tanque").toString();
            if (id.equals(temp)) {
                registro.put((JSONObject) response.get(i));
            }
        }

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < registro.length(); i++) {
            jsonValues.add(registro.getJSONObject(i));
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "fechaRegistro";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                }
                catch (JSONException e) {
                    System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                }

                return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < registro.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        System.out.println("EHHHHHHHHHHHHHHHHHHHHH"+sortedJsonArray);
        return sortedJsonArray;
    }

    public static Integer[] obtenerEstadisticas(JSONArray response) throws JSONException {
        ArrayList<String> tanques = ResponseUtils.obtenerListaTanques(response);
        ArrayList<Integer> estadisticas = new ArrayList<>();
        estadisticas.add(0);
        estadisticas.add(0);
        for (String x : tanques) {
            JSONObject x1 = (JSONObject) ResponseUtils.obtenerUltimoRegistro(x, response);
            if (x1.getString("estado").equals("ES") || x1.getString("estado").equals("ME")) {
                estadisticas.set(0, estadisticas.get(0) + 1);
            } else {
                estadisticas.set(1, estadisticas.get(1) + 1);
            }
        }
        Integer[] datos = {estadisticas.get(0), estadisticas.get(1)};
        return datos;
    }

    public static Object obtenerUltimoRegistro(String id, JSONArray response) throws JSONException {
        JSONArray registro = obtenerRegistrosTanque(id, response);
        return (JSONObject) registro.get(registro.length() - 1);
    }

    public static void presentar_informacion_de_tanques(final String token, final ArrayList<String> lista_tanques, final Activity actividad, final JSONArray responsearray) throws JSONException {


            /*for (String id_tanque : lista_tanques) {
                JSONObject tanque_json = (JSONObject) ResponseUtils.obtenerUltimoRegistro(id_tanque, response);*/
                // OJO
        System.out.println("VAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET, "https://amstdb.herokuapp.com/db/tanque/2", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //System.out.println(response);

                                System.out.println("ENTRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                                    try {
                                        ArrayList<Category> lista_categorias = new ArrayList<>();
                                        /*for (String id_tanque : lista_tanques) {
                                        JSONObject tanque_json = (JSONObject) obtenerUltimoRegistro(id_tanque, responsearray);

                                        String mensaje = response.getString("tipo");
                                        Drawable imagen = obtener_imagen_estado_del_tanque("full", actividad);
                                        Category elemento_tanque = new Category(tanque_json.getString("estado"), tanque_json.getString("fechaRegistro"),
                                                tanque_json.getString("tanque"), imagen);*/
                                        Category elemento_tanque = new Category("asd","sda","dasd",obtener_imagen_estado_del_tanque("lleno",actividad));
                                        lista_categorias.add(elemento_tanque);
                                    //}
                                        System.out.println("**********************************************************"+lista_categorias);
                                        ListView lv = (ListView) actividad.findViewById(R.id.listView);

                                        AdapterItem adapter = new AdapterItem(actividad, lista_categorias);

                                        lv.setAdapter(adapter);

                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                final int pos = position;
                                                //Intent tanque = new Intent(getBaseContext(),Tanque.class);
                                                //startActivity(tanque); //AQUI ALGO COMO PUT EXTRAS PARA PASAR INFO DE QUE TANQUE SE REQUIERE PARAMETROS
                                            }
                                        });
                                }catch (Exception e) {
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

    }

    public static Drawable obtener_imagen_estado_del_tanque(String porcentaje_tanque, Activity actividad) {
        Resources res = actividad.getResources();
        if (porcentaje_tanque.equals("ES")) {
            return res.getDrawable(R.drawable.tanque_full);
        } else if (porcentaje_tanque.equals("ME")) {
            return res.getDrawable(R.drawable.tanque_medio);
        } else return res.getDrawable(R.drawable.tanque_low);
    }

}

    /*private static Category obtener_tanque_clasificado(final JSONObject tanque_json, final Activity actividad) throws JSONException {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, "https://amstdb.herokuapp.com/db/tanque/"+tanque_json.getString("tanque"), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            ArrayList<Category> lista_categorias= new ArrayList<>();
                            String mensaje=response.getString("tipo");
                            Drawable imagen = obtener_imagen_estado_del_tanque(mensaje,actividad);
                            Category elemento_tanque = new Category(tanque_json.getString("estado"),tanque_json.getString("fechaRegistro"),
                                    tanque_json.getString("tanque"),imagen);
                            lista_categorias.add(elemento_tanque);
                            ListView lv = (ListView) actividad.findViewById(R.id.listView);

                            AdapterItem adapter = new AdapterItem(actividad, lista_categorias);

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
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,
                                        String>();
                params.put("Authorization", "JWT " + token);
                System.out.println(token);
                return params;
            }
        };;

    }
}*/
