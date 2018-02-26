package castlepanic.game;

import java.util.List;
import java.util.ArrayList;

public class Game
{
	private List<Tower> _towers;
	private List<Wall> _walls;
	private List<Arc> _arcs;
	
	private List<Monster> _monsterPool;
	
	private Deck<CastleCard> _castleCards;
	private Deck<WizardCard> _wizardCards;
	
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
		
		_monsterPool = new ArrayList<>();
		_castleCards = new Deck<>();
		_wizardCards = new Deck<>();
	}
	
	/**
	 * Player plays these cards.  There should be one main card (ie. Hit) and any supplement cards (ie. Double Strike)
	 */
	public boolean playCards( List<Card> cards )
	{
		return false;
	}
	
	public List<Tower> getTowers(){ return _towers; }
	public List<Wall> getWalls(){ return _walls; }
	public List<Arc> getArcs>(){ return _arcs; }
	
	public List<Monster> getMonsterPool(){ return _monsterPool; }
	
	public Deck getCastleCards(){ return _castleCards; }
	public Deck getWizardCards(){ return _wizardCards; }
}