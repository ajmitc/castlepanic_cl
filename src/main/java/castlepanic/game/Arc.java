package castlepanic.game;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

public class Arc
{
	public static final Color RED   = Color.RED;
	public static final Color GREEN = Color.GREEN;
	public static final Color BLUE  = Color.BLUE;
	
	private int _number;
	private Color _color;
	private List<Ring> _rings;
	
	public Arc( int num, Color color )
	{
		_number = num;
		_color = color;
		_rings = new ArrayList<>( 5 );
		_rings.add( new Ring( Ring.CASTLE,    this ) );
		_rings.add( new Ring( Ring.SWORDSMAN, this ) );
		_rings.add( new Ring( Ring.KNIGHT,    this ) );
		_rings.add( new Ring( Ring.ARCHER,    this ) );
		_rings.add( new Ring( Ring.FOREST,    this ) );
	}
	
	public Ring getRing( int range )
	{
		if( range >= Ring.CASTLE && range <= Ring.FOREST )
			return _rings.get( range - 1 );
		return null;
	}
	
	public int getNumber(){ return _number; }
	public Color getColor(){ return _color; }
	public List<Ring> getRings(){ return _rings; }

	public String getColorName()
    {
        if( _color == RED ) return "Red";
        if( _color == GREEN ) return "Green";
        if( _color == BLUE ) return "Blue";
        return "Unknown";
    }
}
