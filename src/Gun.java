import java.awt.*;
import java.util.Vector;
/**
 * The super-class for all other Guns (and any other stationary objects that 
 * take up squares) to extend.
 *
 */

public abstract class Gun
{
  protected int fireDelay;   //How many ticks pass between each fire?
  protected int ticksUntilFire; //Countdown until next fire
  
  protected int turretAngle;

  protected Guy target;
  
  //final int DELAY;

  protected Gun( int newDelay )
  {
    this.fireDelay = newDelay;
    this.ticksUntilFire = 0;
    this.target = null;

    this.turretAngle = 0; //this could be random-ish
  }

  public abstract void draw( int x, int y, Graphics g );



  /**
   * Find the best target for this Gun
   *
   * @param position the Square that this Gun is on
   */
  public Guy findTarget( Square position )
  {
    if ( this.target != null )
    {
      return null;
    }
    /*
     * To find a target for this gun, we'll first search the Squares that
     * border it. There are 8:
     *
     * [x-1,y-1]  [x,y-1]  [x+1,y-1]
     * 
     * [x-1,y  ]  [     ]  [x+1,y  ]
     *
     * [x-1,y+1]  [x,y+1]  [x+1,y+1]
     *
     * Ideally, I would like to start with the square that is the closest to
     * to direction that the turrent is currently pointed
     */


    Vector<Square> list = position.getNeighbors();
    Guy g = null;

    for ( Square s : list )
    {
      g = s.getGuy();
      
      if ( g != null )
      {
	this.target = g;
	return g;
      }
    }

    return null;
  }


  public Guy getTarget()
  {
    return this.target;
  }


  public void clearTarget()
  {
    this.target = null;
  }

  /**
   * Tell this Gun to move its turret towards its target
   */
  public void updateAim()
  {
    //need a notion of turret speed
  }



  public int getTurretAngle()
  {
    return this.turretAngle;
  }

  /**
   *
   */
  public void rotateTurret( int delta )
  {
    this.turretAngle += delta;
  }
  
  
}
