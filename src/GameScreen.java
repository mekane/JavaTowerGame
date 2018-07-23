/**
 *
 *
 */

import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.*;
import java.io.*;

public class GameScreen extends JComponent
{
  public static final int SPACE = 1;

  private final int size_x = 32; //map is 13 rooms wide (2 for walls)
  private final int size_y = 28; 

  //use this Font to draw the room info (vnum)
  private final Font roomFont = new Font( "SansSerif", Font.PLAIN, 14 );

  Board board;

  private Vector<Guy> guyList;
  private Vector<Gun> gunList;

  private boolean drawBorders = false;
  private boolean drawPaths   = false;
  private boolean drawLines   = false;

  private Square hover = null;

  /**
   * 
   */
  public GameScreen( )
  {
    super();
    this.setName("Game Screen");

    board = new Board( size_x, size_y );

    int x = (size_x+2)*(Square.SIZE+SPACE)+SPACE;
    int y = (size_y+2)*(Square.SIZE+SPACE)+SPACE;
    System.out.println("Game Screen Size: "+x+" by "+y);

    board.getSquareAt(520,246).select();

    guyList = new Vector<Guy>();
    gunList = new Vector<Gun>();
    //guyList.add( new Guy("The Dude",45,230,5, 483,225) ); /*The DUDE*/
    
    //Dimension minDim = new Dimension( Math.max(x,640), Math.max(y,480) );    
    Dimension minDim = new Dimension( x, y );
    
    this.setMinimumSize( minDim );
    this.setPreferredSize( minDim );
    
    this.setVisible(true);
  }
  
  
  
  /**
   * Custom Painting - draw everything in the world
   */
  public void paint( Graphics g )
  {
    int w = this.getWidth();
    int h = this.getHeight();
    int SS = Square.SIZE+SPACE;

    //clear screen
    g.setColor(Color.black);
    g.fillRect( 0,0, w, h );

    //fill drawable area with square color
    //g.setColor(Square.BACKGROUND_COLOR);
    //g.setColor(Color.orange);
    //g.fillRect( SS,SS, w-4*SS, h-4*SS );


    Square square = null;

    for ( int x = 1 ; x <= size_x ; x++ )
    {
      for ( int y = 1 ; y <= size_y ; y++ )
      {
	square = board.getSquare(x-1,y-1);

	int ex = x*(Square.SIZE+SPACE);
	int ey = y*(Square.SIZE+SPACE);

	if ( square == null )
	{
	  //System.out.println("Square ("+(x-1)+","+(y-1)+") is null");
	  continue;
	}
	else
	{
	  square.draw( ex, ey, drawBorders, g );
	}

	//draw the valid edges for each square 
	if ( drawLines )
	{
	  int S2 = Square.SIZE/2;
	  int S4 = Square.SIZE/4;
	  ex += S2;
	  ey += S2;
	  
	  Square v = square.getEdgeNorth();
	  if ( v != null )
	  {
	    g.setColor(Color.orange);
	    g.drawLine( ex,ey, ex   ,ey-S4 );//draw edge north
	  }
	  v = square.getEdgeEast();
	  if ( v != null )
	  {
	    g.setColor(Color.blue); 
	    g.drawLine( ex,ey, ex+S4,ey );
	  }
	  v = square.getEdgeSouth();
	  if ( v != null )
	  {
	    g.setColor(Color.yellow);
	    g.drawLine( ex,ey, ex   ,ey+S4 );
	  }
	  v = square.getEdgeWest();
	  if ( v != null )
	  {
	    g.setColor(Color.cyan);
	    g.drawLine( ex,ey, ex-S4,ey );
	  } 
	}
		
      }//y
    }//x

    
    /*Test-guns*/
    for ( int x = 1 ; x <= size_x ; x++ )
    {
      for ( int y = 1 ; y <= size_y ; y++ )
      {
	square = board.getSquare(x-1,y-1);
	int ex = x*(Square.SIZE+SPACE);
	int ey = y*(Square.SIZE+SPACE);
	if ( square == null ) {continue;}
	else
	{
	  if ( square.hasGun() )
	  {
	    Guy target = square.getGun().getTarget();
	    if ( target != null )
	    {
	      g.setColor(Color.yellow);
	      Point pos = target.getPosition();
	      g.drawLine( ex+Square.SIZE/2, ey+Square.SIZE/2, 
			  (int)pos.getX(), (int)pos.getY() );
	    }
	  }
	}
      }
    }//test-guns

    
    for ( Guy guy : guyList )
    {
      if ( drawPaths )
      {
	Point p1 = guy.getPosition();
	Point p2 = null;
	for ( Square s : guy.path )
	{
	  p2 = board.getSquareCoordinates( s );
	  g.setColor(Color.blue);
	  g.drawLine( (int)p1.getX(), (int)p1.getY(), 
		      (int)p2.getX(), (int)p2.getY() );
	  p1 = p2;
	  //System.out.println("  "+s.getName());
	}
      }
      
      guy.draw(g);      
    }

  }//end - paint


