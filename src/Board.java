import java.util.*;
import java.awt.Point;

/**
 * Encapsulates a game board. Holds a 2-dimensional array of Square objects,
 * which make up the playing field. 
 *
 * Also stores a representation of the board as a square graph with each Square
 * as a vertex with edges to the surrounding squares. 
 * Keeps track of finding shortest paths, etc.
 */

public class Board
{
  private int width;
  private int height;
  
  public Square[][] squareList;

  /**
   * Initialize space for a board with the given height and width. 
   * Note that the vertices and squares still need to be set up individually.
   * 
   * Use factory method(s) for default rectangular boards
   */
  public Board( int newWidth, int newHeight )
  {
    this.width  = newWidth;
    this.height = newHeight;
    squareList = new Square[width][height];

    //init squares
    for ( int i = 0 ; i < width ; i++ )
    {
      for ( int j = 0 ; j < height ; j++ )
      {
	squareList[i][j] = new Square("Square["+i+","+j+"]");
      }
    }

    //set up wall (could be moved to a factory method for default board)
    int h1 = height-1;
    int h2 = height/2;
    int w1 = width-1;
    int w2 = width/2;

    for ( int i = 0 ; i < width ; i++ )
    {
      squareList[i][0] = new Square("Empty",Square.STATE_INVIS);
      squareList[i][h1] = new Square("Empty",Square.STATE_INVIS);

      //The Walls horizontally along the top and bottom
      squareList[i][1] = new Square("The Wall",Square.STATE_PROTECT);
      squareList[i][1].fill( new Wall() );
      squareList[i][h1-1] = new Square("The Wall",Square.STATE_PROTECT);
      squareList[i][h1-1].fill( new Wall() );
    }

    for ( int j = 0 ; j < height ; j++ )
    {
      if ( j >= (h2-3) && j < (h2+3) )
      {
	squareList[0][j] = new Square("Safe",Square.STATE_PROTECT);
	squareList[1][j] = new Square("Safe",Square.STATE_PROTECT);
	squareList[w1-1][j] = new Square("Safe",Square.STATE_PROTECT);
	squareList[w1][j] = new Square("Safe",Square.STATE_PROTECT);
      }
      else
      {
	squareList[0][j] = new Square("Empty",Square.STATE_INVIS);
	squareList[w1][j] = new Square("Empty",Square.STATE_INVIS);
	if ( j > 0 && j < h1)
	{
	  //The Side Walls
	  squareList[1][j] = new Square("The Wall",Square.STATE_PROTECT);
	  squareList[1][j].fill( new Wall() );
	  squareList[w1-1][j] = new Square("The Wall",Square.STATE_PROTECT);
	  squareList[w1-1][j].fill( new Wall() );
	}
      }
    }

    //Set up all the edges too (for the graph)
    for ( int i = 0 ; i < width ; i++ )
    {
      for ( int j = 0 ; j < height ; j++ )
      {
	//System.out.println("Square["+i+","+j+"]");
	Square s = squareList[i][j];
	if ( s.isSolid() )
	{
	  continue;
	}

	if ( j > 0 && squareList[i][j-1].isPassable() )
	{
	  //System.out.println("  north -> ["+i+","+(j-1)+"]");
	  s.setEdgeNorth(squareList[i][j-1]);
	}
 
	if ( i < (width-1) && squareList[i+1][j].isPassable() )
	{
	  //System.out.println("  east -> ["+(i+1)+","+j+"]");
	  s.setEdgeEast(squareList[i+1][j]);
	}

	if ( j < (height-1) && squareList[i][j+1].isPassable() )
	{
	  //System.out.println("  south -> ["+i+","+(j+1)+"]");	
	  s.setEdgeSouth(squareList[i][j+1]);
	}

	if ( i > 0 && squareList[i-1][j].isPassable() )
	{
	  //System.out.println("  west -> ["+(i-1)+","+j+"]");
	  s.setEdgeWest(squareList[i-1][j]);
	}
      }
    }
  }


  /**
   * Get the Square object for the given space on the board.
   * @return the Square object, or null if there is no square or the position
   *         is out of bounds
   */
  public Square getSquare( int x, int y )
  {
    if ( x < 0 || x >= width || y < 0 || y >= height )
    {
      return null;
    }

    return squareList[x][y];
  }



  /**
   * Take a pair of coordinates from a mouseclick and returns a Point object
   * containing the indices into the squareList of that square
   */
  //protected
  public Point getSquareIndexFromCoordinates( int x, int y )
  {
    int sX = x / (Square.SIZE+GameScreen.SPACE) - 1;
    int sY = y / (Square.SIZE+GameScreen.SPACE) - 1;
    
    return new Point(sX,sY);
  }

