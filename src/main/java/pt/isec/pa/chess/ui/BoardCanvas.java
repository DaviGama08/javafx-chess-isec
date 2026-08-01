package pt.isec.pa.chess.ui;
import javafx.application.Platform;
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
import java.util.*;

public class BoardCanvas extends Canvas {
    private static final double MARGIN = 40;
    private final ChessGameManager facade;
    private static LinearGradient gradient;
    private final Map<String, Image> pieceImages = new HashMap<>();
    private boolean learningMode;
    private boolean editMode;

    private int selCol = -1;
    private int selRow = -1;
    private List<int[]> highlights = new ArrayList<>();

    public BoardCanvas(ChessGameManager facade) {
        this.facade = facade;

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
        facade.addPropertyChangeListener(ChessGameManager.PROP_BOARD_CHANGED, this::handleBoardChange);
        facade.addPropertyChangeListener(ChessGameManager.PROP_GAME_STARTED, this::handleNewGame);
        facade.addPropertyChangeListener(ChessGameManager.PROP_UNDO_PERFORMED, this::onUndoPerformed);
        facade.addPropertyChangeListener(ChessGameManager.PROP_REDO_PERFORMED, this::onRedoPerformed);
        facade.addPropertyChangeListener(ChessGameManager.PROP_GAME_OVER, this::handleQuitGame);
        facade.addPropertyChangeListener(ChessGameManager.PROP_LEARNING_MODE_CHANGED, this::handleLearningModeChanged);
        facade.addPropertyChangeListener(ChessGameManager.PROP_EDIT_MODE, this::handleEditModeChanged);

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

        double h = getHeight();
        double w = getWidth();
        double s = Math.min(w - 2 * MARGIN, h - 2 * MARGIN);
        double cell = s / cols;
        double offsetY = (h - s) / 2;
        double offsetX = (w - s) / 2;

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
        if(facade.getBoard() == null)
            return;
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

                double x = offsetX + c * cell + pad;
                double y = offsetY + (row - 1 - r) * cell + pad;
                if (img != null) {
                    g.drawImage(img, x, y, imgSide, imgSide);
                } else {
                    EPieceType type = facade.getBoard().getPiece(c, r).getEPieceType();
                    boolean white = facade.getBoard().getPiece(c, r).isWhiteTeam();
                    g.setFill(Color.BLACK);
                    g.setFont(Font.font("Serif", cell * .72));
                    g.setTextAlign(TextAlignment.CENTER);
                    g.setTextBaseline(VPos.CENTER);
                    g.fillText(unicodePiece(type, white), x + imgSide / 2, y + imgSide / 2);
                }
            }
    }

    private static String unicodePiece(EPieceType type, boolean white) {
        return switch (type) {
            case KING -> white ? "♔" : "♚";
            case QUEEN -> white ? "♕" : "♛";
            case ROOK -> white ? "♖" : "♜";
            case BISHOP -> white ? "♗" : "♝";
            case KNIGHT -> white ? "♘" : "♞";
            case PAWN -> white ? "♙" : "♟";
        };
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

    //----------------------- Menu Handles -----------------

    public void handleNewGame(PropertyChangeEvent evt){
        this.setDisable(false);
        Platform.runLater(this::draw);
        clearHighlights();
        draw();
    }

    public void handleBoardChange(PropertyChangeEvent evt) {
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
            AlertManager.getInstance().launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao desenhar tabuleiro.",e.getMessage());
        }
    }

    private void handleQuitGame(PropertyChangeEvent evt){
        setDisable(true);
        clearHighlights();
        setSelected(-1, -1);
    }

    private void handleLearningModeChanged(PropertyChangeEvent evt){
        this.learningMode = (boolean) evt.getNewValue();
        clearHighlights();
        draw();
    }

    private void handleEditModeChanged(PropertyChangeEvent evt) {
        this.editMode = (boolean) evt.getNewValue();
        if(editMode){
            clearHighlights();
            draw();
        }
        else{
            setDisable(true);
            clearHighlights();
            setSelected(-1, -1);
        }
    }


    // ---------------------- HELPERS INTERNOS --------------
    public void clearHighlights() {
        highlights.clear();
    }

    public void setSelected(int col, int row) {
        selCol = col;
        selRow = row;
        draw();
    }

    private void handleBoardClick(int col, int row) {
        if (editMode) {
            List<ButtonType> options;
            if (facade.hasPiece(col, row)) {
                options = List.of(
                        new ButtonType("Editar peça",   ButtonBar.ButtonData.APPLY),
                        new ButtonType("Remover peça",  ButtonBar.ButtonData.NO)
                );
            } else {
                options = List.of(
                        new ButtonType("Adicionar peça", ButtonBar.ButtonData.YES)
                );
            }

            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setTitle("Edit Mode");
            dlg.setHeaderText("Casa " + (char)('a' + col) + (row + 1));
            dlg.getDialogPane().getButtonTypes().setAll(options);

            dlg.showAndWait().ifPresent(bt -> {
                String action = bt.getText();
                switch (action) {
                    case "Remover peça" -> {
                        if (facade.hasPiece(col, row))
                            if(!facade.removePiece(col, row)){
                                AlertManager.getInstance()
                                        .launchAlertBox(Alert.AlertType.ERROR,
                                                "Erro","Erro ao remover a peça",
                                                "Não foi possível remover a peça.");
                            }

                    }
                    case "Editar peça" -> {
                        if (facade.hasPiece(col, row)) {
                            addPiece(col, row, true);
                        }
                    }
                    case "Adicionar peça" -> addPiece(col, row, false);

                }
                draw();
            });
            return;
        }
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

    private void addPiece(int col, int row, boolean withRemove) {
        Dialog<Boolean> colorDlg = new Dialog<>();
        colorDlg.setTitle("Adicionar peça");
        colorDlg.setHeaderText("Selecione a cor da peça:");
        colorDlg.getDialogPane().getButtonTypes().setAll(
                new ButtonType("Branca", ButtonBar.ButtonData.YES),
                new ButtonType("Preta",  ButtonBar.ButtonData.NO),
                ButtonType.CANCEL
        );
        colorDlg.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.YES) return true;
            if (btn.getButtonData() == ButtonBar.ButtonData.NO)  return false;
            return null;
        });

        Optional<Boolean> optIsWhite = colorDlg.showAndWait();
        if (optIsWhite.isEmpty())
            return;
        boolean isWhite = optIsWhite.get();

        Dialog<EPieceType> typeDlg = new Dialog<>();
        typeDlg.setTitle("Adicionar peça");
        typeDlg.setHeaderText("Escolha o tipo de peça:");
        typeDlg.getDialogPane().getButtonTypes().setAll(
                new ButtonType("Peão",   ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Torre",  ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Cavalo", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Bispo",  ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Dama",   ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Rei",    ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL
        );

        typeDlg.setResultConverter(btn -> {
            if (btn == null || btn.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return null;
            return switch (btn.getText()) {
                case "Peão"   -> EPieceType.PAWN;
                case "Torre"  -> EPieceType.ROOK;
                case "Cavalo" -> EPieceType.KNIGHT;
                case "Bispo"  -> EPieceType.BISHOP;
                case "Dama"   -> EPieceType.QUEEN;
                case "Rei"    -> EPieceType.KING;
                default       -> null;
            };
        });

        Optional<EPieceType> optType = typeDlg.showAndWait();
        if (optType.isEmpty())
            return;
        EPieceType type = optType.get();

        if (withRemove) {
            facade.removePiece(col, row);
        }

        boolean success = facade.addPiece(type, isWhite, col, row);
        if (!success) {
            AlertManager.getInstance()
                    .launchAlertBox(Alert.AlertType.ERROR,
                            "Erro",
                            "Não foi possível criar a peça:"," posição inválida ou já ocupada.");
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

    private void onUndoPerformed(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            clearSelection();
            clearHighlights();
            draw();
        });
    }

    private void onRedoPerformed(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            clearSelection();
            clearHighlights();
            draw();
        });
    }

}
