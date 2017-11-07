import java.util.Comparator;

public class NodeComparator implements Comparator<TableNode> {

	      public int compareH1(TableNode object1,TableNode object2)
	      {
	          if (object1.h1 > object2.h1) 
	        	  return 1;
	          else                                   
	        	  return -1;
	      }
	  

	      public int compareH2(TableNode object1,TableNode object2)
	      {
	          if (object1.h2 > object2.h2) 
	        	  return 1;
	          else                                   
	        	  return -1;
	      }


//		@Override
//		public int compare(TableNode object1, TableNode object2) {
//			if(PuzzleSolver.hValue==1) {
//				if (object1.h1 > object2.h1) 
//		        	  return 1;
//		          else                                   
//		        	  return -1;
//			}
//			if (PuzzleSolver.hValue==2) {
//				if (object1.h2> object2.h2) 
//		        	  return 1;
//		          else                                   
//		        	  return -1;
//			}
//			return 1;
//		}
	      
	      @Override
			public int compare(TableNode object1, TableNode object2) {
				if(PuzzleSolver.hValue==1) {
					if ((object1.h1+object1.pathCost )> (object2.h1+object2.pathCost )) 
			        	  return 1;
			          else                                   
			        	  return -1;
				}
				else {
					if ((object1.h2+object1.pathCost )> (object2.h2+object2.pathCost )) 
			        	  return 1;
			          else                                   
			        	  return -1;
				}
				
			}
}
