package compiler.constants;
public class CompilerEnum 
{
	public static String INITIAL_SYMBOL = "prog";

	public static enum TOKEN_TYPE 
	{
		id,
		intNum,
		floatNum,
		keyword,
		eq,
		neq,
		lt,
		gt,
		leq,
		geq,
		operator,
		puntuation,
		sr,
		eof,
		Int,
		Float,
		typeerror
	}
	
	public static enum TOKEN_VALUE
	{
		AND("and"),
		NOT("not"),
		OR("or"),
		IF("if"),
		THEN("then"),
		ELSE("else"),
		FOR("for"),
		CLASS("class"),
		INT("int"),
		FLOAT("float"),
		GET("get"),
		PUT("put"),
		RETURN("return"),
		PROGRAM("program");
	
	private final String value;
		
		TOKEN_VALUE (String value) 
		{
			this.value=value;
		}
		
		public String tokenValue()
		{
			return value;
		}
}
	
public static enum SYMBOL_TABLE_ENTRY_CATEGORY
	{
		Class,
		Function,
		Parameter,
		Variable
	}
	
}
