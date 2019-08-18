package com.example.carlo.amst5;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResponseUtils {

    //Se utiliza este metodo para obtener una lista con todos los tanques registrados en la API
    //de manera unica - NO repeticion
    public static ArrayList<String> obtenerListaTanques(JSONArray response) throws JSONException {
        ArrayList<String> tanques = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject p = (JSONObject) response.get(i);
            String temp = (String) p.get("tanque").toString();
            if (!tanques.contains(temp)) {
                tanques.add(temp);
            }
        }
        return tanques;
    }

    //Metodo para obtener todos los registros que representen a un ID de tanque en especifico
    //Ademas de que te retorna la informacion de los registros de manera ordenada por fechas
    public static JSONArray obtenerRegistrosTanque(String id, JSONArray response) throws JSONException {
        JSONArray registro = new JSONArray();
        for (int i = 0; i < response.length(); i++) {
            JSONObject p = (JSONObject) response.get(i);
            String temp = (String) p.get("tanque").toString();
            if (id.equals(temp)) {
                registro.put((JSONObject) response.get(i));
            }
        }

        //AQUI comienza el proceso de ordenado de la informacion para facilidad de recursos
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
                catch (JSONException e) {   }

                return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < registro.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        return sortedJsonArray;
    }

    //Se obtienen representacion numericas - estadisticas de cantidad de tanques
    //estables o vacios
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

    //Se obtiene el registro mas actual de un ID de tanque en especifico
    public static Object obtenerUltimoRegistro(String id, JSONArray response) throws JSONException {
        JSONArray registro = obtenerRegistrosTanque(id, response);
        //Esto se resume a retornar el ultimo debido a que la lista de los registros ya
        //se encuentra previamente ordenada
        return (JSONObject) registro.get(registro.length() - 1);
    }

    //Este metodo nos permite representar el estado del tanque de manera
    //grafica configurando la imagen mas apropiada para el tanque
    public static Drawable obtener_imagen_estado_del_tanque(String porcentaje_tanque, Activity actividad) {
        Resources res = actividad.getResources();
        if (porcentaje_tanque.equals("ES")) {
            return res.getDrawable(R.drawable.tanque_full);
        } else if (porcentaje_tanque.equals("ME")) {
            return res.getDrawable(R.drawable.tanque_medio);
        } else return res.getDrawable(R.drawable.tanque_low);
    }
}
