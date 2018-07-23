import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 *
 *
 * @author Marty Kane
 * @version 0.0.1  (4/19/2007)
 *
 */

public class TowerGame extends JFrame implements WindowListener,
			   		         ActionListener,
						 MouseListener,
						 MouseMotionListener,
						 KeyListener
{
  private final Toolkit tk = Toolkit.getDefaultToolkit();

  private GameScreen gameScreen;
  private DisplayPanel displayPanel;

  private boolean paused;
  java.util.Timer t;
  UpdateTimer timer;

  private Square currentSquare; //The highlighted square (mouse hover)


  /**
   * Start a new TowerGame Application window
   */
  public TowerGame()
  {
    super("Tower Game");
    this.setName("Main Window");

    //listeners
    this.addWindowListener(this);
    this.addKeyListener(this);

    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.setIconImage(null);

    //Set up GUI    
    this.setupMenuBar();

    this.getContentPane().setLayout(new BorderLayout());

    gameScreen = new GameScreen();
    gameScreen.addMouseListener(this);
    gameScreen.addMouseMotionListener(this);

    this.getContentPane().add( gameScreen, BorderLayout.CENTER);

    displayPanel = new DisplayPanel();

    JPanel dp = new JPanel();
    dp.add(displayPanel);
    dp.setBackground(Color.black);
    dp.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

    this.getContentPane().add( dp, BorderLayout.EAST);

    paused = false;
    currentSquare = null;

    //set this to the System look n feel
    try 
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (Exception exc)
    {
      System.err.println("Error loading L&F: " + exc);
    }
    this.setDefaultLookAndFeelDecorated(true);
    this.pack();
    this.setLocation( 0, 0 );

    /* this.setFullScreen(); */
    //Sort of "half screen"
    /*
    Dimension d = tk.getScreenSize(); 
    this.setMaximizedBounds(new Rectangle(new Dimension(d.width/2-2, 
							d.height )));
    this.setSize( 1, 1 );
    */

    this.setResizable(false);
    this.setVisible(true);
  }


  //run the app!
  public static void main( String args[] )
  {
    TowerGame app = new TowerGame();

    app.t = new java.util.Timer();
    app.timer = new UpdateTimer( app.gameScreen );

    //start game
    app.t.scheduleAtFixedRate( app.timer, 1000, UpdateTimer.TIME ); 

  }//main


  //action listener
  public void actionPerformed( ActionEvent e )
  {
    //System.out.println("Action: "+e.getActionCommand());

    if ( (e.getActionCommand()).equals("") )
    {
      
    }
    else if ( (e.getActionCommand()).equals("new") )
    {

    }
    else if ( (e.getActionCommand()).equals("open") )
    {
      /* Loads board from "board.txt"
         Could use fileChooser dialog to select, but maybe later */
      pause();
      gameScreen.removeMouseListener(this);
      gameScreen.removeMouseMotionListener(this);
      try {
	gameScreen.load();
      }
      catch ( java.io.IOException ioe)
      {
	System.out.println("Error loading file: "+ioe.getMessage());
	return;
      }
      gameScreen.addMouseListener(this);
      gameScreen.addMouseMotionListener(this);
    }
    else if ( (e.getActionCommand()).equals("save") )
    {
      /* Saves board to "board.txt" 
         Could use fileChooser dialog to select, but maybe later */
      gameScreen.save();
    }
    else if ( (e.getActionCommand()).equals("quit") )
    {
      quit();
    }

    repaint();
  }//end - actionPerformed


  //mouse events
  public void mouseClicked(MouseEvent e)
  {
    //Figure out which square was clicked on
    int x = e.getX();
    int y = e.getY();
    System.out.println("Click: ("+x+","+y+")");

    if ( e.getButton() == MouseEvent.BUTTON1 )
    {
      gameScreen.toggleFillSquare(x,y);
    }
    else
    {
      gameScreen.addGuy( new Guy("New Guy",x,y, 5, 520,246) );
    }

    repaint();
  }


  public void mouseExited(MouseEvent e)
  {
    currentSquare = gameScreen.highlightSquare(-1,-1);
  }

  public void mouseEntered(MouseEvent e)
  {
  }

  public void mousePressed(MouseEvent e)
  {
  }

  public void mouseReleased(MouseEvent e) 
  {
  }


  //mouse motion
  public void mouseDragged(MouseEvent e)
  {
    gameScreen.fillSquare(e.getX(),e.getY());
  }

  public void mouseMoved(MouseEvent e)
  {
    //System.out.println("mouse: ("+e.getX()+","+e.getY()+")");
    currentSquare = gameScreen.highlightSquare(e.getX(),e.getY());
  }

  //keyboard
  public void keyPressed(KeyEvent e)
  {
    if ( e.getKeyChar() == 'p' || e.getKeyChar() == ' ' )
    {
      if ( !paused )
      {
	pause();
      }
      else
      {
	unpause();
      }
      paused = !paused;
    }
    else if ( e.getKeyChar() == 'g' )
    {
      gameScreen.toggleBorders();
    }
    else if ( e.getKeyChar() == 's' )
    {
      gameScreen.togglePaths();
    }
    else if ( e.getKeyChar() == 'l' )
    {
      gameScreen.toggleLines();
    }
    else if ( e.getKeyChar() == 'm' )
    {
      if ( currentSquare == null )
      {
	return; 
      }
      gameScreen.addMachineGun(currentSquare);
    }
  }

  public void keyReleased(KeyEvent e)
  {
  }

  public void keyTyped(KeyEvent e) 
  {

  }


  //window listener stuff
  public void windowClosing(WindowEvent we)
  {
    quit();
  }    
  
  public void windowOpened(WindowEvent we)
  {
  }
  
  public void windowClosed(WindowEvent we)
  {
  }
  
  public void windowIconified(WindowEvent we)
  {
  }
  
  public void windowDeiconified(WindowEvent we)
  {
  }
  
  public void windowActivated(WindowEvent we)
  {
  }
  
  public void windowDeactivated(WindowEvent we)
  {
  }


  
  private void pause()
  {
    t.cancel();
    this.setTitle("Paused");
  }

  private void unpause()
  {
    t = new java.util.Timer(); 
    timer = new UpdateTimer( gameScreen );
    t.scheduleAtFixedRate( timer, 0, UpdateTimer.TIME ); 
    this.setTitle("Tower Game");
  }

  /**
   * Attempt to quit the program. Warn the user and allow them to stay.
   * If they want to quit anyway, exit the program
   */
  private void quit()
  {
    String message = "Are you sure you want to quit?";
    int result;
    result = JOptionPane.showConfirmDialog( this, 
					    message,
					    "Really Quit?",
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.WARNING_MESSAGE );
    
    if ( result == JOptionPane.YES_OPTION )
    {
      System.exit(0);
    }
    else if ( result ==JOptionPane.NO_OPTION )
    {
      return;
    }
    else if ( (result == JOptionPane.CANCEL_OPTION) ||
	      (result == JOptionPane.CLOSED_OPTION) )
    {
      return;
    }
  }//end - quit
  
  
  //Private space saving methods
  
  /**
   * Set up the Menu Bar for the TowerGame
   */
  private void setupMenuBar()
  {
    //Set up menu bar
    JMenuBar mb = new JMenuBar();    
    //file menu
    JMenu  file = new JMenu("File");
    file.setMnemonic('F');
    //new area
    JMenuItem jmi = new JMenuItem("New Game");
    jmi.setActionCommand("new");
    jmi.setMnemonic('N');
    jmi.setToolTipText("Start a new game");
    jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
					      KeyEvent.CTRL_MASK));
    jmi.addActionListener(this);
    file.add(jmi);
    //open area
    jmi = new JMenuItem("Open Map");
    jmi.setActionCommand("open");
    jmi.setMnemonic('O');
    jmi.setToolTipText("Open a map for editing");
    jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
					      KeyEvent.CTRL_MASK));
    jmi.addActionListener(this);
    file.add(jmi);
    //save area
    jmi = new JMenuItem("Save Map");
    jmi.setActionCommand("save");
    jmi.setMnemonic('S');
    jmi.setToolTipText("Save this map");
    jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
					      KeyEvent.CTRL_MASK));
    jmi.addActionListener(this);
    file.add(jmi);

    //exit
    jmi = new JMenuItem("Quit");
    jmi.setActionCommand("quit");
    jmi.setMnemonic('Q');
    jmi.addActionListener(this);
    jmi.setToolTipText("Quit the program");
    jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 
					      KeyEvent.CTRL_MASK));
    file.add(jmi);
    mb.add(file);
    this.setJMenuBar( mb );
    //end of setmenubar
  }


  private void setFullScreen()
  {
    // For Full Screen 
    try
    {
      //try to maximize the window if possible on this platform
      if ( tk.isFrameStateSupported(MAXIMIZED_BOTH) )
      {
	this.setExtendedState(MAXIMIZED_BOTH);
      }
      else
      {//if not possible, then just make the window as big as the screen
	Dimension d = tk.getScreenSize(); 
	this.setSize( d.width-4, d.height-30 );
      }
    }
    catch ( HeadlessException he )
    {
      Dimension d = tk.getScreenSize(); 
      this.setSize( d.width-4, d.height-30 );
    }
  }

}
