package main.presentation.legacy.teameditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import main.presentation.common.Logger;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.AbstractLegacyImageBasedScreenPanel;
import main.presentation.teameditor.common.TeamUpdater;

public abstract class LegacyTeamEditorScreenDecorator extends AbstractLegacyImageBasedScreenPanel
{
	private static final long serialVersionUID = -7716937697732616483L;
	
	protected LegacyTeamEditorScreen teamEditorScreen;
	protected TeamUpdater teamUpdater;
	protected boolean buttonsEnabled = false;
	
	public LegacyTeamEditorScreenDecorator(LegacyTeamEditorScreen screenToPaint)
	{
		super(ImageUtils.createBlankBufferedImage(new Dimension(1, 1)), null);
		this.teamEditorScreen = screenToPaint;
		this.teamUpdater = screenToPaint.teamUpdater;
		screenToPaint.addMouseListener(this);
		addKeyBindings(screenToPaint);
	}
	
	public void paintElements(Graphics2D graphics)
	{
		paintText(graphics);
		paintImages(graphics);
		paintButtonShading(graphics);
		teamEditorScreen.requestFocusInWindow();
	}
	
	public void enableButtons()
	{
		buttonsEnabled = true;
	}
	
	public void disableButtons()
	{
		buttonsEnabled = false;
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (buttonsEnabled)
			teamEditorScreen.handleCommand(command);
	}
	
	private void addKeyBindings(AbstractLegacyImageBasedScreenPanel keyEventSource)
	{
		Action printAction = new AbstractAction() {
			private static final long serialVersionUID = 5659988121919607773L;

			@Override
			public void actionPerformed(ActionEvent event) {
		        String command = event.getActionCommand();
		        int charValue = (int)command.charAt(0);
		        Logger.debug("Action; length is " + command.length() + ", first char has int value of " + charValue);
		        
		        if (charValue == 10)
		        	fireKeyAction(new ActionEvent(event.getSource(), event.getID(), LegacyTextField.ENTER));
		        else if (charValue == 27)
		        	fireKeyAction(new ActionEvent(event.getSource(), event.getID(), LegacyTextField.ESCAPE));
		        else
		        	fireKeyAction(event);
		    }
		};
		
		for (int i = 0; i < LegacyTextField.VALID_CHARS.length(); i++)
		{
			char character = LegacyTextField.VALID_CHARS.charAt(i);
			keyEventSource.getInputMap().put(KeyStroke.getKeyStroke(character), character);
			keyEventSource.getActionMap().put(character, printAction);
		}
		
		keyEventSource.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), LegacyTextField.ENTER);
		keyEventSource.getActionMap().put(LegacyTextField.ENTER, printAction);
		
		keyEventSource.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), LegacyTextField.ESCAPE);
		keyEventSource.getActionMap().put(LegacyTextField.ESCAPE, printAction);
	}
	
	//Note that this is a little coupled, but it works 
	protected void fireKeyAction(ActionEvent event)
	{
		teamEditorScreen.keyAction(event);
	}

	protected abstract void keyAction(ActionEvent keyAction);
}
