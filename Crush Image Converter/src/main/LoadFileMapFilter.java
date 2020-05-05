package main;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class LoadFileMapFilter extends FileFilter
{

	private static final String IMAGE_EXTENSION = "map";

	// Accept all directories and all *.map files.
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;

		String extension = getExtension(f);
		if ((extension != null) && (extension.equals(IMAGE_EXTENSION)))
		{
			return true;
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription()
	{
		return "Crush! Deluxe Map Files";
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