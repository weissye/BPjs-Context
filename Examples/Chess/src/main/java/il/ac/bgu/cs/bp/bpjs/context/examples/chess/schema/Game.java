package il.ac.bgu.cs.bp.bpjs.context.examples.chess.schema;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "ChangeGameState", query = "Update Game g set g.state=:state where g=:game"),
        @NamedQuery(name = "ChangeMyColor", query = "Update Game g set g.myColor=:color where g=:game"),
        @NamedQuery(name = "GameStateInit", query = "SELECT g FROM Game g WHERE g.state='INIT'"),
        @NamedQuery(name = "GameStatePlaying", query = "SELECT g FROM Game g WHERE g.state='PLAYING'"),
        @NamedQuery(name = "GameStateGameOver", query = "SELECT g FROM Game g WHERE g.state='GAME_OVER'"),
}) public class Game extends BasicEntity {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game", orphanRemoval = true)
    private final List<Cell> board;

    @Enumerated(EnumType.STRING)
    public final GameState state = GameState.INIT;

    @Enumerated(EnumType.STRING)
    private final Color myColor;

    protected Game() {
        super("Game");
        myColor = Color.White;
        this.board = null;
    }

    public Game(Cell[] board) {
        super("Game");
        myColor = Color.White;
        this.board = Arrays.asList(board);
    }

    public Color MyColor() {
        return myColor;
    }

    public Color OpponentColor() {
        return Color.values()[(myColor.ordinal()+1) % 2];
    }

    public Cell Board(int i, int j) {
        return board.get(8*i + j);
    }

    public enum GameState {
        INIT,
        PLAYING,
        GAME_OVER
    }
}

