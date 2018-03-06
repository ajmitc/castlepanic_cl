package leader.view;

public class GameMenuItem
{
	private String _display;
	private GameMenuItemCallback _callback;
	
	private Object _object;
	
	public GameMenuItem( String display, GameMenuItemCallback callback )
	{
		this( display, null, callback );
	}
	
	public GameMenuItem( String display, Object o, GameMenuItemCallback callback )
	{
		_display = display;
		_callback = callback;
		_object = o;
	}
	
	public String getDisplay(){ return _display; }
	public GameMenuItemCallback getCallback(){ return _callback; }
	
	public Object getObject(){ return _object; }
	public void setObject( Object o ){ _object = o; }
}