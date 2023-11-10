package fifteenpuzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.*;


import static fifteenpuzzle.DirectionInterface.*;



public class Solver {

	public static void main(String[] args) {
//		System.out.println("number of arguments: " + args.length);
//		for (int i = 0; i < args.length; i++) {
//			System.out.println(args[i]);
//		}

		if (args.length < 2) {
			System.out.println("File names are not specified");
			System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file"); //heuristic is amount of theoretical moves to get to goal state
			return;
		}

		// TODO
		int[][] puzzle;
		try {
			puzzle=getPuzzle(args[0]);
		}
		catch(FileNotFoundException e){
			System.out.println(e);
			return;
		}

		// solve...
		PriorityQueue<QueueInsert> PQ=new PriorityQueue<>();// create priorityQueue
		HashSet<HashSetKey> hashSet=new HashSet<>();
		int heuristicValue=heuristic(puzzle);
		PQ.add(new QueueInsert(new LinkedList<int[]>(),0,heuristicValue,puzzle,findTile(puzzle,0))); //add current state into priorityQueue

		while(PQ.peek().getHeuristic()!=0){//if not solved continue
			solve(PQ,hashSet);
			if(PQ.size()>10000){
				PQ=limit(PQ);
			}
		}

		QueueInsert QI= PQ.poll(); //get the solved instruction
		
		File output = new File(args[1]);
		try {
			outputSolution(output, QI.getMoveList()); //put the solved instruction into the file
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}



	}
	private static PriorityQueue<QueueInsert> limit(PriorityQueue<QueueInsert> PQ){
		PriorityQueue<QueueInsert> newPQ=new PriorityQueue<QueueInsert>();
		for(int i = 0; i< 1000; i++){
			newPQ.add(PQ.poll());
		}
		return newPQ;
	}
	public static int[][] getPuzzle(String arg0) throws FileNotFoundException{
		File input = new File(arg0);
		Scanner scanner=new Scanner(input);
		int size=scanner.nextInt();
		scanner.nextLine();
		int[][] puzzle=new int[size][size];

		String line;
		String num;
		int i=0;
		while(scanner.hasNextLine()){
			line=scanner.nextLine();

			for(int j=0;j<size;j++){
				num=line.substring(3*j,2*(j+1)+j).strip();
				if(num.equals("")){
					continue;
				}
				puzzle[i][j]=Integer.parseInt(num);
			}
			i++;
		}
		scanner.close();
		return puzzle;
	}

	public static void solve(PriorityQueue<QueueInsert> PQ, HashSet<HashSetKey> hashSet){

		QueueInsert QI=PQ.poll();
		int[][] tempPuzzle;
		int tempHeuristic;
		QueueInsert tempQueueInsert;
		LinkedList<int[]> tempMoveList;

		for(int i=0;i<4;i++){
			try {
				addMove(QI.getBoard(), QI.getMoveList(), i,QI.getBlankLoc());//adds move to moveList
				QI.getMoveList().add(new int[]{QI.getBoard()[QI.getBlankLoc()[0]][QI.getBlankLoc()[1]],i});
			}

			catch(IndexOutOfBoundsException e){
				continue;
			}

			if(hashSet.contains(new HashSetKey(QI.getBoard()))) {
				backwardMove(QI.getBoard(),QI.getMoveList().getLast());
				QI.getMoveList().removeLast();
				continue;
			}
			tempHeuristic=heuristic(QI.getBoard());
			tempPuzzle=clonePuzzle(QI.getBoard());
			tempMoveList=cloneMoveList(QI.getMoveList());



			tempQueueInsert= new QueueInsert(tempMoveList, QI.getMoveNum() + 1, tempHeuristic,tempPuzzle,findTile(tempPuzzle,0));
			PQ.add(tempQueueInsert); //adds new move list to priorityQueue
			hashSet.add(new HashSetKey(tempPuzzle));

			backwardMove(QI.getBoard(),QI.getMoveList().getLast());
			QI.getMoveList().removeLast();



		}

	}

	public static int heuristic(int[][] puzzle){

		int heuristicValue=0;
		for(int i=0;i<puzzle.length;i++){
			for(int j=0;j<puzzle.length;j++){
				if(puzzle[i][j]==0) {
					continue;
				}

				heuristicValue+=Math.abs(j-((puzzle[i][j]-1)% puzzle.length))+Math.abs(i-(puzzle[i][j]-1)/ puzzle.length);//


			}//Math.abs(j-((puzzle[i][j]-1)%puzzle.length)) gets the horizontal magnitude from right position
			//Math.abs(i-((puzzle[i][j]-1)/puzzle.length)) gets the vertical magnitude from right position
		}

		return heuristicValue;
	}


