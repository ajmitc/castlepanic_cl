package castlepanic.game;

import castlepanic.game.card.Card;
import castlepanic.game.card.Deck;
import castlepanic.game.monster.Monster;

import java.util.List;
import java.util.ArrayList;

public class Player
{
	private String _name;
	private Deck<Card> _hand;
	
	private List<Monster> _slain;
	
	public Player()
	{
		this( "Player" );
	}
	
	public Player( String name )
	{
		_name = name;
		_hand = new Deck();
		_slain = new ArrayList<>();
	}
	
	public String getName(){ return _name; }
	public void setName( String n ){ _name = n; }
	
	public Deck<Card> getHand(){ return _hand; }
	
	public List<Monster> getSlainMonsters(){ return _slain; }
	
	public String toString(){ return _name; }
}
