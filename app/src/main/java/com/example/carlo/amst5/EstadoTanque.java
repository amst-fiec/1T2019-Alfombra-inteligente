package com.example.carlo.amst5;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import static com.example.carlo.amst5.ResponseUtils.*;

public class EstadoTanque extends AppCompatActivity {

    private RequestQueue mQueue;
    private String token = "";
    private Activity actividad = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_tanque);
        mQueue = Volley.newRequestQueue(this);
        Intent login = getIntent();
        this.token = (String) login.getExtras().get("token");
        Obtener_estado_de_tanques();
    }

    private void Obtener_estado_de_tanques() {

        String url1 = " https://amstdb.herokuapp.com/db/registroEstadoTanque";

        JsonArrayRequest request = new JsonArrayRequest(

                Request.Method.GET, url1, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            //Se crea lista de tanques definidos como Categorias
                            final ArrayList<Category> lista_tanques = new ArrayList<>();
                            ArrayList<String> tanques = ResponseUtils.obtenerListaTanques(response);
                            //Se obtiene informacion de todos los tanques pero solo se usa la ultima actualizacion que se realizo
                            //para poder presentar al usuario mediante el metodo obtenerUltimoRegistro
                            for (String id_tanque : tanques) {
                                JSONObject x1 = (JSONObject) ResponseUtils.obtenerUltimoRegistro(id_tanque,response);
                                //Se crea el tanque con una figura representativa de su estado y se lo añade a la lista
                               Drawable imagen = (Drawable) obtener_imagen_estado_del_tanque(x1.getString("estado"),actividad);
                               Category elemento_tanque = new Category(x1.getString("estado"),
                                       x1.getString("fechaRegistro"),
                                        x1.getString("tanque"), imagen);
                               lista_tanques.add(elemento_tanque);//tanque añadido
                            }

                            //Se presenta la informacion de los tanques en un ListView
                            ListView lv = (ListView) findViewById(R.id.listView);
                            AdapterItem adapter = new AdapterItem(actividad, lista_tanques);
                            lv.setAdapter(adapter);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Si el tanque es clickeado en la lista entonces se abre una ventana que
                                    //muestra el registro historico de ese tanque
                                    Intent tanque = new Intent(getBaseContext(),registroHistorico.class);
                                    //Se añade el ID del tanque clickeado a la otra ventana para saber sobre que tanque
                                    //hay que presentar la informacion
                                    tanque.putExtra("id_tanque",lista_tanques.get(position).getTitle());
                                    tanque.putExtra("token",token);
                                    startActivity(tanque);
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "JWT " + token);
                return params;
            }
        };
        mQueue.add(request);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Obtener_estado_de_tanques();
            }
        };
        handler.postDelayed(runnable, 3000);
    }
    @Override

    //En caso de que se regrese a la pestaña actual desde los registros historicos
    //se necesita actualizar el estado de los tanques al tiempo actual
    protected void onResume() {
        this.Obtener_estado_de_tanques();
        super.onResume();
    }
}