	public static LinkedList<int[]> cloneMoveList(LinkedList<int[]> moveList){
		LinkedList<int[]> newMoveList=new LinkedList<>();
		for(int[] move:moveList){
			newMoveList.add(move.clone());
		}
		return newMoveList;
	}

	public static int[][] clonePuzzle(int[][] puzzle){
		int[][] newPuzzle=new int[puzzle.length][puzzle.length];
		for(int i=0;i<puzzle.length;i++){
			System.arraycopy(puzzle[i], 0, newPuzzle[i], 0, puzzle.length);
		}
		return newPuzzle;
	}
	public static int[] findTile(int[][] puzzle, int num){
		int[] tileLoc=new int[2];
		for(int i=0;i<puzzle.length;i++){
			for(int j=0;j< puzzle.length;j++){
				if(num==puzzle[i][j]){
					tileLoc[0]=i;
					tileLoc[1]=j;
					return tileLoc;
				}
			}
		}
		return null;
	}
	public static void fullMove(int[][] puzzle, LinkedList<int[]> moveList){
		for(int[] move:moveList){
			move(puzzle,move);
		}
	}
	public static void move(int[][] puzzle, int[] tileDir){
		int[] tileLoc=findTile(puzzle,tileDir[0]);
		if(tileDir[1]==UP){
			puzzle[tileLoc[0]-1][tileLoc[1]]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
		else if(tileDir[1]==DOWN){
			puzzle[tileLoc[0]+1][tileLoc[1]]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
		else if(tileDir[1]==LEFT){
			puzzle[tileLoc[0]][tileLoc[1]-1]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
		else{
			puzzle[tileLoc[0]][tileLoc[1]+1]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
	}
	public static void backwardMove(int[][] puzzle, int[] tileDir){
		int[] tileLoc=findTile(puzzle,tileDir[0]);
		if(tileDir[1]==DOWN){
			puzzle[tileLoc[0]-1][tileLoc[1]]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
		else if(tileDir[1]==UP){
			puzzle[tileLoc[0]+1][tileLoc[1]]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
		else if(tileDir[1]==RIGHT){
			puzzle[tileLoc[0]][tileLoc[1]-1]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
		else{
			puzzle[tileLoc[0]][tileLoc[1]+1]=puzzle[tileLoc[0]][tileLoc[1]];
			puzzle[tileLoc[0]][tileLoc[1]]=0;
		}
	}
	public static void addMove(int[][] puzzle,LinkedList<int[]> moveList, int move,int[] blankLoc){
		//moving tile to blankLoc
		if(move==UP){
			if((blankLoc[0]+1)>puzzle.length)
				throw new IndexOutOfBoundsException();
			puzzle[blankLoc[0]][blankLoc[1]]=puzzle[blankLoc[0]+1][blankLoc[1]];
			puzzle[blankLoc[0]+1][blankLoc[1]]=0;
		}
		else if(move==DOWN){
			if((blankLoc[0]-1)<0)
				throw new IndexOutOfBoundsException();
			puzzle[blankLoc[0]][blankLoc[1]]=puzzle[blankLoc[0]-1][blankLoc[1]];
			puzzle[blankLoc[0]-1][blankLoc[1]]=0;
		}
		else if(move==LEFT){
			if((blankLoc[1]+1)>puzzle.length)
				throw new IndexOutOfBoundsException();
			puzzle[blankLoc[0]][blankLoc[1]]=puzzle[blankLoc[0]][blankLoc[1]+1];
			puzzle[blankLoc[0]][blankLoc[1]+1]=0;
		}
		else{
			if((blankLoc[1]-1)<0)
				throw new IndexOutOfBoundsException();
			puzzle[blankLoc[0]][blankLoc[1]]=puzzle[blankLoc[0]][blankLoc[1]-1];
			puzzle[blankLoc[0]][blankLoc[1]-1]=0;
		}



	}

	public static void printPuzzle(int[][] puzzle){
		for(int[] x:puzzle){
			for(int y:x){
				if(y==0) {
					System.out.print("   ");
					continue;
				}
				System.out.print(y+"  ");
			}
			System.out.println();
		}
	}


	public static void outputSolution(File output,LinkedList<int[]> solution)throws FileNotFoundException{
		PrintWriter out=new PrintWriter(output);
		String string="";
		for(int[] move:solution){
			string+=move[0];// adds tile to string

			if(move[1]==UP)//adds letter to string
				string+=" U\n";
			else if(move[1]==DOWN)
				string+=" D\n";
			else if(move[1]==LEFT)
				string+=" L\n";
			else
				string+=" R\n";
			out.write(string);//adds string to file
			string="";
		}
		out.close();
	}
}
