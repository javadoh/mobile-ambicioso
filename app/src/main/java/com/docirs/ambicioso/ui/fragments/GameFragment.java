package com.docirs.ambicioso.ui.fragments;

/**
 * Created by luiseliberal on 24/12/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.io.beans.LocalUsersBean;
import com.docirs.ambicioso.io.beans.RemoteUserBean;
import com.docirs.ambicioso.ui.activities.MainActivity;
import com.docirs.ambicioso.ui.adapters.MessageAdapter;
import com.docirs.ambicioso.utils.AmbiciosoApplication;
import com.docirs.ambicioso.utils.sockets.Message;
import com.squareup.picasso.Picasso;


/**
 * A chat fragment containing messages view and input form.
 */
public class GameFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_LOGIN = 0;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView mMessagesView;
    private RecyclerView.Adapter mAdapter;
    private Socket mSocket;

    private Boolean isConnected = true;
    //
    public static final String TAG = GameFragment.class.getName();
    LinearLayout linearHistoricCards;
    TextView titleTurn,pointsTurn, cardsTurn, pointsRemoteTurn, cardsRemoteTurn,
            totalPointsPlayerOne, totalPointsPlayerTwo, metaGame;
    ImageView cardShown, imgAvatarOne, imgAvatarTwo;
    Button btn_call_card, btn_call_end_turn;
    String alias, token, imei, gameType, imgAvatarSelected, cardPicked;
    private boolean mPLaying = false;
    private Handler mPlayingHandler = new Handler();
    LocalUsersBean localUsersBean;
    RemoteUserBean remoteUserBean;
    //
    Context mContext;
    String pckName;

    public static GameFragment newInstance(Bundle arguments){
        GameFragment f = new GameFragment();
        if(arguments != null){
            f.setArguments(arguments);
        }
        return f;
    }

    public GameFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter(activity, mMessages);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        AmbiciosoApplication app = (AmbiciosoApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on("new card", onNewCard);
        mSocket.on("user left", onUserLeft);
        mSocket.on("playing", onTyping);
        mSocket.on("stop playing", onStopTyping);
        mSocket.on("end turn", onEndTurn);
        mSocket.connect();

        startSignIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        mContext = getActivity().getBaseContext();
        pckName = getActivity().getPackageName();

        linearHistoricCards = (LinearLayout) rootView.findViewById(R.id.idLinearHistoricCards);
        //TEXTVIEWS DINAMICAS
        this.titleTurn = (TextView) rootView.findViewById(R.id.titleTurn);
        pointsTurn = (TextView) rootView.findViewById(R.id.pointsPlayerOneTurn);
        cardsTurn = (TextView) rootView.findViewById(R.id.cardsPlayerOneTurn);
        pointsRemoteTurn = (TextView) rootView.findViewById(R.id.pointsRemoteTurn);
        cardsRemoteTurn = (TextView) rootView.findViewById(R.id.cardsRemoteTurn);
        totalPointsPlayerOne = (TextView) rootView.findViewById(R.id.txtTotalPointsPlayerOne);
        totalPointsPlayerTwo = (TextView) rootView.findViewById(R.id.txtTotalPointsPlayerTwo);
        metaGame = (TextView) rootView.findViewById(R.id.metaGame);
        //IMAGEVIEWS DINAMICAS
        cardShown = (ImageView) rootView.findViewById(R.id.cardShown);
        imgAvatarOne = (ImageView) rootView.findViewById(R.id.imgAvatarOne);
        //SELECCIONAMOS LA IMAGEN DEL AVATAR QUE ESCOGIO EL USUARIO PARA MOSTRAR
        String uri = "@drawable/" + imgAvatarSelected;
        // extension removed from the String
        int imageResource = getResources().getIdentifier(uri, "drawable", pckName);
        Drawable res;
        if (imageResource != 0) {
            Picasso.with(mContext).load(imageResource).resize(getResources().getDimensionPixelSize(R.dimen.imgavatar_width), getResources().getDimensionPixelSize(R.dimen.imgavatar_height)).into(imgAvatarOne);
        }else {
            uri = "@drawable/" + "no_avatar";
            imageResource = getResources().getIdentifier(uri, null, pckName);
            Picasso.with(mContext).load(imageResource).resize(getResources().getDimensionPixelSize(R.dimen.imgavatar_width), getResources().getDimensionPixelSize(R.dimen.imgavatar_height)).into(imgAvatarOne);
        }

        imgAvatarTwo = (ImageView)rootView.findViewById(R.id.imgAvatarTwo);
        //BUTTONS
        btn_call_card = (Button) rootView.findViewById(R.id.btn_call_card);
        btn_call_end_turn = (Button) rootView.findViewById(R.id.btn_call_end_turn);
        btn_call_card.setOnClickListener(this);
        btn_call_end_turn.setOnClickListener(this);

        //AGREGAMOS ELEMENTOS AL LAYOUT HISTORICO
        LinearLayout.LayoutParams params;
        TextView txtAlias = new TextView(mContext);
        txtAlias.setTextColor(Color.parseColor("#000000"));
        txtAlias.setTextSize(14);
        txtAlias.setTypeface(null, Typeface.BOLD);
        txtAlias.setText("ALIAS: " + alias);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        txtAlias.setLayoutParams(params);
        linearHistoricCards.addView(txtAlias);

        TextView txtToken = new TextView(mContext);
        txtToken.setTextColor(Color.parseColor("#000000"));
        txtToken.setTextSize(14);
        txtToken.setTypeface(null, Typeface.BOLD);
        txtToken.setText("TOKEN: " + token);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        txtToken.setLayoutParams(params);
        linearHistoricCards.addView(txtToken);

        TextView txtImei = new TextView(mContext);
        txtImei.setTextColor(Color.parseColor("#000000"));
        txtImei.setTextSize(14);
        txtImei.setTypeface(null, Typeface.BOLD);
        txtImei.setText("IMEI: " + imei);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        txtImei.setLayoutParams(params);
        linearHistoricCards.addView(txtImei);

        Toast.makeText(getActivity().getBaseContext(), "ALIAS: "+alias+", TOKEN: "+token+", IMEI: "+imei,
                Toast.LENGTH_LONG).show();

        if (getArguments() != null) {
            alias = getArguments().getString("ALIAS_PLAYER");
            token = getArguments().getString("TOKEN_PLAYER");
            imei = getArguments().getString("IMEI_PLAYER");
            gameType = getArguments().getString("GAME_TYPE");
            imgAvatarSelected = getArguments().getString("IMAGE_AVATAR_SELECTED");
            localUsersBean = (LocalUsersBean) getArguments().get("LOCAL_USER_BEAN");
        }

        return rootView;
    }


    @Override
    public void onClick(View v) {
        Log.d("LOG_TAG", "ACCION DESDE EL TABLERO DE JUEGO: " + v.getId());
        switch (v.getId()){
            case R.id.btn_call_card:
                //PEDIMOS UNA CARTA
                try {
                    JSONObject jsonReqObj = new JSONObject();
                    jsonReqObj.put("alias", alias);
                    jsonReqObj.put("imei", imei);
                    jsonReqObj.put("token", token);
                    jsonReqObj.put("turn", "A");
                    jsonReqObj.put("room", 1);

                    mSocket.emit("new card", jsonReqObj);

                }catch (Exception e){
                    Toast.makeText(getActivity().getBaseContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_call_end_turn:
                //DESHABILITAMOS LAYOUT LOCAL
                try {
                    JSONObject jsonReqObj = new JSONObject();
                    jsonReqObj.put("alias", alias);
                    jsonReqObj.put("player1", alias);
                    jsonReqObj.put("player2", "PEPE");
                    jsonReqObj.put("token", token);
                    jsonReqObj.put("action", gameType);
                    jsonReqObj.put("room", 1);

                    mSocket.emit("end turn", jsonReqObj);

                    endOfTurnDisableLayout();

                }catch (Exception e){
                    Toast.makeText(getActivity().getBaseContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on("new card", onNewCard);
        mSocket.on("user left", onUserLeft);
        mSocket.on("playing", onTyping);
        mSocket.on("stop playing", onStopTyping);
        mSocket.on("end turn", onEndTurn);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_LONG).show();

        //mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);

        //addLog(getResources().getString(R.string.message_welcome));
        //addParticipantsLog(numUsers);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_game, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void endOfTurnDisableLayout(){
        pointsTurn.setText("0");
        pointsTurn.setText("0");
        cardsTurn.setText("0");
        pointsRemoteTurn.setText("0");
        cardsRemoteTurn.setText("0");
        btn_call_card.setEnabled(false);
        btn_call_end_turn.setEnabled(false);
        titleTurn.setText("");
    }

    private void beginTurnEnableLayout(){
        pointsTurn.setText("0");
        cardsTurn.setText("0");
        pointsRemoteTurn.setText("0");
        cardsRemoteTurn.setText("0");
        btn_call_card.setEnabled(true);
        btn_call_end_turn.setEnabled(true);
        titleTurn.setText(alias);
    }

    private void addMessageNewCard(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MSG_IN_GAME)
                .alias(username)
                .lastCard(cardPicked)
                .build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_MSG_IN_GAME)
                .alias(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
    }

    private void removePlaying(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_MSG_IN_GAME && message.getAlias().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void startSignIn() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void leave() {
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
    }

    //######################## SOCKETSSSS #####################//

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=alias)
                            mSocket.emit("add user", alias);
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewCard = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int codeServer;
                    String alias;
                    JSONObject jsonResponse;
                    try {
                        codeServer = data.getInt("code");
                        alias = data.getString("alias");
                        jsonResponse = data.getJSONObject("res");

                        Log.d(TAG, "Respuesta New Card ### Code: " + codeServer + ", alias: " + alias + ", json res: " + jsonResponse.toString());

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String alias, token, remoteAlias;
                    int room, codeServer;
                    JSONObject resJson;
                    try {
                        codeServer = data.getInt("code");
                        alias = data.getString("alias");
                        token = data.getString("token");
                        room = data.getInt("room");
                        remoteAlias = data.getString("remotealias");
                        resJson = data.getJSONObject("res");

                        Log.d(TAG, "Respuesta onEndTurn ### Code: " + codeServer + ", alias: " + alias +
                                ", token: " + token + ", room: " + room + ", RemoteAlias: " + remoteAlias +
                                ", JsonObject: " + resJson.toString());

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

}


