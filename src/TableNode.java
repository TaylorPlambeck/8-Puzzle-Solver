
public class TableNode {

	public TableNode parent;
	public int h1;
	public int h2;
	public int pathCost;
	public int[] table;
	
	public TableNode(TableNode parent,int[] table,int h1, int h2,int pathCost) {
		this.parent=parent;
		this.table=table;
		this.h1=h1;
		this.h2=h2;
		this.pathCost=pathCost;
	}

//	public Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }


}
