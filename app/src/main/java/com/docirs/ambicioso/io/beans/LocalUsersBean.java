package com.docirs.ambicioso.io.beans;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by luiseliberal on 08/12/16.
 */
public class LocalUsersBean implements Serializable {

    //PLAYER LOCAL
    private String alias;
    private String imei;
    private String token;
    private int turnPoints;
    private int playerOneTotalPoints;
    private String cardNamePicked;
    private int cardsRemainingFromPill;
    private String activeTurnAlias;
    private String action;

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

    public int getTurnPoints() {
        return turnPoints;
    }

    public void setTurnPoints(int turnPoints) {
        this.turnPoints = turnPoints;
    }

    public int getPlayerOneTotalPoints() {
        return playerOneTotalPoints;
    }

    public void setPlayerOneTotalPoints(int playerOneTotalPoints) {
        this.playerOneTotalPoints = playerOneTotalPoints;
    }

    public String getCardNamePicked() {
        return cardNamePicked;
    }

    public void setCardNamePicked(String cardNamePicked) {
        this.cardNamePicked = cardNamePicked;
    }

    public int getCardsRemainingFromPill() {
        return cardsRemainingFromPill;
    }

    public void setCardsRemainingFromPill(int cardsRemainingFromPill) {
        this.cardsRemainingFromPill = cardsRemainingFromPill;
    }

    public String getActiveTurnAlias() {
        return activeTurnAlias;
    }

    public void setActiveTurnAlias(String activeTurnAlias) {
        this.activeTurnAlias = activeTurnAlias;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
