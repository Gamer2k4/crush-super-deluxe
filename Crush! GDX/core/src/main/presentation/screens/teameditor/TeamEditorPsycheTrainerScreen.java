package main.presentation.screens.teameditor;

import java.awt.Point;

import main.data.entities.Skill;
import main.presentation.ImageType;
import main.presentation.game.StaticImage;

public class TeamEditorPsycheTrainerScreen extends AbstractTeamEditorTrainerScreen
{
	protected TeamEditorPsycheTrainerScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_PSYCHE, screenOrigin);
		definePsycheSkillButtons();
		refreshContent();
	}

	private void definePsycheSkillButtons()
	{
		defineSkillButton(Skill.SENSEI, 89, 26);
		defineSkillButton(Skill.AWE, 33, 66);
		defineSkillButton(Skill.LEADER, 33, 106);
		defineSkillButton(Skill.STOIC, 33, 146);
		defineSkillButton(Skill.HEALER, 145, 66);
		defineSkillButton(Skill.KARMA, 145, 106);
		defineSkillButton(Skill.SLY, 145, 146);
		defineSkillButton(Skill.INTUITION, 145, 186);
	}

	@Override
	protected ImageType getMaskTextureImageType()
	{
		return ImageType.SCREEN_TEAM_EDITOR_PSYCHE_MASK;
	}

	@Override
	protected Point getScreenClickedButtonCoords()
	{
		return new Point(210, 75);
	}
}
