import java.awt.Color;

/**
 * A non-gun really. It just fills a square; doesn't shoot
 *
 */

public class Wall extends Gun
{
  public static final int DELAY = -1;

  public Wall( )
  {
    super( DELAY );
  }
    

  public void draw( int x, int y, java.awt.Graphics g )
  {
    Color oldColor = g.getColor();

    g.setColor(new Color(205,225,225));
    g.fillRect( x, y, Square.SIZE, Square.SIZE ); 

    g.setColor(oldColor);
  }
  
}
