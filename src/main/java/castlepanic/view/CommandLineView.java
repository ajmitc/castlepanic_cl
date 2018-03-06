package castlepanic.view;

import java.util.stream.*;

import castlepanic.Model;
import castlepanic.game.*;

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
		if( menu.getTitle() != null && !menu.getTitle().equals( "" ) )
			System.out.println( menu.getTitle() );
		for( int i = 0; i < menu.getItems().size(); ++i )
		{
			GameMenuItem item = menu.getItems().get( i );
            if( item.getOptionDisplay() != null && !item.getOptionDisplay().equals( "" ) )
                System.out.print( item.getOptionDisplay() + ") " );
            else
                System.out.print( (i + 1) + ") " );
			System.out.println( item.getDisplay() );
		}
		boolean done = false;
		while( !done )
		{
			String inp = _input.getInput( menu.getPrompt() );

			GameMenuItem selectedItem = null;
            for( GameMenuItem item: menu.getItems() )
            {
                if( item.getOptionDisplay() != null && item.getOptionDisplay().equalsIgnoreCase( inp ) )
                {
                    selectedItem = item;
                    break;
                }
            }

            if( selectedItem == null )
            {
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
                selectedItem = menu.getItems().get( selection );
            }

			//item.getCallback().performAction( item, _model, this );
			return selectedItem;
		}
		return null;
	}
	
	public void refresh()
	{
		Game game = _model.getGame();
		CommandLineTable table = new CommandLineTable();
        table.setDisplayHeaders( true );
        table.setDisplayRowNames( true );
        table.setRowNamesHeader( "Ring/Arc" );
        table.getRowNames().add( "Forest" );
        table.getRowNames().add( "Archer" );
        table.getRowNames().add( "Knight" );
        table.getRowNames().add( "Swordsman" );
        table.getRowNames().add( "Wall" );
        table.getRowNames().add( "Castle" );
        table.setColumnSeparator( "  " );
		for( int i = 1; i <= 6; ++i )
		{
			Arc arc = game.getArc( i );
			int col = table.addColumn( arc.getNumber() + "(" + arc.getColorName() + ")" );
			for( int r = Ring.FOREST; r >= Ring.SWORDSMAN; --r )
			{
				Ring ring = arc.getRing( r );
				String monsterString = ring.getMonsters().stream().filter( m -> m.getHitpoints() > 0 ).map( m -> m.toString() ).collect( Collectors.joining( ", " ) );
				table.add( monsterString, col );
			}
			
			// Add Wall
			Wall wall = game.getWall( i );
			if( !wall.isDestroyed() )
			{
				StringBuilder wallString = new StringBuilder();
				String c = "-";
				if( wall.isFortified() )
					c = "=";
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
				table.add( wallString.toString(), col );
			}
			else
			{
				table.add( "", col );
			}

            Ring ring = arc.getRing( Ring.CASTLE );
            if( ring.getMonsters().size() > 0 )
            {
                String monsterString = ring.getMonsters().stream().filter( m -> m.getHitpoints() > 0 ).map( m -> m.toString() ).collect( Collectors.joining( ", " ) );
                table.add( monsterString, col );
            }
            else
            {
                // Add Tower
                Tower tower = game.getTower( i );
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
                    table.add( towerString.toString(), col );
                }
                else
                {
                    table.add( "", col );
                }
            }
		}
		
		System.out.println( table.toString() );
	}
	
	public Input getInput(){ return _input; }
}