  /**
   * Called by container to update the game status
   *
   */
  public synchronized void updateGame()
  {
    //first clear the board of Guy positions
    Square s;
    for ( int x = 1 ; x <= size_x ; x++ )
    {
      for ( int y = 1 ; y <= size_y ; y++ )
      {
	s = board.getSquare(x-1,y-1);

	if ( s == null )
	{
	  continue;
	}
	else
	{
	  s.clearGuys();
	}
      }
    }    

    //update all the Guys
    for ( Guy guy : guyList )
    {
      guy.update(board);
      //update Board to keep track of where the guys are
    }

    //Kill Guys who reached the goal and update the index for those that didn't
    ListIterator li = guyList.listIterator();
    Guy guy;
    
    while ( li.hasNext() )
    {
      guy = (Guy)li.next();
      if ( guy.isDone() )
      {
	li.remove();
	for ( Gun g : gunList )
	{
	  if ( g.getTarget() == guy )
	  {
	    g.clearTarget();
	  }
	}
      }
      else
      {
	Point pos = guy.getPosition();
        s = board.getSquareAt( (int)pos.getX(),(int)pos.getY() );
	s.addGuy( guy );
      }
    }

    if ( guyList.isEmpty() )
    {
      repaint();
      return;
    }

    //udpate Guns
    /* For lack of a better idea, we'll just go over each square and see if
       it has a gun or not. If so, aim the gun */
    for ( int x = 1 ; x <= size_x ; x++ )
    {
      for ( int y = 1 ; y <= size_y ; y++ )
      {
	s = board.getSquare(x-1,y-1);

	if ( s == null )
	{
	  continue;
	}
	else
	{
	  if ( s.hasGun() )
	  {
	    s.highlight();
	    s.getGun().findTarget(s);
	  }
	}
      }
    }    
    

    repaint();
  }



  public void toggleSelectSquare( int x, int y )
  {
    Square square = board.getSquareAt(x,y);
    
    if ( square == null )
    {
      return;
    }
    
    square.toggleSelect();
  }

  /**
   *
   */
  public void toggleFillSquare( int x, int y )
  {
    Square square = board.getSquareAt(x,y);
    
    if ( square == null )
    {
      return;
    }

    /* TEST - get Square info */
    System.out.println(" Square is passable: "+square.isPassable());
    /**/

    int state = square.getState();
    if ( state == Square.STATE_INVIS || state == Square.STATE_PROTECT )
    {
      return;
    }

    if ( square.isSolid() )
    {
      board.emptySquare(square);
    }
    else
    {
      board.fillSquare(square);
    }
    repaint();
  }


  public void fillSquare( int x, int y )
  {
    Square square = board.getSquareAt(x,y);
    
    if ( square == null || square.isSolid() )
    {
      return;
    }
    else
    {
      int state = square.getState();
      if ( state == Square.STATE_INVIS || state == Square.STATE_PROTECT )
      {
	return;
      }
      
      board.fillSquare(square);
    }
    repaint();
  }

  /**
   * TEST - we need a more general purpose method, obviously
   */
  public void addMachineGun( Square square )
  {
    if ( square == null || square.isSolid() )
    {
      return;
    }
    else
    {
      int state = square.getState();
      if ( state == Square.STATE_INVIS || state == Square.STATE_PROTECT )
      {
	return;
      }
      
      gunList.add(board.addGun(square));
    }
    repaint();
  }

  /**
   *
   */
  public Square highlightSquare( int x, int y )
  {
    if ( hover != null )
    {
      hover.unhighlight();
    }

    hover = board.getSquareAt(x,y);
    
    if ( hover == null )
    {
      return null;
    }
    else
    {
      hover.highlight();
    }
    repaint();
    return hover;
  }

  
  public synchronized void addGuy( Guy newGuy )
  {
    guyList.add(newGuy);
    repaint();
  }

