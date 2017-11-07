import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

// 8 Puzzle using A* Algorithm
//Taylor Plambeck
//August 6th, 2017
//CS 420 Artificial Intelligence
/*

1 4 2
3 5 0
6 7 8

1 2 5
3 8 7
6 4 0

 */

public class PuzzleSolver {
	//main class
	//global variables below
	static int dataCollectorCounter= 1;
	static long depth=0; //used to store the depth of the current solution
	static int optionSelect; //used to select which option the user wants
	static Scanner input = new Scanner(System.in); //create a input scanner, to be used for option select AND custom table
	static int[] table = new int[9];
	static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	static int hValue=1; //this is the heuristic function value. checked in NodeComparator class to see how f(n)=g(n)+h(n) is calculated. hValue=1 uses h1, hValue=2 uses h2

	public static void main(String[] args) {  
		// Display options to screen
		System.out.println("Input Number and Press Enter to Select Table Option");
		System.out.println("[1] Generate Random Table");
		System.out.println("[2] Input Custom Table");
		System.out.println("[3] Generate 100 Test Tables");
		optionSelect=input.nextInt();
		
		if(optionSelect==1) {
			//generate random table here
			for(int i=0;i<9;i++)	//use loop to store a basic 0-8 table, will be shuffled below
			{
				table[i]=i;
			}
			int index, temp;
			Random random = new Random();
			for (int i = table.length - 1; i > 0; i--)	//shuffle the table array
			{
				index = random.nextInt(i + 1);
				temp = table[index];
				table[index] = table[i];
				table[i] = temp;
			}
			printTable(table); //output table
		}
		
	else if(optionSelect==2) {				//  OPTION TWO - USER'S CUSTOM TABLE   --------------------------------------------------------------------------------------
			// Allows user to paste in their own table
			System.out.println("Enter Custom Table:");
			System.out.print("(Paste in the table and press enter)\n");
	        String[] strNums = null;	
	        try {		//try/catch reads in the lines for the custom table and saves them correctly using the more efficient BufferedReader
				strNums = reader.readLine().split("\\s");
				for(int i=0; i<strNums.length; i++) {
		            table[i] = Integer.parseInt(strNums[i]);
		        }
				strNums = reader.readLine().split("\\s");
				for(int i=3; i<strNums.length+3; i++) {
		            table[i] = Integer.parseInt(strNums[i-3]);
		        }
				strNums = reader.readLine().split("\\s");
				for(int i=6; i<strNums.length+6; i++) {
		            table[i] = Integer.parseInt(strNums[i-6]);
		        }
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} //end of Option 2
		
	else {				//  OPTION three - take data   --------------------------------------------------------------------------------------
	for(int repeat=0;repeat<10;repeat++) {
		hValue=1;
		for(int i=0;i<9;i++)	//use loop to store a basic 0-8 table, will be shuffled below
		{
			table[i]=i;
		}
		int index, temp;
		Random random = new Random();
		for (int i = table.length - 1; i > 0; i--)	//shuffle the table array
		{
			index = random.nextInt(i + 1);
			temp = table[index];
			table[index] = table[i];
			table[i] = temp;
		}
		if(isPuzzleSolvable(table))  //use function to determine inversion count, if solveable, take time and solve it
	    {
			takeData(table);
	    }
	}//end of for loop
	printSpreadsheetData();
} //end of Option 3
		if(optionSelect!=3) {
		//this chunk below is to actually solve the puzzle and print out its solution.
		if(isPuzzleSolvable(table))  //use function to determine inversion count, if solveable, take time and solve it
        {
			//below is how you run the program one time, using both h(n) functions for User Option 1 OR 2
			long startTime1, endTime1;
			long startTime2, endTime2;
			System.out.println("Current table is SOLVABLE");	
        	startTime1 = System.currentTimeMillis();
    		solvePuzzle(table);  //here we go!
    		endTime1 =System.currentTimeMillis();
    		System.out.println("Total Time Taken for H1: " + (endTime1 - startTime1)+"milliseconds\n");
    		//set hValue equal to the second h value
    		hValue=2;
        	startTime2 = System.currentTimeMillis();
    		solvePuzzle(table);  //here we go!
    		endTime2 =System.currentTimeMillis();
    		System.out.println("Total Time Taken for H2: " + (endTime2 - startTime2)+"milliseconds");
        }
        else System.out.println("Current table is NOT SOLVABLE. Re-Run Program.");  //this ends the program
		}
	}//end main
	

	public static void solvePuzzle(int[] table) {
		//this is the (long) function that will be used to solve the entire puzzle. This will be creating nodes, managing the frontier and hashtable and ultimately setting us up with a SOL
		int pathCost=0; //step cost, increases by 1 every time we loop the while loop
		int nodeCounter=0;
		PriorityQueue<TableNode> Frontier =new PriorityQueue<TableNode>(1, new NodeComparator());	//actual Frontier priority queue
		List<TableNode> nodeList = new ArrayList<TableNode>(); //this stores the frontier nodes in memory. THIS WAS USED FOR DEBUGGING, I could keep track of every table in the frontier and print them
		TableNode originalNode=new TableNode(null,table,h1Function(table),h2Function(table),pathCost++);	//create the root parent
		nodeList.add(originalNode);	//store in the list in case i have to debug
		Frontier.add(nodeList.get(nodeCounter++)); //store in the frontier
		
		TableNode currentPoppedNode;
		Hashtable<String, Integer> exploredSet =new Hashtable<String, Integer>();
		int hashtableCounter=0; //just using this because i have to have some int input, this can tell me how many nodes were expanded though
		int[] tempTableContainer= new int[9];
		
		//While Loop that will perform the search algorithm through the nodes
		while(true) {
			if(Frontier.isEmpty()) {
	        System.out.println("Failed - Frontier is Empty"); //failed, frontier doesn't have a value
		}
		currentPoppedNode=Frontier.remove(); //pop node
		if(puzzleGoalTest(currentPoppedNode.table)) {		//First - check to see if the puzzle is solved
			System.out.println("Table is solved");
			Frontier.add(currentPoppedNode); //adds this back to the frontier so I can print out the soL path below the while loop.
			break; //leave while loop
		}
		else { 
			//current table is not solved yet, insert table->string conversion into hashtable, we are now going to explore that node
			exploredSet.put(Arrays.toString(currentPoppedNode.table),hashtableCounter++);
		}
      
// This next step is to hard-code the available actions depending on location of Zero square. This is where the children are created and tested for any action
// Number on the far left on the next line is which spot the 0 is in. Goes from 0-8. This is LONG, but works perfectly.        
/* 0 */ if(currentPoppedNode.table[0]==0) {
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	//if the zero tile is in the first spot, then we have two options; 1 or 3
        	//create two nodes, only if they are NOT in frontier NOR the exploredSet
        	currentPoppedNode.table[0]=currentPoppedNode.table[1];
        	currentPoppedNode.table[1]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[0]=currentPoppedNode.table[3];
        	currentPoppedNode.table[3]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);  //saves the parent back to its original state!
        }// end of option0
        	
/* 1 */ else if(currentPoppedNode.table[1]==0) {
        	//if the zero tile is in the second spot, then we have option 0,2,4
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[1]=currentPoppedNode.table[0];
        	currentPoppedNode.table[0]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[1]=currentPoppedNode.table[2];
        	currentPoppedNode.table[2]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action        	
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[1]=currentPoppedNode.table[4];
        	currentPoppedNode.table[4]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        }//end of option1
/* 2 */ else if(currentPoppedNode.table[2]==0) {
        	//if the zero tile is in the third spot, then we have two options; 1 or 5
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[2]=currentPoppedNode.table[1];
        	currentPoppedNode.table[1]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[2]=currentPoppedNode.table[5];
        	currentPoppedNode.table[5]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        } //end of option 2
/* 3 */ else if(currentPoppedNode.table[3]==0) {
        	//if the zero tile is in the 4th spot, then we have 0,4,6
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[3]=currentPoppedNode.table[0];
        	currentPoppedNode.table[0]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[3]=currentPoppedNode.table[4];
        	currentPoppedNode.table[4]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[3]=currentPoppedNode.table[6];
        	currentPoppedNode.table[6]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        } //end of option3
/* 4 */ else if(currentPoppedNode.table[4]==0) {
        	//if the zero tile is in the 5th spot, then we have two options; 1 or 3 or 5 or 7
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[4]=currentPoppedNode.table[1];
        	currentPoppedNode.table[1]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[4]=currentPoppedNode.table[3];
        	currentPoppedNode.table[3]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[4]=currentPoppedNode.table[5];
        	currentPoppedNode.table[5]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[4]=currentPoppedNode.table[7];
        	currentPoppedNode.table[7]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        } //end of option 4

/* 5 */ else if(currentPoppedNode.table[5]==0) {
			
        	//if the zero tile is in the 6th spot, then we have two options; 2,4,8
			tempTableContainer=Arrays.copyOf(currentPoppedNode.table, 9);
        	currentPoppedNode.table[5]=currentPoppedNode.table[2];
        	currentPoppedNode.table[2]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        	currentPoppedNode.table[5]=currentPoppedNode.table[4];
        	currentPoppedNode.table[4]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        	currentPoppedNode.table[5]=currentPoppedNode.table[8];
        	currentPoppedNode.table[8]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		nodeList.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost));	//have to add to both. memory location is in actual list, then queue takes and orders them
        		Frontier.add(nodeList.get(nodeCounter++)); //stores list[0] into the queue as the first initial parent, nC is now 1
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        }//end of option5
/* 6 */ else if(currentPoppedNode.table[6]==0) {
        	//if the zero tile is in the 7th spot, then we have two options; 3,7
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[6]=currentPoppedNode.table[3];
        	currentPoppedNode.table[3]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[6]=currentPoppedNode.table[7];
        	currentPoppedNode.table[7]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        } //end of 6
/* 7 */ else if(currentPoppedNode.table[7]==0) {
        	//if the zero tile is in the 8th spot, then we have two options; 4,6,8
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[7]=currentPoppedNode.table[4];
        	currentPoppedNode.table[4]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[7]=currentPoppedNode.table[6];
        	currentPoppedNode.table[6]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action.
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[7]=currentPoppedNode.table[8];
        	currentPoppedNode.table[8]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        } //end of 7
/* 8 */ else {
        	//if the zero tile is in the final (technically 9th) spot, then we have two options; 5 or 7
        	tempTableContainer=currentPoppedNode.table.clone(); //this holds the original table, just as a copy so we can reuse it when we want to test each action
        	currentPoppedNode.table[8]=currentPoppedNode.table[5];
        	currentPoppedNode.table[5]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	//now that we have created and tested the first action, we reset the table back to normal and try the next action
        	currentPoppedNode.table=tempTableContainer.clone();
        	currentPoppedNode.table[8]=currentPoppedNode.table[7];
        	currentPoppedNode.table[7]=0;
        	if(!exploredSet.containsKey(Arrays.toString(currentPoppedNode.table))) {
        		//if the new table created from the action is already in the exploredSet
        		Frontier.add(new TableNode(currentPoppedNode,currentPoppedNode.table,h1Function(currentPoppedNode.table),h2Function(currentPoppedNode.table),pathCost)); //CHild into the queue
        	}
        	currentPoppedNode.table=Arrays.copyOf(tempTableContainer, 9);
        } //end of last IF for the children

