package castlepanic;

import castlepanic.game.Game;
import castlepanic.game.Player;
import castlepanic.game.card.CastleCard;
import castlepanic.view.GameMenu;
import castlepanic.view.GameMenuItem;
import castlepanic.view.GameMenuItemCallback;
import castlepanic.util.Util;

import java.util.List;
import java.util.ArrayList;

public class CommandLineController extends Controller
{
	private boolean _exit;
	private GameMenu _mainmenu;
	
	private List<Card> _selectedCards;
	
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
		_model.setGame( new Game() );
		boolean done = false;
		while( !done )
		{
			String inp = _view.getInput().getInput( "How many players? " );
			try
			{
				int i = Integer.decode( inp );
				for( int p = 0; p < i; ++p )
				{
					_model.getGame().getPlayers().add( new Player( "Player " + (p + 1) ) );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
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
		while( left > 0 )
		{
			int i = Util.randInt( _model.getGame().getMonsterPool().size() );
			Monster monster = _model.getGame().getMonsterPool().get( i );
			if( monster.getHitpoints() > 0 && !monster.isBoss() )
			{
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
			moveMonsters();
			addMonsters();
			_model.getGame().nextPlayer();
		}
	}
	
	private void drawUp()
	{
		Player player = _model.getGame().getGame().getCurrentPlayer();
		while( player.getHand().size() < _model.getGame().getHandSize() )
		{
			CastleCard card = _model.getGame().getCastleCards().draw();
			player.getHand().add( card );
		}
	}
	
	private void discardAndDraw()
	{
		System.out.println( "Discard and Draw is not yet supported" );
	}
	
	private void trade()
	{
		if( _model.getGame().getPlayers().size() > 1 )
			System.out.println( "Trading is not yet supported" );
	}
	
	private void playCards()
	{
		Player player = _model.getGame().getCurrentPlayer();
		boolean done = false;
		while( !done )
		{
			System.out.println( "Play Cards Phase" );
			GameMenu hand = new GameMenu( "Select Card(s) to Play" );
			int i = 0;
			for( ; i < player.getHand().size(); ++i )
			{
				Card card = player.getHand().get( i );
				if( _selectedCards.contains( card ) )
					continue;
				hand.getItems().add( new GameMenuItem( card.getTitle() + " (" + card.getDescription() + ")", card, new GameMenuItemCallback(){
					public void performAction( GameMenuItem item, Model model, View view )
					{
						selectCard( (Card) item.getObject() );
					}
				} ))
			}
			hand.getItems().add( new GameMenuItem( "Done", new GameMenuItem(){
				public void performAction( GameMenuItem item, Model model, View view )
					{
						playSelectedCards();
						done = true;
					}
			}))
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
	 */
	private void playSelectedCards()
	{
		if( _selectedCards.size() == 0 )
		{
			System.err.println( "No cards selected" );
			return;
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
			}
		}
		
		// Get the main effect card
		for( Card card: _selectedCards )
		{
			switch( card.getEffect() )
			{
				case HIT_BLUE:    // Hit 1 Monster in Blue Arcs
					action.hit = true;
					action.color = Arc.BLUE;
					break;
				case HIT_GREEN:   // Hit 1 Monster in Green Arcs
					action.hit = true;
					action.color = Arc.GREEN;
					break;
				case HIT_RED:     // Hit 1 Monster in Red Arcs
					action.hit = true;
					action.color = Arc.RED;
					break;
				case HIT_BLUE_ARCHER:  // Hit 1 Monster in the Blue Archer Ring
					action.hit = true;
					action.color = Arc.BLUE;
					action.ring = Ring.ARCHER;
					break;
				case HIT_RED_ARCHER:   // Hit 1 Monster in the Red Archer Ring
					action.hit = true;
					action.color = Arc.RED;
					action.ring = Ring.ARCHER;
					break;
				case HIT_GREEN_ARCHER: // Hit 1 Monster in the Green Archer Ring
					action.hit = true;
					action.color = Arc.GREEN;
					action.ring = Ring.ARCHER;
					break;
				case HIT_BLUE_KNIGHT:  // Hit 1 Monster in the Blue Knight Ring
					action.hit = true;
					action.color = Arc.BLUE;
					action.ring = Ring.KNIGHT;
					break;
				case HIT_RED_KNIGHT:   // Hit 1 Monster in the Red Knight Ring
					action.hit = true;
					action.color = Arc.RED;
					action.ring = Ring.KNIGHT;
					break;
				case HIT_GREEN_KNIGHT: // Hit 1 Monster in the Green Knight Ring
					action.hit = true;
					action.color = Arc.GREEN;
					action.ring = Ring.KNIGHT;
					break;
				case HIT_BLUE_SWORDSMAN:  // Hit 1 Monster in the Blue Swordsman Ring
					action.hit = true;
					action.color = Arc.BLUE;
					action.ring = Ring.SWORDSMAN;
					break;
				case HIT_RED_SWORDSMAN:   // Hit 1 Monster in the Red Swordsman Ring
					action.hit = true;
					action.color = Arc.RED;
					action.ring = Ring.SWORDSMAN;
					break;
				case HIT_GREEN_SWORDSMAN: // Hit 1 Monster in the Green Swordsman Ring
					action.hit = true;
					action.color = Arc.GREEN;
					action.ring = Ring.SWORDSMAN;
					break;
				case DRAW_2_CARDS:
					action.drawCards = 2;
					break;
				case DRAW_CASTLE_CARD_FOR_EVERY_HIT_PLAYED:
					_drawCardForEachHitPlayed = true;
					break;
				case BRICK:
					++action.numBricks;
					break;
				case MORTAR:
					++action.numMortars;
					break;
			}
		}
		
		if( !action.buildTower && !action.buildWall && action.numBricks > 0 && action.numMortars > 0 )
		{
			if( action.numBricks == 1 && action.numMortars == 1 )
				action.buildWall = true;
			else
			{
				System.err.println( "Invalid number of Bricks (" + action.numBricks + ") and Mortars (" + action.numMortars + ")" );
				return;
			}
		}
		
		if( applyAction( action ) )
		{
			// Action successful, discard used cards
			for( Card card: _selectedCards )
			{
				_model.getGame().getCurrentPlayer().getHand().discard( card );
				if( card instanceof CastleCard )
				{
					_model.getGame().getCastleCards().discard( card );
				}
				else if( card instanceof WizardCard )
				{
					_model.getGame().getWizardCards().discard( card );
				}
			}
			_model.getGame().getCurrentPlayer().getHand().clearDiscard();
		}
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
		
		System.err.println( "Unrecognized combination of cards" );
		return false;
	}
	
	public boolean applyHit( ActionEffect action )
	{
		Game game = _model.getGame();
		Monster target = null;
		
		// find monster to hit
		List<Arc> arcs = _arcs;
		if( action.arc > 0 && action.arc <= 6 )
		{
			arcs = game.getArcs().stream().filter( arc -> arc.getNumber() == action.arc ).collect( Collectors.toList() );
		}
		else if( color != null )
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
			List<Monster> targets = rings.stream().flatMap( ring -> ring.getMonsters() ).filter( monster -> monster.getHitpoints() > 0 ).collect( Collectors.toList() );
			if( targets.size() == 0 )
			{
				System.err.println( "No monsters found in target area" );
				return false;
			}
			Monster monster = null;
			if( targets.size() > 1 )
			{
				// Player must choose Monster
			}
			else if( targets.size() == 1 )
			{
				monster = targets.get( 0 );
			}
			
			int damage = Math.min( action.damage, 1 );
			if( action.slay )
				damage = monster.getHitpoints();
			monster.adjHitpoints( -damage );
			if( monster.getHitpoints() > 0 )
			{
				if( action.moveBack > 0 || action.moveBackToForest )
				{
					Ring ring = rings.stream().filter( ring -> ring.getMonsters().contains( monster ) ).collect( Collectors.toList() ).get( 0 );
					if( action.moveBackToForest )
						action.moveBack = Ring.FOREST - ring.getRange();
					if( action.moveBack > 0 )
					{
						ring.getMonsters().remove( monster );
						int targetRing = Math.min( ring.getRange() + action.moveBack, Ring.FOREST );
						ring = ring.getArc().getRing( targetRing );
						ring.getMonsters().add( monster );
					}	
				}
			}
			else
			{
				System.out.println( monster.getType() + " slain!" );
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
			String inp = _view.getInput().getInput( "Build wall on which arc? " );
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
				--count;
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
			String inp = _view.getInput().getInput( "Build tower on which arc? " );
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
				--count;
				continue;
			}
			// Check if wall already exists there
			Tower tower = _model.getGame().getTower( arcNum );
			if( !tower.isDestroyed() )
			{
				System.err.println( "There is already a tower there, cannot build a new one!" );
				continue;
			}
			towersToBuild.add( wall );
		}
		
		for( Tower tower: towersToBuild )
		{
			tower.setDestroyed( false );
		}
		System.out.println( "Tower(s) rebuilt!" );
		return true;
	}
	
	
	private void moveMonsters()
	{
		_model.getGame().moveMonsters();
	}
	
	/**
	 * Draw 2 monsters to add to the board
	 */
	private void addMonsters()
	{
		addMonsters( 2 );
	}
	
	private void addMonsters( int count )
	{
		for( int i = 0; i < count; ++i )
		{
			Monster monster = _model.getGame().getMonsterPool().get( Util.nextInt( _model.getGame().getMonsterPool().size() ) );
			addMonster( monster );
		}
	}
	
	private void addMonster( Monster monster )
	{
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
		Arc arc = getArc( arcNumber );
		Ring ring = arc.getRing( ringNumber );
		ring.getMonsters().add( monster );
	}
	
	private void exitGame()
	{
		_exit = true;
	}
	
	private static class ActionEffect
	{
		// Main Action
		public boolean hit  = false;
		public boolean buildWall = false;
		public boolean buildTower = false;
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
		
		public boolean brickOrMortar = false;  // if true, walls/towers may be built with a brick or mortar
		public int numBricks = 0;
		public int numMortars = 0;
		
		// Validation
		boolean requireHit = false;
	}
}