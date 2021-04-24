package com.example.duofinder.DB.POJO;

import java.util.Objects;

public class Plays {
    public String userId;
    public String username;
    public String gameId;

    public Plays(){

    }

    public Plays(String userId, String username, String gameId){
        this.userId = userId;
        this.username = username;
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plays plays = (Plays) o;
        return Objects.equals(userId, plays.userId) &&
                Objects.equals(username, plays.username) &&
                Objects.equals(gameId, plays.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, gameId);
    }
}
