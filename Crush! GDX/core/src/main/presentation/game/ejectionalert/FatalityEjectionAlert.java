package main.presentation.game.ejectionalert;

import main.data.Data;
import main.data.Event;
import main.presentation.ImageType;

public class FatalityEjectionAlert extends MedicalEjectionAlert
{
	public FatalityEjectionAlert(Data data, Event event)
	{
		super(ImageType.EJECT_KILL, data, event);
		//add additional text - regeneration, resuscitation, karma, and so on
	}

	@Override
	protected String explanationText()
	{
		return "KILLED BY";
	}
}
