package 2048;

/*
 * The class implements the strategy that calculates the best result for the next move
 */
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        if (this.numberOfEmptyTiles == o.numberOfEmptyTiles) {
            return Integer.compare(this.score, o.score);
        } else {
            return Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
        }
    }
}
