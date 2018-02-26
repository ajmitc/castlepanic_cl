package castlepanic.game.monster;

public enum MonsterType
{
	GOBLIN( "Goblin", 1 ),
	ORC( "Orc", 2 ),
	TROLL( "Troll", 3 ),
	GOBLIN_KING( "Goblin King", 2 ),
	ORC_WARLORD( "Orc Warlord", 3 ),
	TROLL_MAGE( "Troll Mage", 3 );
	
	private String _name;
	private int _hitpoints;
	MonsterType( String n, int hp )
	{
		_name = n;
		_hitpoints = hp;
	}
	
	public String getName(){ return _name; }
	public int getHitpoints(){ return _hitpoints; }
	
	public String toString(){ return _name; }
}