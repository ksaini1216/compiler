package lexicalphase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import aide.CompilerReadWrite;
import aide.Utility;
import compiler.constants.CompilerEnum.TOKEN_TYPE;
import compiler.constants.CompilerEnum.TOKEN_VALUE;
public class LexicalTokenizer 
{
public static LexicalToken token;
public static List<LexicalToken> outputTokens = new ArrayList<LexicalToken>();
public static List<String> aToCcFormat = new ArrayList<String>();
public static Map<Integer, ArrayList<String>> map;


public static Map<Integer, ArrayList<String>> getMap() 
{
	return map;
}

public static LexicalToken alphaLexeme(String input, int lexemeBegin, Integer lineNumber) 
{
	int forward = lexemeBegin;
	forward++;
	for (; forward < input.length();)
	{
		if (Character.isLetter(input.charAt(forward)) ||
				Character.isDigit(input.charAt(forward)) || input.charAt(forward) == '_') 
		{
			forward++;
		} 
		else 
		{
			for (TOKEN_VALUE value : TOKEN_VALUE.values())
			{
				if (input.substring(lexemeBegin, forward).equalsIgnoreCase(value.tokenValue())) 
				{
					if (input.substring(lexemeBegin, forward).equalsIgnoreCase("int"))
					{
						return new LexicalToken(TOKEN_TYPE.Int, input.substring(lexemeBegin, forward), lineNumber);
					}
					else if (input.substring(lexemeBegin, forward).equalsIgnoreCase("float")) 
					{
						return new LexicalToken(TOKEN_TYPE.Float, input.substring(lexemeBegin, forward), lineNumber);
					} 
					else {
						return new LexicalToken(TOKEN_TYPE.keyword, input.substring(lexemeBegin, forward), lineNumber);
					}
				}
			}
				return new LexicalToken(TOKEN_TYPE.id, input.substring(lexemeBegin, forward), lineNumber);
		}
	}

		
	for (TOKEN_VALUE value : TOKEN_VALUE.values()) 
	{
		if (input.substring(lexemeBegin, forward).equalsIgnoreCase(value.tokenValue())) 
		{
			if (input.substring(lexemeBegin, forward).equalsIgnoreCase("int")) 
			{
				return new LexicalToken(TOKEN_TYPE.Int, input.substring(lexemeBegin, forward), lineNumber);
			} 
			else if (input.substring(lexemeBegin, forward).equalsIgnoreCase("float"))
			{
				return new LexicalToken(TOKEN_TYPE.Float, input.substring(lexemeBegin, forward), lineNumber);
			} 
			else {
				return new LexicalToken(TOKEN_TYPE.keyword, input.substring(lexemeBegin, forward), lineNumber);
				}
			}
		}
		return new LexicalToken(TOKEN_TYPE.id, input.substring(lexemeBegin, forward), lineNumber);
	}

