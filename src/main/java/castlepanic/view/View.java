package castlepanic.view;

import castlepanic.Model;

public abstract class View
{
	protected Model _model;
	
	public View( Model model )
	{
		_model = model;
	}
	
	public abstract GameMenuItem displayMenu( GameMenu menu );
	public abstract void refresh();
	
	public Model getModel(){ return _model; }
}
