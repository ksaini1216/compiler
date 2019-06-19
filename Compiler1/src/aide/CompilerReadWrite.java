package aide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import compiler.constants.Constants;
import lexicalphase.LexicalToken;

public class CompilerReadWrite
{
	private static BufferedReader read;
	private static BufferedWriter write;
	private static ArrayList<String> productionWithEpsilon = new ArrayList<>();
	private static ArrayList<String> productionWithEpsilonInFirstSet = new ArrayList<>();
	
	public static TreeMap<Integer, String> readInput() throws IOException 
	{
		read = new BufferedReader(new InputStreamReader(new FileInputStream("data/inputFiles/input.txt")));
		String line = "";
		int lineNumber = 0;
		TreeMap<Integer, String> input = new TreeMap<>();
//		try
//		{

			while ((line = read.readLine()) != null)
			{
				lineNumber++;
				exit: for (int currentChar = 0; currentChar < line.length(); currentChar++)
				{

					switch (line.charAt(currentChar))
					{

					case '/':
						currentChar++;
						if (line.charAt(currentChar) == '/')
						{
							line = line.substring(0, currentChar - 1);

						} 
						else if (line.charAt(currentChar) == '*') 
						{
							String intialLine = line;
							int nestedBlock  = 1;
							int currentLineLength = line.length();
							line = line.substring(0, currentChar - 1);
							input.put(lineNumber, line);
							lineNumber++;

							
						if (intialLine.substring(currentChar + 1, currentLineLength).contains("*/")) 
						{
								line = line + " " + intialLine.substring(intialLine.indexOf("*/") + 2);
								lineNumber--;
								break;
							}

							while ((line = read.readLine()) != null && (nestedBlock > 0))
							{
								if 		(line.contains("/*")) 
								{	
									nestedBlock++;	
								}
								else if (line.contains("*/"))
								{	
									nestedBlock--;	
								}
								lineNumber++;
							}

							if (line != null) 
							{

								if (line.contains("*/")) 
								{

									line = line.substring(line.indexOf("*/") + 2);
								}
							} 
							else
							{
								break exit;
							}
						}
						break;

					default:
						break;
					}
				}

				input.put(lineNumber, line);
			}
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		} 
//		finally
//		{
//
//			read.close();
//		}

		return input;
	}

	public static void writeOutput(List<LexicalToken> tokens) throws IOException
	{
	
		write = new BufferedWriter(new FileWriter(new File(Constants.TOKEN_FILE_PATH)));
//		try
//		{
	
			for (LexicalToken token : tokens) 
			{
				write.write(token.toString());
				write.newLine();
			}
//		}
//		catch (Exception e) 
//		{
//	
//			e.printStackTrace();
//		}
//		finally
//		{
//	
//			write.close();
//		}
	
	}

	public static TreeMap<String, String> readGrammar() throws IOException
	{
		
		read = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.GRAMMAR_FILE_PATH)));
		int productionNumber = 0;
		String production = "";
		String LHS = "";
		String RHS = "";
		TreeMap<String, String> grammar = new TreeMap<>();
		
//		try 
//		{
			
			while ((production = read.readLine()) != null) 
			{	
				productionNumber++;
				grammar.put(Integer.toString(productionNumber), production);
				LHS = production.split("->")[0].replaceAll("\\s","");
				RHS = production.split("->")[1].replaceAll("\\s","");
				
				if (RHS.equals("EPSILON")) 
				{
					
					productionWithEpsilon.add(LHS);
				}
			}
//		} 
//		catch (Exception e) 
//		{
//			
//			e.printStackTrace();
//		} 
//		finally 
//		{
//			
//			read.close();
//		}
		
		return grammar;
	}
	
	public static TreeMap<String, ArrayList<String>> readFirstSets() throws IOException
	{
		
		read = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.FIRSTSETS_FILE_PATH)));
		String set = "";
		TreeMap<String, ArrayList<String>> firstSets = new TreeMap<>();
		
//		try 
//		{
			
			String key="";
			String value="";
			ArrayList<String> valueSet;
			while ((set = read.readLine()) != null) 
			{
				
				key = set.split("->")[0].replaceAll("\\s", "");
				value = set.split("->")[1].replaceAll("\\s", "");
				valueSet = new ArrayList<>();
				
				for (int i = 0; i < value.split("\\|").length; i++)
				{
					
					if (value.split("\\|")[i].equals("EPSILON")) 
					{
						
						productionWithEpsilonInFirstSet.add(key);
					} 
					else 
					{

						valueSet.add(value.split("\\|")[i]);
					}				
				}
				
				firstSets.put(key, valueSet);
			}
//		} catch (Exception e) 
//		{
//			
//			e.printStackTrace();
//		} 
//		finally {
//			
//			read.close();
//		}
		return firstSets;
	}

	public static TreeMap<String, ArrayList<String>> readFollowSets() throws IOException
	{
		
		read = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.FOLLOWSETS_FILE_PATH)));
		String set = "";
		
		TreeMap<String, ArrayList<String>> followSets = new TreeMap<>();
		
//		try 
//		{
		 String key="";
			String value="";
			ArrayList<String> valueSet;
			while ((set = read.readLine()) != null)
			{
				
				key = set.split("->")[0].replaceAll("\\s", "");
				value = set.split("->")[1].replaceAll("\\s", "");
				valueSet = new ArrayList<>();
				
				for (int i = 0; i < value.split("\\|").length; i++) 
				{
					
					valueSet.add(value.split("\\|")[i]);
				}
				
				followSets.put(key, valueSet);
			}
//		}
//		catch (Exception e) 
//		{
//			
//			e.printStackTrace();
//		} 
//		finally 
//		{
//		read.close();
//		}
		return followSets;
	}
	public static ArrayList<String> productionsWithEpsilon() 
	{		
		return productionWithEpsilon;
	}

	public static ArrayList<String> productionsWithEpsilonInFirstSets()
	{	
		return productionWithEpsilonInFirstSet;
	}
}
