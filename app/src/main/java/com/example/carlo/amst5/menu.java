package com.example.carlo.amst5;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeInitializationResult;

public class menu extends AppCompatActivity {
    String token = "";
    private RequestQueue mQueue;
    dbAdapter helper;
    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent login = getIntent();
        helper = new dbAdapter(this);

        this.token = (String) login.getExtras().get("token");
        this.token = viewdata();
        mQueue = Volley.newRequestQueue(this);
        initializeComponents();
    }

    public void Salir(View v) {
        this.deleteDatabase("db");
        this.finish();
        System.exit(0);
    }

    //Se carga la informacion de la bateria del dispositivo sensor IoT (Alfombra inteligente)
    public void initializeComponents(){
        final ProgressBar bateria = (ProgressBar) findViewById(R.id.progressBar2);
        final ProgressBar bateria_circulo = (ProgressBar) findViewById(R.id.progressBar3);
        final TextView porcentaje = findViewById(R.id.porcentaje_bateria);
        ///////////////////////////////////////////////////////
        //Se obtiene la informacion de la bateria del dispositivo 7 de la API Django
        String url1 = " https://amstdb.herokuapp.com/db/dispositivo/7";

        JsonObjectRequest request = new JsonObjectRequest(

                Request.Method.GET, url1, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Se obtiene nivel de bateria y se presenta informacion en la pantalla en caso de exito
                            String nivel_bateria = response.getString("bateria");
                            bateria_circulo.setProgress(Integer.parseInt(nivel_bateria));
                            bateria.setProgress(Integer.parseInt(nivel_bateria));
                            porcentaje.setText(nivel_bateria+"%");

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

        //Se mantiene actualizada el porcentaje de la bateria cada 3 segundos
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                initializeComponents();
            }
        };
        handler.postDelayed(runnable, 3000);

        ///////////////////////////////////////////////////////

    }

    public String viewdata()
    {
        String data = helper.getData();
        return data;
    }

    public void revisarEstadoTanque(View v) {
        Intent menu_tanques = new Intent(getBaseContext(),
                EstadoTanque.class);
        menu_tanques.putExtra("token", token);
        startActivity(menu_tanques);
    }

    public void ver_estadisticas(View v) {
        Intent ventana = new Intent(getBaseContext(),
                Estadisticas.class);
        ventana.putExtra("token", token);
        startActivity(ventana);
    }

    //Se presenta video de Youtube sobre el aplicativo movil - VIDEO_ID = "vdGGdPUUSXw"
    public void ver_info(View v) {
        String YOUTUBE_API_KEY = "AIzaSyDWrCvV7CajCVZorPkVkRHC5ijN6IZF4Uw";
        final String VIDEO_ID = "vdGGdPUUSXw";
        boolean autoplay = true;
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, YOUTUBE_API_KEY, VIDEO_ID, 0, autoplay, true);
        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                Toast.makeText(this, "NO CONEXION", Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }
}