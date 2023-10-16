package com.docirs.ambicioso.io.beans;

import java.io.Serializable;

/**
 * Created by luiseliberal on 08/12/16.
 */
public class RemoteUserBean implements Serializable {

    //PLAYER REMOTO
    private String remoteAlias;
    private String remoteToken;
    private int remoteTurnPoints;
    private int remoteTotalPoints;
    private String remoteCardNamePicked;
    private int cardsRemainingFromPill;
    private int activeTurnAlias;
    private String remoteAction;

    public String getRemoteAlias() {
        return remoteAlias;
    }

    public void setRemoteAlias(String remoteAlias) {
        this.remoteAlias = remoteAlias;
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public void setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
    }

    public int getRemoteTurnPoints() {
        return remoteTurnPoints;
    }

    public void setRemoteTurnPoints(int remoteTurnPoints) {
        this.remoteTurnPoints = remoteTurnPoints;
    }

    public int getRemoteTotalPoints() {
        return remoteTotalPoints;
    }

    public void setRemoteTotalPoints(int remoteTotalPoints) {
        this.remoteTotalPoints = remoteTotalPoints;
    }

    public String getRemoteCardNamePicked() {
        return remoteCardNamePicked;
    }

    public void setRemoteCardNamePicked(String remoteCardNamePicked) {
        this.remoteCardNamePicked = remoteCardNamePicked;
    }

    public int getCardsRemainingFromPill() {
        return cardsRemainingFromPill;
    }

    public void setCardsRemainingFromPill(int cardsRemainingFromPill) {
        this.cardsRemainingFromPill = cardsRemainingFromPill;
    }

    public int getActiveTurnAlias() {
        return activeTurnAlias;
    }

    public void setActiveTurnAlias(int activeTurnAlias) {
        this.activeTurnAlias = activeTurnAlias;
    }

    public String getRemoteAction() {
        return remoteAction;
    }

    public void setRemoteAction(String remoteAction) {
        this.remoteAction = remoteAction;
    }
}