	public static LexicalToken numericLexeme(String input, int lexemeBegin, Integer lineNumber)
	{
		int forward = lexemeBegin;
		forward++;
		int checkPoint;
		for (; forward < input.length();) 
		{
			if (Character.isDigit(input.charAt(forward)))
			{
				forward++;
			}
			else if (input.charAt(forward) == '.' && forward < input.length() - 1)
			{
				forward++;
				if (Character.isDigit(input.charAt(forward)) && forward < input.length() - 1) 
				{
					checkPoint = forward;
					forward++;
					for (; forward < input.length();) 
					{
						if (Character.isDigit(input.charAt(forward)))
						{
							if (input.charAt(forward) != '0') 
							{checkPoint = forward;}
							forward++;
						} 

						else if (input.charAt(forward) == 'e' && forward < input.length() - 1) 
						{
							forward++;
							if (input.charAt(forward) == '0')
							{
								forward++;
								return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);
							} 

							else if (Character.isDigit(input.charAt(forward))) 
							{forward++;

							for (; forward < input.length();)
							{
								if (Character.isDigit(input.charAt(forward))) 
								{
									forward++;
								}

								else 
								{
									return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);
								}
							}

							return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);

							} 
							else if ((input.charAt(forward) == '+' && (forward < input.length() - 1))
									|| (input.charAt(forward) == '-' && (forward < input.length() - 1)))
							{
								forward++;

								for (; forward < input.length();) 
								{
									if (Character.isDigit(input.charAt(forward))) 
									{
										forward++;
									} 
									else {
										return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);
									}
								}
								return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);
							} 
							else {
								forward--;

								return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);
							}
						} 
						else {
							return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, checkPoint + 1),
									lineNumber);
						}
					}
					return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, checkPoint + 1), lineNumber);
				} 
				else if (Character.isDigit(input.charAt(forward)) && forward == input.length() - 1) 
				{
					forward++;
					return new LexicalToken(TOKEN_TYPE.floatNum, input.substring(lexemeBegin, forward), lineNumber);
				} 
				else 
				{
					forward--;
					return new LexicalToken(TOKEN_TYPE.intNum, input.substring(lexemeBegin, forward), lineNumber);
				}
			}
			else {
				return new LexicalToken(TOKEN_TYPE.intNum, input.substring(lexemeBegin, forward), lineNumber);
			}
		}
		return new LexicalToken(TOKEN_TYPE.intNum, input.substring(lexemeBegin, forward), lineNumber);
	}

	public static List<LexicalToken> nextToken(Integer lineNumber, String input)
	{
		List<LexicalToken> tokens = new ArrayList<LexicalToken>();
		Utility util = new Utility();
		for (int currentChar = 0; currentChar < input.length();) 
		{
			switch (input.charAt(currentChar))
			{
			case '=': currentChar++;
			if (input.charAt(currentChar) == '=') 
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "eq", lineNumber));
				currentChar++;
			} 
			else
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "=", lineNumber));
			}
			break;

			case '<': currentChar++;
			if (input.charAt(currentChar) == '>')
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "neq", lineNumber));
				currentChar++;
			} 
			else if (input.charAt(currentChar) == '=') 
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "leq", lineNumber));
				currentChar++;
			} 
			else 
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "lt", lineNumber));
			}
			break;

			case '>': currentChar++;
			if (input.charAt(currentChar) == '=') 
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "geq", lineNumber));
				currentChar++;
			} 
			else
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "gt", lineNumber));
			}
			break;

			case ';': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, ";", lineNumber));
			currentChar++;
			break;

			case ',': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, ",", lineNumber));
			currentChar++;
			break;

			case '.': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, ".", lineNumber));
			currentChar++;
			break;

			case ':': currentChar++;

			if (input.charAt(currentChar) == ':') 
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.operator, "sr", lineNumber));
				currentChar++;
			}
			else 
			{
				tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, ":", lineNumber));
			}
			break;

			case '(':tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, "(", lineNumber));
			currentChar++;
			break;

			case ')': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, ")", lineNumber));
			currentChar++;
			break;

			case '{': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, "{", lineNumber));
			currentChar++;
			break;

			case '}': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, "}", lineNumber));
			currentChar++;
			break;

			case '[': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, "[", lineNumber));
			currentChar++;
			break;

			case ']': tokens.add(new LexicalToken(TOKEN_TYPE.puntuation, "]", lineNumber));
			currentChar++;
			break;

			case '+': tokens.add(new LexicalToken(TOKEN_TYPE.operator, "+", lineNumber));
			currentChar++;
			break;

			case '-': tokens.add(new LexicalToken(TOKEN_TYPE.operator, "-", lineNumber));
			currentChar++;
			break;

			case '*': tokens.add(new LexicalToken(TOKEN_TYPE.operator, "*", lineNumber));
			currentChar++;
			break;

			case '/': tokens.add(new LexicalToken(TOKEN_TYPE.operator, "/", lineNumber));
			currentChar++;
			break;

			default: if (Character.isWhitespace(input.charAt(currentChar)))
			{
				currentChar++;
			} 
			else if (Character.isLetter(input.charAt(currentChar))) 
			{
				token = alphaLexeme(input, currentChar, lineNumber);
				currentChar += token.getToken_Val().length();
				tokens.add(token);
			} 
			else if (Character.isDigit(input.charAt(currentChar)) && input.charAt(currentChar) == '0'
					&& currentChar < input.length() - 1)
			{
				currentChar++;
				if (input.charAt(currentChar) == '.')
				{
					currentChar--;
					token = numericLexeme(input, currentChar, lineNumber);
					currentChar += token.getToken_Val().length();
					tokens.add(token);
				}
				else 
				{
					tokens.add(new LexicalToken(TOKEN_TYPE.intNum, "0", lineNumber));
				}
			} 
			else if (Character.isDigit(input.charAt(currentChar))) 
			{
				token = numericLexeme(input, currentChar, lineNumber);
				currentChar += token.getToken_Val().length();
				tokens.add(token);
			}
			else 
			{
				util.setMap(map);
				map = util.reportError(lineNumber, "Error (" + Character.toString(input.charAt(currentChar))
				+ ") reported during lexical phase in line ");
				currentChar++;
			}
			break;
			}
		}
		return tokens;
	}

	public static void lexical_Analyzer() throws IOException {

		TreeMap<Integer, String> inputList = CompilerReadWrite.readInput();
		map = new Utility().getMap();
		List<LexicalToken> receivedTokensList = null;
		int tokenCounter = 0;

		try {

			Iterator<Map.Entry<Integer, String>> entrySet = inputList.entrySet().iterator();
			Map.Entry<Integer, String> entry;
			String aToCc = "";
			while (entrySet.hasNext()) {

				entry = entrySet.next();
				if (entry.getValue() != null) {

					receivedTokensList = nextToken(entry.getKey(), entry.getValue());
					for (LexicalToken token : receivedTokensList) {
						if (token.type.toString().equalsIgnoreCase("id")
								|| token.type.toString().equalsIgnoreCase("floatNum")
								|| token.type.toString().equalsIgnoreCase("intNum")) {
							aToCc += token.type.toString() + " ";
						} 
						else 
						{
							aToCc += token.tokenValue + " ";
						}

						tokenCounter++;
						outputTokens.add(token);
					}
				}
			}


			System.out.println("Token Stream: ");
			System.out.println(aToCc.substring(0, aToCc.length()));
			aToCcFormat.add(aToCc.substring(0, aToCc.length()));
			CompilerReadWrite.writeOutput(outputTokens);
		} 

		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Content not found in input file");
		}
		System.out.println("Number of valid tokens: " + tokenCounter);
		System.out.println();
		System.out.println("Lexical-Phase Ends");
		System.out.println();
	}
}