        pathCost++; //increase path cost every time we have to pop a node
		} // END OF WHILE LOOP
		
		//tables will be held in the solution list here
		List<int[]> solutionList = new ArrayList<int[]>(); //this stores the frontier nodes in memory, allows control of parents, also allows search to see if there is already one in frontier
		TableNode finalNodeHolder=Frontier.peek();
		while(finalNodeHolder.parent != null)		//this DANK while loop follows parents from any node up to the root, while also printing out the corresponding h2 for each
		{
    	  	solutionList.add(finalNodeHolder.table);
			finalNodeHolder=finalNodeHolder.parent;
		}
if(optionSelect!=3) {
	if(hValue==1) {
		System.out.println("** USING HEURISTIC 1 **");
	}
	else System.out.println("** USING HEURISTIC 2 **");
	System.out.println("Final Solution from Starting State to End State\n");
	printTable(finalNodeHolder.table);//prints out the original table
  	
	for(int x=solutionList.size();x>0;x--) {
		printTable(solutionList.get(x-1));	//print out every table in the solutionList until we are solved
	}
	System.out.println("Depth of Final Solution - "+solutionList.size());	//size of solution is depth
	int searched=exploredSet.size();
	System.out.println("Expanded - "+searched+" nodes");//size of hashtable is how many were explored
}
		//All of these if statemeents are just used for option 3 to print out the spreadsheet
		depth=solutionList.size();
		if(hValue==1) {
			if(depth<4) {
				h1depth2++;
				h1expanded2+=exploredSet.size();
			}
			else if(depth<6) {
				h1depth4++;
				h1expanded4+=exploredSet.size();
			}
			else if(depth<8) {
				h1depth6++;
				h1expanded6+=exploredSet.size();
			}
			else if(depth<10) {
				h1depth8++;
				h1expanded8+=exploredSet.size();
			}
			else if(depth<12) {
				h1depth10++;
				h1expanded10+=exploredSet.size();
			}
			else if(depth<14) {
				h1depth12++;
				h1expanded12+=exploredSet.size();
			}
			else if(depth<16) {
				h1depth14++;
				h1expanded14+=exploredSet.size();
			}
			else if(depth<18) {
				h1depth16++;
				h1expanded16+=exploredSet.size();
			}
			else if(depth<20) {
				h1depth18++;
				h1expanded18+=exploredSet.size();
			}
			else if(depth<22) {
				h1depth20++;
				h1expanded20+=exploredSet.size();
			}
			else if(depth<24) {
				h1depth22++;
				h1expanded22+=exploredSet.size();
			}
			else if(depth<26) {
				h1depth24++;
				h1expanded24+=exploredSet.size();
			}
			else {
				h1depth26++;
				h1expanded26+=exploredSet.size();
			}
		}
		else {
				if(depth<4) {
					h2depth2++;
					h2expanded2+=exploredSet.size();
				}
				else if(depth<6) {
					h2depth4++;
					h2expanded4+=exploredSet.size();
				}
				else if(depth<8) {
					h2depth6++;
					h2expanded6+=exploredSet.size();
				}
				else if(depth<10) {
					h2depth8++;
					h2expanded8+=exploredSet.size();
				}
				else if(depth<12) {
					h2depth10++;
					h2expanded10+=exploredSet.size();
				}
				else if(depth<14) {
					h2depth12++;
					h2expanded12+=exploredSet.size();
				}
				else if(depth<16) {
					h2depth14++;
					h2expanded14+=exploredSet.size();
				}
				else if(depth<18) {
					h2depth16++;
					h2expanded16+=exploredSet.size();
				}
				else if(depth<20) {
					h2depth18++;
					h2expanded18+=exploredSet.size();
				}
				else if(depth<22) {
					h2depth20++;
					h2expanded20+=exploredSet.size();
				}
				else if(depth<24) {
					h2depth22++;
					h2expanded22+=exploredSet.size();
				}
				else if(depth<26) {
					h2depth24++;
					h2expanded24+=exploredSet.size();
				}
				else {
					h2depth26++;
					h2expanded26+=exploredSet.size();
				}
		}
	} //end of puzzle solver fxn

	public static void printTable(int[] table) {
		//this is used to output the current table at any given time
		System.out.println(table[0]+" "+table[1]+" "+table[2]);
		System.out.println(table[3]+" "+table[4]+" "+table[5]);
		System.out.println(table[6]+" "+table[7]+" "+table[8]+"\n");
	}
	
	public static boolean puzzleGoalTest(int[] table) {
		//this is used to see if the table is solved to the goal position
		for(int k=0;k<9;k++)
		{
			if(table[k]!=k)
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isPuzzleSolvable(int[] table) {
		//this is used to see if the table is solvable in its current state.
		int inversionCount=0;	//this is used to see if the current table is solvable. (odd=not solvable)
		int j;
		for(j=0;j<8;j++)
		{
			for(int k=j+1;k<9;k++)
			{
				if( (table[j]>table[k]) && table[k]!=0 )
				{
					inversionCount++;
				}
			}
		}
		//System.out.println("The number of inversions is: "+inversionCount);
		if((inversionCount%2) == 0)
		{
			return true;
		}
		return false;
	}
	
	public static int h1Function(int[] table) {
		//how many numbers are NOT in their own position??
		int h1Value=0;
		for(int k=1;k<9;k++)
		{
			if(table[k]!=k)
			{
				h1Value++;
			}
		}	
		//System.out.println("The value of H1 is: "+h1Value);
		return h1Value;
	}
	
	public static int h2Function(int[] table) {
		//sum of the distance of every number from its appropriate tile
		//AKA Manhattan Distance
		int h2DistanceValue=0;
		if(table[0]==1 | table[2]==1 | table[4]==1) {
			h2DistanceValue+=1;
		}
		if(table[3]==1 | table[5]==1 | table[7]==1) {
			h2DistanceValue+=2;
		}
		if(table[6]==1 | table[8]==1) {
			h2DistanceValue+=3;
		}     //--------------------------------------------------
		if(table[1]==2 | table[5]==2) {
			h2DistanceValue+=1;
		}
		if(table[0]==2 | table[4]==2 | table[8]==2) {
			h2DistanceValue+=2;
		}
		if(table[3]==2 | table[7]==2) {
			h2DistanceValue+=3;
		}     
		if(table[6]==2) {
			h2DistanceValue+=4;
		}     //--------------------------------------------------
		if(table[0]==3 | table[4]==3 | table[6]==3) {
			h2DistanceValue+=1;
		}
		if(table[1]==3 | table[5]==3 | table[7]==3) {
			h2DistanceValue+=2;
		}
		if(table[2]==3 | table[8]==3) {
			h2DistanceValue+=3;
		}     //--------------------------------------------------
		if(table[1]==4 | table[5]==4 | table[3]==4 | table[7]==4) {
			h2DistanceValue+=1;
		}
		if(table[0]==4 | table[2]==4 | table[6]==4| table[8]==4) {
			h2DistanceValue+=2;
		}     //--------------------------------------------------
		if(table[2]==5 | table[8]==5 | table[4]==5) {
			h2DistanceValue+=1;
		}
		if(table[3]==5 | table[1]==5 | table[7]==5) {
			h2DistanceValue+=2;
		}
		if(table[6]==5 | table[0]==5) {
			h2DistanceValue+=3;
		}     //--------------------------------------------------
		if(table[3]==6 | table[7]==6) {
			h2DistanceValue+=1;
		}
		if(table[0]==6 | table[4]==6 | table[8]==6) {
			h2DistanceValue+=2;
		}
		if(table[1]==6 | table[5]==6) {
			h2DistanceValue+=3;
		}     
		if(table[2]==6) {
			h2DistanceValue+=4;
		}     //--------------------------------------------------
		if(table[6]==7 | table[8]==7 | table[4]==7) {
			h2DistanceValue+=1;
		}
		if(table[3]==7 | table[1]==7 | table[5]==7) {
			h2DistanceValue+=2;
		}
		if(table[2]==7 | table[0]==7) {
			h2DistanceValue+=3;
		}     //--------------------------------------------------
		if(table[5]==8 | table[7]==8) {
			h2DistanceValue+=1;
		}
		if(table[2]==8 | table[4]==8 | table[6]==8) {
			h2DistanceValue+=2;
		}
		if(table[1]==8 | table[3]==8) {
			h2DistanceValue+=3;
		}     
		if(table[0]==8) {
			h2DistanceValue+=4;
		}     //--------------------------------------------------
		//System.out.println("The value of H2 is: "+h2DistanceValue);
		return h2DistanceValue;
	}
	//everything BELOW THIS LINE IS JUST FOR OPTION 3, NOT RELEVANT TO PROFESSOR OR GRADER. JUST USED TO PRINT MY SPREADSHEET    -------------------------------------------
	public static void printSpreadsheetData() {
		//print out all the data for the spreadsheet. IGNORE ALL THESE PRINTS
		System.out.println("H1 Depth2 Count"+h1depth2);  
		System.out.println("H1 Depth4 Count"+h1depth4);
		System.out.println("H1 Depth6 Count"+h1depth6);
		System.out.println("H1 Depth8 Count"+h1depth8);
		System.out.println("H1 Depth10 Count"+h1depth10);
		System.out.println("H1 Depth12 Count"+h1depth12);
		System.out.println("H1 Depth14 Count"+h1depth14);
		System.out.println("H1 Depth16 Count"+h1depth16);
		System.out.println("H1 Depth18 Count"+h1depth18);
		System.out.println("H1 Depth20 Count"+h1depth20);
		System.out.println("H1 Depth22 Count"+h1depth22);
		System.out.println("H1 Depth24 Count"+h1depth24);
		System.out.println("H1 Depth26 Count\n"+h1depth26);
		
		System.out.println("H2 Depth2 Count"+h2depth2);  
		System.out.println("H2 Depth4 Count"+h2depth4);
		System.out.println("H2 Depth6 Count"+h2depth6);
		System.out.println("H2 Depth8 Count"+h2depth8);
		System.out.println("H2 Depth10 Count"+h2depth10);
		System.out.println("H2 Depth12 Count"+h2depth12);
		System.out.println("H2 Depth14 Count"+h2depth14);
		System.out.println("H2 Depth16 Count"+h2depth16);
		System.out.println("H2 Depth18 Count"+h2depth18);
		System.out.println("H2 Depth20 Count"+h2depth20);
		System.out.println("H2 Depth22 Count"+h2depth22);
		System.out.println("H2 Depth24 Count"+h2depth24);
		System.out.println("H2 Depth26 Count\n"+h2depth26);
		
		System.out.println("H1 ex2 Count"+(h1expanded2)/(h1depth2));  
		System.out.println("H1 ex4 Count"+(h1expanded4)/(h1depth4));
		System.out.println("H1 4x6 Count"+(h1expanded6)/(h1depth6));
		System.out.println("H1 ex8 Count"+(h1expanded8)/(h1depth8));
		System.out.println("H1 ex10 Count"+(h1expanded10)/(h1depth10));
		System.out.println("H1 ex12 Count"+(h1expanded12)/(h1depth12));
		System.out.println("H1 ex14 Count"+(h1expanded14)/(h1depth14));
		System.out.println("H1 ex16 Count"+(h1expanded16)/(h1depth16));
		System.out.println("H1 ex18 Count"+(h1expanded18)/(h1depth18));
		System.out.println("H1 ex20 Count"+(h1expanded20)/(h1depth20));
		System.out.println("H1 ex22 Count"+(h1expanded22)/(h1depth22));
		System.out.println("H1 ex24 Count"+(h1expanded24)/(h1depth24));
		System.out.println("H1 ex26 Count\n"+(h1expanded26)/(h1depth26));
		
		System.out.println("H2 e2 Count"+(h2expanded2)/(h2depth2));  
		System.out.println("H2 e4 Count"+(h2expanded4)/(h2depth4));
		System.out.println("H2 e6 Count"+(h2expanded6)/(h2depth6));
		System.out.println("H2 e8 Count"+(h2expanded8)/(h2depth8));
		System.out.println("H2 e10 Count"+(h2expanded10)/(h2depth10));
		System.out.println("H2 e12 Count"+(h2expanded12)/(h2depth12));
		System.out.println("H2 e14 Count"+(h2expanded14)/(h2depth14));
		System.out.println("H2 e16 Count"+(h2expanded16)/(h2depth16));
		System.out.println("H2 e18 Count"+(h2expanded18)/(h2depth18));
		System.out.println("H2 e20 Count"+(h2expanded20)/(h2depth20));
		System.out.println("H2 e22 Count"+(h2expanded22)/(h2depth22));
		System.out.println("H2 e24 Count"+(h2expanded24)/(h2depth24));
		System.out.println("H2 e26 Count\n"+(h2expanded26)/(h2depth26));
		
		System.out.println("H1 time2 Count"+(h1Time2)/(h1depth2));  
		System.out.println("H1 time4 Count"+(h1Time4)/(h1depth4));
		System.out.println("H1 time6 Count"+(h1Time6)/(h1depth6));
		System.out.println("H1 time8 Count"+(h1Time8)/(h1depth8));
		System.out.println("H1 time10 Count"+(h1Time10)/(h1depth10));
		System.out.println("H1 time12 Count"+(h1Time12)/(h1depth12));
		System.out.println("H1 time14 Count"+(h1Time14)/(h1depth14));
		System.out.println("H1 time16 Count"+(h1Time16)/(h1depth16));
		System.out.println("H1 time18 Count"+(h1Time18)/(h1depth18));
		System.out.println("H1 time20 Count"+(h1Time20)/(h1depth20));
		System.out.println("H1 time22 Count"+(h1Time22)/(h1depth22));
		System.out.println("H1 time24 Count"+(h1Time24)/(h1depth24));
		System.out.println("H1 time26 Count\n"+(h1Time26)/(h1depth26));
		
		System.out.println("H2 time2 Count"+(h2Time2)/(h2depth2));  
		System.out.println("H2 time4 Count"+(h2Time4)/(h2depth4));
		System.out.println("H2 time6 Count"+(h2Time6)/(h2depth6));
		System.out.println("H2 time8 Count"+(h2Time8)/(h2depth8));
		System.out.println("H2 time10 Count"+(h2Time10)/(h2depth10));
		System.out.println("H2 time12 Count"+(h2Time12)/(h2depth12));
		System.out.println("H2 time14 Count"+(h2Time14)/(h2depth14));
		System.out.println("H2 time16 Count"+(h2Time16)/(h2depth16));
		System.out.println("H2 time18 Count"+(h2Time18)/(h2depth18));
		System.out.println("H2 time20 Count"+(h2Time20)/(h2depth20));
		System.out.println("H2 time22 Count"+(h2Time22)/(h2depth22));
		System.out.println("H2 time24 Count"+(h2Time24)/(h2depth24));
		System.out.println("H2 time26 Count\n"+(h2Time26)/(h2depth26));
	}
	
	public static void takeData(int[] table) {
		long startTime1, endTime1;
		long startTime2, endTime2;
		//System.out.println("Current table is SOLVABLE");	
    	startTime1 = System.currentTimeMillis();
    	System.out.print(dataCollectorCounter++);
		solvePuzzle(table);  //here we go!
		endTime1 =System.currentTimeMillis();
		
		if(depth<4) {
			h1Time2+=((endTime1 - startTime1));
		}
		else if(depth<6) {
			h1Time4+=((endTime1 - startTime1));
		}
		else if(depth<8) {
			h1Time6+=((endTime1 - startTime1));
		}
		else if(depth<10) {
			h1Time8+=((endTime1 - startTime1));
		}
		else if(depth<12) {
			h1Time10+=((endTime1 - startTime1));
		}
		else if(depth<14) {
			h1Time12+=((endTime1 - startTime1));
		}
		else if(depth<16) {
			h1Time14+=((endTime1 - startTime1));
		}
		else if(depth<18) {
			h1Time16+=((endTime1 - startTime1));
		}
		else if(depth<20) {
			h1Time18+=((endTime1 - startTime1));
		}
		else if(depth<22) {
			h1Time20+=((endTime1 - startTime1));
		}
		else if(depth<24) {
			h1Time22+=((endTime1 - startTime1));
		}
		else if(depth<26) {
			h1Time24+=((endTime1 - startTime1));
		}
		else {
			h1Time26+=((endTime1 - startTime1));
		}
		
		//set hValue equal to the second h value
		hValue=2;

    	startTime2 = System.currentTimeMillis();
    	System.out.print(dataCollectorCounter++);
		solvePuzzle(table);  //here we go!
		endTime2 =System.currentTimeMillis();
		if(depth<4) {
			h2Time2+=((endTime1 - startTime1));
		}
		else if(depth<6) {
			h2Time4+=((endTime1 - startTime1));
		}
		else if(depth<8) {
			h2Time6+=((endTime1 - startTime1));
		}
		else if(depth<10) {
			h2Time8+=((endTime1 - startTime1));
		}
		else if(depth<12) {
			h2Time10+=((endTime1 - startTime1));
		}
		else if(depth<14) {
			h2Time12+=((endTime1 - startTime1));
		}
		else if(depth<16) {
			h2Time14+=((endTime1 - startTime1));
		}
		else if(depth<18) {
			h2Time16+=((endTime1 - startTime1));
		}
		else if(depth<20) {
			h2Time18+=((endTime1 - startTime1));
		}
		else if(depth<22) {
			h2Time20+=((endTime1 - startTime1));
		}
		else if(depth<24) {
			h2Time22+=((endTime1 - startTime1));
		}
		else if(depth<26) {
			h2Time24+=((endTime1 - startTime1));
		}
		else {
			h2Time26+=((endTime1 - startTime1));
		}
	}//end of takeData
	
	//everything below here was to create my spreadshett via the program instead of by running 100 different executions
	static long h1Time2=0;
	static long h2Time2=0;
	static long h1Time4=0;
	static long h2Time4=0;
	static long h1Time6=0;
	static long h2Time6=0;
	static long h1Time8=0;
	static long h2Time8=0;
	static long h1Time10=0;
	static long h2Time10=0;
	static long h1Time12=0;
	static long h2Time12=0;
	static long h1Time14=0;
	static long h2Time14=0;
	static long h1Time16=0;
	static long h2Time16=0;
	static long h1Time18=0;
	static long h2Time18=0;
	static long h1Time20=0;
	static long h2Time20=0;
	static long h1Time22=0;
	static long h2Time22=0;
	static long h1Time24=0;
	static long h2Time24=0;
	static long h1Time26=0;
	static long h2Time26=0;
	static long h1expanded2=0;	//total nodes expanded for h1 depth2
	static long h2expanded2=0;	//total nodes expanded for h2 depth2
	static long h1expanded4=0;
	static long h2expanded4=0;	//total nodes expanded for h2 depth4
	static long h1expanded6=0;
	static long h2expanded6=0;
	static long h1expanded8=0;
	static long h2expanded8=0;
	static long h1expanded10=0;
	static long h2expanded10=0;
	static long h1expanded12=0;
	static long h2expanded12=0;
	static long h1expanded14=0;
	static long h2expanded14=0;
	static long h1expanded16=0;
	static long h2expanded16=0;
	static long h1expanded18=0;
	static long h2expanded18=0;
	static long h1expanded20=0;
	static long h2expanded20=0;
	static long h1expanded22=0;
	static long h2expanded22=0;
	static long h1expanded24=0;
	static long h2expanded24=0;
	static long h1expanded26=0;
	static long h2expanded26=0;
	static long h1depth2=1;	//amount of h1 runs of depth 2
	static long h1depth4=1;
	static long h1depth6=1;
	static long h1depth8=1;
	static long h1depth10=1;
	static long h1depth12=1;
	static long h1depth14=1;
	static long h1depth16=1;
	static long h1depth18=1;
	static long h1depth20=1;
	static long h1depth22=1;
	static long h1depth24=1;
	static long h1depth26=1;
	static long h2depth2=1;	//amount of h2 runs in depth 2
	static long h2depth4=1;
	static long h2depth6=1;
	static long h2depth8=1;
	static long h2depth10=1;
	static long h2depth12=1;
	static long h2depth14=1;
	static long h2depth16=1;
	static long h2depth18=1;
	static long h2depth20=1;
	static long h2depth22=1;
	static long h2depth24=1;
	static long h2depth26=1;
	
	
}	//end class
