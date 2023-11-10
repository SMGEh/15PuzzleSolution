package fifteenpuzzle;

import java.util.Arrays;

public class HashSetKey {
    private int[][] board;
    public HashSetKey(int[][] board){
        this.board=board;
    }
    @Override
    public boolean equals(Object o){
        return Arrays.deepEquals(board,((HashSetKey) o).board);
    }
    @Override
    public int hashCode(){
        return Arrays.deepHashCode(board);
    }
}
