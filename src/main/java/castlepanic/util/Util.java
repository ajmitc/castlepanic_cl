package castlepanic.util;

import java.util.Random;
import java.util.Date;

public class Util
{
	private static Random GEN = new Random( new Date().getTime() );
	public static int randInt( int min, int max )
	{
		return min + GEN.nextInt( max - min );	
	}
	
	public static int randInt( int max )
	{
		return randInt( 0, max );
	}
	
	public static int roll()
	{
		return randInt( 6 ) + 1;
	}
	private Util(){}
}