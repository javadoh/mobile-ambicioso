package com.docirs.ambicioso.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.io.beans.AsyncResponse;
import com.docirs.ambicioso.io.beans.LocalUsersBean;
import com.docirs.ambicioso.io.beans.RemoteUserBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import io.socket.client.Socket;

/**
 * Created by luiseliberal on 08/12/16.
 */
public class AsyncHttpTask extends AsyncTask<String, Void, JSONObject> {

        private static final String TAG = AsyncHttpTask.class.getName();
        ProgressBar progressBar;
        Context context;
        private LocalUsersBean localUserBean;
        private RemoteUserBean remoteUserBean;
        ImageView imgNoData, imgNoConex;
        String flagCall;
        JSONObject jsonReqObj;
        //ESPECIFICO PARA PARTIDAS DE INVITADOS
        public AsyncResponse delegate = null;
        //MANEJO DE SOCKETS
        private Socket mSocket;


        public interface AsyncResponse {
            void processFinish(JSONObject outputJson);
        }

        public AsyncHttpTask(Context context,
                             ProgressBar progressBar, LocalUsersBean localUserBean, ImageView imgNoData,
                             ImageView imgNoConex, String flagCall, JSONObject jsonReqObj){
            this.context = context;
            this.progressBar = progressBar;
            this.imgNoData = imgNoData;
            this.imgNoConex = imgNoConex;
            this.localUserBean = localUserBean;
            this.flagCall = flagCall;
            this.jsonReqObj = jsonReqObj;
        }

    public AsyncHttpTask(Context context,
                         ProgressBar progressBar, RemoteUserBean remoteUserBean, ImageView imgNoData,
                         ImageView imgNoConex, String flagCall, JSONObject jsonReqObj){
        this.context = context;
        this.progressBar = progressBar;
        this.imgNoData = imgNoData;
        this.imgNoConex = imgNoConex;
        this.remoteUserBean = remoteUserBean;
        this.flagCall = flagCall;
        this.jsonReqObj = jsonReqObj;
    }

        @Override
        public void onPreExecute() {

            ((Activity)context).setProgressBarIndeterminateVisibility(true);
        }

