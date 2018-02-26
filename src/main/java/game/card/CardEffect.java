package castlepanic.game.card;

public enum CardEffect
{
	HIT_BLUE,         // Hit 1 Monster in Blue Arcs
	HIT_GREEN,        // Hit 1 Monster in Green Arcs
	HIT_RED,          // Hit 1 Monster in Red Arcs
	
	HIT_BLUE_ARCHER,  // Hit 1 Monster in the Blue Archer Ring
	HIT_RED_ARCHER,   // Hit 1 Monster in the Red Archer Ring
	HIT_GREEN_ARCHER, // Hit 1 Monster in the Green Archer Ring
	
	HIT_BLUE_KNIGHT,  // Hit 1 Monster in the Blue Knight Ring
	HIT_RED_KNIGHT,   // Hit 1 Monster in the Red Knight Ring
	HIT_GREEN_KNIGHT, // Hit 1 Monster in the Green Knight Ring
	
	HIT_BLUE_SWORDSMAN,  // Hit 1 Monster in the Blue Swordsman Ring
	HIT_RED_SWORDSMAN,   // Hit 1 Monster in the Red Swordsman Ring
	HIT_GREEN_SWORDSMAN, // Hit 1 Monster in the Green Swordsman Ring
	
	SLAY_NOT_FOREST,     // Slay 1 Monster on board not in forest
	SLAY,                // Slay 1 Monster on board
	PLAY_TWICE;          // Play the next card twice
	
	private String _desc;
	
	CardEffect( String desc )
	{
		_desc = desc;
	}
	
	public String getDescription(){ return _desc; }
	public String toString(){ return _desc; }
}