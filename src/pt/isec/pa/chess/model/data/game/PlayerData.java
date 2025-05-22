package pt.isec.pa.chess.model.data.game;

import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;

public record PlayerData(String name, ETeamColor team, int score) {}
