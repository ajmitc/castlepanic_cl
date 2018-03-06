package castlepanic.game;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.*;

import java.awt.Color;

import castlepanic.game.monster.*;
import castlepanic.game.card.*;

public class Game
{
	private boolean _gameover;
	
	private List<Tower> _towers;
	private List<Wall> _walls;
	private List<Arc> _arcs;
	
	private List<Monster> _monsterPool;
	
	private Deck<CastleCard> _castleCards;
	private Deck<WizardCard> _wizardCards;
	
	private List<Player> _players;
	private int _currentPlayer;
	
	private int _handSize;
	
	// turn modifiers
	private boolean _drawCardForEveryHitPlayed = false;
	
	public Game()
	{
		_gameover = false;
		
		_towers = new ArrayList<>( 6 );
		_walls  = new ArrayList<>( 6 );
		_arcs   = new ArrayList<>( 6 );
		
		for( int i = 0; i < 6; ++i )
		{
			_towers.add( new BasicTower() );
			_walls.add( new BasicWall() );
		}
		_arcs.add( new Arc( 1, Arc.RED ) );
		_arcs.add( new Arc( 2, Arc.RED ) );
		_arcs.add( new Arc( 3, Arc.GREEN ) );
		_arcs.add( new Arc( 4, Arc.GREEN ) );
		_arcs.add( new Arc( 5, Arc.BLUE ) );
		_arcs.add( new Arc( 6, Arc.BLUE ) );
		
		_monsterPool = new ArrayList<>();
		_castleCards = new Deck<>();
		_wizardCards = new Deck<>();
		
		buildCastleDeck();
		buildWizardDeck();
		buildMonsterPool();
		
		_players = new ArrayList<>();
		_currentPlayer = 0;
		
		_handSize = 6;
	}
	
	private void buildCastleDeck()
	{
		// Hit cards
		addCastleCard( 1, "Green Swordsman", "Hit 1 Monster in the Green Swordsman ring.", CardEffect.HIT_GREEN_SWORDSMAN );
		addCastleCard( 1, "Green Knight", "Hit 1 Monster in the Green Knight ring.", CardEffect.HIT_GREEN_KNIGHT );
		addCastleCard( 1, "Green Archer", "Hit 1 Monster in the Green Archer ring.", CardEffect.HIT_GREEN_ARCHER );
		
		addCastleCard( 1, "Blue Swordsman", "Hit 1 Monster in the Blue Swordsman ring.", CardEffect.HIT_BLUE_SWORDSMAN );
		addCastleCard( 1, "Blue Knight", "Hit 1 Monster in the Blue Knight ring.", CardEffect.HIT_BLUE_KNIGHT );
		addCastleCard( 1, "Blue Archer", "Hit 1 Monster in the Blue Archer ring.", CardEffect.HIT_BLUE_ARCHER );
		
		addCastleCard( 1, "Red Swordsman", "Hit 1 Monster in the Red Swordsman ring.", CardEffect.HIT_RED_SWORDSMAN );
		addCastleCard( 1, "Red Knight", "Hit 1 Monster in the Red Knight ring.", CardEffect.HIT_RED_KNIGHT );
		addCastleCard( 1, "Red Archer", "Hit 1 Monster in the Red Archer ring.", CardEffect.HIT_RED_ARCHER );
		
		addCastleCard( 1, "Brick", "Play with a Mortar to rebuild 1 Wall.", CardEffect.BRICK );
		addCastleCard( 1, "Mortar", "Play with a Brick to rebuild 1 Wall.", CardEffect.MORTAR );
		
		// Special cards
		addCastleCard( 1, "Fortify Wall", "Place Fortify token on 1 Wall.  When hit, token damages Monsters and stops Boulders.", CardEffect.FORTIFY_WALL );
		addCastleCard( 1, "Never Lose Hope", "Immediately discard as many cards as you wish.  For every card you discard, draw 1 Castle card.", CardEffect.DISCARD_AND_DRAW_MULTIPLE );
		addCastleCard( 1, "Change Color", "Play this card WITH any hit card to change the color of the hit card.", CardEffect.CHANGE_COLOR );
		addCastleCard( 1, "Change Range", "Play this card WITH any hit card to change the range of the hit card.", CardEffect.CHANGE_RANGE );
		addCastleCard( 1, "Knock Back", "Play this card WITH a hit card to move the hit Monster back 1 space AFTER damaging it.", CardEffect.MOVE_BACK_1_SPACE );
		addCastleCard( 1, "Nice Shot", "Play this card WITH any hit card to slay the hit Monster.", CardEffect.SLAY );
		addCastleCard( 1, "Draw 2 Cards", "Play this card to add 2 cards to your hand, even if it exceeds the normal hand size.", CardEffect.DRAW_2_CARDS );
		addCastleCard( 1, "Berserk", "Draw 1 card from the Castle deck for every hit card you play during the remainder of this turn.", CardEffect.DRAW_CASTLE_CARD_FOR_EVERY_HIT_PLAYED );
		
		_castleCards.shuffle();
	}
	
