package com.docirs.ambicioso.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.io.beans.LocalUsersBean;
import com.docirs.ambicioso.io.beans.RemoteUserBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by luiseliberal on 11/12/16.
 */
public class SyncHttpTask extends AsyncTask<String, Void, JSONObject> {

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

    public interface AsyncResponse {
        void processFinish(JSONObject outputJson);
    }

    public SyncHttpTask(Context context,
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

    public SyncHttpTask(Context context,
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



    public SyncHttpTask(AsyncResponse delegate){
        this.delegate = delegate;
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
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if(delegate != null){
            delegate.processFinish(jsonObject);
        }
    }
}
