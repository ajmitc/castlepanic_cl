package castlepanic.view;

import java.util.List;
import java.util.ArrayList;

public class GameMenu
{
	private String _title;
	private String _prompt;
	private List<GameMenuItem> _items;
	
	public GameMenu( String title )
	{
		this( title, "> " );
	}
	
	public GameMenu( String title, String prompt )
	{
		_title = title;
		_prompt = prompt;
		_items = new ArrayList<GameMenuItem>();	
	}
	
	public String getTitle(){ return _title; }
	public String getPrompt(){ return _prompt; }
	public List<GameMenuItem> getItems(){ return _items; }
}