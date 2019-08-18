package com.example.carlo.amst5;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private String token = null;
    dbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
        helper = new dbAdapter(this);
        String data = helper.getData();
        System.out.println(FirebaseInstanceId.getInstance().getToken());
        if (data.length()!=0){
            saltarSesion(data);
        }
    }

    //Se recolecta la informacion ingresada y se valida la info con el metodo iniciarSesion
    public void irMenuPrincipal(View v){
        final EditText usuario = (EditText) findViewById(R.id.tx_usuario);
        final EditText password = (EditText) findViewById(R.id.tx_pass);
        String str_usuario = usuario.getText().toString();
        String str_password = password.getText().toString();
        iniciarSesion(str_usuario,str_password);
    }

    private void saltarSesion(String data){
        Intent menuPrincipal = new Intent(getBaseContext(), menu.class);
        menuPrincipal.putExtra("token", data);
        startActivity(menuPrincipal);
    }

    private void iniciarSesion(String usuario, String password){
        Map<String, String> params = new HashMap();
        params.put("username", usuario);
        params.put("password", password);
        JSONObject parametros = new JSONObject(params);
        String login_url = "https://amstdb.herokuapp.com/db/nuevo-jwt";

        //Se trata de obtener un token a traves de la API de Django
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, login_url, parametros,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            //En caso de validacion correcta se obtiene el token y se ingresa a la ventana principal
                            token = response.getString("token");
                            Intent menuPrincipal = new Intent(getBaseContext(), menu.class);
                            menuPrincipal.putExtra("token", token);
                            startActivity(menuPrincipal);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog alertDialog = new
                        AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alerta");
                alertDialog.setMessage("Credenciales Incorrectas");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        mQueue.add(request);
    }
}