# ♟️ ChessBoard — Java Chess Game

A fully-featured, two-player chess game built in **Java** using **Swing** for the GUI. The game enforces all standard chess rules including special moves, and features a smooth animated board-flip between turns.

---

##  Features

- **Full Chess Rule Enforcement**
  - Legal move generation for all 6 piece types
  - Check detection — illegal moves that leave the King in check are filtered out
  - Checkmate & stalemate detection with dialog notification
- **Special Moves**
  - ♟ En Passant
  - ♜ Castling (both kingside and queenside)
  - ♟ Pawn Promotion (automatic)
- **Interactive GUI**
  - Click to select a piece — valid destinations are highlighted in green (empty) or red (capture)
  - Selected piece square is highlighted in teal
  - Board animates a 180° flip after each turn, giving each player their own perspective
- **Piece Tracking**
  - Captured pieces are tracked in separate lists for white and black
- **Coordinate System**
  - Internally uses standard chess notation (`a–h`, `1–8`) with seamless conversion to array indices

---

##  Project Structure

```
ChessBoard/
└── src/
    ├── Main.java                    # Entry point (legacy/testing)
    ├── board/
    │   └── ChessBoard.java          # Core board logic, rendering, move execution, game state
    ├── pieces/
    │   ├── Piece.java               # Abstract base class for all pieces
    │   ├── PieceIdentification.java # Enum identifying each piece type and color
    │   ├── Pawn.java                # Pawn movement, en passant, promotion
    │   ├── Knight.java              # Knight L-shaped movement
    │   ├── Bishop.java              # Bishop diagonal movement
    │   ├── Rook.java                # Rook straight-line movement
    │   ├── Queen.java               # Queen combined movement
    │   └── King.java                # King movement, castling, check-awareness
    ├── Global/
    │   └── Global.java              # Debug utility methods (print 1D/2D arrays)
    └── pieceImages/
        ├── white/                   # PNG images for white pieces
        └── black/                   # PNG images for black pieces
```

---

##  Architecture Overview

### `Piece` (Abstract Base Class)
All chess pieces extend `Piece`. Each subclass implements:
- **`moveCheck()`** — Populates `moveSet` (all candidate squares) and `validMoveSet` (which are legal).
- **`movePiece()`** — Hook for special move side effects.

The base class provides:
- `getValidMoveSet()` — Calls `moveCheck()` then runs `filterCheckMoves()` to strip any move that would leave the King in check (using board simulation).
- `getValidMoveSetRaw()` — Returns the raw validity array without check-filtering (used internally to avoid infinite recursion).
- Coordinate conversion utilities: `chessColToIndex`, `chessRowToIndex`, `colToChessCol`, `rowToChessRow`.

### `ChessBoard` (JPanel)
The central class responsible for:
- **Rendering** — Draws the board, piece images, selection highlights, and valid-move overlays.
- **Input handling** — `MouseAdapter` delegates to `selectPiece()` and `movePiece()`.
- **Game state** — Tracks whose turn it is via the `flipped` flag. After every legal move, the board animates a 180° rotation so each player always sees their pieces at the bottom.
- **Game-over detection** — `checkGameOver()` checks for checkmate and stalemate after every move.
- **Static board state** — The `Piece[][] board` array is the single source of truth for all piece positions.

### Move Validation Flow
```
User clicks a square
    └── selectPiece()         → stores selected [row, col]
    └── movePiece()
          └── piece.getValidMoveSet()
                └── moveCheck()         (piece-specific rules)
                └── filterCheckMoves()  (simulate each move, reject if King is in check)
          └── If destination is in valid set → execute move
                └── Handle en passant capture
                └── Handle castling rook repositioning
                └── Handle pawn promotion
                └── flipBoard() → animate 180° rotation
                └── checkGameOver()
```

### `PieceIdentification` (Enum)
Encodes both the piece type and color (`W_PAWN`, `B_KING`, etc.). Each constant carries an `isWhite` flag for quick color checks.

---

##  Getting Started

### Prerequisites
- **Java 21+** (uses pattern matching `switch` and unnamed main methods)
- An IDE such as **IntelliJ IDEA** is recommended (project includes `.iml` and `.idea` configs)

### Running the Game
1. Open the project in IntelliJ IDEA.
2. Mark `src/` as the **Sources Root**.
3. Run `board.ChessBoard` — the `main` method inside `ChessBoard.java` launches the game window.

> **Note:** `Main.java` contains an older test entry point and is not the primary launcher.

---

##  How to Play

| Action | How |
|--------|-----|
| Select a piece | Click on any of your pieces (white plays first, board unflipped) |
| See valid moves | Green overlay = empty square; Red overlay = capturable enemy |
| Move a piece | Click a highlighted destination square |
| Deselect | Click an empty square or outside the board |
| Turn switch | The board smoothly flips 180° automatically after each valid move |

Special moves happen automatically:
- **En passant** is offered in the turn immediately after a double pawn push.
- **Castling** is available when the King and Rook haven't moved and the path is clear.
- **Promotion** triggers automatically when a pawn reaches the back rank.

---

##  Key Design Decisions

- **Board-flip perspective** — Rather than switching coordinate logic, the board physically rotates so each player's pieces are always at the bottom, making the game feel natural for local two-player play.
- **Simulation-based check filtering** — Every candidate move is simulated on the real board array, the King's safety is checked, and then the board is restored. This is a clean, piece-agnostic approach that handles pins and discovered checks automatically.
- **Static board array** — `ChessBoard.board` is `static` so all `Piece` subclasses can access global board state without dependency injection, simplifying move validation logic in each piece class.
- **`getValidMoveSetRaw()`** — Exposes the unfiltered move validity array to break the recursion cycle that would occur if `isSquareAttacked()` called `getValidMoveSet()` (which itself calls `isSquareAttacked()`).

---

##  Dependencies

This project uses only the **Java Standard Library**:
- `javax.swing` — GUI framework
- `java.awt` — Graphics and event handling
- No external libraries or build tools required

---

##  Potential Future Improvements

- [ ] Move history / algebraic notation log
- [ ] Captured piece display panels (sidebar)
- [ ] Draw conditions: 50-move rule, threefold repetition, insufficient material
- [ ] AI opponent (Minimax with alpha-beta pruning)
- [ ] Network multiplayer support
- [ ] Save / load game state (PGN format)
- [ ] Promotion piece selection dialog

---

##  License

This project is for educational/personal use. No license is currently specified.
