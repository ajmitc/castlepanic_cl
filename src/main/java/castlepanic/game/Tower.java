package castlepanic.game;

public abstract class Tower
{
	protected boolean _destroyed;
	protected int _fire;
	
	public Tower()
	{
		_destroyed = false;
		_fire = 0;
	}
	
	public boolean isDestroyed(){ return _destroyed; }
	public void setDestroyed( boolean v ){ _destroyed = v; }
	
	public int getFire(){ return _fire; }
	public void setFire( int v ){ _fire = v; }
	public void adjFire( int v ){ _fire += v; }
}
