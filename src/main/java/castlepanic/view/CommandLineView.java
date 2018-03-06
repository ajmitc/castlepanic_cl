package castlepanic.view;

import java.util.stream.*;

import castlepanic.game.Game;
import castlepanic.game.Arc;
import castlepanic.game.Ring;

public class CommandLineView extends View
{
	public static final int WALL_WIDTH = 10;
	public static final int TOWER_WIDTH = 10;
	
	private Input _input = new Input();
	
	public CommandLineView( Model model )
	{
		super( model );
	}
	
	public GameMenuItem displayMenu( GameMenu menu )
	{
		if( menu.getTitle() != null && menu.getTitle().equals( "" ) )
			System.out.println( menu.getTitle() );
		for( int i = 0; i < menu.getItems().size(); ++i )
		{
			GameMenuItem item = menu.getItems().get( i );
			System.out.print( (i + 1) + ") " );
			System.out.println( item.getDisplay() );
		}
		boolean done = false;
		while( !done )
		{
			String inp = _input.getInput( menu.getPrompt() );
			int selection = -1;
			try
			{
				selection = Integer.decode( inp );
			}
			catch( Exception e )
			{
				System.err.println( "Invalid selection" );
				continue;
			}
			selection -= 1;
			if( selection < 0 || selection >= menu.getItems().size() )
			{
				System.err.println( "Invalid selection" );
				continue;
			}
			GameMenuItem item = menu.getItems().get( selection );
			//item.getCallback().performAction( item, _model, this );
			return item;
		}
		return null;
	}
	
	public void refresh()
	{
		Game game = _model.getGame();
		CommandLineTable table = new CommandLineTable();
		for( int i = 0; i < game.getArcs().size(); ++i )
		{
			Arc arc = game.getArcs().get( i );
			int col = table.addColumn( "" + arc.getNumber() );
			for( int r = Ring.FOREST; r > Ring.CASTLE; --r )
			{
				Ring ring = arg.getRings().get( r );
				String monsterString = ring.getMonsters().stream().map( m -> m.toString() ).collect( Collectors.joining( ", " ) );
				table.add( monsterString, col );
			}
			
			// Add Wall
			Wall wall = game.getWalls().get( i );
			if( !wall.isDestroyed() )
			{
				StringBuilder wallString = new StringBuilder();
				String c = '-';
				if( wall.isFortified() )
					c = '=';
				int fire = wall.getFire();
				for( int j = 0; j < WALL_WIDTH; ++j )
				{
					if( fire > 0 && j > 0 )
					{
						wallString.append( "F" );
						fire -= 1;
					}
					else
						wallString.append( c );
				}
				table.add( wallString, col );
			}
			else
			{
				table.add( "", col );
			}
			
			// Add Tower
			Tower tower = game.getTowers().get( i );
			if( !tower.isDestroyed() )
			{
				StringBuilder towerString = new StringBuilder();
				int fire = wall.getFire();
				for( int j = 0; j < TOWER_WIDTH; ++j )
				{
					if( fire > 0 && j > 0 )
					{
						towerString.append( "F" );
						fire -= 1;
					}
					else
						towerString.append( "T" );
				}
				table.add( towerString, col );
			}
			else
			{
				table.add( "", col );
			}
		}
		
		System.out.println( table.toString() );
	}
	
	public Input getInput(){ return _input; }
}