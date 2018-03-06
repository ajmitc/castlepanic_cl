package castlepanic;

import castlepanic.game.*;
import castlepanic.game.card.Card;
import castlepanic.game.card.CardEffect;
import castlepanic.game.card.CastleCard;
import castlepanic.game.card.WizardCard;
import castlepanic.game.monster.Monster;
import castlepanic.view.*;
import castlepanic.util.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.*;
import java.awt.Color;

public class CommandLineController extends Controller
{
	private boolean _exit;
	private GameMenu _mainmenu;
	
	private List<Card> _selectedCards;


    private boolean _drawCardForEachHitPlayed = false;
    private boolean _donePlayingCards = false;
	
	public CommandLineController( Model model, View view )
	{
		super( model, view );
		_exit = false;
		_selectedCards = new ArrayList<>();
		
		_mainmenu = new GameMenu( "Castle Panic" );
		_mainmenu.getItems().add( new GameMenuItem( "New game", new GameMenuItemCallback(){
			public void performAction( GameMenuItem item, Model model, View view )
			{
				newGame();
			}
		} ) );
		
		_mainmenu.getItems().add( new GameMenuItem( "Continue game", new GameMenuItemCallback(){
			public void performAction( GameMenuItem item, Model model, View view )
			{
				continueGame();
			}
		} ) );
		
		_mainmenu.getItems().add( new GameMenuItem( "Exit", new GameMenuItemCallback(){
			public void performAction( GameMenuItem item, Model model, View view )
			{
				exitGame();
			}
		} ) );
		
		while( !_exit )
		{
			GameMenuItem item = _view.displayMenu( _mainmenu );
			item.getCallback().performAction( item, _model, _view );
		}
	}
	
