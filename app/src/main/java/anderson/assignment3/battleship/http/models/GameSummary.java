package anderson.assignment3.battleship.http.models;

/**
 * Created by anderson on 11/2/15.
 */
public class GameSummary {
    public static final String DONE = "DONE";
    public static final String WAITING = "WAITING";
    public static final String PLAYING = "PLAYING";

    public String id, name, status;

    public GameSummary(){}
}
