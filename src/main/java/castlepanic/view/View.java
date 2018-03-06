package castlepanic.view;

public abstract class View
{
	protected Model _model;
	
	public View( Model model )
	{
		_model = model;
	}
	
	public GameMenuItem displayMenu( GameMenu menu );
	
	public Model getModel(){ return _model; }
}