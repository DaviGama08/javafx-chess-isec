package pt.isec.pa.chess.model.data.game;

import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;

import java.io.Serial;
import java.io.Serializable;

public class Player implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private final ETeamColor team;
    private int score;

    public Player(String name, ETeamColor team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {return name;}
    public ETeamColor getTeam(){return team;}
    public int getScore() {return score;}

    public void setName(String name){
        this.name = name;
    }

    @Override
    public Player clone() {
        try {
            return (Player) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
