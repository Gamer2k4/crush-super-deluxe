package main.presentation.game.ejectionalert;

import main.data.Data;
import main.data.Event;
import main.presentation.ImageType;

public class InjuryEjectionAlert extends MedicalEjectionAlert
{
	public InjuryEjectionAlert(Data data, Event event)
	{
		super(ImageType.EJECT_INJURY, data, event);
	}

	@Override
	protected String explanationText()
	{
		return "INJURED BY";
	}
}
