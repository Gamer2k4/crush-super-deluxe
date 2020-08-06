package main.presentation.legacy.framework;

import java.awt.event.ActionEvent;

public enum GuiCommandType
{
	MOUSE_MOVE,
	MOUSE_PRESS,
	MOUSE_DOUBLE_CLICK,
	MOUSE_RELEASE;
	
	public static GuiCommandType fromActionEvent(ActionEvent ae)
	{
		for (GuiCommandType command : values())
		{
			if (command.name().equals(ae.getActionCommand()))
				return command;
		}
		
		throw new IllegalArgumentException("No GuiCommand exists for action command [" + ae.getActionCommand() + "]");
	}
}
