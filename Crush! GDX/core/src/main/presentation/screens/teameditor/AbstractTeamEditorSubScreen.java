package main.presentation.screens.teameditor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public abstract class AbstractTeamEditorSubScreen implements ActionListener
{
	protected TeamEditorParentScreen parentScreen;
	protected TeamUpdater teamUpdater = null;
	protected StaticImage subScreenImage;
	
	protected Point screenOrigin;
	
	protected static final String PLAYER_LABELS = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	protected AbstractTeamEditorSubScreen(TeamEditorParentScreen parentScreen)
	{
		this.subScreenImage = null;
		this.screenOrigin = new Point(0, 0);
		this.parentScreen = parentScreen;
		
		if (parentScreen != null)
		{
			teamUpdater = parentScreen.getTeamUpdater();
			teamUpdater.addUpdateListener(this);
		}
	}
	
	public List<Actor> getActors()
	{
		List<Actor> actors = new ArrayList<Actor>();
		
		actors.add(subScreenImage.getImage());
		
		return actors;
	}
	
	public List<GameText> getScreenTexts()
	{
		return new ArrayList<GameText>();
	}
	
	public List<ImageButton> getScreenButtons()
	{
		return new ArrayList<ImageButton>();
	}
	
	protected abstract void refreshContent();
	
	protected abstract void handleCommand(ScreenCommand command);
	

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//subclasses will need to implement this class to act on TeamUpdater updates
	}
}
