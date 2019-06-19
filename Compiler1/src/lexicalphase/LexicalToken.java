package lexicalphase;
import compiler.constants.CompilerEnum.TOKEN_TYPE;
public class LexicalToken
{
	public final TOKEN_TYPE type;
	public final String tokenValue;
	public final Integer lineNumber;
	
	public LexicalToken(TOKEN_TYPE type, String tokenValue, Integer lineNumber)
	{
		this.type = type;
		this.tokenValue = tokenValue;
		this.lineNumber = lineNumber;
	}
 
	public TOKEN_TYPE getType() 
	{
		return type;
	}
	public String getToken_Val() 
	{
		return tokenValue;
	}
	public Integer getLineNum()
	{
		return lineNumber;
	}
	public String toString() 
	{
		return "<TokenType: "+type.toString()+", TokenValue: '"+tokenValue+"', Line Number: "+lineNumber+">";
	}	
}
