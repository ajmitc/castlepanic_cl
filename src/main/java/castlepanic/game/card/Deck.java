package castlepanic.game.card;

import java.util.List;
import java.util.ArrayList;

public class Deck<T>
{
	private List<T> _cards = new ArrayList<>();
	private List<T> _discard = new ArrayList<>();
	
	public Deck()
	{
		
	}
	
	public void add( T card ){ _cards.add( card ); }
	
	public void add( List<T> cards ){ _cards.addAll( cards ); }
	
	public T draw()
	{
		if( _cards.size() == 0 )
		{
			shuffleDiscard();
		}
		return _cards.remove( 0 );
	}
	
	public void discard( T card )
	{
		_discard.add( card );
	}
	
	public void shuffle()
	{
		Collections.shuffle( _cards );
	}
	
	public void clearDiscard()
	{
		_discard.clear();
	}
	
	public shuffleDiscard()
	{
		add( _discard );
		clearDiscard();
		shuffle();
	}
	
	public int size(){ return _cards.size(); }
}
