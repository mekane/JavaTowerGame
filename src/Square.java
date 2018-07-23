import java.awt.*;
import java.util.Vector;

/**
 * A class to represent a square on the board. Holds state information about
 * the square.
 *
 * The states a Square can have:
 *   -selected: clicked on (actually used for showing goal square)
 *   -highlighted: shows when a square is being hovered over
 *
 *   -nothing: normal background and border. empty.
 *   -has a gun: normal border and gun. can be clicked and highlighted.
 *   -protected: border, no highlight, no click, no build (for edge of board)
 *   -invisible: no border, no highlight, no click, no build
 *
 *
 */
public class Square
{
  //CONSTANTS
  public static final int SIZE = 15;
  
  public  static final Color BACKGROUND_COLOR = new Color(64,64,64);

  private static final Color BORDER_NORMAL    = new Color(128,128,128);
  private static final Color BORDER_CAN_BUILD = Color.green;
  private static final Color BORDER_NO_BUILD  = Color.red;

  public static final int STATE_NORMAL  = 1; //can build
  public static final int STATE_INVIS   = 2; //no build
  public static final int STATE_PROTECT = 3; //no build
  public static final int STATE_UNUSED  = 4;

  //Class Member Variables
  private String name;
  private Color borderColor = BORDER_NORMAL;
  private int state;

  //graph data
  private Square north; //connected vertices
  private Square east;
  private Square south;
  private Square west;


  private Gun gun; //The gun that lives in this square. If null, none
  private Vector<Guy> guyList; //the Guys that are on this Square


  /**
   * Creates a nameless, blank square
   *
   */
  public Square( )
  {
    this( "", STATE_NORMAL);
  }

  /**
   *
   */
  public Square( String newName )
  {
    this(newName, STATE_NORMAL);
  }


  /**
   * Create a new Square
   * @param newName  the name of the Square
   * @param newState the state the Square should have. 
   */
  public Square( String newName, int newState )
  {
    this.name  = newName;
    this.state = newState;
    this.borderColor = BORDER_NORMAL;

    this.gun   = null;
    this.guyList = new Vector<Guy>();
  }

  /**
   * Just draw this square, including border
   * @param x the X coordinate of the Square
   * @param y the Y coordinate of the Square
   * @param g the Graphics Object to draw to
   */
  public void draw( int x, int y, Graphics g )
  {
    draw( x, y, true, g );
  }

  /**
   * Draw this Square with or without border
   * @param x the X coordinate of the Square
   * @param y the Y coordinate of the Square
   * @param border whether or not to draw the border
   * @param g the Graphics Object to draw to
   */
  public void draw( int x, int y, boolean border, Graphics g )
  {
    //System.out.println("Drawing square "+name+" ("+selected+")");
    Color oldColor = g.getColor();

    if ( this.state == STATE_INVIS )// draw normal background
    {
      g.setColor( Color.black );
    }
    else
    {
      g.setColor( BACKGROUND_COLOR );
    }
    g.fillRect( x-1, y-1, SIZE+1, SIZE+1 );
    
    if ( this.gun != null )// draw the gun in this square
    {
      gun.draw( x, y, g );
    }

    /*
    Color c = highlighted ? Color.green : (selected ? Color.red : borderColor);
    */

    if ( (border || borderColor != BORDER_NORMAL) && 
	 !(this.state == STATE_INVIS) && !(this.state == STATE_PROTECT) )
    {
      g.setColor(borderColor);
      g.drawRect( x-1, y-1, SIZE, SIZE );
    }

    g.setColor(oldColor);
  }


  public String getName() { return this.name; }

  public int getState() { return this.state; }

  public void select()   
  { 
    //if ( !this.state == STATE_INVIS && !this.state == STATE_PROTECT )
    //{
      this.borderColor = Color.red; //default for now
    //}
  }

  public void unselect() { this.borderColor = BORDER_NORMAL; }
  public void deselect() { this.borderColor = BORDER_NORMAL; }
  public void toggleSelect()  
  {
    this.borderColor = (borderColor==BORDER_NORMAL?Color.red : BORDER_NORMAL); 
  }


  public void highlight()   
  { 
    if ( state == STATE_INVIS || state == STATE_PROTECT || gun != null )
    {
      borderColor = BORDER_NO_BUILD;
    }
    else
    {
      borderColor = BORDER_CAN_BUILD;
    }
  }

  public void unhighlight() 
  { 
    this.borderColor = BORDER_NORMAL;
  }

  public void dehighlight() 
  { 
    this.borderColor = BORDER_NORMAL;
  }


