package castlepanic;

public class Model
{
	private Game _game;
	
	public Model()
	{
		_game = null;
	}
	
	public Game getGame(){ return _game; }
	public void setGame( Game game ){ _game = game; }
}