	public void newGame()
	{
        System.out.println( "=== Game Setup ===" );
		_model.setGame( new Game() );
		boolean done = false;
		while( !done )
		{
			String inp = ((CommandLineView) _view).getInput().getInput( "How many players? " );
			try
			{
				int i = Integer.decode( inp );
				for( int p = 0; p < i; ++p )
				{
					_model.getGame().getPlayers().add( new Player( "Player " + (p + 1) ) );
				}
                done = true;
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
        System.out.println( "Dealing " + _model.getGame().getHandSize() + " cards to each player" );
		// Deal cards
		for( Player player: _model.getGame().getPlayers() )
		{
			for( int i = 0; i < _model.getGame().getHandSize(); ++i )
			{
				CastleCard card = _model.getGame().getCastleCards().draw();
				player.getHand().add( card );
			}
		}
		// Place 6 monsters (no bosses, no special monster tokens)
		int left = 6;
        System.out.println( "Placing " + left + " monsters on the board" );
		while( left > 0 )
		{
			int i = Util.randInt( _model.getGame().getMonsterPool().size() );
			Monster monster = _model.getGame().getMonsterPool().get( i );
			if( monster.getHitpoints() > 0 && !monster.getType().isBoss() )
			{
                System.out.println( "   Adding " + monster + " in Archer Ring of Arc " + left );
				addMonsterToRing( monster, left, Ring.ARCHER );
				_model.getGame().getMonsterPool().remove( monster );
				--left;
			}
		}
		playGame();
	}
	
	public void continueGame()
	{
		System.out.println( "Not yet supported" );
		return;
		// TODO Load game from file
		//playGame();
	}
	
	public void playGame()
	{
		while( !_model.getGame().isGameOver() )
		{
			drawUp();
			discardAndDraw();
			trade();
			playCards();
            _model.getGame().getArcs().stream().flatMap( arc -> arc.getRings().stream() ).forEach( ring -> cleanUp( ring ) );
			moveMonsters();
            _model.getGame().getArcs().stream().flatMap( arc -> arc.getRings().stream() ).forEach( ring -> cleanUp( ring ) );
			addMonsters();
            _model.getGame().getArcs().stream().flatMap( arc -> arc.getRings().stream() ).forEach( ring -> cleanUp( ring ) );
			_model.getGame().nextPlayer();
		}
	}

    private void cleanUp( Ring ring )
    {
        // Remove slain monsters
        List<Monster> slain = ring.getMonsters().stream().filter( m -> m.getHitpoints() == 0 ).collect( Collectors.toList() );
        slain.stream().forEach( m -> ring.getMonsters().remove( m ) );
    }
	
	private void drawUp()
	{
        System.out.println( "=== Draw Up Phase ===" );
		Player player = _model.getGame().getCurrentPlayer();
        System.out.println( "   Drawing " + (_model.getGame().getHandSize() - player.getHand().size()) + " cards into hand of " + player );
		while( player.getHand().size() < _model.getGame().getHandSize() )
		{
			CastleCard card = _model.getGame().getCastleCards().draw();
			player.getHand().add( card );
		}
	}
	
	private void discardAndDraw()
	{
        System.out.println( "=== Discard and Draw Phase ===" );
		System.out.println( "Discard and Draw is not yet supported" );
	}
	
	private void trade()
	{
        System.out.println( "=== Trade Phase ===" );
		if( _model.getGame().getPlayers().size() > 1 )
			System.out.println( "Trading is not yet supported" );
	    else
            System.out.println( "Trading Phase skipped with only 1 player" );
	}
	
	private void playCards()
	{
        System.out.println( "=== Play Cards Phase ===" );

		Player player = _model.getGame().getCurrentPlayer();
		_donePlayingCards = false;
        _selectedCards.clear();
		while( !_donePlayingCards )
		{
            _view.refresh();
            // Display selected cards
            if( _selectedCards.size() > 0 )
                System.out.println( "Selected Cards: " + _selectedCards.stream().map( card -> card.getTitle() ).collect( Collectors.joining( ", " ) ) );
			GameMenu hand = new GameMenu( "Select Card(s) to Play" );
			int i = 0;
			for( ; i < player.getHand().size(); ++i )
			{
				Card card = player.getHand().peek( i );
				if( _selectedCards.contains( card ) )
					continue;
				hand.getItems().add( new GameMenuItem( "" + card, card, new GameMenuItemCallback(){
					public void performAction( GameMenuItem item, Model model, View view )
					{
						selectCard( (Card) item.getObject() );
					}
				} ));
			}
			hand.getItems().add( new GameMenuItem( "P", "Play Selected Cards", null, new GameMenuItemCallback(){
				public void performAction( GameMenuItem item, Model model, View view )
					{
						playSelectedCards( false );
                        _selectedCards.clear();
					}
			}));
			hand.getItems().add( new GameMenuItem( "E", "End Play Cards Phase", null, new GameMenuItemCallback(){
				public void performAction( GameMenuItem item, Model model, View view )
					{
						playSelectedCards( true );
						_donePlayingCards = true;
					}
			}));
			GameMenuItem selected = _view.displayMenu( hand );
			selected.getCallback().performAction( selected, _model, _view );
		}
	}
	
	private void selectCard( Card card )
	{
		_selectedCards.add( card );
	}
	
	/**
	 * Player plays these cards.  There should be one main card (ie. Hit) and any supplement cards (ie. Double Strike)
     * Return true if done playing cards, false on error (and should repeat card selection process)
	 */
	private boolean playSelectedCards( boolean silent )
	{
		if( _selectedCards.size() == 0 )
		{
            if( !silent )
                System.err.println( "No cards selected" );
			return false;
		}
	
		ActionEffect action = new ActionEffect();
		
		// Search for modifiers
		for( Card card: _selectedCards )
		{
			switch( card.getEffect() )
			{
				case SLAY_NOT_FOREST:     // Slay 1 Monster on board not in forest
					action.slay = true;
					action.validRingFarthest = Ring.ARCHER;
					break;
				case SLAY:                // Slay 1 Monster on board
					action.slay = true;
					break;
				case PLAY_TWICE:          // Play the next card twice
					action.playTimes = 2;
					action.requireHit = true;
					break;
				case BUILD_TOWER_WITH_BRICK_OR_MORTAR:
					action.brickOrMortar = true;
					action.buildTower = true;
					break;
				case HIT_2_MONSTERS_IN_SAME_SPACE_NOT_FOREST:
					action.validRingFarthest = Ring.ARCHER;
					action.numMonstersAffected = 2;
					action.sameSpace = true;
					break;
				case MOVE_BACK_1_SPACE:
					action.moveBack = 1;
					action.requireHit = true;
					break;
				case CHANGE_RANGE:
					action.changeRange = true;
					action.requireHit = true;
					break;
				case CHANGE_COLOR:
					action.changeColor = true;
					action.requireHit = true;
					break;
			}
		}
		
		// Get the main effect card
		for( Card card: _selectedCards )
		{
			switch( card.getEffect() )
			{
				case HIT_BLUE:    // Hit 1 Monster in Blue Arcs
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.BLUE;
					break;
				case HIT_GREEN:   // Hit 1 Monster in Green Arcs
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.GREEN;
					break;
				case HIT_RED:     // Hit 1 Monster in Red Arcs
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.RED;
					break;
				case HIT_BLUE_ARCHER:  // Hit 1 Monster in the Blue Archer Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.BLUE;
					action.ring = Ring.ARCHER;
					break;
				case HIT_RED_ARCHER:   // Hit 1 Monster in the Red Archer Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.RED;
					action.ring = Ring.ARCHER;
					break;
				case HIT_GREEN_ARCHER: // Hit 1 Monster in the Green Archer Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.GREEN;
					action.ring = Ring.ARCHER;
					break;
				case HIT_BLUE_KNIGHT:  // Hit 1 Monster in the Blue Knight Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.BLUE;
					action.ring = Ring.KNIGHT;
					break;
				case HIT_RED_KNIGHT:   // Hit 1 Monster in the Red Knight Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.RED;
					action.ring = Ring.KNIGHT;
					break;
				case HIT_GREEN_KNIGHT: // Hit 1 Monster in the Green Knight Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.GREEN;
					action.ring = Ring.KNIGHT;
					break;
				case HIT_BLUE_SWORDSMAN:  // Hit 1 Monster in the Blue Swordsman Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.BLUE;
					action.ring = Ring.SWORDSMAN;
					break;
				case HIT_RED_SWORDSMAN:   // Hit 1 Monster in the Red Swordsman Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.RED;
					action.ring = Ring.SWORDSMAN;
					break;
				case HIT_GREEN_SWORDSMAN: // Hit 1 Monster in the Green Swordsman Ring
                    if( action.hit )
                    {
                        System.out.println( "You have selected two many hit cards" );
                        return false;
                    }
					action.hit = true;
                    action.damage = 1;
					action.color = Arc.GREEN;
					action.ring = Ring.SWORDSMAN;
					break;
				case DRAW_2_CARDS:
					action.drawCards = 2;
					break;
				case DRAW_CASTLE_CARD_FOR_EVERY_HIT_PLAYED:
					_drawCardForEachHitPlayed = true;
					break;
                case DISCARD_AND_DRAW_MULTIPLE:
                    action.discardAndDraw = true;
                    break;
				case BRICK:
					++action.numBricks;
					break;
				case MORTAR:
					++action.numMortars;
					break;
				case FORTIFY_WALL:
					action.fortifyWall = true;
					break;
			}
		}

        // Set the buildWall primary action
		if( !action.buildTower && !action.buildWall && action.numBricks > 0 && action.numMortars > 0 )
		{
			if( action.numBricks == 1 && action.numMortars == 1 )
				action.buildWall = true;
			else
			{
				System.err.println( "Invalid number of Bricks (" + action.numBricks + ") and Mortars (" + action.numMortars + ")" );
				return false;
			}
		}
		
		
        int primaryCount = action.countPrimaryActions();
		if( primaryCount == 0 )
        {
            System.err.println( "You must select a card that executes a primary action." );
            ((CommandLineView) _view).getInput().waitForInput();
            return false;
        }
        else if( primaryCount > 1 )
        {
            System.err.println( "You selected cards that execute more than 1 primary action.  This is not allowed." );
            return false;
        }


		if( applyAction( action ) )
		{
            System.out.println( "Action successful, discarding cards" );
			// Action successful, discard used cards
			for( Card card: _selectedCards )
			{
				_model.getGame().getCurrentPlayer().getHand().discard( card );
				if( card instanceof CastleCard )
				{
					_model.getGame().getCastleCards().discard( (CastleCard) card );
				}
				else if( card instanceof WizardCard )
				{
					_model.getGame().getWizardCards().discard( (WizardCard) card );
				}
			}
			_model.getGame().getCurrentPlayer().getHand().clearDiscard();
            return true;
		}

        return false;
	}
	
	/**
	 * Hit a monster, inflicting "damage" damage on one or more mosters.
	 * @param arc The numbered arc that this hit is limited to.  -1 if hit is not limited to an arc.
	 * @param color The color that this hit is limited to.  Null if hit is not limited to a color.
	 * @param ring The Ring that this hit is limited to.  -1 if hit is not limited to a ring.
	 * @param slay True if the monster should be slain.  False if simply damaged.
	 * @param damage Amount of damage to inflict on monster.  Normally, this should be 1.
	 */
	public boolean applyAction( ActionEffect action )
	{
		if( action.hit )
		{
			return applyHit( action );
		}
		else if( action.buildWall )
		{
			return applyBuildWall( action );
		}
		else if( action.buildTower )
		{
			return applyBuildTower( action );
		}
		else if( action.fortifyWall )
		{
			return applyFortifyWall( action );
		}
		else if( action.drawCards > 0 )
		{
			return applyDrawCards( action );
		}
		else if( action.discardAndDraw )
		{
			return applyDiscardAndDrawCards( action );
		}
		
		System.err.println( "Unrecognized combination of cards" );
		return false;
	}
	
	public boolean applyHit( ActionEffect action )
	{
		Game game = _model.getGame();
		Monster target = null;

        if( action.changeColor )
        {
            GameMenu colorMenu = new GameMenu( "Select new color" );
            colorMenu.getItems().add( new GameMenuItem( "Red", Arc.RED, null ) );
            colorMenu.getItems().add( new GameMenuItem( "Green", Arc.GREEN, null ) );
            colorMenu.getItems().add( new GameMenuItem( "Blue", Arc.BLUE, null ) );
            GameMenuItem selected = _view.displayMenu( colorMenu );
            action.color = (Color) selected.getObject();
        }

        if( action.changeRange )
        {
            GameMenu rangeMenu = new GameMenu( "Select new range" );
            rangeMenu.getItems().add( new GameMenuItem( "Forest", Ring.FOREST, null ) );
            rangeMenu.getItems().add( new GameMenuItem( "Archer", Ring.ARCHER, null ) );
            rangeMenu.getItems().add( new GameMenuItem( "Knight", Ring.KNIGHT, null ) );
            rangeMenu.getItems().add( new GameMenuItem( "Swordsman", Ring.SWORDSMAN, null ) );
            rangeMenu.getItems().add( new GameMenuItem( "Castle", Ring.CASTLE, null ) );
            GameMenuItem selected = _view.displayMenu( rangeMenu );
            action.ring = (Integer) selected.getObject();
        }
		
		// find monster to hit
		List<Arc> arcs = _model.getGame().getArcs();
		if( action.arc > 0 && action.arc <= 6 )
		{
			arcs = game.getArcs().stream().filter( arc -> arc.getNumber() == action.arc ).collect( Collectors.toList() );
		}
		else if( action.color != null )
		{
			arcs = game.getArcs().stream().filter( arc -> arc.getColor() == action.color ).collect( Collectors.toList() );
		}
		
		List<Ring> rings = null;
		if( action.ring >= Ring.CASTLE && action.ring <= Ring.FOREST )
		{
			rings = arcs.stream().flatMap( arc -> arc.getRings().stream() ).filter( ring -> ring.getRange() == action.ring ).collect( Collectors.toList() );
		}
		else
		{
			rings = arcs.stream().flatMap( arc -> arc.getRings().stream() ).filter( ring -> ring.getRange() >= action.validRingClosest && ring.getRange() <= action.validRingFarthest ).collect( Collectors.toList() );
		}
		
		for( int i = 0; i < action.playTimes; ++i )
		{
			List<Monster> targets = rings.stream().flatMap( ring -> ring.getMonsters().stream() ).filter( monster -> monster.getHitpoints() > 0 ).collect( Collectors.toList() );
			final Monster monster;
			if( targets.size() > 1 )
			{
				// Player must choose Monster
                GameMenu monsterMenu = new GameMenu( "Select Monster" );
                for( Monster m: targets )
                {
                    monsterMenu.getItems().add( new GameMenuItem( "" + m, m, null ) );
                }
                GameMenuItem selectedItem = _view.displayMenu( monsterMenu );
                if( selectedItem != null )
                {
                    monster = (Monster) selectedItem.getObject();
                }
                else
                {
                    monster = null;
                }
			}
			else if( targets.size() == 1 )
			{
				monster = targets.get( 0 );
			}
            else
            {
                // No monsters!
				System.err.println( "No monsters found in target area" );
				return false;
            }
            
            if( monster == null )
            {
                System.err.println( "Monster is null, not hitting!" );
                return false;
            }
			
			int damage = Math.min( action.damage, 1 );
			if( action.slay )
				damage = monster.getHitpoints();
            System.out.println( "Inflicting " + damage + " damage on " + monster );
			monster.adjHitpoints( -damage );
			if( monster.getHitpoints() > 0 )
			{
                System.out.println( "  " + monster + " has " + monster.getHitpoints() + " HP remaining" );
				if( action.moveBack > 0 || action.moveBackToForest )
				{
					Ring ring = rings.stream().filter( r -> r.getMonsters().contains( monster ) ).collect( Collectors.toList() ).get( 0 );
					if( action.moveBackToForest )
						action.moveBack = Ring.FOREST - ring.getRange();
					if( action.moveBack > 0 )
					{
                        System.out.println( "  Moving " + monster + " back " + action.moveBack + " rings" );
						ring.getMonsters().remove( monster );
						int targetRing = Math.min( ring.getRange() + action.moveBack, Ring.FOREST );
						ring = ring.getArc().getRing( targetRing );
						ring.getMonsters().add( monster );
					}	
				}
                ((CommandLineView) _view).getInput().waitForInput();
			}
			else
			{
				System.out.println( monster.getType() + " slain!" );
                ((CommandLineView) _view).getInput().waitForInput();
				return true;
			} 
		}
		
		// How are these considered in the above logic?
		//public boolean sameSpace = false;
		//public int numMonstersAffected = 1;
		
		return true;
	}
	
	/**
	 * Player has played 1 Brick and 1 Mortar, or any number of Bricks/Mortars with the Wizard card to build walls
	 */
	public boolean applyBuildWall( ActionEffect action )
	{
		int count = 0;
		if( action.brickOrMortar )
		{
			count += action.numBricks + action.numMortars;
		}
		else
		{
			if( action.numBricks != action.numMortars )
			{
				System.err.println( "Number of Bricks != Number of Mortars" );
				return false;
			}
			
			count = action.numBricks;
		}
		
		List<Wall> wallsToBuild = new ArrayList<>();
		for( int i = 0; i < count; ++i )
		{
			// Player chooses which walls to rebuild
			String inp = ((CommandLineView) _view).getInput().getInput( "Build wall on which arc? " );
			if( inp.equalsIgnoreCase( "cancel" ) )
			{
				return false;
			}
			int arcNum = 0;
			try
			{
				arcNum = Integer.decode( inp );
				if( arcNum < 1 || arcNum > 6 )
					throw new Exception();
			}
			catch( Exception e )
			{
				System.err.println( "Invalid arc number, please enter 1-6" );
				--i;
				continue;
			}
			// Check if wall already exists there
			Wall wall = _model.getGame().getWall( arcNum );
			if( !wall.isDestroyed() )
			{
				System.err.println( "There is already a wall there, cannot build a new one!" );
				continue;
			}
			wallsToBuild.add( wall );
		}
		
		for( Wall wall: wallsToBuild )
		{
			wall.setDestroyed( false );
		}
		System.out.println( "Wall(s) rebuilt!" );
		return true;
	}
	
	/**
	 * Player has played 1 Brick and 1 Mortar, or any number of Bricks/Mortars with the Wizard card to build walls
	 */
	public boolean applyBuildTower( ActionEffect action )
	{
		int count = 0;
		if( action.brickOrMortar )
		{
			count += action.numBricks + action.numMortars;
		}
		else
		{
			if( action.numBricks != action.numMortars )
			{
				System.err.println( "Number of Bricks != Number of Mortars" );
				return false;
			}
			
			count = action.numBricks;
		}
		
		List<Tower> towersToBuild = new ArrayList<>();
		for( int i = 0; i < count; ++i )
		{
			// Player chooses which walls to rebuild
			String inp = ((CommandLineView) _view).getInput().getInput( "Build tower on which arc? " );
			if( inp.equalsIgnoreCase( "cancel" ) )
			{
				return false;
			}
			int arcNum = 0;
			try
			{
				arcNum = Integer.decode( inp );
				if( arcNum < 1 || arcNum > 6 )
					throw new Exception();
			}
			catch( Exception e )
			{
				System.err.println( "Invalid arc number, please enter 1-6" );
				--i;
				continue;
			}
			// Check if wall already exists there
			Tower tower = _model.getGame().getTower( arcNum );
			if( !tower.isDestroyed() )
			{
				System.err.println( "There is already a tower there, cannot build a new one!" );
				continue;
			}
			towersToBuild.add( tower );
		}
		
		for( Tower tower: towersToBuild )
		{
			tower.setDestroyed( false );
		}
		System.out.println( "Tower(s) rebuilt!" );
		return true;
	}

    private boolean applyFortifyWall( ActionEffect action )
    {
        while( true )
        {
            String inp = ((CommandLineView) _view).getInput().getInput( "Fortify wall on which arc? " );
            if( inp.equalsIgnoreCase( "cancel" ) )
            {
                return false;
            }
            int arcNum = 0;
            try
            {
                arcNum = Integer.decode( inp );
                if( arcNum < 1 || arcNum > 6 )
                    throw new Exception();
            }
            catch( Exception e )
            {
                System.err.println( "Invalid arc number, please enter 1-6" );
                continue;
            }
            // Check if wall already exists there
            Wall wall = _model.getGame().getWall( arcNum );
            if( wall.isDestroyed() )
            {
                System.err.println( "There is no wall there, cannot fortify without a wall!" );
                continue;
            }
            wall.setFortified( true );
            return true;
        }
    }

    private boolean applyDrawCards( ActionEffect action )
    {
        for( int i = 0; i < action.drawCards; ++i )
        {
            CastleCard card = _model.getGame().getCastleCards().draw();
            _model.getGame().getCurrentPlayer().getHand().add( card );
            System.out.println( "You draw " + card );
        }
        ((CommandLineView) _view).getInput().waitForInput();
        return true;
    }

    // Immediately discard any number of cards and draw that many castle cards
    private boolean applyDiscardAndDrawCards( ActionEffect action )
    {
        List<Card> selectedDiscards = new ArrayList<>();
        while( true )
        {
            GameMenu handMenu = new GameMenu( "Select Card(s) to Discard" );
            for( int i = 0; i < _model.getGame().getCurrentPlayer().getHand().size(); ++i )
            {
                Card card = _model.getGame().getCurrentPlayer().getHand().peek( i );
                if( selectedDiscards.contains( card ) )
                    continue;
                handMenu.getItems().add( new GameMenuItem( "" + card, card, null ) );
            }
            handMenu.getItems().add( new GameMenuItem( "D", "Discard Selected Cards", null, null ) );
            handMenu.getItems().add( new GameMenuItem( "C", "Cancel", null, null ) );
            GameMenuItem selected = _view.displayMenu( handMenu );
            if( selected.getOptionDisplay() != null && selected.getOptionDisplay().equals( "C" ) )
            {
                return false;
            }
            if( selected.getOptionDisplay() != null && selected.getOptionDisplay().equals( "D" ) )
            {
                break;
            }
            else
            {
                selectedDiscards.add( (Card) selected.getObject() );
            }
        }

        for( Card card: selectedDiscards )
        {
            if( card instanceof CastleCard )
                _model.getGame().getCastleCards().discard( (CastleCard) card );
            else if( card instanceof WizardCard )
                _model.getGame().getWizardCards().discard( (WizardCard) card );
            _model.getGame().getCurrentPlayer().getHand().discard( card );
            System.out.println( "You discard " + card );
        }
        _model.getGame().getCurrentPlayer().getHand().clearDiscard();

        // Draw new cards
        for( int i = 0; i < selectedDiscards.size(); ++i )
        {
            CastleCard card = _model.getGame().getCastleCards().draw();
            System.out.println( "You draw " + card );
            _model.getGame().getCurrentPlayer().getHand().add( card );
        }

        return true;
    }
	
	
	private void moveMonsters()
	{
        System.out.println( "=== Move Monsters Phase ===" );
		_model.getGame().moveMonsters();
        ((CommandLineView) _view).getInput().waitForInput();
	}
	
	/**
	 * Draw 2 monsters to add to the board
	 */
	private void addMonsters()
	{
        System.out.println( "=== Add 2 Monsters Phase ===" );
		addMonsters( 2 );
        ((CommandLineView) _view).getInput().waitForInput();
	}
	
	private void addMonsters( int count )
	{
		for( int i = 0; i < count; ++i )
		{
			Monster monster = _model.getGame().getMonsterPool().remove( Util.randInt( _model.getGame().getMonsterPool().size() ) );
			addMonster( monster );
		}
	}
	
	private void addMonster( Monster monster )
	{
        System.out.println( "Adding " + monster );
		switch( monster.getType() )
		{
			case MOVE_BLUE:
				_model.getGame().moveMonsters( Arc.BLUE );
				break;
			case MOVE_GREEN:
				_model.getGame().moveMonsters( Arc.GREEN );
				break;
			case MOVE_RED:
				_model.getGame().moveMonsters( Arc.RED );
				break;
			case DRAW_3_MONSTERS:
				addMonsters( 3 );
				break;
			case DRAW_4_MONSTERS:
				addMonsters( 4 );
				break;
			case MOVE_CLOCKWISE:
				_model.getGame().moveMonstersClockwise();
				break;
			case MOVE_COUNTER_CLOCKWISE:
				_model.getGame().moveMonstersCounterClockwise();
				break;
			default:
				addMonsterToForest( monster );
		}
	}
	
	private void addMonsterToForest( Monster monster )
	{
		// Roll the dice
		int arc = Util.roll();
		addMonsterToForest( monster, arc );
	}
	
	private void addMonsterToForest( Monster monster, int arcNumber )
	{
		addMonsterToRing( monster, arcNumber, Ring.FOREST );
	}
	
	private void addMonsterToRing( Monster monster, int arcNumber, int ringNumber )
	{
		Arc arc = _model.getGame().getArc( arcNumber );
		Ring ring = arc.getRing( ringNumber );
        System.out.println( "Adding " + monster + " to " + ring + " ring of Arc " + arcNumber );
		ring.getMonsters().add( monster );
	}
	
	private void exitGame()
	{
		_exit = true;
	}
	
	private static class ActionEffect
	{
		// Primary Action
		public boolean hit  = false;
		public boolean buildWall = false;
		public boolean buildTower = false;
		public boolean fortifyWall = false;
		public boolean discardAndDraw = false;
		public int drawCards = 0;
		
		// Board location
		public int arc = -1;
		public Color color = null;
		public int ring = -1;
		public boolean sameSpace = false;
		
		// Modifiers
		public boolean slay = false;
		public int damage = 0;
		public int validRingClosest = Ring.CASTLE;
		public int validRingFarthest = Ring.FOREST;
		public int playTimes = 1;
		public int numMonstersAffected = 1;
		public int moveBack = 0;  // Number of spaces to knock back a monster
		public boolean moveBackToForest = false;
		public boolean changeRange = false;
		public boolean changeColor = false;
		
		public boolean brickOrMortar = false;  // if true, walls/towers may be built with a brick or mortar
		public int numBricks = 0;
		public int numMortars = 0;
		
		// Validation
		boolean requireHit = false;

        public int countPrimaryActions()
        {
            int primaryCount = 0;
            if( hit ) ++primaryCount;
            if( buildWall ) ++primaryCount;
            if( buildTower ) ++primaryCount;
            if( drawCards > 0 ) ++primaryCount;
            if( fortifyWall ) ++primaryCount;
            if( discardAndDraw ) ++primaryCount;
            return primaryCount;
        }
	}
}
