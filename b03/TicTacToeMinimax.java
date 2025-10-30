import java.util.*;

/**
 *
 * Player encoding:
 *  'X' = maximizing player (we compute best move for X)
 *  'O' = minimizing player
 *
 * Utility:
 *  win for X -> +1
 *  win for O -> -1
 *  draw -> 0
 *
 */
public class TicTacToeMinimax {

    // Board is 3x3 stored in array index 0..8
    static final char EMPTY = '.';
    static final char X = 'X'; // MAX
    static final char O = 'O'; // MIN

    // Counters for nodes visited
    static long maxNodesVisited;
    static long minNodesVisited;

    // Toggle pruning per run
    static boolean useAlphaBeta = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("TicTacToe Minimax demo");
        System.out.println("Choose mode: (y) with alpha-beta pruning, (n) no pruning, (b) both");
        System.out.print("> ");
        String line = sc.nextLine().trim().toLowerCase();
        if (line.equals("b")) {
            runBenchmark();
            sc.close();
            return;
        }

        useAlphaBeta = line.equals("y");

        char[] board = new char[9];
        Arrays.fill(board, EMPTY);


        printBoard(board);

        // choose which player's turn it is by counting
        char toMove = nextPlayer(board);
        System.out.println("To move: " + toMove);

        // Compute best move for X
        if (toMove == X) {
            System.out.println("Computing best move for X (MAX) with pruning = " + useAlphaBeta);
            maxNodesVisited = 0;
            minNodesVisited = 0;
            long start = System.currentTimeMillis();
            MoveResult best = findBestMove(board, X);
            long dur = System.currentTimeMillis() - start;
            System.out.println("Best move index: " + best.move + " (row,col = " +
                    (best.move/3) + "," + (best.move%3) + "), value=" + best.value);
            System.out.println("Nodes visited (MAX calls): " + maxNodesVisited);
            System.out.println("Nodes visited (MIN calls): " + minNodesVisited);
            System.out.println("Time: " + dur + " ms");
        } else {
            System.out.println("Computing best move for O (MIN) with pruning = " + useAlphaBeta);
            maxNodesVisited = 0;
            minNodesVisited = 0;
            long start = System.currentTimeMillis();
            MoveResult best = findBestMove(board, O);
            long dur = System.currentTimeMillis() - start;
            System.out.println("Best move index: " + best.move + " (row,col = " +
                    (best.move/3) + "," + (best.move%3) + "), value=" + best.value);
            System.out.println("Nodes visited (MAX calls): " + maxNodesVisited);
            System.out.println("Nodes visited (MIN calls): " + minNodesVisited);
            System.out.println("Time: " + dur + " ms");
        }

