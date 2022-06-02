package main.presentation.common.image;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.TeamColorsManager;
import main.presentation.game.StaticImage;

public class TeamLineupGenerator
{
	public static List<StaticImage> getLineup(Team team, Point origin, boolean hideInjuredPlayers)
	{
		List<StaticImage> playerImages = new ArrayList<StaticImage>();
		
		for (int i = 8; i >= 0; i--)
		{
			Player player = team.getPlayer(i);
			
			if (player == null)
				continue;
			
			if (hideInjuredPlayers && player.getWeeksOut() > 0)	//I think the original game shows players that have one week left as well, though that may be a mistake on their part
				continue;
			
			Image playerImage = new Image(TeamColorsManager.getInstance().getPlayerImage(team, player.getRace()));
			
			int positionIndex = (i + 1) / 2;	//integer division rounds this down
			int indexMultiplier = 1;
			
			if (i % 2 == 0)
				indexMultiplier = -1;
			
//			Point coords = new Point(275 + (indexMultiplier * 45 * positionIndex), (129 * (3 - t) - 108));
			Point coords = new Point(origin.x + (indexMultiplier * 45 * positionIndex), origin.y);
			
			StaticImage image = new StaticImage(playerImage, coords);
			playerImages.add(image);
		}
		
		return playerImages;
	}
}
