package com.docirs.ambicioso.utils.sockets;

/**
 * Created by luiseliberal on 31/12/16.
 */
public class Message {

    public static final int TYPE_MSG_LOGIN = 0;
    public static final int TYPE_MSG_IN_GAME = 1;

    private int mType;
    //NEW GAME
    private String mAlias;
    private String mToken;
    private int mRoom;
    private int mNumUsers;
    private String mGameType;
    //NEW CARD
    private String mTurn;
    private int mKeepingCards;
    private String mLastCard;
    //END TURN
    private String mLastLine;
    private int mTotalPointsPlayerOne;
    private int mTotalPointsPlayerTwo;
    //
    private int mTotalPointsPlayerOneTurn;
    private int mTotalPointsPlayerTwoTurn;
    private int mTotalCardsPlayerOneTurn;
    private int mTotalCardsPlayerTwoTurn;
    //SERVER
    private int mCode;


    private Message() {
    }

    public int getType() {
        return mType;
    }

    ;

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        this.mAlias = alias;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public int getRoom() {
        return mRoom;
    }

    public void setRoom(int room) {
        this.mRoom = room;
    }

    public int getNumUsers() {
        return mNumUsers;
    }

    public void setNumUsers(int numUsers) {
        this.mNumUsers = numUsers;
    }

    public String getGameType() {
        return mGameType;
    }

    public void setGameType(String gameType) {
        this.mGameType = gameType;
    }

    public String getTurn() {
        return mTurn;
    }

    public void setTurn(String turn) {
        this.mTurn = turn;
    }

    public int getKeepingCards() {
        return mKeepingCards;
    }

    public void setKeepingCards(int keepingCards) {
        this.mKeepingCards = keepingCards;
    }

    public String getLastCard() {
        return mLastCard;
    }

    public void setLastCard(String lastCard) {
        this.mLastCard = lastCard;
    }

    public String getLastLine() {
        return mLastLine;
    }

    public void setLastLine(String lastLine) {
        this.mLastLine = lastLine;
    }

    public int getTotalPointsPlayerOne() {
        return mTotalPointsPlayerOne;
    }

    public void setTotalPointsPlayerOne(int totalPointsPlayerOne) {
        this.mTotalPointsPlayerOne = totalPointsPlayerOne;
    }

    public int getTotalPlayerPointsTwo() {
        return mTotalPointsPlayerTwo;
    }

    public void setTotalPlayerTwo(int totalPointsPlayerTwo) {
        this.mTotalPointsPlayerTwo = totalPointsPlayerTwo;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public int getmTotalPointsPlayerOneTurn() {
        return mTotalPointsPlayerOneTurn;
    }

    public void setmTotalPointsPlayerOneTurn(int mTotalPointsPlayerOneTurn) {
        this.mTotalPointsPlayerOneTurn = mTotalPointsPlayerOneTurn;
    }

    public int getmTotalPointsPlayerTwoTurn() {
        return mTotalPointsPlayerTwoTurn;
    }

    public void setmTotalPointsPlayerTwoTurn(int mTotalPointsPlayerTwoTurn) {
        this.mTotalPointsPlayerTwoTurn = mTotalPointsPlayerTwoTurn;
    }

    public int getmTotalCardsPlayerOneTurn() {
        return mTotalCardsPlayerOneTurn;
    }

    public void setmTotalCardsPlayerOneTurn(int mTotalCardsPlayerOneTurn) {
        this.mTotalCardsPlayerOneTurn = mTotalCardsPlayerOneTurn;
    }

    public int getmTotalCardsPlayerTwoTurn() {
        return mTotalCardsPlayerTwoTurn;
    }

    public void setmTotalCardsPlayerTwoTurn(int mTotalCardsPlayerTwoTurn) {
        this.mTotalCardsPlayerTwoTurn = mTotalCardsPlayerTwoTurn;
    }

    public static class Builder {
        private final int mType;
        //NEW GAME
        private String mAlias;
        private String mToken;
        private int mRoom;
        private int mNumUsers;
        private String mGameType;
        //NEW CARD
        private String mTurn;
        private int mKeepingCards;
        private String mLastCard;
        //END TURN
        private String mLastLine;
        private int mTotalPointsPlayerOne;
        private int mTotalPointsPlayerTwo;
        //SERVER
        private int mCode;
        //
        private int mTotalPointsPlayerOneTurn;
        private int mTotalPointsPlayerTwoTurn;
        private int mTotalCardsPlayerOneTurn;
        private int mTotalCardsPlayerTwoTurn;

        public Builder(int type) {
            mType = type;
        }

        public Builder alias(String alias) {
            mAlias = alias;
            return this;
        }

        public Builder token(String token) {
            mToken = token;
            return this;
        }

        public Builder room(int room) {
            mRoom = room;
            return this;
        }

        public Builder numUsers(int numUsers) {
            mNumUsers = numUsers;
            return this;
        }

        public Builder gameType(String gameType) {
            mGameType = gameType;
            return this;
        }

        public Builder turn(String turn) {
            mTurn = turn;
            return this;
        }

        public Builder keepingCards(int keepingCards) {
            mKeepingCards = keepingCards;
            return this;
        }

        public Builder lastCard(String lastCard) {
            mLastCard = lastCard;
            return this;
        }

        public Builder lastLine(String lastLine) {
            mLastLine = lastLine;
            return this;
        }

        public Builder totalPointsPlayerOne(int totalPointsPlayerOne) {
            mTotalPointsPlayerOne = totalPointsPlayerOne;
            return this;
        }

        public Builder totalPointsPlayerTwo(int totalPointsPlayerTwo) {
            mTotalPointsPlayerTwo = totalPointsPlayerTwo;
            return this;
        }

        public Builder totalCardsPlayerOneTurn(int totalCardsPlayerOneTurn) {
            mTotalCardsPlayerOneTurn = totalCardsPlayerOneTurn;
            return this;
        }

        public Builder totalCardsPlayerTwoTurn(int totalCardsPlayerTwoTurn) {
            mTotalCardsPlayerTwoTurn = totalCardsPlayerTwoTurn;
            return this;
        }

        public Builder totalPointsPlayerOneTurn(int totalPointsPlayerOneTurn) {
            mTotalPointsPlayerOneTurn = totalPointsPlayerOneTurn;
            return this;
        }

        public Builder totalPointsPlayerTwoTurn(int totalPointsPlayerTwoTurn) {
            mTotalPointsPlayerTwoTurn = totalPointsPlayerTwoTurn;
            return this;
        }

        public Builder code(int code) {
            mCode = code;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mAlias = mAlias;
            message.mToken = mToken;
            message.mRoom = mRoom;
            message.mNumUsers = mNumUsers;
            message.mGameType = mGameType;
            message.mTurn = mTurn;
            message.mKeepingCards = mKeepingCards;
            message.mLastCard = mLastCard;
            message.mLastLine = mLastLine;
            message.mTotalPointsPlayerOne = mTotalPointsPlayerOne;
            message.mTotalPointsPlayerTwo = mTotalPointsPlayerTwo;
            message.mCode = mCode;
            message.mTotalCardsPlayerOneTurn = mTotalCardsPlayerOneTurn;
            message.mTotalCardsPlayerTwoTurn = mTotalCardsPlayerTwoTurn;
            message.mTotalPointsPlayerOneTurn = mTotalPointsPlayerOneTurn;
            message.mTotalPointsPlayerTwoTurn = mTotalPointsPlayerTwoTurn;
            return message;
        }
    }
}
