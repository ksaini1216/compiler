package syntacticphase;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SyntacticASTNode 
{
	private String _data = "";
	private String _type;
	private String _nodeType = "";
	private Integer _lineNumber;
	private String _subtreeString = "";
	private List<SyntacticASTNode> _leafs = new LinkedList<>();
	private int _nodeLevel = 0;
	

	public String getData() 
	{
		return _data;
	}

	public void setData(String data) 
	{
		this._data = data;
	}

	public String getType() 
	{
		return _type;
	}

	public void setType(String type) 
	{
		this._type = type;
	}

	public List<SyntacticASTNode> getLeafs()
	{
		return _leafs;
	}

	public void setLeafs(List<SyntacticASTNode> childrens)
	{
		this._leafs = childrens;
	}

	public String getNodeType()
	{
		return _nodeType;
	}

	public void setNodeType(String nodeType) 
	{
		this._nodeType = nodeType;
	}
	
	public Integer getLineNumber()
	{
		return _lineNumber;
	}

	public void setLineNumber(Integer lineNumber) 
	{
		this._lineNumber = lineNumber;
	}

	public String getSubtreeString()
	{
		return _subtreeString;
	}

	public void setSubtreeString(String subtreeString) 
	{
		this._subtreeString = subtreeString;
	}

	public void astTraversel(SyntacticASTNode root) 
	{
		if(root.getLeafs().size() == 0) {
			for (int i = 0; i < _nodeLevel; i++ ) 
			{
				System.out.print("  ");
			}
			
		String toPrint = String.format("%-45s" , "Node."+root.getNodeType()); 
			
		for (int i = 0; i < _nodeLevel; i++ )
	    			toPrint = toPrint.substring(0, toPrint.length() - 2);
				toPrint += String.format("%-15s" , (root.getData() == null ? " | " : " | " + root.getData()));    	
				toPrint += String.format("%-15s" , (root.getType() == null ? " | " : " | " + root.getType()));
			
			System.out.println(toPrint);
			return;
		}
		
		for (int i = 0; i < _nodeLevel; i++ )
		{
			System.out.print("  ");
		}
		
		String toPrint = String.format("%-45s" , "Node."+root.getNodeType()); 
		
		for (int i = 0; i < _nodeLevel; i++ )
		toPrint = toPrint.substring(0, toPrint.length() - 2);
		toPrint += String.format("%-15s" , (root.getData() == null ? " | " : " | " + root.getData()));    	
		toPrint += String.format("%-15s" , (root.getType() == null ? " | " : " | " + root.getType()));
		
		System.out.println(toPrint);
		_nodeLevel++;
		
		List<SyntacticASTNode> childrens = root.getLeafs();
		
		for (SyntacticASTNode child : childrens) 
		{ 
			astTraversel(child);
		}
		_nodeLevel--;
	}

    public void print(SyntacticASTNode root) throws IOException {
    	
    	System.out.println("Node type                                     | data         | type         ");
    	    	astTraversel(root);
    	
    }
}
