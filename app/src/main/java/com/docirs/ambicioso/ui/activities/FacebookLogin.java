package com.docirs.ambicioso.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.docirs.ambicioso.R;
/*import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;*/

import org.json.JSONObject;

/**
 * Created by luiseliberal on 26/11/16.
 */
public class FacebookLogin extends Activity{

    public static final String TAG = FacebookLogin.class.getName();
    /*private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private AccessToken accessToken;
    private Profile profileUserFb;*/
    private String[] userFbData = new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //INICIALIZAMOS FACEBOOK (AGREGAR VALIDACION SI YA ESTA CONECTADO)
            /*FacebookSdk.sdkInitialize(FacebookLogin.this);
            //INICIALIZAMOS EL CALLBACK
            callbackManager = CallbackManager.Factory.create();
            accessToken = AccessToken.getCurrentAccessToken();

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                    accessToken = newToken;
                }
            };

            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                    //VER QUE HACER AQUI
                    profileUserFb = newProfile;
                }
            };
            accessTokenTracker.startTracking();
            profileTracker.startTracking();

            setContentView(R.layout.dialog_facebook_activity_login);
            loginButton = (LoginButton) findViewById(R.id.login_button);

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getFacebookData();
                }

                @Override
                public void onCancel() {

                    Toast.makeText(getApplicationContext(), getString(R.string.facebookCancelSession), Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(FacebookException e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.facebookErrorSession), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error: ", e);
                    finish();
                }
            });

            //loginButton.setReadPermissions("public_profile", "email", "user_location");*/

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error: ", e);
            Toast.makeText(getApplicationContext(), getString(R.string.facebookErrorSession), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //AL PRESIONAR LOGIN FACEBOOK INICIA UNA ACTIVIDAD INTRINSECA QUE SE DEBE MANEJAR SU RESULTADO
        //callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(Profile.getCurrentProfile() != null) {
            profileUserFb = Profile.getCurrentProfile();
        }
        if(AccessToken.getCurrentAccessToken() != null){
            accessToken = AccessToken.getCurrentAccessToken();
        }*/
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        //Facebook login
        /*accessTokenTracker.stopTracking();
        profileTracker.stopTracking();*/
    }


    /*private void nextActivity(Profile profile, String userFbData[]){

        if(profile == null){
            profile = Profile.getCurrentProfile();
        }

        if(profile != null){

            //CON ESTA DATA NOS VAMOS AL DIALOGO DE COMENTARIOS
            Log.d(TAG, "User Data: "+userFbData.toString());
            Log.d(TAG, "Name and Image from main profile: " + profile.getFirstName() + "," + profile.getProfilePictureUri(100, 100).toString());

            //ESTABLECEMOS EN MEMORIA LOS DATOS DE LA SESION DE FACEBOOK
            //MemoryBeanAux.setUserFbData(userFbData);
            //MemoryBeanAux.setUserFbUlrImage(profile.getProfilePictureUri(100, 100).toString());
            finish();
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), getString(R.string.facebookCancelSession), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void getFacebookData(){

        try {

            /*Toast.makeText(getApplicationContext(), getString(R.string.facebookRecoverDataSingle), Toast.LENGTH_SHORT).show();

            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            userFbData[0] = object.optString("name", getString(R.string.noUserName));
                            userFbData[1] = object.optString("gender", getString(R.string.noGender));
                            userFbData[2] = object.optString("birthday", getString(R.string.noBirthday));
                            userFbData[3] = object.optString("email", getString(R.string.noEmail));

                            if(object.optJSONObject("location") != null) {
                                String location = object.optJSONObject("location").optString("name", getString(R.string.noCountry));
                                userFbData[4] = location.substring(0, location.indexOf(","));
                                userFbData[5] = location.substring(location.lastIndexOf(",") + 1);
                            }else{
                                userFbData[4] = getString(R.string.noCity);
                                userFbData[5] = getString(R.string.noCountry);
                            }

                            nextActivity(profileUserFb, userFbData);
                        }
                    });

            Bundle sesionFaceBook = new Bundle();
            sesionFaceBook.putString("fields", "id, name, birthday, gender, email, location");
            request.setParameters(sesionFaceBook);
            request.executeAsync();*/

        } catch (Exception e) {
            Log.d(TAG, "Error: ", e);
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.facebookCancelSession), Toast.LENGTH_SHORT).show();
        }
    }
}
