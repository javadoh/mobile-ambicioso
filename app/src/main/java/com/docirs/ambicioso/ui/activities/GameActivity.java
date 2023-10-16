package com.docirs.ambicioso.ui.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.io.beans.LocalUsersBean;
import com.docirs.ambicioso.io.beans.RemoteUserBean;
import com.docirs.ambicioso.ui.fragments.GameFragment;
import com.docirs.ambicioso.utils.AmbiciosoApplication;
import com.docirs.ambicioso.utils.AsyncHttpTask;
import com.docirs.ambicioso.utils.Constants;
import com.docirs.ambicioso.utils.SyncHttpTask;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by luiseliberal on 26/11/16.
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private String alias, remoteAlias, token, imei, gameType, imgAvatarSelected, imgAvatarRemotePlayer, turnPlaying, remoteImei;
    private TextView titleTurn, pointsTurn, cardsTurn, pointsRemoteTurn, cardsRemoteTurn, metaGame,
            totalPointsPlayerOne, totalPointsPlayerTwo;
    private int room, numUsers;
    LocalUsersBean localUsersBean;
    RemoteUserBean remoteUsersBean;
    ImageView cardShown, imgAvatarOne, imgAvatarTwo;
    Button btn_call_card, btn_call_end_turn;
    LinearLayout linearHistoricCards;
    private Socket mSocket;
    private boolean isConnected;
    private ProgressBar progressBar;

    public static final String TAG = GameActivity.class.getName();
    //SONIDO
    final MediaPlayer player = new MediaPlayer();
    int media_length;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    //VARIABLES DE CONTROL DE VISTA
    private int intPlayerOnePointsEarned, intPlayerTwoPointsEarned, intPlayerOneTurnPoints,
            intPlayerTwoTurnPoints, intPlayerOneTurnCards, intPlayerTwoTurnCards;
    private ArrayList<Integer> strHistoricCardsTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        //AUDIO DE FONDO
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setPlayer(GameActivity.this);
        player.setLooping(true);
        player.start();
        //CAPTURA DE VALORES DE SESION - TEXTO BUSQUEDA Y TIPO
        Bundle busquedaSesion = getIntent().getExtras();

        alias = busquedaSesion.getString("ALIAS_PLAYER");
        token = busquedaSesion.getString("TOKEN_PLAYER");
        imei = busquedaSesion.getString("IMEI_PLAYER");
        gameType = busquedaSesion.getString("GAME_TYPE");
        imgAvatarSelected = busquedaSesion.getString("IMAGE_AVATAR_SELECTED");
        localUsersBean = (LocalUsersBean) busquedaSesion.get("LOCAL_USER_BEAN");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        linearHistoricCards = (LinearLayout)findViewById(R.id.idLinearHistoricCards);
        //TEXTVIEWS DINAMICAS
        titleTurn = (TextView)findViewById(R.id.titleTurn);
        pointsTurn = (TextView)findViewById(R.id.pointsPlayerOneTurn);
        cardsTurn = (TextView)findViewById(R.id.cardsPlayerOneTurn);
        pointsRemoteTurn = (TextView)findViewById(R.id.pointsRemoteTurn);
        cardsRemoteTurn = (TextView)findViewById(R.id.cardsRemoteTurn);
        totalPointsPlayerOne = (TextView) findViewById(R.id.txtTotalPointsPlayerOne);
        totalPointsPlayerTwo = (TextView) findViewById(R.id.txtTotalPointsPlayerTwo);

        metaGame = (TextView)findViewById(R.id.metaGame);
        //IMAGEVIEWS DINAMICAS
        cardShown = (ImageView)findViewById(R.id.cardShown);
        imgAvatarOne = (ImageView)findViewById(R.id.imgAvatarOne);
        //INICIO TURNO A POR DEFECTO
        turnPlaying = "A";
        //SELECCIONAMOS LA IMAGEN DEL AVATAR QUE ESCOGIO EL USUARIO PARA MOSTRAR
        String uri = "@drawable/" + imgAvatarSelected;
        // extension removed from the String
        int imageResource = getResources().getIdentifier(uri, "drawable", getPackageName());
        Drawable res;
        if (imageResource != 0) {
            Picasso.with(getBaseContext()).load(imageResource).resize(getResources().getDimensionPixelSize(R.dimen.imgavatar_width), getResources().getDimensionPixelSize(R.dimen.imgavatar_height)).into(imgAvatarOne);
        }else {
            uri = "@drawable/" + "no_avatar";
            imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Picasso.with(getBaseContext()).load(imageResource).resize(getResources().getDimensionPixelSize(R.dimen.imgavatar_width), getResources().getDimensionPixelSize(R.dimen.imgavatar_height)).into(imgAvatarOne);
        }

        imgAvatarTwo = (ImageView)findViewById(R.id.imgAvatarTwo);
        //BUTTONS
        btn_call_card = (Button)findViewById(R.id.btn_call_card);
        btn_call_end_turn = (Button)findViewById(R.id.btn_call_end_turn);
        btn_call_card.setOnClickListener(this);
        btn_call_end_turn.setOnClickListener(this);

        //AGREGAMOS ELEMENTOS AL LAYOUT HISTORICO
        /*LinearLayout.LayoutParams params;
        TextView txtAlias = new TextView(this);
        txtAlias.setTextColor(Color.parseColor("#000000"));
        txtAlias.setTextSize(14);
        txtAlias.setTypeface(null, Typeface.BOLD);
        txtAlias.setText("ALIAS: " + alias);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        txtAlias.setLayoutParams(params);
        linearHistoricCards.addView(txtAlias);

        TextView txtToken = new TextView(this);
        txtToken.setTextColor(Color.parseColor("#000000"));
        txtToken.setTextSize(14);
        txtToken.setTypeface(null, Typeface.BOLD);
        txtToken.setText("TOKEN: " + token);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        txtToken.setLayoutParams(params);
        linearHistoricCards.addView(txtToken);

        TextView txtImei = new TextView(this);
        txtImei.setTextColor(Color.parseColor("#000000"));
        txtImei.setTextSize(14);
        txtImei.setTypeface(null, Typeface.BOLD);
        txtImei.setText("IMEI: " + imei);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        txtImei.setLayoutParams(params);
        linearHistoricCards.addView(txtImei);*/

        Toast.makeText(getBaseContext(), "ALIAS: "+alias+", TOKEN: "+token+", IMEI: "+imei,
                Toast.LENGTH_LONG).show();

        //SOCKETS
        try{
            AmbiciosoApplication app = (AmbiciosoApplication) getApplication();
            mSocket = app.getSocket();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            //LOCALMENTE INICIALIZAMOS LOGIN DE SOCKET
            mSocket.on("login", onLogin);
            mSocket.on("user joined", onUserJoined);
            mSocket.on("new card", onNewCard);
            mSocket.on("user left", onUserLeft);
            mSocket.on("playing", onPlaying);
            mSocket.on("stop playing", onStopPlaying);
            mSocket.on("end turn", onEndTurn);
            mSocket.on("user before", onSendUserBefore);
            mSocket.connect();    //Connect socket to server
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Log.d("LOG_TAG", "ACCION DESDE EL TABLERO DE JUEGO: "+v.getId());
        switch (v.getId()){
            case R.id.btn_call_card:
                //PEDIMOS UNA CARTA
                try {
                    getNewCardLocalPlayer();
                }catch (Exception e){
                    Toast.makeText(getBaseContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_call_end_turn:
                //DESHABILITAMOS LAYOUT LOCAL
                try {
                    callEndTurnLocalPlayer();
                    endOfTurnDisableLayout();
                }catch (Exception e){
                    Toast.makeText(getBaseContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void endOfTurnDisableLayout(){
        btn_call_card.setEnabled(false);
        btn_call_end_turn.setEnabled(false);
    }

    private void beginTurnEnableLayout(){
        btn_call_card.setEnabled(true);
        btn_call_end_turn.setEnabled(true);
    }

    //MANEJO DE LLAMADO ASINCRONO
    public void callEndTurnLocalPlayer() {

        try {
            JSONObject jsonReqObj = new JSONObject();
            jsonReqObj.put("alias", alias);
            jsonReqObj.put("player1", alias);
            jsonReqObj.put("player2", "PEPE");
            jsonReqObj.put("token", token);
            jsonReqObj.put("action", gameType);
            jsonReqObj.put("turn", turnPlaying);
            jsonReqObj.put("room", room);

            mSocket.emit("end turn", jsonReqObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MANEJO DE LLAMADO SINCRONO
    public void getNewCardLocalPlayer() {
        //SOLICITUD DE TOKEN
        try {
            JSONObject jsonReqObj = new JSONObject();
            jsonReqObj.put("alias", alias);
            jsonReqObj.put("imei", imei);
            jsonReqObj.put("token", token);
            jsonReqObj.put("turn", turnPlaying);
            jsonReqObj.put("room", room);

            mSocket.emit("new card", jsonReqObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this override the implemented method from asyncTask
    /*@Override
    public void processFinish(JSONObject result){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        Log.d(TAG, "GAME ACTIVITY JSON RETORNO: "+result.toString());
        Toast.makeText(getBaseContext(), result.toString(), Toast.LENGTH_LONG).show();
    }*/

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Request: " + requestCode + ", Result: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "RESULTADO OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "RESULTADO ERROR");
        }
    }

    @Override
    public void onBackPressed() {
            //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
            /*if(!Constants.isAdsDisabled) {
                //ADMOB INTERSTITIAL
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }*/
            //################# HACER VALIDACION ANTES DE SALIR ###############//
            super.onBackPressed();
            player.stop();
            this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        media_length = player.getCurrentPosition();
        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        /*if(!Constants.isAdsDisabled) {
            //ADMOB INTERSTITIAL
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        player.seekTo(media_length);
        player.start();
    }

    public void setPlayer(Context mContext){

        AssetFileDescriptor afd;
        try {
            afd = mContext.getResources().openRawResourceFd(R.raw.game_back);
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG, getString(R.string.errorGral),e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("login", onLogin);
        mSocket.off("new card", onNewCard);
        mSocket.off("end turn", onEndTurn);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("playing", onPlaying);
        mSocket.off("stop playing", onStopPlaying);
        mSocket.off("user before", onSendUserBefore);
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void attemptLogin() {
        // Check for a valid username.
        boolean emitOk;

        if (!TextUtils.isEmpty(alias)) {
            // perform the user login attempt.
            try {
                JSONObject jsonReqObj = new JSONObject();
                jsonReqObj.put("alias", alias);
                jsonReqObj.put("imei", imei);
                jsonReqObj.put("action", gameType);
                jsonReqObj.put("imgavatar", imgAvatarSelected);

                mSocket.emit("add player", jsonReqObj);
                emitOk = true;
            }catch (JSONException je){
                je.printStackTrace();
                emitOk = false;
            }
        }else{emitOk = false;}

    }
    //##############################################################################//
    //################################## SOCKETSSSS ################################//
    //##############################################################################//

    //SOCKETS EMIT
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String whoPlay = (String) args[0];
                    JSONObject data = (JSONObject) args[1];
                    try {
                        token = data.getString("token");
                        room = data.getInt("room");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    Message msg = handler.obtainMessage();
                    if (numUsers == 2) {
                        msg.arg1 = 1;
                        //NO ES TU TURNO YA QUE TE UNISTE DE ULTIMO A LA SALA
                        progressBar.setIndeterminate(false);
                        progressBar.setVisibility(View.GONE);
                        endOfTurnDisableLayout();
                    } else {
                        msg.arg1 = 2;
                        //EL TURNO INICIAL ES EL DE EL PRIMER JUGADOR CONECTADO
                        titleTurn.setText(alias);
                        //DESHABILITAMOS LA USABILIDAD E INTERACCION HASTA QUE ENTRE OTRO USUARIO A LA SALA
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getBaseContext(), "ESPERAMOS OTRO USARIO...", Toast.LENGTH_LONG).show();

                    }
                    handler.sendMessage(msg);
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //perform anything
                        mSocket.disconnect();
                        isConnected = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //perform anything
            isConnected = false;
        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    isConnected = true;
                    //PRUEBA DE EMISION DE ROOMS POR IMEI
                    //mSocket.emit("room", imei);
                    //SOLICITAMOS AGREGAR JUGADOR
                    attemptLogin();
                }
            });
        }
    };

    private Emitter.Listener onNewCard = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int codeServer;
                    String lastCard, turn;
                    JSONObject jsonRes;
                    try {
                        String whoPlay = (String) args[0];
                        JSONObject data = (JSONObject) args[1];
                        //jsonRes = new JSONObject(data);
                        lastCard = data.getString("last_card");
                        turn = data.getString("turn");

                        Log.d(TAG, //"Respuesta New Card ### Code: " + codeServer + ", alias: " + alias +
                               "whoplay: +"+whoPlay+", card: "+lastCard+", turn: "+turn);

                        updateCardInTable(lastCard);//SE ACTUALIZA LA PPAL Y LAS HISTORICAS
                        updatePlayerTurnPointsCards(turn, 9, whoPlay);//SEGUNDA VARIABLE DEBE VENIR DE EQUIVALENCIA

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String aliasJoined, imgAvatarPlayerTwo;
                    int roomPlayerTwo;
                    try {
                        String whoPlay = (String) args[0];
                        JSONObject data = (JSONObject) args[1];
                        aliasJoined = data.getString("alias");

                        if(!alias.equalsIgnoreCase(aliasJoined)){
                            remoteAlias = data.getString("alias");
                        }
                        token = data.getString("token");
                        roomPlayerTwo = data.getInt("room");
                        numUsers = data.getInt("numUsers");
                        imgAvatarPlayerTwo = data.getString("imgavatar");

                        if(imgAvatarPlayerTwo != null){
                            String uri = "@drawable/" + imgAvatarPlayerTwo;
                            int imageResource = getResources().getIdentifier(uri, "drawable", getPackageName());
                            Picasso.with(getBaseContext()).load(imageResource).into(imgAvatarTwo);
                        }

                        Log.d(TAG, "Respuesta OnUserJoined ### alias: " + alias + ", " +
                                "room player 1: " + room + ", room player 2: " + roomPlayerTwo + ", token: " +
                                token + ", numUsers: " + numUsers+ ", imgAvatarPLayerTwo: "+imgAvatarPlayerTwo);

                        if(token != null && numUsers == 2){
                            //INICIO DEL JUEGO SE ACTIVA LA PANTALLA DEL JUGADOR QUE ESTABA ESPERANDO
                            try {
                                //ENVIAMOS LOS DATOS DEL JUGADOR INICIAL AL QUE SE UNIO
                                JSONObject jsonReqObj = new JSONObject();
                                jsonReqObj.put("alias", alias);
                                jsonReqObj.put("imei", imei);
                                jsonReqObj.put("imgavatar", imgAvatarSelected);
                                jsonReqObj.put("room", room);

                                mSocket.emit("user before", jsonReqObj);

                                //HABILITAMOS LA PANTALLA Y FUNCIONALIDADES
                                progressBar.setIndeterminate(false);
                                progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                );

                                updateAliasPlaying(alias);//SE ACTUALIZA EL ALIAS DEL QUE COMIENZA EL TURNO

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.d(TAG, "No pudo limpiar el flag que no existe");
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };


    private Emitter.Listener onSendUserBefore = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String aliasJoined;
                    try {
                        String whoPlay = (String) args[0];
                        JSONObject data = (JSONObject) args[1];
                        aliasJoined = data.getString("alias");

                        if(!alias.equalsIgnoreCase(aliasJoined)){
                            remoteAlias = data.getString("alias");
                        }

                        imgAvatarRemotePlayer = data.getString("imgavatar");

                        if(imgAvatarRemotePlayer != null){//ACTUALIZAMOS LA VISTA DEL AVATAR JUGADOR EXTERNO
                            String uri = "@drawable/" + imgAvatarRemotePlayer;
                            int imageResource = getResources().getIdentifier(uri, "drawable", getPackageName());
                            Picasso.with(getBaseContext()).load(imageResource).into(imgAvatarTwo);
                        }

                        remoteImei = data.getString("imei");
                        Log.d(TAG, "Respuesta onSendUserBefore ### alias del jugador previo: "+remoteAlias+", " +
                                "image remote avatar: "+imgAvatarRemotePlayer+", remoteImei: "+remoteImei);

                        updateAliasPlaying(remoteAlias);//SE ACTUALIZA EL ALIAS DEL QUE COMIENZA EL TURNO

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String whoPlay = (String) args[0];
                    JSONObject data = (JSONObject) args[1];
                    String alias;
                    int numUsers;
                    try {
                        alias = data.getString("alias");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onPlaying = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String whoPlay = (String) args[0];
                    JSONObject data = (JSONObject) args[1];
                    String alias;
                    try {
                        alias = data.getString("alias");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onStopPlaying = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String whoPlay = (String) args[0];
                    JSONObject data = (JSONObject) args[1];
                    String alias;
                    try {
                        alias = data.getString("alias");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onEndTurn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String token, turn, ultimaLinea;
                    int room, codeServer;
                    try {
                        String whoPlay = (String) args[0];
                        JSONObject data = (JSONObject) args[1];
                        ultimaLinea = data.getString("ultima_linea");
                        turn = data.getString("turn");

                        Log.d(TAG, "Respuesta8 onEndTurn ### , whoplay: " + whoPlay + ", Ultima linea: " +
                                ultimaLinea + ", Turn: " + turn);
                        if("A".equalsIgnoreCase(turn) && whoPlay.equalsIgnoreCase(remoteAlias)){
                            //ACCIONES DE ACTUALIZACION DE VISTAS
                            turnPlaying = "A";
                            updateAliasPlaying(alias);
                            //SE LIMPIA EL LAYOUT JUGADOR DEL TURNO QUE VA A COMENZAR
                            updatePlayerOneTableData("0", "0", "");
                            intPlayerOneTurnPoints = 0;
                            intPlayerOneTurnCards = 0;
                            //HABILITAMOS EL LAYOUT SI ES TURNO A
                            beginTurnEnableLayout();

                        }else {
                            turnPlaying = "B";
                            updateAliasPlaying(remoteAlias);
                            //SE LIMPIA EL LAYOUT JUGADOR DEL TURNO QUE VA A COMENZAR
                            intPlayerTwoTurnPoints = 0;
                            intPlayerTwoTurnCards = 0;
                            updatePlayerTwoTableData("0", "0", "");
                            //DESHABILITAMOS EL LAYOUT SI ES EL TURNO B
                            //endOfTurnDisableLayout();
                        }

                        //LIMPIAMOS EL ARREGLO DE STRING DE CARTAS HISTORICAS
                        strHistoricCardsTurn = null;
                        //SE LIMPIAN LAS VISTAS HISTORICAS EN EL LAYOUT DE CARTAS SUPERIOR
                        updateHistoricCardsView(null);

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Runnable onPlayingTimeout = new Runnable() {
        @Override
        public void run() {
            mSocket.emit("stop playing");
        }
    };

    //####################### METODOS QUE ACTUALIZAN LAS VISTAS #######################//
    private void updateAliasPlaying(String alias){

        if(!TextUtils.isEmpty(alias)) {//SETEAMOS EL TXTVIEW DEL ALIAS
            titleTurn.setText(alias);
        }
    }

    private void updatePlayerOneTableData(String pointsTurnPlayerOne, String totalPointsPlayer1,
                                          String totalCardsTurnPlayerOne){

            pointsTurn.setText(pointsTurnPlayerOne);
            cardsTurn.setText(totalCardsTurnPlayerOne);

        if(!TextUtils.isEmpty(totalPointsPlayer1)) {
            totalPointsPlayerOne.setText(totalPointsPlayer1);
        }

    }

    private void updatePlayerTwoTableData(String pointsTurnPlayerTwo, String totalPointsPlayer2,
                                          String totalCardsTurnPlayerTwo){

            pointsRemoteTurn.setText(pointsTurnPlayerTwo);
            cardsRemoteTurn.setText(totalCardsTurnPlayerTwo);

        if(!TextUtils.isEmpty(totalPointsPlayer2)){
            totalPointsPlayerTwo.setText(totalPointsPlayer2);
        }

    }

    private void updateCardInTable(String cardPicked){

        if(!TextUtils.isEmpty(cardPicked)) {

            try {
                if(cardPicked.length() > 0){
                    cardPicked = "logodocirs";
                }
                String uri = "@drawable/" + cardPicked;
                int imageResource = getResources().getIdentifier(uri, "drawable", getPackageName());
                Picasso.with(this).load(imageResource).into(cardShown);
                //AGREGAMOS AL ARREGLO DE CARTAS HISTORICAS
                if (strHistoricCardsTurn != null) {
                    strHistoricCardsTurn.add(imageResource);
                } else {
                    strHistoricCardsTurn = new ArrayList<>();
                    strHistoricCardsTurn.add(imageResource);
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    private void updateHistoricCardsView(ArrayList<Integer> historicCardsTurn){

        try {
            if (historicCardsTurn != null) {

                LinearLayout.LayoutParams params;
                for (Integer card : historicCardsTurn) {
                    ImageView cardHistoric = new ImageView(this);
                    params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.LEFT;
                    cardHistoric.setLayoutParams(params);
                    cardHistoric.setImageResource(card);
                    //AGREGAMOS LA IMAGEN DE LA CARTA HISTORICA AL LINEAR SUPERIOR DE LA VISTA
                    linearHistoricCards.addView(cardHistoric);
                }
            } else {
                //ELIMINAMOS TODAS LAS VISTAS DE CARTAS HISTORICAS DEL TURNO ANTERIOR
                linearHistoricCards.removeAllViews();
                linearHistoricCards.invalidate();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void updatePlayerTurnPointsCards(String turn, int newPoints, String whoPlayed) {

    try{
        if ("A".equalsIgnoreCase(turn) && whoPlayed.equalsIgnoreCase(alias)) {
            intPlayerOneTurnPoints = intPlayerOneTurnPoints + newPoints;//PUNTOS DEL TURNO
            intPlayerOneTurnCards = intPlayerOneTurnCards + 1; //SE INCREMENTA EL NUMERO DE CARTAS
            intPlayerOnePointsEarned = intPlayerOnePointsEarned + newPoints;//PUNTOS TOTALES
            //SETEAMOS LAS VISTAS
            pointsTurn.setText(String.valueOf(intPlayerOneTurnPoints));
            cardsTurn.setText(String.valueOf(intPlayerOneTurnCards));
            totalPointsPlayerOne.setText(String.valueOf(intPlayerOnePointsEarned));

        } else {
            intPlayerTwoTurnPoints = intPlayerTwoTurnPoints + newPoints;
            intPlayerTwoPointsEarned = intPlayerTwoPointsEarned + newPoints;
            intPlayerTwoTurnCards = intPlayerTwoTurnCards + 1;

            //SETEAMOS LAS VISTAS
            pointsRemoteTurn.setText(String.valueOf(intPlayerTwoTurnPoints));
            cardsRemoteTurn.setText(String.valueOf(intPlayerTwoTurnCards));
            totalPointsPlayerTwo.setText(String.valueOf(intPlayerTwoPointsEarned));
        }

    }catch (Exception e){e.printStackTrace();}
    }

    //REQUERIDO PARA EL MANEJO DE MENSAJES DENTRO DE LOS HILOS
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1) {
                Toast.makeText(getBaseContext(), "Son 2 los jugadores podemos empezar!", Toast.LENGTH_SHORT).show();
            }else if(msg.arg1 == 2){
            Toast.makeText(getBaseContext(), "Solo estas tu de momento", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
