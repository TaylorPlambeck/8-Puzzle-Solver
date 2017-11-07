# 8-Puzzle-Solver

<img src="https://user-images.githubusercontent.com/28762594/32518808-91f1eaf2-c3bf-11e7-8acf-cf516efb2345.JPG" height="464" width="328">


  This project has been written using Java and compiled with Eclipse Neon. Upon running, you will be asked to choose between three options.  Simply enter the corresponding number and press Enter.  The first option will randomly generate a table (Table being any state of the 8-Puzzle board).  If it is solvable then it will send it to a solver function for it to print out the solution.  If it is not solvable, a message will pop up indicating that it wasn’t solvable.  My program won’t automatically create another random table for you if the first one isn’t solvable, so if you want to try again restart the program.  
  
  The second option is for the user to input a custom table.  Accepted in the form of a 3x3 matrix.

  Be sure to PASTE the table in and then press enter.  If you try typing a line at a time and pressing Enter three times I can’t guarantee the program will work. Copy it from somewhere else and then paste it into the console, then press Enter.  The program will solve your puzzle if it is solvable.  If not, then the program will tell you it is not solvable and you will have to restart if you want to enter another one.
	
  The third option was just one that I used to generate the spreadsheet seen on the next page.  This was easier for me than inputting in 100 different tables.  I coded in all of the calculation.  You can try this if you like by using option three, but it takes a couple minutes.  Otherwise, option three should have no use to the Professor or the grader.
	
  Lastly, no matter how you choose your input table, every execution will print out the solution for BOTH heuristic one and two.  They will be back to back, H1 will print first and then the program will solve H2.  This includes depth and runtimes for both.
	
  I used a priority queue as my frontier and a hash table as my explored set.  The state is stored in the key of the hash table as a string, with its value being the order of it being expanded.  This gives us a node count.  All code is my own, and where I couldn’t figure out better methods I ended up hardcoding in some things.  You can see in the Manhattan distance and when I create the children that it is a many lines of code, I am sure there is a more efficient way but I wanted to figure it out myself.  The main class is the PuzzleSolver, which contains all the logic.  The second class TableNode creates an object that holds details about any given node, such as its parent, the table is represents, the h1 and h2 value and the path cost.  When I created a Frontier, it was a Priority Queue of TableNode’s.  The last class is just the Comparator, used to prioritize the Frontier by (h1+pathCost) OR (h2+pathCost).  By combining the TableNode and Comparator, we can choose which way we solve the table and keep everything organized.
