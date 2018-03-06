package castlepanic.game;

public abstract class Wall
{
	protected boolean _destroyed;
	protected boolean _fortified;
	protected int _fire;
	
	public Wall()
	{
		_destroyed = false;
		_fortified = false;
		_fire      = 0;
	}
	
	public boolean isDestroyed(){ return _destroyed; }
	public void setDestroyed( boolean v ){ _destroyed = v; }
	
	public boolean isFortified(){ return _fortified; }
	public void setFortified( boolean v ){ _fortified = v; }
	
	public int getFire(){ return _fire; }
	public void setFire( int v ){ _fire = v; }
	public void adjFire( int v ){ _fire += v; }
	
	public String toString()
	{
		return "Wall";
	}
}
