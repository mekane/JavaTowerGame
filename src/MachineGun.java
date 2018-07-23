import java.awt.*;

/**
 * Your basic machine gun
 *
 */

public class MachineGun extends Gun
{
  public static final int DELAY = 5;

  public MachineGun( )
  {
    super( DELAY );
  }
    
  
  public void draw( int x, int y, java.awt.Graphics g )
  {
    Color oldColor = g.getColor();
    
    g.setColor(new Color(105,125,125));
    g.fillRect( x, y, Square.SIZE, Square.SIZE ); 
    
    g.setColor( Color.black );
    g.drawOval( x+1, y+1, Square.SIZE-3, Square.SIZE-3 ); 

    int r = Square.SIZE / 2;
    int cx = x + r;
    int cy = y + r;

    int tx = cx+(int)(Math.cos(turretAngle) * r);
    int ty = cy+(int)(Math.sin(turretAngle) * r);

    g.drawLine( cx, cy, tx, ty );

    g.setColor(oldColor);
  }
  
}