	private void addCastleCard( int count, String title, String desc, CardEffect effect )
	{
		for( int i = 0; i < count; ++i )
			_castleCards.add( new CastleCard( title, desc, effect ) );
	}
	
	private void buildWizardDeck()
	{
		addWizardCard( 1, "Hypnotize", "2 Monsters in the same space (not the Forest) attack each other simultaneously.", CardEffect.HIT_2_MONSTERS_IN_SAME_SPACE_NOT_FOREST );
		addWizardCard( 1, "Mystical Manufacturing", "Play this card WITH 1 Brick or 1 Mortar card to rebuild 1 destroyed Tower.", CardEffect.BUILD_TOWER_WITH_BRICK_OR_MORTAR );
		
		_wizardCards.shuffle();
	}
	
	private void addWizardCard( int count, String title, String desc, CardEffect effect )
	{
		for( int i = 0; i < count; ++i )
			_wizardCards.add( new WizardCard( title, desc, effect ) );
	}
	
	private void buildMonsterPool()
	{
		addMonsterToPool(  6, MonsterType.GOBLIN );
		addMonsterToPool( 10, MonsterType.ORC );
		addMonsterToPool( 10, MonsterType.TROLL );
		addMonsterToPool(  1, MonsterType.GOBLIN_KING );
		addMonsterToPool(  1, MonsterType.ORC_WARLORD );
		addMonsterToPool(  1, MonsterType.TROLL_MAGE );
		addMonsterToPool(  2, MonsterType.MOVE_BLUE );
		addMonsterToPool(  2, MonsterType.MOVE_GREEN );
		addMonsterToPool(  2, MonsterType.MOVE_RED );
		
		Collections.shuffle( _monsterPool );
	}
	
	private void addMonsterToPool( int count, MonsterType type )
	{
		for( int i = 0; i < count; ++i )
		{
			Monster monster = new Monster( type );
			_monsterPool.add( monster );
		}
	}
	
	
	public void moveMonsters()
	{
		_arcs.stream().forEach( arc -> moveMonsters( arc ) );
	}
	
	public void moveMonsters( Arc arc )
	{
		arc.getRings().stream().forEach( ring -> moveMonsters( ring ) );
	}
	
	public void moveMonsters( Color color )
	{
		_arcs.stream().filter( arc -> arc.getColor() == color ).forEach( arc -> moveMonsters( arc ) );
	}
	
	public void moveMonsters( Ring ring )
	{
		List<MoveMonsterSpec> specs = ring.getMonsters().stream().map( monster -> getMoveMonsterSpec( monster, ring ) ).collect( Collectors.toList() );
        specs.stream().forEach( spec -> moveMonster( spec ) );
	}
	
	public MoveMonsterSpec getMoveMonsterSpec( Monster monster, Ring fromRing )
	{
		Ring nextRing = null;
		if( monster.getType() == MonsterType.DRAGON )
		{
			// TODO Roll dice and lookup in table
		}
		// TODO Add other bosses here
		else if( fromRing.getRange() == Ring.CASTLE )
		{
			// Monsters move clockwise
			Arc nextArc = _arcs.get( (fromRing.getArc().getNumber() + 1) % 6 );
			nextRing = nextArc.getRing( Ring.CASTLE );
		}
		else
		{
			nextRing = fromRing.getArc().getRing( fromRing.getRange() - 1 );
		}
		
		if( nextRing != null )
		{
			//moveMonster( monster, fromRing, nextRing );
            return new MoveMonsterSpec( monster, fromRing, nextRing );
		}
        return null;
	}
	
