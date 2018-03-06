package castlepanic.game;

public class Result
{
	public static final int OK = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;
	public static final int EXCEPTION = 3;
	
	private boolean _ok;
	private int _reasonCode;
	private String _reasonDetails;
	
	public Result()
	{
		this( true, OK, "OK" );
	}
	
	public Result( boolean ok, int code, String details )
	{
		_ok = ok;
		_reasonCode = code;
		_reasonDetails = details;
	}
	
	public boolean isOK(){ return _ok; }
	public void setOK( boolean v ){ _ok = v; }
	
	public int getReasonCode(){ return _reasonCode; }
	public void setReasonCode( int code ){ _reasonCode = code; }
	
	public String getReasonDetails(){ return _reasonDetails; }
	public void setReasonDetails( String s ){ _reasonDetails = s; }
}