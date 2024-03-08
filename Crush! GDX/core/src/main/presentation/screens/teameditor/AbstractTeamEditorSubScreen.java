package main.presentation.screens.teameditor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public abstract class AbstractTeamEditorSubScreen implements ActionListener
{
	protected TeamEditorParentScreen parentScreen;
	protected TeamUpdater teamUpdater = null;
	protected StaticImage subScreenImage;
	
	protected Point screenOrigin;
	
	private JButton refreshStageButton = new JButton();
	
	protected static final String PLAYER_LABELS = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String REFRESH_STAGE = "REFRESH_STAGE";
	
	
	private boolean isDoubleClick = false;
	private long timeOfLastCommand = 0L;
	private ScreenCommand lastCommand = null;
	private static final long MINIMUM_DOUBLE_CLICK_TIME = 250L;
	
	protected AbstractTeamEditorSubScreen(TeamEditorParentScreen parentScreen)
	{
		refreshStageButton.setActionCommand(REFRESH_STAGE);
		
		this.subScreenImage = null;
		this.screenOrigin = new Point(0, 0);
		this.parentScreen = parentScreen;
		
		if (parentScreen != null)
		{
			teamUpdater = parentScreen.getTeamUpdater();
			teamUpdater.addUpdateListener(this);
			refreshStageButton.addActionListener(parentScreen);
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

	public List<CrushSprite> getStaticSprites()
	{
		return new ArrayList<CrushSprite>();
	}
	
	public List<ImageButton> getScreenButtons()
	{
		return new ArrayList<ImageButton>();
	}
	
	protected boolean canUserEditTeam()
	{
		return !areTeamsLockedForEditing() && !isCpuTeam();
	}
	
	protected boolean areTeamsLockedForEditing()
	{
		return parentScreen.areTeamsLockedForEditing();
	}
	
	protected boolean isCpuTeam()
	{
		return !teamUpdater.getTeam().humanControlled;
	}
	
	protected void refreshParentStage()
	{
		refreshStageButton.doClick();
	}
	
	protected abstract void refreshContent();
	
	@SuppressWarnings("unused")
	protected void pressKey(String key)
	{
		return;
	}
	
	protected void handleCommand(ScreenCommand command)
	{
		isDoubleClick = false;
		
		long currentTime = System.currentTimeMillis();
		long timeSinceLastClick = currentTime - timeOfLastCommand;
		timeOfLastCommand = currentTime;
		
		if (lastCommand == command && timeSinceLastClick <= MINIMUM_DOUBLE_CLICK_TIME)
		{
			isDoubleClick = true;
			return;
		}
		
		lastCommand = command;
	}
	
	protected boolean isDoubleClick()
	{
		return isDoubleClick;
	}
	
	protected boolean dragEnabled()
	{
		return false;
	}
	
	protected boolean keysEnabled()
	{
		return false;
	}
	
	@SuppressWarnings("unused")
	protected void mouseClicked(Point clickCursorCoords)
	{
		//do nothing by default
	}
	
	protected void mouseReleased()
	{
		//do nothing by default
	}
	
	@SuppressWarnings("unused")
	protected void handleMouseEnterEvent(ActionEvent event)
	{
		//do nothing by default
	}
	
	@SuppressWarnings("unused")
	protected void handleMouseExitEvent(ActionEvent event)
	{
		//do nothing by default
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		//subclasses will need to implement this class to act on TeamUpdater updates
	}
}
