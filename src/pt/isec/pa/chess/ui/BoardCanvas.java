package pt.isec.pa.chess.ui;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;
import pt.isec.pa.chess.model.data.Game.Position;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BoardCanvas extends Canvas {

    private final ChessGameManager facade;
    private final Map<String, Image> pieceImages = new HashMap<>();
    private Position selected = null;

    private final Runnable onMoveDone;

    public BoardCanvas(ChessGameManager facade, Runnable onMoveDone) {
        this.facade = facade;
        this.onMoveDone = onMoveDone;

        loadPieceImages();
        setOnMouseClicked(this::handleClick);

        updateSize(600, 600);
    }
    public void updateSize(double paneW, double paneH) {
        double side = Math.min(paneW, paneH);

        setWidth(side);
        setHeight(side);

        setLayoutX((paneW - side) / 2);
        setLayoutY((paneH - side) / 2);

        draw();
    }

    private void handleClick(MouseEvent e) {
        if (!facade.isStarted()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Inicia um novo jogo primeiro (Game → New).");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }            // ignora clicks antes do início
        GameBoard board;
        try {
            board = facade.getBoard();
        } catch (CloneNotSupportedException ex) {
            return;
        }

        double cell = getWidth() / board.getNumCols();
        int col = (int) (e.getX() / cell);
        int row = (board.getNumRows() - 1) - (int) (e.getY() / cell);

        if (col < 0 || col >= board.getNumCols()
                || row < 0 || row >= board.getNumRows())
            return;

        if (selected == null)
            selected = new Position(col, row);
        else {
            if (facade.movePiece(selected.getCol(), selected.getRow(), col, row)) {
                onMoveDone.run();
            }
            selected = null;
        }
        draw();
    }

    private void loadPieceImages() {
        String[] t = {"pawn", "rook", "knight", "bishop", "queen", "king"};
        String[] c = {"W", "B"};
        for (String type : t)
            for (String col : c) {
                String key = type + col;
                String path = "/pt/isec/pa/chess/ui/res/images/pieces/%s%s.png"
                        .formatted(type, col);
                pieceImages.put(key,
                        new Image(Objects.requireNonNull(
                                getClass().getResource(path)).toExternalForm()));
            }
    }

    void draw() {
        GraphicsContext g = getGraphicsContext2D();

        GameBoard board;
        try {
            board = facade.getBoard();
        } catch (CloneNotSupportedException ex) {
            return;
        }

        int cols = board.getNumCols();
        int rows = board.getNumRows();

        double s = getWidth();              // já é quadrado
        double cell = s / cols;

        g.clearRect(0, 0, s, s);
        drawBoard(g, s, cell, rows, cols);
        drawPieces(g, cell, rows, cols);
        highlight(g, cell, rows);
    }

    private void drawBoard(GraphicsContext g, double s, double cell, int rows, int cols) {
        Color light = Color.web("#F8F8D8"), dark = Color.web("#8A4600");

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                g.setFill(((r + c) & 1) == 0 ? light : dark);
                g.fillRect(c * cell, r * cell, cell, cell);
            }

        double fontSize = cell * .28;                 // proporcional!
        g.setFont(Font.font(fontSize));
        g.setFill(Color.BLACK);

        // colunas a‑h
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.TOP);
        for (int c = 0; c < cols; c++)
            g.fillText(String.valueOf((char) ('a' + c)),
                    (c + .5) * cell, s - fontSize * 1.2);

        // linhas 1‑8
        g.setTextAlign(TextAlignment.RIGHT);
        g.setTextBaseline(VPos.CENTER);
        for (int r = 0; r < rows; r++) {
            double y = ((rows - 0.5) - r) * cell;
            g.fillText(String.valueOf(r + 1),
                    fontSize * 0.8, y);
        }
    }

    private void drawPieces(GraphicsContext g, double cell, int rows, int cols) {
        double pad = cell * .1, imgSide = cell - 2 * pad;

        GameBoard board;
        try {
            board = facade.getBoard();
        } catch (CloneNotSupportedException ex) {
            return;
        }

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                Piece p = board.getPiece(c, r);
                if (p == null) continue;

                String key = p.getEPieceType().name().toLowerCase() +
                        (p.isWhiteTeam() ? "W" : "B");
                Image img = pieceImages.get(key);
                if (img == null) continue;

                double y = ((rows - 1 - r) * cell) + pad;
                g.drawImage(img, c * cell + pad, y, imgSide, imgSide);
            }
    }

    private void highlight(GraphicsContext g, double cell, int rows) {
        if (selected == null) return;

        g.setStroke(Color.RED);
        g.setLineWidth(cell * 0.07);

        double y0 = (rows - 1 - selected.getRow()) * cell;
        g.strokeRect(selected.getCol() * cell,
                y0,
                cell, cell);
    }
}