	public void moveMonster( MoveMonsterSpec monsterspec )
	{
		boolean doMoveMonster = true;
			
		// Check if there's a structure (Wall or Tower)
		if( monsterspec.toRing.getRange() == Ring.CASTLE )
		{
			// Check if there's a wall
			if( monsterspec.fromRing.getRange() == Ring.ARCHER )
			{
				Wall wall = getWall( monsterspec.toRing.getArc().getNumber() );
				if( !wall.isDestroyed() )
				{
					if( wall.getFire() > 0 )
					{
						System.out.println( monsterspec.monster.getType() + " catches " + wall.getFire() + " fire from wall!" );
						monsterspec.monster.adjFire( wall.getFire() );
					}

					if( wall.isFortified() )
					{
						// Damage monster (if no fire) and remove fortified token
						if( wall.getFire() == 0 )
							monsterspec.monster.adjHitpoints( -1 );
						System.out.println( "Wall in Arc " + monsterspec.toRing.getArc().getNumber() + " no longer fortified!" );
						wall.setFortified( false );
					}
					else
					{
						// Wall is destroyed
						if( wall.getFire() == 0 )
						{
							// Monster takes damage
							monsterspec.monster.adjHitpoints( -1 );
						}
						System.out.println( "Wall in Arc " + monsterspec.toRing.getArc().getNumber() + " destroyed!" );
						wall.setDestroyed( true );
					}
					doMoveMonster = false;
				}
			} // end moving from ARCHER ring to CASTLE ring
			else if( monsterspec.fromRing.getRange() == Ring.CASTLE )
			{
				// Move from one CASTLE space to another CASTLE space
				// Check if there's a tower
				Tower tower = getTower( monsterspec.toRing.getArc().getNumber() );
				if( !tower.isDestroyed() )
				{
					if( tower.getFire() > 0 )
					{
						System.out.println( monsterspec.monster.getType() + " catches " + tower.getFire() + " fire from tower!" );
						monsterspec.monster.adjFire( tower.getFire() );
					}

					// Tower is destroyed
					if( tower.getFire() == 0 )
					{
						// Monster takes damage
						monsterspec.monster.adjHitpoints( -1 );
					}
					System.out.println( "Tower in Arc " + monsterspec.toRing.getArc().getNumber() + " destroyed!" );
					tower.setDestroyed( true );

					doMoveMonster = false;
				}
			}
		}

		if( doMoveMonster && monsterspec.monster.getHitpoints() > 0 )
		{
            System.out.println( "Moving " + monsterspec.monster + " from " + monsterspec.fromRing + " ring of Arc " + monsterspec.fromRing.getArc().getNumber() + " to ring " + monsterspec.toRing + " ring of Arc " + monsterspec.toRing.getArc().getNumber() );
			monsterspec.fromRing.getMonsters().remove( monsterspec.monster );
			monsterspec.toRing.getMonsters().add( monsterspec.monster );
		}

		if( monsterspec.monster.getHitpoints() == 0 )
		{
			System.out.println( monsterspec.monster.getType() + " killed by structure" );
		}
	}
	
	
	public void moveMonstersClockwise()
	{
		List<Monster> moved = new ArrayList<>();
		_arcs.stream().flatMap( arc -> arc.getRings().stream() ).forEach( ring -> {
			int arcNumber = ring.getArc().getNumber();
			int nextArcNumber = (arcNumber + 1) % 6;
			Ring nextRing = getArc( nextArcNumber ).getRing( ring.getRange() );
            MoveMonsterSpec spec = new MoveMonsterSpec( null, ring, nextRing );
			ring.getMonsters().stream().filter( monster -> !moved.contains( monster ) ).forEach( monster -> {
                spec.monster = monster;
				moveMonster( spec );
				moved.add( monster );
			});
		});
	}
	
	public void moveMonstersCounterClockwise()
	{
		List<Monster> moved = new ArrayList<>();
		_arcs.stream().flatMap( arc -> arc.getRings().stream() ).forEach( ring -> {
			int arcNumber = ring.getArc().getNumber();
			int nextArcNumber = arcNumber - 1;
			if( nextArcNumber < 1 ) nextArcNumber = 6;
			Ring nextRing = getArc( nextArcNumber ).getRing( ring.getRange() );
            MoveMonsterSpec spec = new MoveMonsterSpec( null, ring, nextRing );
			ring.getMonsters().stream().filter( monster -> !moved.contains( monster ) ).forEach( monster -> {
                spec.monster = monster;
				moveMonster( spec );
				moved.add( monster );
			});
		});
	}
	
	public boolean isGameOver(){ return _gameover; }
	public void setGameOver( boolean v ){ _gameover = v; }
	
	public List<Tower> getTowers(){ return _towers; }
	public List<Wall> getWalls(){ return _walls; }
	public List<Arc> getArcs(){ return _arcs; }
	
	public Arc getArc( int arcNumber )
	{
		if( arcNumber >= 1 && arcNumber <= 6 )
			return _arcs.get( arcNumber - 1 );
		return null;
	}
	
	public Wall getWall( int arcNumber )
	{
		if( arcNumber >= 1 && arcNumber <= 6 )
			return _walls.get( arcNumber - 1 );
		return null;
	}
	
	public Tower getTower( int arcNumber )
	{
		if( arcNumber >= 1 && arcNumber <= 6 )
			return _towers.get( arcNumber - 1 );
		return null;
	}
	
	public List<Monster> getMonsterPool(){ return _monsterPool; }
	
	public Deck<CastleCard> getCastleCards(){ return _castleCards; }
	public Deck<WizardCard> getWizardCards(){ return _wizardCards; }
	
	public List<Player> getPlayers(){ return _players; }
	public Player getPlayer( int i ){ return _players.get( i ); }
	
	public Player getCurrentPlayer()
	{
		return _players.get( _currentPlayer );
	}
	
	public void nextPlayer()
	{
		_currentPlayer = (_currentPlayer + 1) % _players.size();
	}
	
	public int getHandSize(){ return _handSize; }
	public void setHandSize( int s ){ _handSize = s; }
	
	
    private static class MoveMonsterSpec
    {
        public Monster monster = null;
        public Ring fromRing = null;
        public Ring toRing = null;
        public MoveMonsterSpec( Monster m, Ring f, Ring t )
        {
            monster = m;
            fromRing = f;
            toRing = t;
        }
    }
}
