package pt.isec.pa.chess.ui.views;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pt.isec.pa.chess.model.data.Game.Position;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;
import pt.isec.pa.chess.ui.res.ImageManager;

import java.util.*;
import java.util.function.Consumer;

public class BoardCanvas extends Canvas {
    private static final double MARGIN = 40;
    private static LinearGradient gradient;
    private final Map<String, Image> pieceImages = new HashMap<>();
    private GameBoard board;
    private Position selected = null;
    private List<Position> highlights = new ArrayList<>();

    // Java interface that permits to access the onClick and transmit the information to the controller
    private Consumer<Position> onPositionSelected;
    public BoardCanvas() {
        gradient = new LinearGradient(
                0, 0, 1, 1,       // de canto superior esquerdo para inferior direito
                true,             // proporcional
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.SADDLEBROWN),
                new Stop(1, Color.BURLYWOOD)
        );
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
        if (board == null)
            return;

        double w = getWidth();
        double h = getHeight();
        double s = Math.min(w - 2 * MARGIN, h - 2 * MARGIN);
        double offsetX = (w - s) / 2;
        double offsetY = (h - s) / 2;

        int cols = board.getNumCols();
        int rows = board.getNumRows();
        double cell = s / cols;

        double x = e.getX() - offsetX;
        double y = e.getY() - offsetY;

        // Verifica se o clique foi dentro da área do tabuleiro
        if (x < 0 || y < 0 || x >= s || y >= s)
            return;

        int col = (int) (x / cell);
        int row = (int) ((s - y) / cell); // invertido porque 0 é no topo

        if (col < 0 || col >= cols || row < 0 || row >= rows)
            return;

        if (onPositionSelected != null)
            onPositionSelected.accept(new Position(col, row));
    }

    private void loadPieceImages() {
        String[] t = {"pawn", "rook", "knight", "bishop", "queen", "king"};
        String[] c = {"W", "B"};
        for (String type : t)
            for (String col : c) {
                String key = type + col;
                Image img = ImageManager.getImage(key + ".png");
                if(img != null)
                    pieceImages.put(key, img);
            }
    }

    public void draw() {
        if(board == null)
                return;

        GraphicsContext g = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        double s = Math.min(w - 2 * MARGIN, h - 2 * MARGIN);


        double offsetX = (w - s) / 2;
        double offsetY = (h - s) / 2;

        int cols = board.getNumCols();
        int rows = board.getNumRows();
        double cell = s / cols;

        g.clearRect(0, 0, w, h);
        g.setFill(gradient);
        g.fillRect(offsetX - 5, offsetY - 5, s + 10, s + 10);
        drawBoard(g, offsetX, offsetY, cell, rows, cols);
        drawPieces(g, offsetX, offsetY, cell, rows, cols);
        highlight(g, offsetX, offsetY, cell, rows);
    }

    private void drawBoard(GraphicsContext g, double offsetX, double offsetY, double cell, int row, int col) {
        Color light = Color.web("#F8F8D8"), dark = Color.web("#8A4600");

        for (int r = 0; r < row; r++)
            for (int c = 0; c < col; c++) {
                g.setFill(((r + c) & 1) == 0 ? light : dark);
                g.fillRect(offsetX + c * cell, offsetY + r * cell, cell, cell);
            }

        double fontSize = cell * .28;
        g.setFont(Font.font(fontSize));
        g.setFill(Color.BLACK);

        // Letras no topo
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.BOTTOM);
        for (int c = 0; c < col; c++) {
            double x = offsetX + (c + 0.5) * cell;
            g.fillText(String.valueOf((char) ('a' + c)), x, offsetY - 8);
        }

        // Letras no fundo
        g.setTextBaseline(VPos.TOP);
        for (int c = 0; c < col; c++) {
            double x = offsetX + (c + 0.5) * cell;
            g.fillText(String.valueOf((char) ('a' + c)), x, offsetY + cell * row + 8);
        }

        // Números à esquerda
        g.setTextAlign(TextAlignment.RIGHT);
        g.setTextBaseline(VPos.CENTER);
        for (int r = 0; r < row; r++) {
            double y = offsetY + (row - 0.5 - r) * cell;
            g.fillText(String.valueOf(r + 1), offsetX - 8, y);
        }

        // Números à direita
        g.setTextAlign(TextAlignment.LEFT);
        for (int r = 0; r < row; r++) {
            double y = offsetY + (row - 0.5 - r) * cell;
            g.fillText(String.valueOf(r + 1), offsetX + col * cell + 8, y);
        }

    }


    private void drawPieces(GraphicsContext g, double offsetX, double offsetY, double cell, int row, int col) {
        double pad = cell * .1, imgSide = cell - 2 * pad;

        for (int r = 0; r < row; r++)
            for (int c = 0; c < col; c++) {
                Piece p = board.getPiece(c, r);
                if (p == null) continue;

                String key = p.getEPieceType().name().toLowerCase() + (p.isWhiteTeam() ? "W" : "B");
                Image img = pieceImages.get(key);
                if (img == null) continue;

                double x = offsetX + c * cell + pad;
                double y = offsetY + (row - 1 - r) * cell + pad;
                g.drawImage(img, x, y, imgSide, imgSide);
            }
    }


    private void highlight(GraphicsContext g, double offsetX, double offsetY, double cell, int row) {
        if (selected != null) {
            g.setStroke(Color.RED);
            g.setLineWidth(cell * 0.07);

            double x = offsetX + selected.getCol() * cell + 1;
            double y = offsetY + (row - 1 - selected.getRow()) * cell + 1;
            g.strokeRect(x, y, cell - 2, cell - 2);
        }

        if (highlights != null && !highlights.isEmpty()) {
            g.setStroke(Color.web("#FFD700"));
            g.setLineWidth(cell * 0.04);

            for (Position pos : highlights) {
                double x = offsetX + pos.getCol() * cell + 2;
                double y = offsetY + (row - 1 - pos.getRow()) * cell + 2;
                g.strokeRect(x, y, cell - 4, cell - 4);
            }
        }
    }

    public void clearHighlights() {
        highlights.clear();
        draw();
    }
    // Setters
    public void setBoard(GameBoard board) {
        this.board = board;
        draw();
    }

    public void setOnPositionSelected(Consumer<Position> callback) {
        this.onPositionSelected = callback;
    }

    public void setHighlights(List<Position> positions) {
        this.highlights = positions != null ? positions : new ArrayList<>();
        draw();
    }

    public void setSelected(Position pos) {
        this.selected = pos;
        draw();
    }

}
