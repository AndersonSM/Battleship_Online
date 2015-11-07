package anderson.assignment3.battleship.models;

import anderson.assignment3.battleship.http.models.GameSummary;

/**
 * Created by anderson on 11/4/15.
 */
public interface OnGameUpdatedListeners {
    void onGameListUpdated(boolean result, GameSummary[] games);

    void onPlayerGridsUpdated(boolean result, int[][] playerGrid, int[][] enemyGrid);

    void onNewGameCreated(boolean result, String gameId);
}
