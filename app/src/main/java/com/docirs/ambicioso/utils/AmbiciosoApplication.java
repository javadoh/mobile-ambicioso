package com.docirs.ambicioso.utils;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by luiseliberal on 24/12/16.
 */
public class AmbiciosoApplication extends Application{

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.URL_SERVER);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
