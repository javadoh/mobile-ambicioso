package com.docirs.ambicioso.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.utils.Constants;

/**
 * Created by luiseliberal on 26/11/16.
 */
public class Presentation extends Activity{

    public static final String TAG = MainActivity.class.getName();
    private ImageView imagePpal;
    private Animation animation, animationEnd;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //VERIFICAMOS LA CONEXION A INTERNET Y DEPENDIENDO DE ELLO INICIAMOS O NO IN APP PAY CALL
            isConnectedToInternet();

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Información: necesitas de los servicios de google play para usar la tienda.");
        }

        setContentView(R.layout.presentation_fade_in_out);
        context = this;
        imagePpal = (ImageView)findViewById(R.id.imgpresentacion);

        animation = AnimationUtils.loadAnimation(context, R.anim.fadein);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                animationEnd = AnimationUtils.loadAnimation(context, R.anim.fadeout);
                imagePpal.startAnimation(animationEnd);
                //ANIMACION ANIDADA DE FIN
                animationEnd.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent actividadBusq = new Intent(getApplicationContext(), MainActivity.class);
                        //ENVIAMOS LA PETICION DE INICIO DE LA ACTIVIDAD DE BUSQUEDA //ALGUN DIA APLICAR VALIDACION LICENSED
                        startActivity(actividadBusq);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imagePpal.startAnimation(animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "Request: " + requestCode + ", Result: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "IN APP BILL OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "IN APP BILL: The user canceled.");
        }
    }

    private void isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean NisConnected = activeNetwork.isConnectedOrConnecting();
        if (NisConnected) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Constants.internetOn = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Constants.internetOn = true;
            } else {
                Constants.internetOn = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        //IN APP BILLING GOOGLE
        //inAppPayApi.onDestroy();
        super.onDestroy();
    }
}
