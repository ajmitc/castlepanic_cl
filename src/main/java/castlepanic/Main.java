package castlepanic;

import castlepanic.view.*;

public class Main
{
	public static void main( String ... args )
	{
		Model model = new Model();
		View view = new CommandLineView( model );
		new CommandLineController( model, view );
	}
}
