package main.presentation.screens.teameditor;

import java.awt.Point;

import main.data.entities.Skill;
import main.presentation.ImageType;
import main.presentation.game.StaticImage;

public class TeamEditorPowerTrainerScreen extends AbstractTeamEditorTrainerScreen
{
	protected TeamEditorPowerTrainerScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_POWER, screenOrigin);
		definePowerSkillButtons();
		refreshContent();
	}

	private void definePowerSkillButtons()
	{
		defineSkillButton(Skill.TERROR, 117, 26);
		defineSkillButton(Skill.CHARGE, 33, 66);
		defineSkillButton(Skill.CHECKMASTER, 33, 106);
		defineSkillButton(Skill.TACTICS, 33, 146);
		defineSkillButton(Skill.JUGGERNAUT, 201, 66);
		defineSkillButton(Skill.VICIOUS, 145, 106);
		defineSkillButton(Skill.BRUTAL, 145, 146);
		defineSkillButton(Skill.RESILIENT, 257, 106);
		defineSkillButton(Skill.STALWART, 257, 146);
		defineSkillButton(Skill.GUARD, 257, 186);
	}

	@Override
	protected ImageType getMaskTextureImageType()
	{
		return ImageType.SCREEN_TEAM_EDITOR_POWER_MASK;
	}

	@Override
	protected Point getScreenClickedButtonCoords()
	{
		return new Point(40, 75);
	}
}
