package com.docirs.ambicioso.utils;

/**
 * Created by luiseliberal on 26/11/16.
 */
public class Constants {

    public static boolean internetOn;
    public static boolean isAdsDisabled;
    public static String playerAlias;
    public static String[] userFbData;
    public static boolean localPlayer;
    public static String token;
    public static String imei;

    //METODOS EXPUESTOS EN EL SERVIDOR DOCIRS
    public static final String URL_SERVER = "http://www.javadoh.com:8000";//"http://ambicioso.cl";
    public static final String URL_METHOD_CARD_RETURN = "/devuelve_carta_sacada_ambicioso.asp";
    public static final String URL_METHOD_CREATE_GAME = "/crear_registro_ambicioso.asp";
    public static final String URL_METHOD_END_TURN = "/MiTurno_Ambicioso.asp";
    //VARIABLES ESTATICAS DEL JUEGO
    public static final int NUMBER_OF_MAX_PLAYERS = 2;

    public static boolean isInternetOn() {
        return internetOn;
    }

    public static void setInternetOn(boolean internetOn) {
        Constants.internetOn = internetOn;
    }

    public static boolean isAdsDisabled() {
        return isAdsDisabled;
    }

    public static void setIsAdsDisabled(boolean isAdsDisabled) {
        Constants.isAdsDisabled = isAdsDisabled;
    }

    public static String getPlayerAlias() {
        return playerAlias;
    }

    public static void setPlayerAlias(String playerAlias) {
        Constants.playerAlias = playerAlias;
    }

    public static String[] getUserFbData() {
        return userFbData;
    }

    public static void setUserFbData(String[] userFbData) {
        Constants.userFbData = userFbData;
    }

    public static boolean isLocalPlayer() {
        return localPlayer;
    }

    public static void setLocalPlayer(boolean localPlayer) {
        Constants.localPlayer = localPlayer;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Constants.token = token;
    }

    public static String getImei() {
        return imei;
    }

    public static void setImei(String imei) {
        Constants.imei = imei;
    }
}
