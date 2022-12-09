package main.presentation;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

//TODO: Don't use; this doesn't work
//		referred to https://stackoverflow.com/questions/19479877/jfilechooser-in-libgdx
//		and page 149 of "Java Game Development with LibGDX"
public class FileChooser
{
	private static File selectedFile = null;
	private static boolean finished = false;
	
	public static File showOpenDialog()
	{
		finished = false;
		
		new Thread(new Runnable() {             
		    @Override
		    public void run() {
		    	JFileChooser chooser = new JFileChooser();
		        JFrame f = new JFrame();
		        f.setVisible(true);
		        f.toFront();
		        f.setVisible(false);
		        int res = chooser.showSaveDialog(f);
		        f.dispose();
		        if (res == JFileChooser.APPROVE_OPTION) {
		            setSelectedFile(chooser.getSelectedFile());
		            setFinished(true);
		        }
		    }
		}).start();
		
		while (!finished) {}
		
		return selectedFile;
	}
	
	private static void setSelectedFile(File file)
	{
		selectedFile = file;
	}
	
	private static void setFinished(boolean isFinished)
	{
		finished = isFinished;
	}
}