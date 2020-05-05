package main;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class SaveFileFilter extends FileFilter
{

	private static final String JPG_EXTENSION = "jpg";
	private static final String PNG_EXTENSION = "png";

	// Accept all directories and all *.jpg and *.png files.
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;

		String extension = getExtension(f);
		if (JPG_EXTENSION.equals(extension) || PNG_EXTENSION.equals(extension))
		{
			return true;
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription()
	{
		return "JPGs and PNGs";
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