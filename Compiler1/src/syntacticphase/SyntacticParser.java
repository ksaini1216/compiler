package syntacticphase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import aide.CompilerReadWrite;
import aide.Utility;
import compiler.constants.CompilerEnum;
import compiler.constants.CompilerEnum.TOKEN_TYPE;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import lexicalphase.LexicalToken;
import lexicalphase.LexicalTokenizer;


public class SyntacticParser
{
	private static List<String> _terminals;
	private static Map<String, Integer> _rowHeaders;
	private static Map<String, Integer> _columnHeaders;
	private static Map<String, ArrayList<String>> firstSets;
	private static Map<String, ArrayList<String>> followingSets;
	private static Map<String, String> _grammar;
	private static TOKEN_TYPE _type;
	private static SyntacticASTNode _root;
	private static String[][] _parseTable;
	private static List<LexicalToken> _tokenList;
	private static List<String> derivationList;
	private static Stack<String> stack;
	private static Stack<String> ruleStack;
	private static String tokenName;
	private static Stack<SyntacticASTNode> contextStack;
	private static int tokenCounter;
	private static int lineNumber;
	private static String tokenValue;
	
	public static Map<Integer, ArrayList<String>> map;

	public static Map<Integer, ArrayList<String>> getMap() 
	{
		return map;
	}

	
	public static void intializeParser() throws IOException 
	{
		_terminals = new ArrayList<>();
		_rowHeaders = new TreeMap<>();
		_columnHeaders = new TreeMap<>();
		_tokenList = new ArrayList<LexicalToken>();
		derivationList = new ArrayList<>();
		stack = new Stack<>();
		contextStack = new Stack<>();
		ruleStack = new Stack<>();
		tokenCounter = 0;
		tokenName = "";
		tokenValue = "";
		lineNumber = -1;
		_grammar = CompilerReadWrite.readGrammar();
		map = LexicalTokenizer.getMap();
		parser();
	}

	
	public static void parser() throws IOException
	{
		System.out.println("Reading grammar and generating the first and follow sets::");
		firstSets();
		followSets();
		System.out.println("Building the parse table");
		intializeTable();
		buildTable();
		System.out.println("Building the Abstract Syntax tree");
		System.out.println();
		parsing();
		new SyntacticASTNode().print(SyntacticParser.getRoot());
		System.out.println();
	}

	public static void firstSets() throws IOException 
	{
		firstSets = CompilerReadWrite.readFirstSets();

		try 
		{
			Iterator<Map.Entry<String, ArrayList<String>>> entrySet = firstSets.entrySet().iterator();
			Map.Entry<String, ArrayList<String>> entry;
			while (entrySet.hasNext()) {
				entry = entrySet.next();
				for (String string : entry.getValue())
				{
					if (!_terminals.contains(string)) {
						_terminals.add(string);
					}
				}
			}

		} catch (Exception e) {
				e.printStackTrace();
				System.out.println("No content in input file");
		}
	}

