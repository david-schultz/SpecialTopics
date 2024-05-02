import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

public class BigMatrix  {
	
	private HashMap<Integer, HashMap<Integer, Integer>> rowsColumns;	//row, column
	private HashMap<Integer, HashMap<Integer, Integer>> columnsRows;	//column, row
	private int size;
	
	public BigMatrix() {
		size = 0;
		rowsColumns = new HashMap<Integer, HashMap<Integer, Integer>>();
		columnsRows = new HashMap<Integer, HashMap<Integer, Integer>>();
	}
	
	public void setValue(int row, int col, int value) {
		if(rowsColumns.get(row) == null) {
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(size);
			map.put(col, value);
			rowsColumns.put(row, map);
		}
		else {
			HashMap<Integer, Integer> map = rowsColumns.get(row);
			map.put(col, value);
			rowsColumns.put(row, map);
		}
		if(columnsRows.get(col) == null) {
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(size);
			map.put(row, value);
			columnsRows.put(col, map);
		}
		else{
			HashMap<Integer, Integer> map = columnsRows.get(col);
			map.put(row, value);
			columnsRows.put(col, map);
		}
		
		if(value == 0) {
			if(getRowSum(row) == 0) {
				rowsColumns.put(row, null);
			}
			if(getColSum(col) == 0) {
				columnsRows.put(col, null);
			}
		}
	}
	
	public int getValue(int row, int col) {
		HashMap<Integer, Integer> map = rowsColumns.get(row);
		if(map == null || map.get(col) == null)
			return 0;
		return map.get(col);
	}
	
	private boolean matrixisEmpty(Iterable<Integer> set) {
		for(Integer e : set) {
			if(e != 0)
				return false;
		}
		return true;
	}
	
	public List<Integer> getNonEmptyRows() {
		int x = 0;
		Stack<Integer> stack = new Stack<Integer>();
		for(Integer e : rowsColumns.keySet()) {
		  	if(!rowsColumns.get(e).isEmpty()) {
		  		if(!matrixisEmpty(rowsColumns.get(e).values())) {
		  		x++;
		  		stack.push(e);
		  		}
		  	}
		  }
		return stack;
	}
	
	public List<Integer> getNonEmptyRowsInColumn(int col) {
		Stack<Integer> stack = new Stack<Integer>();
		if(!columnsRows.get(col).isEmpty()) {
			for(Integer e : columnsRows.get(col).keySet()) {
				if(getValue(e, col) != 0)
					stack.push(e);
			}
		}
		return stack;
	}
	
	public List<Integer> getNonEmptyCols() {
		Stack<Integer> stack = new Stack<Integer>();
		int  x = 0;
		for(Integer e : columnsRows.keySet()) {
		  	if(!columnsRows.get(e).isEmpty()) {
		  		if(!matrixisEmpty(columnsRows.get(e).values())) {
		  			x++;
		  			stack.push(e);
		  		}
		  	}
		  }
		return stack;
	}
	
	public List<Integer> getNonEmptyColsInRow(int row) {
		Stack<Integer> stack = new Stack<Integer>();
		if(!rowsColumns.get(row).isEmpty()) {
			for(Integer e : rowsColumns.get(row).keySet()) {
				if(getValue(row, e) != 0)
					stack.push(e);
			}
		}
		return stack;
	}
	
	public int getRowSum(int row) {
		int total = 0;
		if(rowsColumns.get(row) == null)
			return 0;
		for(Integer e : rowsColumns.get(row).values()) {
			total += e;
		}
		return total;
	}
	
	public int getColSum(int col) {
		int total = 0;
		if(columnsRows.get(col) == null)
			return 0;
		for(Integer e : columnsRows.get(col).values()) {
			total += e;
		}
		return total;
	}
	
	public int getTotalSum() {
		int total = 0;
		for(Integer e : rowsColumns.keySet()) {
			total += getRowSum(e);
		}
		return total;
	}
	
	public BigMatrix multiplyByConstant(int constant) {
		BigMatrix victim = new BigMatrix();
		//for each value in rowsColumns, multiply by constant and setValue to victim
		for(Integer e : getNonEmptyCols()) {
			for(Integer f: getNonEmptyRowsInColumn(e)) {
				victim.setValue(f, e, (this.getValue(f, e) * constant));
			}
		}
		
		return victim;
	}
	
	//TODO: takes too much time
	public BigMatrix addMatrix(BigMatrix other) {
		BigMatrix victim = new BigMatrix();
		for(Integer e : this.getNonEmptyCols()) {
			for(Integer f: this.getNonEmptyRowsInColumn(e)) {
				victim.setValue(f, e, (this.getValue(f, e)));
			}
		}
		
		for(Integer e : other.getNonEmptyCols()) {
			for(Integer f: other.getNonEmptyRowsInColumn(e)) {
				victim.setValue(f, e, (this.getValue(f, e) + other.getValue(f, e)));
			}
		}
		
		return victim;
	}
	
	public static void main(String[] args) {
		BigMatrix matrix = new BigMatrix();
		matrix.setValue(0, 0, 1);
		matrix.setValue(1000, 10, 2);
		matrix.setValue(10, 1000, 3);
		matrix.setValue(0, 1000, 4);
		matrix.setValue(1000, 0, 5);
		matrix.setValue(0, 10, 6);
		matrix.setValue(10, 0, 7);
		matrix.setValue(10, 1000, 0);
		matrix.setValue(10, 0, 0);
		
		for(Integer e : matrix.getNonEmptyRows()) {
			System.out.println(e);
		}
		//System.out.println(matrix.getRowSum(0));
	}
}
