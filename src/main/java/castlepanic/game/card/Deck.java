package castlepanic.game.card;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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

    /**
     * Look at the card at index, but do not remove it from the Deck
     */
    public T peek( int index )
    {
        if( index < 0 || index > _cards.size() )
            return null;
        return _cards.get( index );
    }
	
	public void discard( T card )
	{
        // If the card hasn't already been removed from the deck, remove it
        if( _cards.contains( card ) )
            _cards.remove( card );
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
	
	public void shuffleDiscard()
	{
		add( _discard );
		clearDiscard();
		shuffle();
	}
	
	public int size(){ return _cards.size(); }
}
