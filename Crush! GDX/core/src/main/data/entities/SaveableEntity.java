package main.data.entities;

import java.text.ParseException;

import main.data.save.SaveStringBuilder;
import main.data.save.SaveToken;
import main.data.save.SaveTokenTag;

public abstract class SaveableEntity
{
	public abstract String saveAsText();
	public abstract String loadFromText(String text) throws ParseException;
	public abstract String getUniqueId();
	
	protected abstract void setMember(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag);
	
	protected String getContentsForTag(SaveStringBuilder saveStringBuilder, SaveTokenTag saveTokenTag)
	{
		String toRet = "";
		SaveToken saveToken = saveStringBuilder.getToken(saveTokenTag);
		
		if (saveToken != null)
		{
			toRet = saveToken.getContents();
		}
		
		return toRet;
	}
	
	@Override
	public String toString()
	{
		return saveAsText();
	}
}