  public void removeGuy( Guy theGuy )
  {
    guyList.remove(theGuy);
    repaint();
  }

  /**
   * Called by parent to turn on or off grid
   */
  public void toggleBorders()
  {
    this.drawBorders = !drawBorders;
    repaint();
  }


  /**
   * Called by parent to turn on or off showing each Guy's path
   */
  public void togglePaths()
  {
    this.drawPaths = !drawPaths;
    repaint();
  }


  /**
   * Called by parent to turn on or off showing each Square's connections
   */
  public void toggleLines()
  {
    this.drawLines = !drawLines;
    repaint();
  }



  public static final String fileName = "board.txt";
  /** 
   * Kind of a test: Save the current state of the Board to a file.
   */
  public void save()
  {
    File outputFile = new File ( fileName );
    PrintWriter fileOut = null;

    try 
    { 
      fileOut = new PrintWriter( new FileOutputStream( outputFile ));
    }
    catch ( FileNotFoundException fnfe )
    {
      System.out.println("Error: could not open file "+outputFile.getName());
      return;
    }

    fileOut.println(size_x+" "+size_y);

    Square square;
    int s = 0;
    for ( int y = 0 ; y < size_y ; y++ )
    {
      for ( int x = 0 ; x < size_x ; x++ )
      {
	square = board.getSquare(x,y);
	
	if ( square == null )
	{
	  System.out.print("4 ");
	}
	else
	{
	  s = square.getState() + (square.isSolid() ? 4 : 0);
	  fileOut.print(s+" ");
	}
      }
      fileOut.println();
    }
    fileOut.flush();
    fileOut.close();
  }

  /*
   * Kind of a test: Load a Board state from a file.
   */
  public void load() throws IOException
  {
    int width  = 0;
    int height = 0;
    File openFile = new File ( fileName );
    BufferedReader fileIn = new BufferedReader(new FileReader(openFile));
	
    String next = fileIn.readLine().trim();
    
    StringTokenizer st = new StringTokenizer(next);
    width  = Integer.parseInt(st.nextToken());
    height = Integer.parseInt(st.nextToken());

    if ( width != size_x || height != size_y )
    {
      System.out.println("Error: saved board size does not match!");
      return;
    }

    //okay - make new board
    System.out.println("New Board: "+width+" by "+height);      
    board.squareList = new Square[width][height];
    
    int line = 1;
    int x = 0;
    int y = -1;
    int state = 0;
    boolean filled = false;

    while ( fileIn.ready() )
    {
      next = fileIn.readLine().trim();
      line++;
      y++;
      st = new StringTokenizer(next);

      if ( st.countTokens() < width )
      {
	System.out.println("Problem on line "+line+": Not enough columns");
	System.out.println(next);
	return;
      }
      
      x = 0;
      while ( st.hasMoreTokens() )
      {  
	filled = false;
	state = Integer.parseInt(st.nextToken());

	if ( state > 4 )
	{
	  filled = true;
	  state -= 4;
	}

	//System.out.println("Square["+x+","+y+"] "+filled);      
	board.squareList[x][y] = new Square("Square["+x+","+y+"]",state);
	
	if ( filled )
	{
	  board.squareList[x][y].fill( new Wall() );
	}
	
	x++;
      }
      
    }

    //Set up all the edges too (for the graph)
    for ( int i = 0 ; i < width ; i++ )
    {
      for ( int j = 0 ; j < height ; j++ )
      {
	Square s = board.squareList[i][j];
	if ( s.isSolid() )
	{
	  continue;
	}

	if ( j > 0 && board.squareList[i][j-1].isPassable() )
	{
	  s.setEdgeNorth(board.squareList[i][j-1]);
	}
 
	if ( i < (width-1) && board.squareList[i+1][j].isPassable() )
	{
	  s.setEdgeEast(board.squareList[i+1][j]);
	}

	if ( j < (height-1) && board.squareList[i][j+1].isPassable() )
	{
	  s.setEdgeSouth(board.squareList[i][j+1]);
	}

	if ( i > 0 && board.squareList[i-1][j].isPassable() )
	{
	  s.setEdgeWest(board.squareList[i-1][j]);
	}
      }
    }

    repaint();
  }
}
