package com.docirs.ambicioso.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.docirs.ambicioso.R;
import com.docirs.ambicioso.utils.sockets.Message;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiseliberal on 31/12/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;

    public MessageAdapter(Context context, List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.TYPE_MSG_LOGIN:
                layout = R.layout.activity_main;
                break;
            case Message.TYPE_MSG_IN_GAME:
                layout = R.layout.activity_game;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setAlias(message.getAlias());
        viewHolder.setCard(message.getLastCard());
        //PLAYER ONE
        viewHolder.setTotalPointsPlayerOneView(message.getTotalPointsPlayerOne());
        viewHolder.setTotalPointsPlayerOneTurnView(message.getmTotalPointsPlayerOneTurn());
        viewHolder.setCardsPlayerOneTurnView(message.getmTotalCardsPlayerOneTurn());
        //PLAYER TWO
        viewHolder.setmTotalPointsPlayerTwoView(message.getTotalPlayerPointsTwo());
        viewHolder.setTotalPointsPlayerTwoTurnView(message.getmTotalPointsPlayerTwoTurn());
        viewHolder.setCardsPlayerTwoTurnView(message.getmTotalCardsPlayerTwoTurn());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mAliasView;
        private ImageView mCardView;
        private List<String> mHistoricCardsTurn;
        //PLAYER ONE
        private TextView mTotalPointsPlayerOneView;
        private TextView mTotalPointsPlayerOneTurnView;
        private TextView mCardsPlayerOneTurnView;
        //PLAYER TWO
        private TextView mTotalPointsPlayerTwoView;
        private TextView mTotalPointsPlayerTwoTurnView;
        private TextView mCardsPlayerTwoTurnView;

        public ViewHolder(View itemView) {
            super(itemView);
            mAliasView = (TextView) itemView.findViewById(R.id.titleTurn);
            //PLAYER ONE
            mTotalPointsPlayerOneTurnView = (TextView) itemView.findViewById(R.id.pointsPlayerOneTurn);
            mCardsPlayerOneTurnView = (TextView) itemView.findViewById(R.id.cardsPlayerOneTurn);
            mTotalPointsPlayerOneView = (TextView) itemView.findViewById(R.id.txtTotalPointsPlayerOne);
            //PLAYER TWO
            mTotalPointsPlayerTwoTurnView = (TextView) itemView.findViewById(R.id.pointsPlayerOneTurn);
            mCardsPlayerTwoTurnView = (TextView) itemView.findViewById(R.id.cardsPlayerOneTurn);
            mTotalPointsPlayerTwoView = (TextView) itemView.findViewById(R.id.txtTotalPointsPlayerTwo);
            //CARTA PEDIDA
            mCardView = (ImageView) itemView.findViewById(R.id.cardShown);
            //LISTA DE CARTAS PEDIDAS
            mHistoricCardsTurn = new ArrayList<>();

        }
        //############### NOMBRE DEL JUGADOR DEL TURNO ACTIVO ################//
        public void setAlias(String alias) {
            if (null == mAliasView) return;
            mAliasView.setText(alias);
            //mUsernameView.setTextColor(getUsernameColor(username));
        }
        //############### CARTA RECIENTE DEL TURNO ################//
        public void setCard(String card) {
            if (null == mCardView) return;
            mCardView.setImageURI(Uri.parse(card));//BUSCAR EL RECURSO EN DRAWABLE PENDIENTE
        }
        //############### CARTAS HISTORICAS TOTALES DEL TURNO ################//
        public void setHistoricCardsTurn(List<String> historicCardsTurn){
            if(null == mHistoricCardsTurn) return;
            mHistoricCardsTurn = historicCardsTurn;
        }
        //############### TOTAL DE PUNTOS TOTALES PARTIDA DEL TURNO JUGADOR UNO Y DOS ################//
        public void setTotalPointsPlayerOneView(int totalPointsPlayerOneView){
            if(null == mTotalPointsPlayerOneView) return;
            mTotalPointsPlayerOneView.setText(totalPointsPlayerOneView);
        }
        public void setmTotalPointsPlayerTwoView(int totalPointsPlayerTwoView){
            if(null == mTotalPointsPlayerTwoView) return;
            mTotalPointsPlayerTwoView.setText(totalPointsPlayerTwoView);
        }
        //############### TOTAL DE PUNTOS EN EL TURNO JUGADOR UNO Y DOS ################//
        public void setTotalPointsPlayerOneTurnView(int totalPointsPlayerOneTurnView){
            if(null == mTotalPointsPlayerOneTurnView) return;
            mTotalPointsPlayerOneTurnView.setText(totalPointsPlayerOneTurnView);
        }
        public void setTotalPointsPlayerTwoTurnView(int totalPointsPlayerTwoTurnView){
            if(null == mTotalPointsPlayerTwoTurnView) return;
            mTotalPointsPlayerTwoTurnView.setText(totalPointsPlayerTwoTurnView);
        }
        //############### TOTAL DE PUNTOS EN EL TURNO JUGADOR UNO Y DOS ################//
        public void setCardsPlayerOneTurnView(int totalCardsPlayerOneTurnView){
            if(null == mCardsPlayerOneTurnView) return;
            mCardsPlayerOneTurnView.setText(totalCardsPlayerOneTurnView);
        }
        public void setCardsPlayerTwoTurnView(int totalCardsPlayerTwoTurnView){
            if(null == mCardsPlayerTwoTurnView) return;
            mCardsPlayerTwoTurnView.setText(totalCardsPlayerTwoTurnView);
        }

    }
}
