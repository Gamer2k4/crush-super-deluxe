package main.presentation.screens.teameditor;

import java.awt.Point;

import main.data.entities.Skill;
import main.presentation.ImageType;
import main.presentation.game.StaticImage;

public class TeamEditorAgilityTrainerScreen extends AbstractTeamEditorTrainerScreen
{
	protected TeamEditorAgilityTrainerScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_AGILITY, screenOrigin);
		defineAgilitySkillButtons();
		refreshContent();
	}

	private void defineAgilitySkillButtons()
	{
		defineSkillButton(Skill.DOOMSTRIKE, 117, 26);
		defineSkillButton(Skill.FIST_OF_IRON, 117, 66);
		defineSkillButton(Skill.STRIP, 33, 106);
		defineSkillButton(Skill.SCOOP, 33, 146);
		defineSkillButton(Skill.JUGGLING, 33, 186);
		defineSkillButton(Skill.QUICKENING, 201, 106);
		defineSkillButton(Skill.JUDO, 145, 146);
		defineSkillButton(Skill.GYMNASTICS, 145, 186);
		defineSkillButton(Skill.COMBO, 257, 146);
		defineSkillButton(Skill.BOXING, 257, 186);
	}

	@Override
	protected ImageType getMaskTextureImageType()
	{
		return ImageType.SCREEN_TEAM_EDITOR_AGILITY_MASK;
	}

	@Override
	protected Point getScreenClickedButtonCoords()
	{
		return new Point(125, 75);
	}
}
