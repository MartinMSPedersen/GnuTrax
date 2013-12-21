import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.awt.*;
import javax.imageio.*;



public class GnuTrax extends JFrame {

    public GnuTrax() {
        super("GnuTrax 1.0");
        setResizable(false);
				setMinimumSize(new Dimension(640,640));
    }
    
    public void addComponentsToPane(final Container pane) 
    {
				int i,j;
				BufferedImage[] image=new BufferedImage[8];
				try {
						image[Traxboard.NS]=ImageIO.read(new File("images/tiles/large/ns.gif")); // 80x80 gif
						image[Traxboard.WE]=ImageIO.read(new File("images/tiles/large/we.gif")); // 80x80 gif
						image[Traxboard.NW]=ImageIO.read(new File("images/tiles/large/nw.gif")); // 80x80 gif
						image[Traxboard.NE]=ImageIO.read(new File("images/tiles/large/ne.gif")); // 80x80 gif
						image[Traxboard.WS]=ImageIO.read(new File("images/tiles/large/ws.gif")); // 80x80 gif
						image[Traxboard.SE]=ImageIO.read(new File("images/tiles/large/se.gif")); // 80x80 gif
						image[Traxboard.INVALID]=ImageIO.read(new File("images/tiles/large/invalid.gif")); // 80x80 gif
						image[Traxboard.EMPTY]=ImageIO.read(new File("images/tiles/large/blank.gif")); // 80x80 gif
				} 
				catch (IOException e) {
						e.printStackTrace();
				}
        JPanel outerPanel=new JPanel();
        outerPanel.setLayout(new GridLayout(8,8));
				imagePanel InnerPanel;
				
				for (i=0; i<8; i++) {
						for (j=0; j<8; j++) {
								InnerPanel=new imagePanel(image[1+(i+j)%6]);
								outerPanel.add(InnerPanel);
						}
				}
        pane.add(outerPanel);
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method is invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        GnuTrax frame = new GnuTrax();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    createAndShowGUI();
		}
	    });
    }

    public static final long serialVersionUID = 2488472L;

}
