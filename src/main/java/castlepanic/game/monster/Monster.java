package castlepanic.game.monster;

public class Monster
{
	private MonsterType _type;
	private int _hitpoints;
	private boolean _tar;
	private int _fire;
	
	public Monster( MonsterType type )
	{
		_type = type;
		_hitpoints = _type.getHitpoints();
		_tar = false;
		_fire = 0;
	}
	
	public MonsterType getType(){ return _type; }
	public int getMaxHitpoints(){ return _type.getHitpoints(); }
	public int getHitpoints(){ return _hitpoints; }
	public void setHitpoints( int v ){ _hitpoints = v; }
	public void adjHitpoints( int v )
	{
		_hitpoints += v;
		if( _hitpoints > _type.getHitpoints() )
			_hitpoints = _type.getHitpoints();
		if( _hitpoints < 0 )
			_hitpoints = 0;
	}
	public boolean hasTar(){ return _tar; }
	public void setTar( boolean v ){ _tar = v; }
	public int getFire(){ return _fire; }
	public void setFire( int v ){ _fire = v; }
	public void adjFire( int v ){ _fire += v; }
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder( _type.getName() );
		sb.append( "[" );
		sb.append( getHitpoints() );
		sb.append( "/" );
		sb.append( getMaxHitpoints() );
		if( _tar )
			sb.append( ";tar" );
		if( _fire > 0 )
			sb.append( ";fire-" + _fire );
		sb.append( "]" );
		return sb.toString();
	}
}
