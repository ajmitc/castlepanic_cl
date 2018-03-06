package castlepanic.view;

public class GameMenuItem
{
	private String _display;
	private GameMenuItemCallback _callback;
    private String _optionDisplay; // Display this instead of the index
	
	private Object _object;
	
	public GameMenuItem( String display, GameMenuItemCallback callback )
	{
		this( display, null, callback );
	}
    
	public GameMenuItem( String display, Object o, GameMenuItemCallback callback )
	{
        this( null, display, o, callback );
	}

	public GameMenuItem( String optionDisplay, String display, Object o, GameMenuItemCallback callback )
	{
		_display = display;
		_callback = callback;
		_object = o;
        _optionDisplay = optionDisplay;
	}
	
	public String getDisplay(){ return _display; }
	public GameMenuItemCallback getCallback(){ return _callback; }

    public String getOptionDisplay(){ return _optionDisplay; }
    public void setOptionDisplay( String s ){ _optionDisplay = s; }
	
	public Object getObject(){ return _object; }
	public void setObject( Object o ){ _object = o; }
}
