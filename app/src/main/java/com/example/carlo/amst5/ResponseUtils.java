package com.example.carlo.amst5;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ResponseUtils {
    public static ArrayList<String> obtenerListaTanques(JSONArray response) throws JSONException {
        ArrayList<String> tanques = new ArrayList<>();
        for (int i = 0 ; i<response.length(); i++){
            JSONObject p = (JSONObject)response.get(i);
            String temp  = (String) p.get("tanque").toString();
            if (!tanques.contains(temp)){
                tanques.add(temp);
            }
        }
        System.out.println(tanques);
        return tanques;
    }

    public static JSONArray obtenerRegistrosTanque(String id, JSONArray response) throws JSONException {
        JSONArray registro = new JSONArray();
        for (int i = 0 ; i<response.length(); i++){
            JSONObject p = (JSONObject)response.get(i);
            String temp  = (String) p.get("tanque").toString();
            if (id.equals(temp)){
                registro.put((JSONObject)response.get(i));
            }
        }
        System.out.println(registro);
        return registro;
    }

    public static Integer[] obtenerEstadisticas( JSONArray response) throws JSONException {
        ArrayList<String> tanques = ResponseUtils.obtenerListaTanques(response);
        ArrayList<Integer> estadisticas = new ArrayList<>();
        estadisticas.add(0);
        estadisticas.add(0);
        for (String x : tanques) {
            JSONObject x1 = (JSONObject) ResponseUtils.obtenerUltimoRegistro(x, response);
            if (x1.getString("estado").equals("ES")){
                estadisticas.set(0,estadisticas.get(0)+1);
            }else{
                estadisticas.set(1,estadisticas.get(1)+1);
            }
        }
        Integer[] datos ={estadisticas.get(0),estadisticas.get(1)};
        return datos;
    }

    public static Object obtenerUltimoRegistro(String id, JSONArray response) throws JSONException {
        JSONArray registro = obtenerRegistrosTanque(id,response);
        return (JSONObject)registro.get(registro.length()-1);
    }

}
