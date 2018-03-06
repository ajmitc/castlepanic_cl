package castlepanic.game.card;

public abstract class Card
{
	protected String _title;
	protected String _description;
	protected CardEffect _effect;
	
	public Card( String title, String desc, CardEffect effect )
	{
		_title = title;
		_description = desc;
		_effect = effect;
	}
	
	public String getTitle(){ return _title; }
	public String getDescription(){ return _description; }
	public CardEffect getEffect(){ return _effect; }

    public String toString()
    {
        return _title + " (" + _description + ")";
    }
}
