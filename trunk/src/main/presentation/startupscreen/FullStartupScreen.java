package main.presentation.startupscreen;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FullStartupScreen extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -3309094530131086326L;
	
	private JPanel contentPane;
	
	private static final int FRAME_HEIGHT = 480;
	private static final int FRAME_WIDTH = 640;
	
	private static final String MAIN_TAG = "main";
	
	public FullStartupScreen()
	{
		super("Crush! Super Deluxe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		setSize(FRAME_HEIGHT, FRAME_WIDTH);
		defineContentPane();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void defineContentPane()
	{
		contentPane = new JPanel();
		contentPane.setLayout(new CardLayout());
		
		//TODO: add different panels
		contentPane.add(new MainScreen(FRAME_HEIGHT, FRAME_WIDTH, this), MAIN_TAG);
		
		setContentPane(contentPane);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		// TODO Auto-generated method stub

	}
}
