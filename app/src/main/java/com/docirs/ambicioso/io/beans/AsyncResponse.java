package com.docirs.ambicioso.io.beans;

import java.io.Serializable;

/**
 * Created by luiseliberal on 24/12/16.
 */
public class AsyncResponse implements Serializable{

    private String alias;
    private String imei;
    private String token;
    private int number_players;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getNumber_players() {
        return number_players;
    }

    public void setNumber_players(int number_players) {
        this.number_players = number_players;
    }
}
