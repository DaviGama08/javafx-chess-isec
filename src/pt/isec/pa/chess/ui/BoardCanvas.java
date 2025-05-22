package pt.isec.pa.chess.ui;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pt.isec.pa.chess.ui.alerts.AlertManager;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.res.SoundManager;

import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class BoardCanvas extends Canvas implements PropertyChangeListener {
    private static final double MARGIN = 40;
    private final ChessGameManager facade;
    private final AlertManager alertManager;
    private static LinearGradient gradient;
    private final Map<String, Image> pieceImages = new HashMap<>();
    private boolean learningMode;

    private int selCol = -1;
    private int selRow = -1;
    private List<int[]> highlights = new ArrayList<>();

    public BoardCanvas(ChessGameManager facade, AlertManager alertManager) {
        this.facade = facade;
        this.alertManager = alertManager;
        facade.addPropertyChangeListener(ChessGameManager.PROP_BOARD_CHANGED, this);

        createViews();
        registerHandlers();
    }

    public void createViews(){
        gradient = new LinearGradient(
                0, 0, 1, 1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.SADDLEBROWN),
                new Stop(1, Color.BURLYWOOD)
        );
        loadPieceImages();
        updateSize(600, 600);
    }

    public void registerHandlers(){
        setOnMouseClicked(this::handleClick);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try{
            draw();

            String playerName = facade.getPlayerName(facade.isWhiteTurn());
            if (playerName.isEmpty())
                return;

            int[] pp = facade.validatePawnPromotion();
            if (pp != null) {
                Dialog<EPieceType> dlg = new Dialog<>();
                dlg.setTitle("Promoção de Peão");
                dlg.setHeaderText("Escolha a peça para promoção:");

                ButtonType btQ = new ButtonType("Dama",  ButtonBar.ButtonData.OK_DONE);
                ButtonType btR = new ButtonType("Torre", ButtonBar.ButtonData.OK_DONE);
                ButtonType btB = new ButtonType("Bispo", ButtonBar.ButtonData.OK_DONE);
                ButtonType btN = new ButtonType("Cavalo",ButtonBar.ButtonData.OK_DONE);
                dlg.getDialogPane().getButtonTypes().setAll(btQ, btR, btB, btN);

                dlg.setResultConverter(btn -> {
                    if (btn == btQ) return EPieceType.QUEEN;
                    if (btn == btR) return EPieceType.ROOK;
                    if (btn == btB) return EPieceType.BISHOP;
                    if (btn == btN) return EPieceType.KNIGHT;
                    return null;
                });

                dlg.showAndWait().ifPresent(choice -> facade.promotePawn(pp[0],pp[1], choice));

                draw();
            }
        } catch (Exception e) {
            alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao desenhar tabuleiro.",e.getMessage());
        }
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
        int cols = facade.getBoard().getNumCols();
        int rows = facade.getBoard().getNumRows();

        double w = getWidth();
        double h = getHeight();
        double s = Math.min(w - 2 * MARGIN, h - 2 * MARGIN);
        double offsetX = (w - s) / 2;
        double offsetY = (h - s) / 2;
        double cell = s / cols;

        double x = e.getX() - offsetX;
        double y = e.getY() - offsetY;
        if (x < 0 || y < 0 || x >= s || y >= s)
            return;

        int col = (int) (x / cell);
        int row = (int) ((s - y) / cell);

        if (col < 0 || col >= cols || row < 0 || row >= rows)
            return;

        handleBoardClick(col, row);
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
        int cols = facade.getBoard().getNumCols();
        int rows = facade.getBoard().getNumRows();
        GraphicsContext g = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        double s = Math.min(w - 2 * MARGIN, h - 2 * MARGIN);


        double offsetX = (w - s) / 2;
        double offsetY = (h - s) / 2;


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
                if (facade.getBoard().getPiece(c, r)== null) continue;

                String key = facade.getBoard().getPiece(c, r).getEPieceType().name().toLowerCase() +
                        (facade.getBoard().getPiece(c, r).isWhiteTeam() ? "W" : "B");
                Image img = pieceImages.get(key);
                if (img == null) continue;

                double x = offsetX + c * cell + pad;
                double y = offsetY + (row - 1 - r) * cell + pad;
                g.drawImage(img, x, y, imgSide, imgSide);
            }
    }

    private void highlight(GraphicsContext g, double offsetX, double offsetY, double cell, int row) {
        if (selRow != -1 && selCol != -1) {
            g.setStroke(Color.RED);
            g.setLineWidth(cell * 0.07);

            double x = offsetX + selCol * cell + 1;
            double y = offsetY + (row - 1 - selRow) * cell + 1;
            g.strokeRect(x, y, cell - 2, cell - 2);
        }

        if (highlights != null && !highlights.isEmpty()) {
            g.setStroke(Color.web("#FFD700"));
            g.setLineWidth(cell * 0.04);

            for (int[] m : highlights) {
                g.strokeRect(offsetX + m[0] * cell + 2, offsetY + (row - 1 - m[1]) * cell + 2, cell - 4, cell - 4);
            }
        }
    }

    public void clearSelection() {
        selCol = selRow = -1;
        draw();
    }

    // ---------------------- HELPERS INTERNOS --------------
    public void clearHighlights() {
        highlights.clear();
    }

    public void setHighlights(List <int[]> moves) {
        this.highlights = (moves != null) ? moves : new ArrayList<>();
        draw();
    }

    public void setSelected(int col, int row) {
        selCol = col;
        selRow = row;
        draw();
    }

    public void setLearningMode(boolean learningMode){
        this.learningMode = learningMode;
    }

    public boolean getLearningMode(){
        return this.learningMode;
    }


    private void handleBoardClick(int col, int row) {
        if (selCol == -1) {
            if (facade.availableMoveWithoutSelecedPiece(col, row)) return;
            setSelected(col, row);
            if (learningMode) highlights = facade.getValidMoves(col, row);
            draw();
            return;
        }

        if (facade.availableMoveWithSelectedPiece(col, row)) {
            setSelected(col, row);
            if (learningMode) highlights = facade.getValidMoves(col, row);
            draw();
            return;
        }

        if (selCol == col && selRow == row) {
            setSelected(-1, -1);
            clearHighlights();
            draw();
            return;
        }

        if (learningMode && !facade.isMoveValid(selCol, selRow, col, row)) { // helper na fachada
            highlights = facade.getValidMoves(selCol, selRow);
            draw();
            return;
        }

        boolean captured = facade.hasPiece(col, row); // helper: há peça na casa destino
        boolean moved = facade.movePiece(selCol, selRow, col, row);
        if (moved) {
            playMoveAudioSequence(captured, col, row);
            setSelected(-1, -1);
            clearHighlights();
            draw();
        }
    }
    private void playMoveAudioSequence(boolean captured, int toCol, int toRow) {
        EPieceType type = facade.getPieceTypeAt(toCol, toRow);

        String pieceMp3 = switch (type) {
            case KING   -> "king.mp3";
            case QUEEN  -> "queen.mp3";
            case ROOK   -> "rook.mp3";
            case BISHOP -> "bishop.mp3";
            case KNIGHT -> "knight.mp3";
            default     -> "pawn.mp3";
        };

        char fromCol = (char) ('a' + selCol);
        char fromRow = (char) ('1' + selRow);
        char tCol = (char) ('a' + toCol);
        char tRow = (char) ('1' + toRow);

        boolean check = facade.isCheck();

        var seq = new java.util.ArrayList<String>();
        seq.add(captured ? "capture.mp3" : "castle.mp3");
        if (check)    seq.add("event-warning.mp3");
        seq.add(pieceMp3);
        seq.add(fromCol + ".mp3");
        seq.add(fromRow + ".mp3");
        seq.add(tCol + ".mp3");
        seq.add(tRow + ".mp3");

        SoundManager.playSequence(seq);
    }
}