  /**
   * Determines whether or not this square is solid. Basically, is it taken up
   * by a gun?
   *
   * Note to self: a wall is a gun that doesn't shoot. 
   *
   * @return true if this square is solid (has a Gun)
   */
  public boolean isSolid()
  {
    return (this.gun != null);
  }

  /**
   * Determines whether this Square has a Gun that can shoot. (Not a wall)
   */
  public boolean hasGun()
  {
    return (this.gun != null) && !(gun instanceof Wall);
  }


  /**
   * Determines whether or not this square is passable by Guys. Will always be
   * false if isSolid() is true, but squares can be unpassable for other 
   * reasons (invisible squares, for example).
   * @return true if this square is passable, false otherwise
   */
  public boolean isPassable()
  {
    boolean pass = true;

    if ( this.isSolid() )
    {
      pass = false;
    }

    if ( this.state == STATE_INVIS )
    {
      pass = false;
    }

    return pass;
  }


  /**
   * Fill in this square. Disconnect all the edges from this square to 
   * surrounding ones, and from them to this as well.
   *
   */
  public void fill( Gun newGun )
  {
    this.gun = newGun;

    //sever edges
    if ( north != null )
    {
      north.setEdgeSouth(null);
      //north = null;
    }

    if ( east != null )
    {
      east.setEdgeWest(null);
      //east = null;
    }

    if ( south != null )
    {
      south.setEdgeNorth(null);
      //south = null;
    }

    if ( west != null )
    {
      west.setEdgeEast(null);
      //west = null;
    }
  }


  /**
   * Get the Gun for this Square. (Could be null)
   */
  public Gun getGun()
  {
    return this.gun;
  }

  

  /**
   * Empty this square. Connect it to surrounding squares, and connect them to
   * it.
   */
  public void empty( Square newNorth, 
		     Square newEast, 
		     Square newSouth, 
		     Square newWest )
  {
    this.gun = null;

    //connect edges
    if ( newNorth != null && !newNorth.isSolid() )
    {
      newNorth.setEdgeSouth(this);
      north = newNorth;
    }

    if ( newEast != null && !newEast.isSolid() )
    {
      newEast.setEdgeWest(this);
      east = newEast;
    }

    if ( newSouth != null && !newSouth.isSolid() )
    {
      newSouth.setEdgeNorth(this);
      south = newSouth;
    }

    if ( newWest != null && !newWest.isSolid() )
    {
      newWest.setEdgeEast(this);
      west = newWest;
    }
  }



  /**
   * 
   * 
   */
  public void clearGuys()
  {
    this.guyList.clear();
  }

  
  /**
   * 
   * 
   */
  public void addGuy( Guy theGuy )
  {
    this.guyList.add(theGuy);
  }


  /**
   *
   *
   *
   */
  public Guy getGuy()
  {
    if ( guyList.isEmpty() )
    {
      return null;
    }
    else
    {
      return this.guyList.firstElement();
    }
  }


  /**
   *
   *
   */
  public void removeGuy( Guy theGuy )
  {
    
  }




  /**
   * Connect this square to the given square
   *
   */
  public void setEdgeNorth( Square v )
  {
    this.north = v;
  }
  
  /**
   * Connect this square to the given square
   *
   */
  public void setEdgeEast( Square v )
  {
    this.east = v;
  }
  
  /**
   * Connect this square to the given square
   *
   */
  public void setEdgeSouth( Square v )
  {
    this.south = v;
  }
  
  /**
   * Connect this square to the given square
   *
   */
  public void setEdgeWest( Square v )
  {
    this.west = v;
  }

  ///////

  /**
   * Connect this square to the given square
   *
   */
  public Square getEdgeNorth()
  {
    return this.north;
  }
  
  /**
   * Connect this square to the given square
   *
   */
  public Square getEdgeEast()
  {
    return this.east;
  }
  
  /**
   * Connect this square to the given square
   *
   */
  public Square getEdgeSouth()
  {
    return this.south;
  }
  
  /**
   * Connect this square to the given square
   *
   */
  public Square getEdgeWest()
  {
    return this.west;
  }
 
  /**
   * Return all Squres connected to this one
   */
  public Vector<Square> getNeighbors()
  {
    Vector<Square> result = new Vector<Square>();

    if ( north != null )
    {
      result.add(north);
    }

    if ( east != null )
    {
      result.add(east);
    }

    if ( south != null )
    {
      result.add(south);
    }

    if ( west != null )
    {
      result.add(west);
    }

    return result;
  }
   
}