        sc.close();
    }

    /** run empty-board search with and without pruning and print node counts */
    static void runBenchmark() {
        char[] empty = new char[9];
        Arrays.fill(empty, EMPTY);

        System.out.println("Benchmark on empty board (best move for X). This may take some time but TicTacToe is small.");
        // Without pruning
        useAlphaBeta = false;
        maxNodesVisited = 0;
        minNodesVisited = 0;
        long t0 = System.currentTimeMillis();
        MoveResult rNo = findBestMove(empty, X);
        long t1 = System.currentTimeMillis();
        System.out.println("WITHOUT pruning:");
        System.out.println(" best move = " + rNo.move + " value=" + rNo.value);
        System.out.println(" MAX nodes: " + maxNodesVisited + " MIN nodes: " + minNodesVisited);
        System.out.println(" time: " + (t1-t0) + " ms");
        System.out.println();

        // With pruning
        useAlphaBeta = true;
        maxNodesVisited = 0;
        minNodesVisited = 0;
        t0 = System.currentTimeMillis();
        MoveResult rYes = findBestMove(empty, X);
        t1 = System.currentTimeMillis();
        System.out.println("WITH alpha-beta pruning:");
        System.out.println(" best move = " + rYes.move + " value=" + rYes.value);
        System.out.println(" MAX nodes: " + maxNodesVisited + " MIN nodes: " + minNodesVisited);
        System.out.println(" time: " + (t1-t0) + " ms");
    }

    /** Determine whose turn it is from board: if X count <= O count => X moves, else O moves */
    static char nextPlayer(char[] board) {
        int cx = 0, co = 0;
        for (char c: board) {
            if (c == X) cx++;
            else if (c == O) co++;
        }
        return (cx <= co) ? X : O;
    }

    /** Represents the result of searching: value and chosen move index (0..8) */
    static class MoveResult {
        int move; // index 0..8, -1 if none
        int value;
        MoveResult(int m, int v) { move = m; value = v; }
    }

    /** Find best move for player (X or O) from state */
    static MoveResult findBestMove(char[] board, char player) {
        // At root we pick action that maximizes (for X) or minimizes (for O)
        if (player == X) {
            int bestVal = Integer.MIN_VALUE;
            int bestMove = -1;
            if (useAlphaBeta) {
                int alpha = Integer.MIN_VALUE/4;
                int beta  = Integer.MAX_VALUE/4;
                for (int m: legalMoves(board)) {
                    char[] nb = boardCloneWithMove(board, m, X);
                    int v = minValue(nb, alpha, beta);
                    if (v > bestVal) { bestVal = v; bestMove = m; }
                    alpha = Math.max(alpha, bestVal);
                }
                return new MoveResult(bestMove, bestVal);
            } else {
                for (int m: legalMoves(board)) {
                    char[] nb = boardCloneWithMove(board, m, X);
                    int v = minValueNoPrune(nb);
                    if (v > bestVal) { bestVal = v; bestMove = m; }
                }
                return new MoveResult(bestMove, bestVal);
            }
        } else { // player == O (MIN)
            int bestVal = Integer.MAX_VALUE;
            int bestMove = -1;
            if (useAlphaBeta) {
                int alpha = Integer.MIN_VALUE/4;
                int beta  = Integer.MAX_VALUE/4;
                for (int m: legalMoves(board)) {
                    char[] nb = boardCloneWithMove(board, m, O);
                    int v = maxValue(nb, alpha, beta);
                    if (v < bestVal) { bestVal = v; bestMove = m; }
                    beta = Math.min(beta, bestVal);
                }
                return new MoveResult(bestMove, bestVal);
            } else {
                for (int m: legalMoves(board)) {
                    char[] nb = boardCloneWithMove(board, m, O);
                    int v = maxValueNoPrune(nb);
                    if (v < bestVal) { bestVal = v; bestMove = m; }
                }
                return new MoveResult(bestMove, bestVal);
            }
        }
    }

    // ---------- Minimax without pruning ----------

    static int maxValueNoPrune(char[] board) {
        maxNodesVisited++;
        Integer term = terminalValue(board);
        if (term != null) return term;
        int v = Integer.MIN_VALUE;
        for (int m : legalMoves(board)) {
            char[] nb = boardCloneWithMove(board, m, X);
            v = Math.max(v, minValueNoPrune(nb));
        }
        return v;
    }

    static int minValueNoPrune(char[] board) {
        minNodesVisited++;
        Integer term = terminalValue(board);
        if (term != null) return term;
        int v = Integer.MAX_VALUE;
        for (int m : legalMoves(board)) {
            char[] nb = boardCloneWithMove(board, m, O);
            v = Math.min(v, maxValueNoPrune(nb));
        }
        return v;
    }

    // ---------- Minimax with alpha-beta pruning ----------

    static int maxValue(char[] board, int alpha, int beta) {
        maxNodesVisited++;
        Integer term = terminalValue(board);
        if (term != null) return term;
        int v = Integer.MIN_VALUE;
        for (int m : legalMoves(board)) {
            char[] nb = boardCloneWithMove(board, m, X);
            v = Math.max(v, minValue(nb, alpha, beta));
            if (v >= beta) return v; // beta cutoff
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    static int minValue(char[] board, int alpha, int beta) {
        minNodesVisited++;
        Integer term = terminalValue(board);
        if (term != null) return term;
        int v = Integer.MAX_VALUE;
        for (int m : legalMoves(board)) {
            char[] nb = boardCloneWithMove(board, m, O);
            v = Math.min(v, maxValue(nb, alpha, beta));
            if (v <= alpha) return v; // alpha cutoff
            beta = Math.min(beta, v);
        }
        return v;
    }

    // ---------- Utilities ----------

    /** deep copy and apply move */
    static char[] boardCloneWithMove(char[] b, int move, char player) {
        char[] nb = Arrays.copyOf(b, b.length);
        nb[move] = player;
        return nb;
    }

    /** return list of empty indices */
    static List<Integer> legalMoves(char[] board) {
        List<Integer> moves = new ArrayList<>(9);
        for (int i = 0; i < 9; ++i) if (board[i] == EMPTY) moves.add(i);
        return moves;
    }

    /** Check terminal state. Returns +1 for X win, -1 for O win, 0 for draw, or null if non-terminal */
    static Integer terminalValue(char[] b) {
        // rows, cols, diags
        int[][] lines = {
            {0,1,2}, {3,4,5}, {6,7,8},
            {0,3,6}, {1,4,7}, {2,5,8},
            {0,4,8}, {2,4,6}
        };
        for (int[] L : lines) {
            if (b[L[0]] != EMPTY && b[L[0]] == b[L[1]] && b[L[1]] == b[L[2]]) {
                return (b[L[0]] == X) ? +1 : -1;
            }
        }
        // draw?
        for (int i = 0; i < 9; ++i) if (b[i] == EMPTY) return null;
        return 0;
    }

    static void printBoard(char[] board) {
        System.out.println("Board:");
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 3; ++c) {
                System.out.print(board[r*3 + c] + " ");
            }
            System.out.println();
        }
    }
}
