package com.example.tictactoe;

import java.util.Map;

public class GameSession {
    int id;
    String player1;
    String player2;
    int board;

    public GameSession(Map<String,String> map) {
        setBoard(Integer.parseInt(map.get("Board")));
        setId(Integer.parseInt(map.get("Session")));
        setPlayer1(map.get("Player1"));
    }
    public GameSession(int id, String player1, int board) {
        this.id = id;
        this.player1 = player1;
        this.board = board;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getBoard() {
        return board;
    }

    public void setBoard(int board) {
        this.board = board;
    }
}
