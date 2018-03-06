package castlepanic.game.monster;

public enum MonsterType
{
	GOBLIN( "Goblin", 1 ),
	ORC( "Orc", 2 ),
	TROLL( "Troll", 3 ),
	GOBLIN_KING( "Goblin King", 2, true ),
	ORC_WARLORD( "Orc Warlord", 3, true ),
	TROLL_MAGE( "Troll Mage", 3, true ),
	
	IMP( "Imp", 1 ),
	CONJURER( "Conjurer", 2 ),
	CLIMBING_TROLL( "Climbing Troll", 3 ),
	OGRE( "Ogre", 4 ),
	GOBLIN_CAVALRY( "Goblin Cavalry", 2 ),
	
	MOVE_BLUE( "Move Blue 1 Ring" ),
	MOVE_GREEN( "Move Green 1 Ring" ),
	MOVE_RED( "Move Red 1 Ring" ),
	DRAW_3_MONSTERS( "Draw 3 More Monsters" ),
	DRAW_4_MONSTERS( "Draw 4 More Monsters" ),
	MOVE_CLOCKWISE( "Move all Monsters clockwise" ),
	MOVE_COUNTER_CLOCKWISE( "Move all Monsters counter-clockwise" ),
	
	NECROMANCE( "Necromancer", 4, true ),
	WARLOCK( "Warlock", 4, true ),
	HYDRA( "Hydra", 4, true ),
	CHIMERA( "Chimera", 5, true ),
	DRAGON( "Dragon", 5, true );
	
	private String _name;
	private int _hitpoints;
	private boolean _boss;
	MonsterType( String n, int hp, boolean boss )
	{
		_name = n;
		_hitpoints = hp;
		_boss = boss;
	}
	
	MonsterType( String n, int hp )
	{
		this( n, hp, false );
	}
	
	MonsterType( String n )
	{
		this( n, 0 );
	}
	
	public String getName(){ return _name; }
	public int getHitpoints(){ return _hitpoints; }
	
	public String toString(){ return _name; }
}