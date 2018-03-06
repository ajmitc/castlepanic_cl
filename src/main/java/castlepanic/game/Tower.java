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
	
	public boolean getFire(){ return _fire; }
	public void setFire( boolean v ){ _fire = v; }
}