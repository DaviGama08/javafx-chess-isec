package pt.isec.pa.chess.model.data.Pieces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.game.Position;
import pt.isec.pa.chess.model.data.game.PositionData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    private GameBoard board;

    @BeforeEach
    void setup() {
        board = new GameBoard();
        board.clearBoard();
    }

    @Test
    @DisplayName("movePiece – peão branco move-se uma casa para a frente")
    void testMovePiece() {
        // Arrange
        board.addPiece(EPieceType.PAWN, true, 0, 1);
        int srcCol = 0, srcRow = 1;   // peão branco
        int dstCol = 0, dstRow = 2;   // destino imediato

        // Act
        boolean moved = board.movePiece(srcCol, srcRow, dstCol, dstRow);

        // Assert
        assertTrue(moved, "Peão deveria mover-se");
        assertNull(board.getPiece(srcCol, srcRow));        // origem vazia
        assertNotNull(board.getPiece(dstCol, dstRow));     // destino ocupado
    }

    @Test
    @DisplayName("isPathClear – caminho livre numa coluna vazia")
    void testIsPathClear() {
        // Arrange – tabuleiro limpo com torre em (0,0)
        board.addPiece(EPieceType.ROOK, true, 0, 0);

        // Act
        boolean clear = board.isPathClear(0, 0, 0, 5);

        // Assert
        assertTrue(clear, "Não há peças a bloquear a coluna");
    }

    @Test
    @DisplayName("validatePawnPromotion – deteta peão branco na oitava linha")
    void testValidatePawnPromotion() {
        // Arrange – tabuleiro limpo, rei branco para evitar xeque e peão em (0,7)
        board.addPiece(EPieceType.KING, true, 4, 0);      // rei branco seguro
        board.addPiece(EPieceType.PAWN, true, 0, 7);      // peão pronto a promover

        // Act
        PositionData pos = board.validatePawnPromotion();

        // Assert
        assertNotNull(pos, "Deveria detetar promoção");
        assertEquals(0, pos.col());
        assertEquals(7, pos.row());
    }

    @Test
    @DisplayName("isKingInCheck – rei branco em xeque por torre preta na mesma coluna")
    void testIsKingInCheck() {
        // Arrange
        board.addPiece(EPieceType.KING,  true,  4, 0);
        board.addPiece(EPieceType.ROOK, false, 4, 7);

        // Act
        boolean inCheck = board.isKingInCheck(true);

        // Assert
        assertTrue(inCheck, "Rei branco deveria estar em xeque");
    }

    @Test
    @DisplayName("getValidMoves – filtro remove movimentos que deixam o rei em xeque")
    void testGetValidMoves() {
        // Arrange
        // Peças brancas
        board.addPiece(EPieceType.KING,  true, 4, 0);
        board.addPiece(EPieceType.ROOK,  true, 0, 0);
        // Peças pretas
        board.addPiece(EPieceType.KING, false, 7, 7);
        board.addPiece(EPieceType.ROOK, false, 4, 7);   // ameaça rei branco

        Position rookPos = new Position(0, 0);

        // Act
        List<Position> legal = board.getValidMoves(rookPos, true);

        // Assert
        // A torre não poderá sair da coluna 0 (qualquer saída deixaria o rei em xeque)
        assertTrue(legal.isEmpty(), "Nenhum movimento deveria ser legal enquanto o rei estiver em xeque");
    }

    @Test
    @DisplayName("isCheckMate – mate simples do rei preto encurralado por torre e rainha brancas")
    void testIsCheckMate() {
        // Arrange
        // Rei preto encurralado no canto
        board.addPiece(EPieceType.KING, false, 0, 0);
        // Peças brancas de mate
        board.addPiece(EPieceType.KING,  true, 4, 4);  // rei branco fora do caminho
        board.addPiece(EPieceType.ROOK,  true, 0, 1);  // corta fuga vertical
        board.addPiece(EPieceType.QUEEN, true, 1, 1);  // dá xeque e corta fugas horizontais/diagonais

        // Act
        boolean mate = board.isCheckMate(false);       // verifica se preto está em mate

        // Assert
        assertTrue(mate, "Rei preto deveria estar em cheque-mate");
    }

    @Test
    @DisplayName("Stalemate – rei preto afogado no canto h8")
    void testStalemateCorner() {
        // peças pretas
        board.addPiece(EPieceType.KING, false, 7, 7);   // h8
        // peças brancas
        board.addPiece(EPieceType.KING,  true, 5, 6);   // f7
        board.addPiece(EPieceType.QUEEN, true, 6, 5);   // g6

        assertFalse(board.isKingInCheck(false), "Rei preto não deve estar em xeque");
        assertTrue (board.isStalemate(false),   "Posição deveria ser afogamento");
        assertFalse(board.isCheckMate(false),   "Não é xeque-mate");
    }

    @Test
    @DisplayName("Empate por material insuficiente – rei contra rei")
    void testDrawKingVsKing() {
        board.addPiece(EPieceType.KING, true,  4, 0);
        board.addPiece(EPieceType.KING, false, 4, 7);

        assertTrue(board.isInsufficientMaterial(), "Rei contra rei deve ser empate por material");
    }

    @Test
    @DisplayName("Empate por material insuficiente – rei + cavalo vs rei")
    void testDrawKingKnightVsKing() {
        // lado branco possui rei e cavalo
        board.addPiece(EPieceType.KING,   true, 4, 0);
        board.addPiece(EPieceType.KNIGHT, true, 1, 2);
        // lado preto apenas rei
        board.addPiece(EPieceType.KING,  false, 4, 7);

        assertTrue(board.isInsufficientMaterial(), "Rei + cavalo vs rei deve ser empate por material");
    }

    @Test
    @DisplayName("Empate por material insuficiente – rei + bispo vs rei")
    void testDrawKingBishopVsKing() {
        // lado branco possui rei e bispo
        board.addPiece(EPieceType.KING,  true, 4, 0);
        board.addPiece(EPieceType.BISHOP,true, 2, 2);
        // lado preto apenas rei
        board.addPiece(EPieceType.KING, false, 4, 7);

        assertTrue(board.isInsufficientMaterial(), "Rei + bispo vs rei deve ser empate por material");
    }

    @Test
    @DisplayName("Castling moves both king and rook when the route is safe")
    void castlingMovesKingAndRook() {
        board.addPiece(EPieceType.KING, true, 4, 0);
        board.addPiece(EPieceType.ROOK, true, 7, 0);

        assertTrue(board.movePiece(4, 0, 6, 0));
        assertEquals(EPieceType.KING, board.getPiece(6, 0).getEPieceType());
        assertEquals(EPieceType.ROOK, board.getPiece(5, 0).getEPieceType());
        assertNull(board.getPiece(7, 0));
    }

    @Test
    @DisplayName("Castling cannot cross an attacked square")
    void castlingThroughCheckIsRejected() {
        board.addPiece(EPieceType.KING, true, 4, 0);
        board.addPiece(EPieceType.ROOK, true, 7, 0);
        board.addPiece(EPieceType.ROOK, false, 5, 7);

        assertFalse(board.movePiece(4, 0, 6, 0));
        assertEquals(EPieceType.KING, board.getPiece(4, 0).getEPieceType());
        assertEquals(EPieceType.ROOK, board.getPiece(7, 0).getEPieceType());
    }

    @Test
    @DisplayName("En passant captures only immediately after a two-square pawn move")
    void enPassantCapturesImmediately() {
        board.addPiece(EPieceType.PAWN, true, 4, 4);
        board.addPiece(EPieceType.PAWN, false, 5, 6);

        assertTrue(board.movePiece(5, 6, 5, 4));
        assertTrue(board.movePiece(4, 4, 5, 5));
        assertNull(board.getPiece(5, 4));
        assertTrue(board.getPiece(5, 5).isWhiteTeam());
    }

    @Test
    @DisplayName("En passant expires after an intervening move")
    void enPassantWindowExpires() {
        board.addPiece(EPieceType.PAWN, true, 4, 4);
        board.addPiece(EPieceType.PAWN, false, 5, 6);
        board.addPiece(EPieceType.ROOK, true, 0, 0);

        assertTrue(board.movePiece(5, 6, 5, 4));
        assertTrue(board.movePiece(0, 0, 0, 1));
        assertFalse(board.movePiece(4, 4, 5, 5));
        assertNotNull(board.getPiece(5, 4));
    }

    @Test
    @DisplayName("Promotion accepts only standard promotion pieces on the back rank")
    void promotionValidatesTypeAndRank() {
        board.addPiece(EPieceType.PAWN, true, 0, 7);

        board.promotePawn(new Position(0, 7), EPieceType.KING);
        assertEquals(EPieceType.PAWN, board.getPiece(0, 7).getEPieceType());

        board.promotePawn(new Position(0, 7), EPieceType.QUEEN);
        assertEquals(EPieceType.QUEEN, board.getPiece(0, 7).getEPieceType());
    }
}
