import java.awt.*;

/**
 * Track a single bullet. This is the generic version. Other classes can 
 * subclass this for different bullet types.
 *
 */
public abstract class Bullet
{
  protected Gun source;
  protected Guy target;

  protected int posX;
  protected int posY;

  protected Bullet( Gun newSource, Guy newTarget )
  {
    this.source = newSource;
    this.target = newTarget;

    this.posX = 0; //these should come from source
    this.posY = 0;
  }

  public abstract void draw( Graphics g );

}

