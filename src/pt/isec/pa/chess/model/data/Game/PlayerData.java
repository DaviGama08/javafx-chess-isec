package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;

public record PlayerData(String name, ETeamColor team, int score) {}