  /**
   * Takes a pair of coordinates from a mouseclick and returns the Square
   * object that is inhabiting that space.
   */
  public Square getSquareAt( int x, int y )
  {
    int sX = x / (Square.SIZE+GameScreen.SPACE) - 1;
    int sY = y / (Square.SIZE+GameScreen.SPACE) - 1;

    if ( sX < 0 || sX >= width || sY < 0 || sY >= height )
    {
      return null;
    }

    return squareList[sX][sY];
  }


  /**
   *
   */
  public Point getSquareCoordinates( Square square )
  {
    Point result = new Point(-1,-1);
    
    for ( int i = 0 ; i < width ; i++ )
    {
      for ( int j = 0 ; j < height ; j++ )
      {
	if ( squareList[i][j] == square )
	{
	  int sX = (i+1)*(Square.SIZE+GameScreen.SPACE) + Square.SIZE/2;
	  int sY = (j+1)*(Square.SIZE+GameScreen.SPACE) + Square.SIZE/2;
	  return new Point( sX, sY );
	}
      }
    }
    return result;
  }

  /**
   *
   */
  public void fillSquare( Square square )
  {
    /*
      Point p = getSquareCoordinates(square);

      getSquareIndexFromCoordinates( int x, int y )

      fillSquare( (int)p.getX(), (int)p.getY() );
    */
    square.fill(new Wall());
  }


  /**
   * Test thingy - just put in a machine gun
   * TODO: add other options
   */
  public Gun addGun( Square square )
  {
    Gun g = new MachineGun();
    square.fill(g);
    return g;
  }


  /**
   * Put a wall in this Square
   */
  public void fillSquare( int x, int y )
  {
    if ( x < 0 || x >= width || y < 0 || y >= height )
    {
      return;
    }
    squareList[x][y].fill(new Wall());
  }


  /**
   * Make sure there's no wall in the given Square
   */
  public void emptySquare( int x, int y )
  {
    if ( x < 0 || x >= width || y < 0 || y >= height )
    {
      return;
    }

    Square n = (y > 0 ? squareList[x][y-1] : null );
    Square e = (x < ( width-1) ? squareList[x+1][y] : null );
    Square s = (y < (height-1) ? squareList[x][y+1] : null );
    Square w = (x > 0 ? squareList[x-1][y] : null );

    squareList[x][y].empty( n, e, s, w );
  }


  /**
   * Make sure there's no wall in the given Square
   */
  public void emptySquare( Square square )
  {
    Point p = getSquareCoordinates(square);

    p = getSquareIndexFromCoordinates( (int)p.getX(), (int)p.getY() );

    emptySquare( (int)p.getX(), (int)p.getY() );
  }


  /**
   * Get a path from a starting Square to another Square. It should be (one of)
   * the shortest path(s).
   *
   */
  public Vector<Square> getShortestPath( Square start, Square goal )
  {
    Map<Square,Integer> d = new HashMap<Square,Integer>();
    Map<Square,Square>  p = new HashMap<Square,Square>();

    d.put(start,0);
    //assume that all the ones that aren't there are infinity

    Set<Square> visited = new HashSet<Square>();
    Set<Square>       Q = new HashSet<Square>(); //unvisited

    Q.add(start);

    Square u = null;
    int min_dist = 99999999;

    while ( !Q.isEmpty() )
    {
      min_dist = 99999999;

      //get next closest vertex to start (u)
      for ( Square s : Q )
      {
	if ( d.get(s) < min_dist )
	{
	  min_dist = d.get(s);
	  u = s;
	}
      }

      if ( u == goal || u == null )
      {
	break;
      }

      Q.remove(u);
      visited.add(u);
      
      Vector<Square> list = u.getNeighbors();
      
      for ( Square v : list )
      {
	if ( visited.contains(v) )
	{
	  continue;
	}

	if ( d.get(v) == null || (d.get(u)+1) < d.get(v) )
	{
	  d.put(v,d.get(u)+1);
	  p.put(v,u);
	  Q.add(v);
	}
      }
    }

    Vector<Square> path = new Vector<Square>();
    u = goal;
    while ( p.containsKey(u) )
    {
      path.add(u);
      u = p.get(u);
    }

    Collections.reverse(path);

    return path;
  }//end - get shortest path
  

  /**
   *
   */
  public void clearAllHighlights()
  {
    for ( int i = 0 ; i < width ; i++ )
    {
      for ( int j = 0 ; j < height ; j++ )
      {
	squareList[i][j].unhighlight();
      }
    }
  }


}


