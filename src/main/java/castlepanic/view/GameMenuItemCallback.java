package leader.view;

import leader.Model;

public interface GameMenuItemCallback
{
	public void performAction( GameMenuItem item, Model model, View view );
}