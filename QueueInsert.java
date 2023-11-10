package fifteenpuzzle;

import java.util.LinkedList;

public class QueueInsert implements Comparable<Object>{

    /**
     * class to have data types inserted together into the heap for on element
     */
    private LinkedList<int[]> moveList;
    private int moveNum;
    private int heuristic;
    private int[][] board;
    private int[] blankLoc;
    public QueueInsert(LinkedList<int[]> moveList, int moveNum, int heuristic, int[][] board,int[] blankLoc){
        this.moveList=moveList;
        this.moveNum=moveNum;
        this.heuristic=heuristic;
        this.board=board;
        this.blankLoc=blankLoc;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public int getMoveNum() {
        return moveNum;
    }

    public LinkedList<int[]> getMoveList() {
        return moveList;
    }
    public int[][] getBoard(){
        return board;
    }
    public int[] getBlankLoc(){return blankLoc;}

    @Override
    public int compareTo(Object o) {
        QueueInsert temp=(QueueInsert) (o);
        return (heuristic)-(temp.getHeuristic());
    }


    @Override
    public String toString() {

        String string="(";
        for(int i=0;i<moveList.size();i++){
            string+="["+moveList.get(i)[0]+", "+moveList.get(i)[1]+"], ";
        }
        string+=")";
        return "QueueInsert{" +
                "moveList=" + string +
                ", moveNum=" + moveNum +
                ", heuristic=" + heuristic +
                '}';
    }
}

