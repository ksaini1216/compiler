package compiler.driver;
import java.io.IOException;
import java.util.Scanner;

import lexicalphase.LexicalTokenizer;
import syntacticphase.SyntacticParser;

public class CompilerDriver 
{
	public static void main(String[] args) throws IOException 
	{
		Scanner scan= new Scanner(System.in);
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(" Please select 1,2,or 3\n");
			builder.append("1: Lexical Analyzer\n");
			builder.append("2: Syntactic Analyzer\n");
			builder.append("3: Exit\n");
			String givenPhase = new String(); 
			givenPhase = builder.toString();
			System.out.println(givenPhase);


			switch (scan.nextLine()) 
			{
			case "1":LexicalTokenizer.lexical_Analyzer();
			break;
			case "2": LexicalTokenizer.lexical_Analyzer();
			SyntacticParser.intializeParser();
			break;
			case "3": System.out.println("Terminating.....");
			System.exit(0);
			break;				
			default: break;
			} 
		} 
		finally {
			scan.close();
		}
	} 
}
