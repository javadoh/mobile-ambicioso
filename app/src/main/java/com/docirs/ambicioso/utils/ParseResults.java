package com.docirs.ambicioso.utils;

import android.content.Context;
import android.util.Log;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.io.beans.FacebookBean;
import com.docirs.ambicioso.io.beans.LocalUsersBean;
import com.docirs.ambicioso.io.beans.RemoteUserBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by luiseliberal on 08/12/16.
 */
public class ParseResults {

    private static final String TAG = ParseResults.class.getName();
    private Context context;
    private LocalUsersBean localUsersBean;
    private RemoteUserBean remoteUsersBean;
    private FacebookBean faceUserBean;

    public ParseResults(LocalUsersBean localUsersBean, Context context){
        this.localUsersBean = localUsersBean;
        this.context = context;
    }

    public ParseResults(RemoteUserBean remoteUsersBean, Context context){
        this.remoteUsersBean = remoteUsersBean;
        this.context = context;
    }

    public LocalUsersBean parseLocalUserResult(String result) {

        try {
            localUsersBean = new LocalUsersBean();
            Type listType = new TypeToken<LocalUsersBean>() {}.getType();
            localUsersBean = new Gson().fromJson(result.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, context.getString(R.string.errorGral), e);
        }

        return localUsersBean;
    }

    public RemoteUserBean parseRemoteUserResult(String result) {

        try {
            remoteUsersBean = new RemoteUserBean();
            Type listType = new TypeToken<RemoteUserBean>() {}.getType();
            remoteUsersBean = new Gson().fromJson(result.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, context.getString(R.string.errorGral), e);
        }

        return remoteUsersBean;
    }

    public void parseFaceUserBeanData (String result){

        String[] userFbData = new String[6];
        try {

            Type listType = new TypeToken<FacebookBean>() {}.getType();
            faceUserBean = new Gson().fromJson(result.toString(), listType);

            userFbData[0] = faceUserBean.getName() != null ? faceUserBean.getName() : context.getString(R.string.noUserName);
            userFbData[1] = faceUserBean.getGender() != null ? faceUserBean.getGender() : context.getString(R.string.noGender);
            userFbData[2] = faceUserBean.getBirthday() != null ? faceUserBean.getBirthday()  : context.getString(R.string.noBirthday);
            userFbData[3] = faceUserBean.getEmail() != null ? faceUserBean.getEmail() : context.getString(R.string.noEmail);
            if(faceUserBean.getLocation() != null) {
                if(faceUserBean.getLocation().getName() != null) {
                    String location = faceUserBean.getLocation().getName().trim() != "" ? faceUserBean.getLocation().getName() : context.getString(R.string.noCountry);
                    userFbData[4] = location.substring(0, location.indexOf(","));
                    userFbData[5] = location.substring(location.lastIndexOf(",") + 1);
                }
            }else{
                userFbData[4] = context.getString(R.string.noCity);
                userFbData[5] = context.getString(R.string.noCountry);
            }
            //SETEAMOS LA CONSTANTE
            Constants.userFbData = userFbData;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, context.getString(R.string.errorGral), e);
        }
    }
}