        @Override
        public JSONObject doInBackground(String... params) {
            JSONObject resultJson = null;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                //MANEJO DE TIMEOUT
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestMethod("GET");

                /*urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);//HACE SET DE METODO POST IMPLICITAMENTE
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Accept", "application/json");//cambiar por text/plain si siguen errores
                byte[] outputBytes = jsonReqObj.toString().getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(outputBytes);
                os.flush();
                os.close();*/

                int statusCode = urlConnection.getResponseCode();

                // 200 REPRESENTA HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    //INVOCACION DE CLASE DE PARSEO GENERAL
                    if("LOCAL_USER".equalsIgnoreCase(flagCall)) {
                        ParseResults parseResults = new ParseResults(localUserBean, context);
                        localUserBean = parseResults.parseLocalUserResult(response.toString());
                    }else if("REMOTE_USER".equalsIgnoreCase(flagCall)) {
                        ParseResults parseResults = new ParseResults(remoteUserBean, context);
                        remoteUserBean = parseResults.parseRemoteUserResult(response.toString());
                    }
                    //CODE 200
                    resultJson = new JSONObject(response.toString());
                } else if(statusCode == 404){
                    String errorJson404 = "{'code': 404, 'error': 'No se pudo encontrar la ubicación solicitada.'}";
                    resultJson = new JSONObject(errorJson404);
                }else if(statusCode == 500){
                    String errorJson500 = "{'code': 500, 'error': 'Ocurrió un error solicitando el token para jugar, lo sentimos.'}";
                    resultJson = new JSONObject(errorJson500);
                }

            } catch (SocketTimeoutException | SocketException e)
            {
                e.printStackTrace();
                String errorJson500 = "{'code': 500, 'error': 'Ocurrió un error solicitando el token para jugar, lo sentimos.'}";
                try{
                    resultJson = new JSONObject(errorJson500);
                    return resultJson;
                }catch (JSONException je){
                    je.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                String errorJson500 = "{'code': 500, 'error': 'Ocurrió un error solicitando el token para jugar, lo sentimos.'}";
                try{
                    resultJson = new JSONObject(errorJson500);
                    return resultJson;
                }catch (JSONException je){
                    je.printStackTrace();
                }
                Log.d(TAG, ex.getLocalizedMessage());
                throw new RuntimeException(context.getString(R.string.errorGral), ex.getCause());
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResult) {

            if(jsonResult != null) {//OBJETO DE RESPUESTA NO SEA VACIO
                Gson gson = new Gson();
                com.docirs.ambicioso.io.beans.AsyncResponse response = gson.fromJson(jsonResult.toString(), com.docirs.ambicioso.io.beans.AsyncResponse.class);

                if (response.getNumber_players() == 1) {

                    if(!"".equalsIgnoreCase(flagCall)) {
                        //SOCKET LOG IN
                        //mSocket.on("login", onLogin);
                        // perform the user login attempt.
                        //mSocket.emit("add user", imei);
                    }else{

                        progressBar.setVisibility(View.GONE);

                    }


                } else if (response.getNumber_players() == 2) {
                    //TENEMOS EL MATCH LISTO REDIRIGIMOS A LA PANTALLA DEL JUEGO
                    progressBar.setVisibility(View.GONE);


                }else {
                    Toast.makeText(context, context.getString(R.string.errorGral2), Toast.LENGTH_SHORT).show();
                }

            }else {
                if(Constants.internetOn) {
                    imgNoData.setVisibility(View.VISIBLE);
                    Toast.makeText(context, context.getString(R.string.errorSinResultados), Toast.LENGTH_SHORT).show();
                }else {
                    imgNoConex.setVisibility(View.VISIBLE);
                    Toast.makeText(context, context.getString(R.string.errorSinConexion), Toast.LENGTH_SHORT).show();
                }
            }
                //adapter = new BusquedaRespuestaAdapter(context, hierbasList, dataUser);
                //mRecyclerView.setAdapter(adapter);
                /*
                if ("LOGIN".equalsIgnoreCase(flagCall)) {

                ProgressBar progressBar = new ProgressBar(context);
                DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                String url = "";
                int user_id = usuarioPerfilNube.getUser_id();
                try {
                    this.jsonReqObj.put("user_id", user_id);

                    //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                    url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_EXAM+user_id;

                    new GeneralHttpTask(context, progressBar, dynamicTestResponse, "DATA_EXAM", jsonReqObj, usuarioPerfilNube).execute(url);

                }catch (Exception e){
                  e.printStackTrace();
                    Log.d(TAG, "Error",e);
                }

            }else if("DATA_EXAM".equalsIgnoreCase(flagCall)){

                Intent intentPostLogin = new Intent();

                try {
                    intentPostLogin.setClass(context.getApplicationContext(), PostLogin.class);
                    intentPostLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentPostLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentPostLogin.putExtra("SESSION_USER", usuarioPerfilNube);
                    intentPostLogin.putExtra("SESSION_EXAMS", examenUsuarioNube);
                    intentPostLogin.putExtra("PASSWORD_USED", this.jsonReqObj.getString("password_used"));
                    context.getApplicationContext().startActivity(intentPostLogin);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Error al iniciar postlogin: ",e);
                }

            }else if("GET_LAST_USER".equalsIgnoreCase(flagCall)) {

                ProgressBar progressBar = new ProgressBar(context);
                DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                String url = "";
                int user_id = usuarioPerfilNube.getUser_id()+1;
                try {
                    this.jsonReqObj.put("user_id", user_id);

                    //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                    url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.ADD_NEW_USER;

                    new GeneralHttpTask(context, progressBar, dynamicTestResponse, "ADD_NEW_USER", this.jsonReqObj,
                            usuarioPerfilNube, dialog).execute(url);

                }catch (JSONException jse){
                    Log.d(TAG, "Error: ",jse);
                    jse.printStackTrace();
                }
                 */
        }

    }
