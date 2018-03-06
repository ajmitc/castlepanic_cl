package castlepanic.game;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

import castlepanic.game.monster.Monster;

public class Ring
{
	public static final int FOREST    = 5;
	public static final int ARCHER    = 4;
	public static final int KNIGHT    = 3;
	public static final int SWORDSMAN = 2;
	public static final int CASTLE    = 1;
	
	private int _range;
	private Arc _arc;
	private List<Monster> _monsters;
	
	public Ring( int range, Arc arc )
	{
		_range = range;
		_arc = arc;
		_monsters = new ArrayList<>();
	}
	
	public int getRange(){ return _range; }
	public Arc getArc(){ return _arc; }
	public List<Monster> getMonsters(){ return _monsters; }

    public String toString()
    {
        switch( _range )
        {
            case FOREST:
                return "Forest";
            case ARCHER:
                return "Archer";
            case KNIGHT:
                return "Knight";
            case SWORDSMAN:
                return "Swordsman";
            case CASTLE:
                return "Castle";
            default:
                return "Unknown";
        }
    }
}
