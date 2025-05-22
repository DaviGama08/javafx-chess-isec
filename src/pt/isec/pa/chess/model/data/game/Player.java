package pt.isec.pa.chess.model.data.game;

import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;

import java.io.Serial;
import java.io.Serializable;

public class Player implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final ETeamColor team;
    private int plays;
    private int score;
    private int wins;

    public Player(String name, ETeamColor team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {return name;}
    public ETeamColor getTeam(){return team;}
    public int getPlays() {return plays;}
    public int getScore() {return score;}
    public int getWins() {return wins;}

    public void setPlays(int plays) {this.plays = plays;}
    public void setScore(int score) {this.score = score;}
    public void setWins(int wins) {this.wins = wins;}

    @Override
    public Player clone() {
        try {
            return (Player) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
