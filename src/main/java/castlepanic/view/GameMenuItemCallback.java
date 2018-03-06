package castlepanic.view;

import castlepanic.Model;

public interface GameMenuItemCallback
{
	public void performAction( GameMenuItem item, Model model, View view );
}
