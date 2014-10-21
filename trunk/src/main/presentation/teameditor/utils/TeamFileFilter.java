package main.presentation.teameditor.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TeamFileFilter extends FileFilter
{

	private static final String DEFAULT_EXTENSION = "csdt";
	private static final String LEGACY_EXTENSION = "tme";

	// Accept all directories and all *.csdt and *.tme files.
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;

		String extension = getExtension(f);
		if ((extension != null) && (extension.equals(DEFAULT_EXTENSION) || extension.equals(LEGACY_EXTENSION)))
		{
			return true;
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription()
	{
		return "Crush! Deluxe Team Files";
	}

	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
		{
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}