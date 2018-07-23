import java.awt.*;
import java.util.Vector;

/**
 * Class representing one 'Guy' in the game
 *
 *
 */
public class Guy
{
  private String name;
  private int x; //position
  private int y;

  private int radius;

  private int speed;

  private int goalX; //Determines where the Guy will try to move towards
  private int goalY;

  public Vector<Square> path = new Vector<Square>();/*TEST - save path*/

  private boolean stuck = false; //true if not path to goal
  private boolean done  = false; //true if already reached goal
  private int doneTimer; //upon reaching goal, dissapear after a while

  private static int MAX_TIMER = 30;
  //other info

  //image
  //conditions

  /**
   * creates a default Guy, with no goal. (He'll just sit there)
   */
  public Guy( String newName, int posX, int posY, int size )
  {
    this( newName, posX, posY, size, posX, posY );
  }


  /**
   * creates a Guy.
   */
  public Guy( String newName, int posX, int posY, int size, int gX, int gY )
  {
    this.name = newName;
    this.x = posX;
    this.y = posY;
    this.radius = size;
    this.speed = 1;

    this.goalX = gX;
    this.goalY = gY;

    this.stuck = false;
    this.done  = false;
  }

  public void draw( Graphics g )
  {
    Color oldColor = g.getColor();

    g.setColor(Color.green);

    if ( stuck )
    {
      g.setColor(Color.orange);
    }
    else if ( done )
    {
      float c = ((float)doneTimer / MAX_TIMER);
      g.setColor( new Color(c,c,c,c ) );
    }

    g.fillOval( x-radius, y-radius, radius*2, radius*2);

    g.setColor(oldColor);
  }


  public void update( Board board )
  {
    stuck = false;
    /*
    int dx = (goalX - x); //distance in x and y directions
    int dy = (goalY - y);

    double distanceFromGoal = Math.sqrt(dx*dx+dy*dy);

    int sx = (dx >= 0 ? 1 : -1); //the direction to move towards the goal
    int sy = (dy >= 0 ? 1 : -1);

    sx = (dx >= radius ? sx : 0 ); //if within radius, stop moving
    sy = (dy >= radius ? sy : 0 );
    */

    Square current = board.getSquareAt(x,y);
    Square    goal = board.getSquareAt(goalX,goalY);

    Square moveTo;
    if ( current == goal )
    {
      moveTo = goal;
      if (!done)
      {
	done = true;
	doneTimer = MAX_TIMER;
      }
      else
      {
	doneTimer = (doneTimer > 0 ? doneTimer-1 : 0);
      }
    }
    else
    {
      path = board.getShortestPath( current, goal );

      if ( path.isEmpty() )
      {
	stuck = true;
	return;
      }
      else
      {
	moveTo = path.firstElement();
      }
    }

    if ( moveTo == null )
    {
      stuck = true;
      return;
    }

    /*
    moveTo.select();
    current.unselect();
    */
    goal.select();

    Point movePoint = board.getSquareCoordinates( moveTo );

    int moveX = (int)movePoint.getX();
    int moveY = (int)movePoint.getY();
    int dx = moveX - x;
    int dy = moveY - y;

    int sx = (dx >= 0 ? 1 : -1); //the direction to move towards the goal
    int sy = (dy >= 0 ? 1 : -1);
    sx = (Math.abs(dx) >= 1 ? sx : 0 ); //if within radius, stop moving
    sy = (Math.abs(dy) >= 1 ? sy : 0 );


    //show info
    /*
    System.out.println("--------------------");
    System.out.println("Guy: "+name+" update:");
    System.out.println("  Position: ("+x+","+y+") = "+current.getName());
    System.out.println("  Move To: ("+moveX+","+moveY+") = "+moveTo.getName());
    System.out.println("  Distance:  ("+dx+","+dy+")");
    System.out.println("  Moving: ("+(speed*sx)+","+(speed*sy)+")");
    System.out.println("--------------------");
    */

    this.x += speed*sx;
    this.y += speed*sy;
  }


  public Point getPosition()
  {
    return new Point(x,y);
  }


  public String getName()
  {
    return this.name;
  }


  public boolean isDone()
  {
    return ( this.done && doneTimer <= 0 );
  }
}
