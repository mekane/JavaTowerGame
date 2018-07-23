/**
 * This class creates a Thread that updates the game a few times a second
 *
 */
public class UpdateTimer extends java.util.TimerTask
{
  public static final long TIME = 40; //time in milliseconds to wait

  private GameScreen gs;

  /**
   * Create an update timer that will cause the given GameScreen to update
   */
  public UpdateTimer( GameScreen s )
  {
    gs = s;
  }

  public void run()
  {
    gs.updateGame();
  }
}
