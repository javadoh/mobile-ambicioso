package com.docirs.ambicioso.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.io.beans.LocalUsersBean;
import com.docirs.ambicioso.ui.fragments.GameFragment;
import com.docirs.ambicioso.utils.AmbiciosoApplication;
import com.docirs.ambicioso.utils.AsyncHttpTask;
import com.docirs.ambicioso.utils.Constants;
import com.docirs.ambicioso.utils.ParseResults;
import com.docirs.ambicioso.utils.SyncHttpTask;
/*import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;*/
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SyncHttpTask.AsyncResponse{

    public static final String TAG = MainActivity.class.getName();
    protected Button btnSinglePlayer, btnMultiplayer, btnShareInvite, btnSendTokenForPlay, btnGotTokenForPlay;
    private Toolbar toolbar;
    EditText editTxtAlias, editTxtToken, editTxtTokenForPlay;
    TextInputLayout layoutAlias, layoutToken, layoutTokenForPlay;
    String url = "";
    String alias, remoteAlias, imei, countryNetworkIso, token, gameType, imgSelectedAvatar, jsonStringResponse;
    boolean tokenHasSend, isConnected;
    ImageView masAvatarImg1, masAvatarImg2, masAvatarImg3, masAvatarImg4, femAvatarImg1, femAvatarImg2, femAvatarImg3, femAvatarImg4;
    LocalUsersBean localUserBean;
    ProgressBar progressBar;
    //SONIDO
    final MediaPlayer player = new MediaPlayer();
    int media_length, room;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    //FACEBOOK
    /*private AccessToken accessToken;
    private Profile profileUser;
    private Profile profileUserFb;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;*/

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //INICIALIZAMOS EL SOCKET
        //AmbiciosoApplication app = (AmbiciosoApplication) getApplication();
        //mSocket = app.getSocket();
        //mSocket.connect();

        String hashKey = MainActivity.printKeyHash(MainActivity.this);
        Log.d(TAG, "KEYHASH: " + hashKey);
        token = null;
        gameType = null;
        jsonStringResponse = null;
        progressBar = new ProgressBar(getBaseContext());

        //INICIALIZAMOS FACEBOOK (AGREGAR VALIDACION SI YA ESTA CONECTADO)
        //FacebookSdk.sdkInitialize(this);
        //AppEventsLogger.activateApp(this);

        //FACEBOOK VALIDACION SI YA ESTA CONECTADO
        //accessToken = AccessToken.getCurrentAccessToken();

        /*accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                accessToken = newToken;
            }
        };

        if(accessToken != null) {
            Log.d(TAG, "AccessToken: " + accessToken.getToken().toString());
        }

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //VER QUE HACER AQUI
                profileUserFb = newProfile;
            }
        };

        Profile.fetchProfileForCurrentAccessToken();
        profileUser = Profile.getCurrentProfile();

        accessTokenTracker.startTracking();
        profileTracker.startTracking();*/

        setContentView(R.layout.activity_main);

        //AUDIO DE FONDO
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setPlayer(MainActivity.this);
        player.setLooping(true);
        player.start();
        //INICIALIZAMOS ELEMENTOS DE LA VISTA
        btnSinglePlayer = (Button) findViewById(R.id.btn_single_player);
        btnMultiplayer = (Button) findViewById(R.id.btn_multi_player);
        btnShareInvite = (Button) findViewById(R.id.btn_share_invite);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        btnSinglePlayer.setOnClickListener(this);
        btnMultiplayer.setOnClickListener(this);
        btnShareInvite.setOnClickListener(this);
        //TOOLBAR INICIALIZACION
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!Constants.isAdsDisabled) {
            //ADMOB INICIALIZACION BANNER
            mAdView = (AdView) findViewById(R.id.adBannerView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
            //ADMOB INTERSTITIAL
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.adintersticial));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });
            requestNewInterstitial();
        }

        try{
            //CARGAMOS LAS PREFERENCIAS
            loadSavedPreferences();
            //CAPTURAMOS LOS DATOS
            if(imei == null || countryNetworkIso == null) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imei = tm.getDeviceId();
                countryNetworkIso = tm.getNetworkCountryIso();
                savePreferences("Imei_Value", imei);
                savePreferences("NetCountryIso_Value", countryNetworkIso);
            }

            if(alias == null || alias.length() < 2){
                openDialogAlias();
            }

            //LOCALMENTE INICIALIZAMOS LOGIN DE SOCKET
            //mSocket.on("login", onLogin);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_alias_change:
                openDialogAlias();
                break;
            case R.id.action_faq:
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View vistaDialogoFaq = li.inflate(R.layout.dialog_faq_from_menu, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                alertDialogBuilder.setView(vistaDialogoFaq);
                alertDialogBuilder.setTitle("FAQ");
                //set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                break;
            case R.id.action_share_invite:

                String flagCall = "invite_game";
                shareTokenForPlay(flagCall);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "### SELECCION DEL MODO DE JUEGO ###");
        //INICIALIZAMOS EL TOKEN
        token = null;

        float f = 0.5f;
        switch (v.getId()){
            case R.id.btn_single_player:

                if(alias == null || alias.length() < 2) {
                    openDialogAlias();
                }else {
                    gameType = "one_player";
                    getPlayTokenWithPlayer(gameType);
                }

                break;
            case R.id.btn_multi_player:

                if(alias == null || alias.length() < 2) {
                    openDialogAlias();
                }else {
                    //INVOCAMOS EL METODO
                    gameType = "multi_player";
                    getPlayTokenWithPlayer(gameType);
                }
                break;
            case R.id.btn_share_invite:

                if(alias == null || alias.length() < 2) {
                    openDialogAlias();
                }else{
                    tokenHasSend = false;
                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                    View vistaDialogoAlias = li.inflate(R.layout.dialog_token_validate_before_play, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);

                    //GET REFERENCIAS DE VISTA
                    layoutTokenForPlay = (TextInputLayout) vistaDialogoAlias.findViewById(R.id.layoutTxtInputTokenForPlay);
                    editTxtTokenForPlay = (EditText) vistaDialogoAlias.findViewById(R.id.txtTokenForPlay);
                    editTxtTokenForPlay.addTextChangedListener(new MyTextWatcher(editTxtTokenForPlay));
                    btnSendTokenForPlay = (Button) vistaDialogoAlias.findViewById(R.id.btnSendTokenForPlay);
                    btnSendTokenForPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            gameType = "multi_player_send_token";
                            if (localUserBean != null) {
                                if(localUserBean.getToken() != null) {
                                    try {
                                        String flagCall = "invite_play";
                                        shareTokenForPlay(flagCall);
                                        tokenHasSend = true;
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        tokenHasSend = false;
                                    }
                                }else{// NO TIENE TOKEN SE SOLICITA UNO ESTE ASEGURA RESPUESTA NO HAYA SIDO NULA
                                    gameType = "multi_player_ask_token";
                                    getPlayTokenWithPlayer(gameType);}
                            }else{// NO TIENE TOKEN SE SOLICITA UNO
                                gameType = "multi_player_ask_token";
                                getPlayTokenWithPlayer(gameType);}
                        }
                    });

                    if(tokenHasSend){
                        layoutTokenForPlay.setVisibility(View.VISIBLE);
                        editTxtTokenForPlay.setVisibility(View.VISIBLE);
                        btnSendTokenForPlay.setEnabled(false);
                    }

                    btnGotTokenForPlay = (Button) vistaDialogoAlias.findViewById(R.id.btnGotTokenForPlay);
                    btnGotTokenForPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutTokenForPlay.setVisibility(View.VISIBLE);
                            editTxtTokenForPlay.setVisibility(View.VISIBLE);
                            btnGotTokenForPlay.setEnabled(false);
                        }
                    });

                    alertDialogBuilder.setView(vistaDialogoAlias);
                    //set dialog message
                    alertDialogBuilder
                            .setPositiveButton(R.string.btnOk,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(!btnGotTokenForPlay.isEnabled() || tokenHasSend){
                                                gameType = "multi_player_has_token";
                                                token = editTxtTokenForPlay.getText().toString();
                                                //SOLICITUD DE TOKEN
                                                getPlayTokenWithPlayer(gameType);
                                                dialog.cancel();
                                            }else{
                                                Toast.makeText(getBaseContext(), "Debes ingresar un token valido para continuar.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                            .setNegativeButton(getString(R.string.btnCancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }

                break;

            case R.id.btnSendTokenForPlay:

                break;

            case R.id.btnGotTokenForPlay:

                break;
            case R.id.mas_avatar1:
                enableAvatarsBeforeSelected();
                masAvatarImg1.setAlpha(f);
                imgSelectedAvatar = "mas_avatar1";
                break;
            case R.id.mas_avatar2:
                enableAvatarsBeforeSelected();
                masAvatarImg2.setAlpha(f);
                imgSelectedAvatar = "mas_avatar2";
                break;
            case R.id.mas_avatar3:
                enableAvatarsBeforeSelected();
                masAvatarImg3.setAlpha(f);
                imgSelectedAvatar = "mas_avatar3";
                break;
            case R.id.mas_avatar4:
                enableAvatarsBeforeSelected();
                masAvatarImg4.setAlpha(f);
                imgSelectedAvatar = "mas_avatar4";
                break;
            case R.id.fem_avatar1:
                enableAvatarsBeforeSelected();
                femAvatarImg1.setAlpha(f);
                imgSelectedAvatar = "fem_avatar1";
                break;
            case R.id.fem_avatar2:
                enableAvatarsBeforeSelected();
                femAvatarImg2.setAlpha(f);
                imgSelectedAvatar = "fem_avatar2";
                break;
            case R.id.fem_avatar3:
                enableAvatarsBeforeSelected();
                femAvatarImg3.setAlpha(f);
                imgSelectedAvatar = "fem_avatar3";
                break;

            case R.id.fem_avatar4:
                enableAvatarsBeforeSelected();
                femAvatarImg4.setAlpha(f);
                imgSelectedAvatar = "fem_avatar4";
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        media_length = player.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        player.seekTo(media_length);
        player.start();
        //FACEBOOK
        /*if(Profile.getCurrentProfile() != null) {
            profileUserFb = Profile.getCurrentProfile();
        }
        if(AccessToken.getCurrentAccessToken() != null){
            accessToken = AccessToken.getCurrentAccessToken();
        }*/
    }

    public void setPlayer(Context mContext){

        AssetFileDescriptor afd;
        try {
            afd = mContext.getResources().openRawResourceFd(R.raw.background_main);
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG, getString(R.string.errorGral),e);
            e.printStackTrace();
        }
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Request: " + requestCode + ", Result: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "IN APP BILL OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "IN APP BILL: The user canceled.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSocket.off("login", onLogin);
    }

    //SALVADO DE ESCOGENCIA DEL CHECKBOX DE TERMINOS EN SHAREDPREFERENCES
    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String aliasPlayer = sharedPreferences.getString("Alias_Value", null);
        String imei = sharedPreferences.getString("Imei_Value", null);
        String countryNetworkIso = sharedPreferences.getString("NetCountryIso_Value", null);
        String imageAvatar = sharedPreferences.getString("Avatar_Value", null);
        if (aliasPlayer != null) {
            this.alias = aliasPlayer;
            Constants.playerAlias = aliasPlayer;
        }
        if(imei != null){
            this.imei = imei;
            Constants.imei = imei;
        }
        if(countryNetworkIso != null){
            this.countryNetworkIso = countryNetworkIso;
        }
        if(imageAvatar != null){
            this.imgSelectedAvatar = imageAvatar;
        }
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    private void openDialogAlias(){
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View vistaDialogoAlias = li.inflate(R.layout.dialog_main_alias_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        //GET REFERENCIAS DE VISTA
        layoutAlias = (TextInputLayout) vistaDialogoAlias.findViewById(R.id.layoutTextInputAlias);
        editTxtAlias = (EditText) vistaDialogoAlias.findViewById(R.id.txtAlias);
        editTxtAlias.addTextChangedListener(new MyTextWatcher(editTxtAlias));

        masAvatarImg1 = (ImageView) vistaDialogoAlias.findViewById(R.id.mas_avatar1);
        masAvatarImg1.setOnClickListener(this);
        masAvatarImg2 = (ImageView) vistaDialogoAlias.findViewById(R.id.mas_avatar2);
        masAvatarImg2.setOnClickListener(this);
        masAvatarImg3 = (ImageView) vistaDialogoAlias.findViewById(R.id.mas_avatar3);
        masAvatarImg3.setOnClickListener(this);
        masAvatarImg4 = (ImageView) vistaDialogoAlias.findViewById(R.id.mas_avatar4);
        masAvatarImg4.setOnClickListener(this);

        femAvatarImg1 = (ImageView) vistaDialogoAlias.findViewById(R.id.fem_avatar1);
        femAvatarImg1.setOnClickListener(this);
        femAvatarImg2 = (ImageView) vistaDialogoAlias.findViewById(R.id.fem_avatar2);
        femAvatarImg2.setOnClickListener(this);
        femAvatarImg3 = (ImageView) vistaDialogoAlias.findViewById(R.id.fem_avatar3);
        femAvatarImg3.setOnClickListener(this);
        femAvatarImg4 = (ImageView) vistaDialogoAlias.findViewById(R.id.fem_avatar4);
        femAvatarImg4.setOnClickListener(this);

        alertDialogBuilder.setView(vistaDialogoAlias);
        //set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(R.string.btnOk,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                savePreferences("Alias_Value", editTxtAlias.getText().toString());
                                savePreferences("Avatar_Value", imgSelectedAvatar);
                                alias = editTxtAlias.getText().toString();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getString(R.string.btnCancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Facebook login
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
    }

    private boolean validateAliasInput(View view) {

        String text = editTxtAlias.getText().toString();
        Pattern p = Pattern.compile("[^A-Za-zÑñáéíóúÁÉÍÓÚ, ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        if (b) {
            editTxtAlias.setError(getString(R.string.errorInputSearchChars));
            requestFocus(editTxtAlias);
            return false;
        } else {
            layoutAlias.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateTokenInput(View view) {

        TextInputLayout layoutText = null;
        EditText editText = null;
        if(view.getId() == R.id.txtTokenForPlay) {
            layoutText = layoutTokenForPlay;
            editText = editTxtTokenForPlay;
        }
        Pattern p = Pattern.compile("[^0-9A-Za-zÑñáéíóúÁÉÍÓÚ, ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(editText.getText().toString());
        boolean b = m.find();
        if (b) {
            editText.setError(getString(R.string.errorInputSearchChars));
            requestFocus(editTxtToken);
            return false;
        } else {
            layoutText.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void afterTextChanged(Editable editable) {
            if(view.getId() == R.id.txtAlias) {
                validateAliasInput(view);
            }else if(view.getId() == R.id.txtTokenForPlay){
                validateTokenInput(view);
            }
        }
    }

    public void getPlayTokenWithPlayer(String gameType) {

        //SOLICITUD DE TOKEN
        LocalUsersBean localUsersBean = new LocalUsersBean();
        this.gameType = gameType;
        try {
            //UN JUGADOR
            if("one_player".equalsIgnoreCase(gameType)){
                Intent actividadResBusq = new Intent(MainActivity.this, SingleGameActivity.class);
                actividadResBusq.putExtra("ALIAS_PLAYER", alias);
                actividadResBusq.putExtra("IMAGE_AVATAR_SELECTED", imgSelectedAvatar);
                startActivity(actividadResBusq);
            //MULTI JUGADOR
            } else if("multi_player".equalsIgnoreCase(gameType)) {
                Intent actividadResBusq = new Intent(MainActivity.this, GameActivity.class);
                actividadResBusq.putExtra("ALIAS_PLAYER", alias);
                actividadResBusq.putExtra("TOKEN_PLAYER", token);
                actividadResBusq.putExtra("IMEI_PLAYER", imei);
                actividadResBusq.putExtra("LOCAL_USER_BEAN", localUserBean);
                actividadResBusq.putExtra("GAME_TYPE", gameType);
                actividadResBusq.putExtra("IMAGE_AVATAR_SELECTED", imgSelectedAvatar);

                startActivity(actividadResBusq);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this override the implemented method from asyncTask
    public void processFinish(JSONObject result){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        Log.d(TAG, "JSON RETORNO: " + result.toString());
        Toast.makeText(getBaseContext(), result.toString(), Toast.LENGTH_LONG).show();
        this.jsonStringResponse = result.toString();
        if(jsonStringResponse.length() > 0) {
            try {
                ParseResults parseResults = new ParseResults(new LocalUsersBean(), getBaseContext());
                this.localUserBean = parseResults.parseLocalUserResult(result.toString());

                token = localUserBean.getToken();
                //BIFURCACION ENTRE TIPOS DE JUEGO SEGUN RESPUESTA DEL SERVIDOR
                if(gameType.equalsIgnoreCase(localUserBean.getAction())){

                    if(!"multi_player_ask_token".equalsIgnoreCase(gameType)) {

                        //attemptLogin();

                    }else{//SI ES SOLICITUD DE TOKEN PARA COMPARTIR
                        Toast.makeText(getBaseContext(), result.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void enableAvatarsBeforeSelected(){
        float f = 1.0f;
        masAvatarImg1.setAlpha(f);
        masAvatarImg1.setAlpha(f);
        masAvatarImg2.setAlpha(f);
        masAvatarImg3.setAlpha(f);
        masAvatarImg4.setAlpha(f);
        femAvatarImg1.setAlpha(f);
        femAvatarImg2.setAlpha(f);
        femAvatarImg3.setAlpha(f);
        femAvatarImg4.setAlpha(f);
    }

    private void shareTokenForPlay(String flagCall){

        String imagePath = "share_invite_play";
        //IMAGEN
        Uri imageUri;
        try {
            //TIENE QUE IR A CAPTURAR EL TOKEN EN EL SERVIDOR - PENDIENTE
            String uri = "@drawable/" + imagePath;
            int imageResource = getResources().getIdentifier(uri, "drawable", getPackageName());

            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                    BitmapFactory.decodeResource(getResources(), imageResource), null, null));

            Intent shareTxtIntent = new Intent(Intent.ACTION_SEND);
            shareTxtIntent.setType("*/*");
            shareTxtIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareTxtIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            if("invite_game".equalsIgnoreCase(flagCall)) {
                shareTxtIntent.putExtra(Intent.EXTRA_TEXT, alias + getString(R.string.shareContentGameText) + getString(R.string.shareContentText2));
            }else if("invite_play".equalsIgnoreCase(flagCall)){
                shareTxtIntent.putExtra(Intent.EXTRA_TEXT, alias + getString(R.string.shareContentPlayText) + token + getString(R.string.shareContentText2));
                tokenHasSend = true;
            }
            shareTxtIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareTxtIntent.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(shareTxtIntent, "Share"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, getString(R.string.errorGral) + ex, Toast.LENGTH_SHORT).show();
        }
    }

    /*private boolean attemptLogin() {
        // Check for a valid username.
        boolean emitOk;

        if (!TextUtils.isEmpty(alias)) {
            // perform the user login attempt.
            try {
                JSONObject jsonReqObj = new JSONObject();
                jsonReqObj.put("alias", alias);
                jsonReqObj.put("imei", imei);
                jsonReqObj.put("action", gameType);

                mSocket.emit("add player", jsonReqObj);
                emitOk = true;
            }catch (JSONException je){
                je.printStackTrace();
                emitOk = false;
            }
        }else{emitOk = false;}

        return emitOk;
    }
    //SOCKETS EMIT
    /*private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent actividadResBusq = new Intent(MainActivity.this, GameActivity.class);
            actividadResBusq.putExtra("ALIAS_PLAYER", alias);
            actividadResBusq.putExtra("TOKEN_PLAYER", token);
            actividadResBusq.putExtra("IMEI_PLAYER", imei);
            actividadResBusq.putExtra("LOCAL_USER_BEAN", localUserBean);
            actividadResBusq.putExtra("GAME_TYPE", gameType);
            actividadResBusq.putExtra("IMAGE_AVATAR_SELECTED", imgSelectedAvatar);
            setResult(RESULT_OK, actividadResBusq);
            finish();

            /*Intent actividadResBusq = new Intent(MainActivity.this, GameActivity.class);
                        actividadResBusq.putExtra("ALIAS_PLAYER", alias);
                        actividadResBusq.putExtra("TOKEN_PLAYER", token);
                        actividadResBusq.putExtra("IMEI_PLAYER", imei);
                        actividadResBusq.putExtra("LOCAL_USER_BEAN", localUserBean);
                        actividadResBusq.putExtra("GAME_TYPE", gameType);
                        actividadResBusq.putExtra("IMAGE_AVATAR_SELECTED", imgSelectedAvatar);

                        startActivity(actividadResBusq);
        }
    };*/

}
