import java.awt.*;
/**
 * A small Panel that displays information about a selected object
 *
 *
 */

public class DisplayPanel extends Canvas
{
  private boolean showDisplay;
  private Guy display; 

  /**
   * Create a new DisplayPanel showing nothing
   *
   */
  public DisplayPanel()
  {
    this( new Guy("",-99,-99,0), false );
  }


  /**
   * Create a new DisplayPanel showing the given guy
   *
   */
  public DisplayPanel( Guy theGuy )
  {
    this( theGuy, true );
  }



  /**
   * Create a new DisplayPanel showing the given guy
   *
   */
  public DisplayPanel( Guy theGuy, boolean show )
  {
    this.display = theGuy;
    this.showDisplay = show;

    Dimension minDim = new Dimension( 200, 200 );
    
    this.setMinimumSize( minDim );
    this.setPreferredSize( minDim );
    
    this.setVisible(true);
  }


  
  public void paint( Graphics g )
  {
    Color oldColor = g.getColor();

    int w = this.getWidth()-1;
    int h = this.getHeight()-1;

    g.setColor( Color.black );
    g.fillRect(0,0, w, h);

    g.setColor(Color.white);
    g.drawRect(0,0, w, h);

    if ( this.showDisplay == false || this.display == null )
    {
      return;
    }
    

    


    g.setColor(oldColor);
  }



  public void setDisplay( Guy newDisplay )
  {
    this.display = newDisplay;
    this.showDisplay = true;
    repaint();
  }

  public void clearDisplay()
  {
    //this.display = null;
    this.showDisplay = false;
    repaint();
  }

}

