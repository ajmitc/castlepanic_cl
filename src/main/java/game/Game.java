package castlepanic.game;

import java.util.List;
import java.util.ArrayList;

public class Game
{
	private List<Tower> _towers;
	private List<Wall> _walls;
	private List<Arc> _arcs;
	
	public Game()
	{
		_towers = new ArrayList<>( 6 );
		_walls  = new ArrayList<>( 6 );
		_arcs   = new ArrayList<>( 6 );
		
		for( int i = 0; i < 6; ++i )
		{
			_towers.add( new BasicTower() );
			_walls.add( new BasicWall() );
		}
		_arcs.add( new Arc( 1, Arc.RED ) );
		_arcs.add( new Arc( 2, Arc.RED ) );
		_arcs.add( new Arc( 3, Arc.GREEN ) );
		_arcs.add( new Arc( 4, Arc.GREEN ) );
		_arcs.add( new Arc( 5, Arc.BLUE ) );
		_arcs.add( new Arc( 6, Arc.BLUE ) );
	}
	
	public List<Tower> getTowers(){ return _towers; }
	public List<Wall> getWalls(){ return _walls; }
	public List<Arc> getArcs>(){ return _arcs; }
}