	public static void followSets() throws IOException {

		followingSets = CompilerReadWrite.readFollowSets();

		try {

			Iterator<Map.Entry<String, ArrayList<String>>> entrySet = followingSets.entrySet().iterator();
			Map.Entry<String, ArrayList<String>> entry;
			while (entrySet.hasNext()) {

				entry = entrySet.next();
				for (String string : entry.getValue()) {

					if (!_terminals.contains(string)) {
						_terminals.add(string);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No content in input file");
		}
	}

	
	public static void intializeTable() {
		
		try {
			_parseTable = new String[followingSets.size()+1][_terminals.size()+1];
			int rowCounter = 0;
			int columnCounter = 0;
			_parseTable[0][0] = "#";

			List<String> rowValues = new ArrayList<>(followingSets.keySet());
			for (String string : rowValues) {

				rowCounter++;
				_parseTable[rowCounter][0] = string;
				_rowHeaders.put(string, rowCounter);
			}

			for (String string : _terminals) {

				columnCounter++;
				_parseTable[0][columnCounter] = string;
				_columnHeaders.put(string, columnCounter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No content in input file");
		}
	}

	public static void buildTable() {

	try {

			Iterator<Map.Entry<String, String>> entrySet = _grammar.entrySet().iterator();
			Map.Entry<String, String> entry;
			String LHS = "";
			String RHS = "";
			ArrayList<String> set;
			String productionNumber;
			ArrayList<String> rulesWithEpsilon = CompilerReadWrite.productionsWithEpsilon();
			while (entrySet.hasNext()) {

				entry = entrySet.next();
				productionNumber = entry.getKey();
				LHS = entry.getValue().split("->")[0].replaceAll("\\s", "");
				
				int index = 0;
				while (entry.getValue().split("->")[1].trim().split("\\s")[index].startsWith("#")) {
					index++;
				}
				
				RHS = entry.getValue().split("->")[1].trim().split("\\s")[index];
				
				if (RHS.equals("EPSILON")) {
					
					set = followingSets.get(LHS);
					for (String string : set) {
						
						_parseTable[_rowHeaders.get(LHS)][_columnHeaders.get(string)] = productionNumber;
					}
				} else {

					set = firstSets.get(RHS);
					for (String string : set) {
						
						_parseTable[_rowHeaders.get(LHS)][_columnHeaders.get(string)] = productionNumber;
					}
					
					if (rulesWithEpsilon.contains(RHS)) {
						
						set = firstSets.get(LHS);
						for (String string : set) {
							
							if (_parseTable[_rowHeaders.get(LHS)][_columnHeaders.get(string)] == null) {
								
								_parseTable[_rowHeaders.get(LHS)][_columnHeaders.get(string)] = productionNumber;
							}							
						}
						
						set = followingSets.get(LHS);
						for (String string : set) {
							
							if (_parseTable[_rowHeaders.get(LHS)][_columnHeaders.get(string)] == null) {
								
								_parseTable[_rowHeaders.get(LHS)][_columnHeaders.get(string)] = productionNumber;
							}							
						}
					}
				}
			}

	} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No content in input file");
		}
	}

	public static void printTable() {
		
		for (int i = 0; i < _parseTable.length; i++) {
			
			for (int j = 0; j < _parseTable[i].length; j++) {
				
				System.out.print(_parseTable[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	
	public static void parsing() throws IOException {
		
		try {
			
			stack.push("$");
			stack.push(CompilerEnum.INITIAL_SYMBOL);
			
			_tokenList = LexicalTokenizer.outputTokens;
			_tokenList.add(new LexicalToken(TOKEN_TYPE.eof, "$", -1));
			
			String ruleLHS="";
			String ruleRHS="";
			nextToken();
			String top = "";
			boolean error = false;
			String rhsDerivation;
			String derivation = "prog";			
			derivationList.add(derivation);
			while (stack.peek() != "$") {
				
				top = stack.peek();
				
				if (_terminals.contains(top)) {
					
					if (top.equals(tokenName)) {
						
						stack.pop();
						nextToken();
					} else {
						
						
						skipErrors(true);
						error = true;
					}
				} else {
					
					if (top.equals("#BEGIN_varDecl")) 
					{
						Stack<String> currentContext = new Stack<>();
						for (int i = 0; i < 2; i++) 
						{
							currentContext.push(ruleStack.peek());
							ruleStack.pop();
						}
						ruleStack.push(top);
						for (int j = 0; j < 2; j++) 
						{
							ruleStack.push(currentContext.peek());
							currentContext.pop();
						}
						stack.pop();
						currentContext = null;
					} 
					else if (top.equals("#BEGIN_funcDecl")) 
					{
						Stack<String> currentContext = new Stack<>();
						for (int i = 0; i < 2; i++) {
							currentContext.push(ruleStack.peek());
							ruleStack.pop();
						}
						ruleStack.push(top);
						for (int j = 0; j < 2; j++) 
						{
							ruleStack.push(currentContext.peek());
							currentContext.pop();
						}
						stack.pop();
						currentContext = null;
					} 
					else if (top.equals("#BEGIN_scopeSpec")) 
					{
						Stack<String> currentContext = new Stack<>();
						currentContext.push(ruleStack.peek());
						ruleStack.pop();
						ruleStack.push(top);
						ruleStack.push(currentContext.peek());
						currentContext.pop();
						stack.pop();
						currentContext = null;
					} 
					else if (top.equals("#BEGIN_assignStat"))
					{
						Stack<String> currentContext = new Stack<>();
						currentContext.push(ruleStack.peek());
						ruleStack.pop();
						ruleStack.push(top);
						ruleStack.push(currentContext.peek());
						currentContext.pop();
						stack.pop();
						currentContext = null;
					} 
					else if (top.equals("#BEGIN_var")) 
					{
						Stack<String> currentContext = new Stack<>();
						currentContext.push(ruleStack.peek());
						ruleStack.pop();
						ruleStack.push(top);
						ruleStack.push(currentContext.peek());
						currentContext.pop();
						stack.pop();
						currentContext = null;
					} 
					else if (top.equals("#BEGIN_multOp")) 
					{
						Stack<String> currentContext = new Stack<>();
						currentContext.push(ruleStack.peek());
						ruleStack.pop();
						ruleStack.push(top);
						ruleStack.push(currentContext.peek());
						currentContext.pop();
						stack.pop();
						currentContext = null;
					} 
					else if (top.equals("#BEGIN_addOp"))
					{
						Stack<String> currentContext = new Stack<>();
						currentContext.push(ruleStack.peek());
						ruleStack.pop();
						ruleStack.push(top);
						ruleStack.push(currentContext.peek());
						currentContext.pop();
						stack.pop();
						currentContext = null;
					}
					else if (top.equals("#BEGIN_relOp"))
					{
						Stack<String> currentContext = new Stack<>();
						currentContext.push(ruleStack.peek());
						ruleStack.pop();
						ruleStack.push(top);
						ruleStack.push(currentContext.peek());
						currentContext.pop();
						stack.pop();
						currentContext = null;
					} 
					else if (top.startsWith("#BEGIN_"))
					{
						ruleStack.push(top);
						stack.pop();
					} else if (top.equals("#END_prog"))
					{
						SyntacticASTNode node = new SyntacticASTNode();
						node.setNodeType(top.substring(5));
						int count = 0;
						Stack<SyntacticASTNode> currentContext = new Stack<>(); 
						LinkedList<SyntacticASTNode> childrens = new LinkedList<>();
						while (!ruleStack.peek().equals("#BEGIN_"+top.substring(5))) 
						{
							count++;
							ruleStack.pop();
							currentContext.push(contextStack.peek());
							contextStack.pop();
						}
						for (int i = 0; i < count; i++)
						{
							childrens.add(currentContext.peek());
							currentContext.pop();
						}
						node.setLeafs(childrens);
						stack.pop();
						contextStack.push(node);
						_root = node;
						childrens = null;
						ruleStack = null;
						contextStack = null;
						currentContext = null;
					} 
					else if (top.startsWith("#END_"))
					{
						SyntacticASTNode node = new SyntacticASTNode();
						node.setNodeType(top.substring(5));
						int count = 0;
						Stack<SyntacticASTNode> currentContext = new Stack<>(); 
						LinkedList<SyntacticASTNode> childrens = new LinkedList<>();
						
						while (!ruleStack.peek().equals("#BEGIN_"+top.substring(5)))
						{
							count++;
							ruleStack.pop();
							currentContext.push(contextStack.peek());
							contextStack.pop();
						}
						for (int i = 0; i < count; i++) 
						{
							childrens.add(currentContext.peek());
							currentContext.pop();
						}
						
						
						node.setLeafs(childrens);
						stack.pop();
						contextStack.push(node);
						childrens = null;
						currentContext = null;
					} 
					else if (top.startsWith("#MAKE_NODE"))
					{
						stack.pop();
						SyntacticASTNode node = new SyntacticASTNode();
						if ((_type == TOKEN_TYPE.id && (_tokenList.get(tokenCounter).type == TOKEN_TYPE.id)) || (_type == TOKEN_TYPE.Int) || (_type == TOKEN_TYPE.Float))
						{
							node.setNodeType("typeNode");
							node.setData(tokenValue);
							node.setLineNumber(lineNumber);
						} 
						else if(_type == TOKEN_TYPE.id)
						{
							
							node.setNodeType("idNode");
							node.setData(tokenValue);
							node.setLineNumber(lineNumber);
						} 
						else if((_type == TOKEN_TYPE.intNum) || (_type == TOKEN_TYPE.floatNum)) 
						{
							
							node.setNodeType("numNode");
							node.setData(tokenValue);
							node.setType(_type == TOKEN_TYPE.intNum ? "int" : "float");
							node.setLineNumber(lineNumber);
						} 
						else if (_type == TOKEN_TYPE.operator || tokenValue.equals("or") || tokenValue.equals("and")) {
							
							node.setNodeType("operatorNode");
							node.setData(tokenValue);
							node.setLineNumber(lineNumber);
						}
						
						contextStack.add(node);
						ruleStack.push(stack.peek());
					} 
					else 
					{
						
						if (_parseTable[_rowHeaders.get(top)][_columnHeaders.get(tokenName)] != null) 
						{
							stack.pop();
							ruleLHS = _grammar.get(_parseTable[_rowHeaders.get(top)][_columnHeaders.get(tokenName)]).split("->")[0].trim();
							ruleRHS = _grammar.get(_parseTable[_rowHeaders.get(top)][_columnHeaders.get(tokenName)]).split("->")[1].trim();
							
							
							if (!ruleRHS.equals("EPSILON")) 
							{
								rhsDerivation = "";
								for (int i = ruleRHS.split("\\s").length-1; i >= 0; i--)
								{
									
									stack.push(ruleRHS.split("\\s")[i]);	
								}
								
								for (String string : ruleRHS.split("\\s"))
								{
									
									if (!string.startsWith("#"))
									{
										
										rhsDerivation+=string+" ";
									}
								}
								
								derivation = derivation.replaceFirst(ruleLHS, rhsDerivation);
								derivation = derivation.replaceAll("\\s+", " ");
								derivationList.add(derivation);
							} else 
							{
								derivation = derivation.replaceFirst(ruleLHS, "");
								derivation = derivation.replaceAll("\\s+", " ");
								derivationList.add(derivation);
							}
							
						} 
						else 
						{
							skipErrors(false);
							error = true;
						}
					}
				}			
			}
			
			if (!tokenName.equals("$") || error == true) 
			{
				derivationList.clear();
				System.out.println("Parsing cannot be completed successfully because of errors in the source file. Please  rectify the given input.");
				System.out.println();
				System.out.println("Printing Tree (Incomplete due to unsuccessful parsing)");
				System.out.println();
			} 
			else 
			{
				System.out.println("Parsing Successful");
				System.out.println();
				System.out.println("Printing Tree");
				System.out.println();
			}
	}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	
	public static void nextToken() 
	{
		try 
		{
			if (_tokenList.get(tokenCounter).type == TOKEN_TYPE.id)
			{
				tokenName = "id";
				lineNumber = _tokenList.get(tokenCounter).getLineNum();
				_type = _tokenList.get(tokenCounter).type;
				tokenValue = _tokenList.get(tokenCounter).tokenValue;
				tokenCounter++;
			} 
			
			else if(_tokenList.get(tokenCounter).type == TOKEN_TYPE.intNum)
			{
				tokenName = "intNum";
				lineNumber = _tokenList.get(tokenCounter).getLineNum();
				_type = _tokenList.get(tokenCounter).type;
				tokenValue = _tokenList.get(tokenCounter).tokenValue;
				tokenCounter++;
			} 
			
			else if(_tokenList.get(tokenCounter).type == TOKEN_TYPE.floatNum)
			{
				tokenName = "floatNum";
				lineNumber = _tokenList.get(tokenCounter).getLineNum();
				_type = _tokenList.get(tokenCounter).type;
				tokenValue = _tokenList.get(tokenCounter).tokenValue;
				tokenCounter++;
			} 
			else 
			{
				tokenName = _tokenList.get(tokenCounter).getToken_Val();
				lineNumber = _tokenList.get(tokenCounter).getLineNum();
				_type = _tokenList.get(tokenCounter).type;
				tokenValue = _tokenList.get(tokenCounter).tokenValue;
				tokenCounter++;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}	
		
	}
	

	public static void skipErrors(boolean isTerminal) 
	{
		try
		{
			Utility util = new Utility();
			
			if (isTerminal) 
			{
                	
				if (tokenCounter<_tokenList.size())
				{
					util.setMap(map);
	                map = util.reportError(lineNumber, "Error ("+tokenName+") reported during Syntactic phase in line ");
					nextToken();
				} 
				else{stack.pop();}	
				
			} 
			else 
			{			

				ArrayList<String> followSet = followingSets.get(stack.peek());
				
				if (followSet.contains(tokenName) || tokenName.equals("$")) 
				{
					util.setMap(map);
	                map = util.reportError(lineNumber, "Error ("+tokenName+") reported during Syntactic phase in line ");
					stack.pop();
					
				} else {

					if (tokenCounter<_tokenList.size())
					{
						util.setMap(map);
		                map = util.reportError(lineNumber, "Error ("+tokenName+") reported during Syntactic phase in line ");
						nextToken();
					} 
					else 
					{	
						util.setMap(map);
		                map = util.reportError(lineNumber, "Error ("+tokenName+") reported during Syntactic phase in line ");
						stack.pop();
					}
				}
			}
	} 	
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	public static SyntacticASTNode getRoot() 
	{
		return _root;
	}

	public static void setRoot(SyntacticASTNode root) 
	{
		SyntacticParser._root = root;
	}
}
