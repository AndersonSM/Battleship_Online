package anderson.assignment3.battleship;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import anderson.assignment3.battleship.http.models.Game;
import anderson.assignment3.battleship.http.models.GameSummary;
import anderson.assignment3.battleship.http.models.OnTaskFinishedListener;
import anderson.assignment3.battleship.http.models.Player;
import anderson.assignment3.battleship.http.tasks.CreateNewGameTask;
import anderson.assignment3.battleship.http.tasks.GetGameListTask;
import anderson.assignment3.battleship.http.tasks.GetGameTask;
import anderson.assignment3.battleship.http.tasks.GetPlayerStatusTask;
import anderson.assignment3.battleship.http.tasks.GetPlayersBoardTask;
import anderson.assignment3.battleship.http.tasks.GuessTask;
import anderson.assignment3.battleship.http.tasks.JoinGameTask;
import anderson.assignment3.battleship.models.OnGameUpdatedListeners;

/**
 * Created by anderson on 10/25/15.
 */
public class GameController {
    // When the value of the space is 0, it means that the space is empty
    public static final int EMPTY = 0;
    // When the value of the space is 1, it means that the space has a ship
    public static final int SHIP = 1;
    // When the value of the space is 2, it means that a ship was hit
    public static final int HIT = 2;
    // When the value of the space is 3, it means that the space was empty and was attacked
    public static final int MISS = 3;

    private final String ID_URL = ":id";
    private final String IN_PROGRESS = "IN PROGRESS";

    private static OnGameUpdatedListeners onGameUpdatedListeners;

    private Context context;
    private static GameController Instance = null;
    private String currentPlayerId = null;
    private String currentGameId = null;
    private String currentGameWinner;
    private int[][] currentPlayerGrid;
    private int[][] currentEnemyGrid;
    private static GameSummary[] games;
    private boolean isPlayersTurn = false;

    public GameController(Context context){
        this.context = context;
    }

    public static GameController getInstance(Context context, OnGameUpdatedListeners listener){
        if(Instance == null ){
            Instance = new GameController(context);
            games = new GameSummary[0];
        }

        if(onGameUpdatedListeners == null) {
            onGameUpdatedListeners = listener;
        }

        return Instance;
    }

    public GameSummary[] getGames(){
        return games.clone();
    }

    public boolean isPlayersTurn() {
        return isPlayersTurn;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public String getCurrentGameWinner() {
        return currentGameWinner;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setIsPlayersTurn(boolean isPlayersTurn) {
        this.isPlayersTurn = isPlayersTurn;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

    public void setCurrentGameWinner(String currentGameWinner) {
        this.currentGameWinner = currentGameWinner;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public int[][] getCurrentPlayerGrid() {
        return currentPlayerGrid;
    }

    public void setCurrentPlayerGrid(int[][] currentPlayerGrid) {
        this.currentPlayerGrid = currentPlayerGrid;
    }

    public int[][] getCurrentEnemyGrid() {
        return currentEnemyGrid;
    }

    public void setCurrentEnemyGrid(int[][] currentEnemyGrid) {
        this.currentEnemyGrid = currentEnemyGrid;
    }

    public void updateGames() throws ExecutionException, InterruptedException, MalformedURLException {
        String urlStr = context.getResources().getString(R.string.GetGameListUrl);
        URL url = new URL(urlStr);

        GetGameListTask task = (GetGameListTask) new GetGameListTask().execute(url);
        task.setOnTaskFinishedListener(new OnTaskFinishedListener<GameSummary[]>() {
            public void onTaskFinished(boolean result, GameSummary[] gameList) {
                games = gameList;

                if(onGameUpdatedListeners != null){
                    onGameUpdatedListeners.onGameListUpdated(result, gameList);
                }
            }
        });
    }

    public Game getGame(String id) throws ExecutionException, InterruptedException, MalformedURLException {
        String urlStr = context.getResources().getString(R.string.GetGameUrl).replace(ID_URL, id);
        URL url = new URL(urlStr);

        GetGameTask task = (GetGameTask) new GetGameTask().execute(url);
        Game game = task.get();

        return game;
    }

    public Player joinGame(String id, String playerName) throws ExecutionException, InterruptedException, MalformedURLException {
        String urlStr = context.getResources().getString(R.string.JoinGameUrl).replace(ID_URL, id);

        JoinGameTask task = (JoinGameTask) new JoinGameTask().execute(urlStr, playerName);
        Player player = task.get();

        return player;
    }

    public void createNewGame(String gameName, String playerName) throws ExecutionException, InterruptedException, MalformedURLException {
        String urlStr = context.getResources().getString(R.string.CreateNewGameUrl);

        CreateNewGameTask task = (CreateNewGameTask) new CreateNewGameTask().execute(urlStr, gameName, playerName);
        task.setOnTaskFinishedListener(new OnTaskFinishedListener<CreateNewGameTask.NewGameResponse>() {
            public void onTaskFinished(boolean result, CreateNewGameTask.NewGameResponse response) {
                if(result) {
                    currentGameId = response.gameId;
                    currentPlayerId = response.playerId;
                }

                if(onGameUpdatedListeners != null){
                    onGameUpdatedListeners.onNewGameCreated(result, currentGameId);
                }
            }
        });
    }

    public void attackSpace(int x, int y) throws ExecutionException, InterruptedException, MalformedURLException, Exception {
        if(x > 9 || x < 0 || y > 9 || y < 0){
            throw new Exception("Invalid coordinates.");
        }

        String urlStr = context.getResources().getString(R.string.GuessUrl).replace(ID_URL, currentGameId);

        GuessTask task = (GuessTask) new GuessTask().execute(urlStr, currentPlayerId, Integer.toString(x), Integer.toString(y));
        GuessTask.GuessResponse response = task.get();
    }

    public boolean getPlayerStatus() throws ExecutionException, InterruptedException, MalformedURLException {
        String urlStr = context.getResources().getString(R.string.GetPlayerStatusUrl).replace(ID_URL, currentGameId);

        GetPlayerStatusTask task = (GetPlayerStatusTask) new GetPlayerStatusTask().execute(urlStr, currentPlayerId);
        GetPlayerStatusTask.StatusResponse response = task.get();

        currentGameWinner = response.winner;

        return response.isYourTurn;
    }

    public void updatePlayersBoard() throws ExecutionException, InterruptedException, MalformedURLException {
        String urlStr = context.getResources().getString(R.string.GetPlayersBoardUrl).replace(ID_URL, currentGameId);

        GetPlayersBoardTask task = (GetPlayersBoardTask) new GetPlayersBoardTask().execute(urlStr, currentPlayerId);
        task.setOnTaskFinishedListener(new OnTaskFinishedListener<GetPlayersBoardTask.BoardsResponse>() {
            @Override
            public void onTaskFinished(boolean result, GetPlayersBoardTask.BoardsResponse response) {
                if(result) {
                    currentPlayerGrid = convertBoard(response.playerBoard);
                    currentEnemyGrid = convertBoard(response.opponentBoard);
                }

                if (onGameUpdatedListeners != null) {
                    onGameUpdatedListeners.onPlayerGridsUpdated(result, currentPlayerGrid, currentEnemyGrid);
                }
            }
        });
    }

    private int[][] convertBoard(GetPlayersBoardTask.BoardSpace[] board){
        int[][] result = new int[10][10];
        for (GetPlayersBoardTask.BoardSpace space : board) {
            if(space.status.equals(space.HIT)){
                result[space.xPos][space.yPos] = HIT;
            } else if(space.status.equals(space.MISS)){
                result[space.xPos][space.yPos] = MISS;
            } else if(space.status.equals(space.SHIP)){
                result[space.xPos][space.yPos] = SHIP;
            } else if(space.status.equals(space.NONE)){
                result[space.xPos][space.yPos] = EMPTY;
            }
        }

        return result;
    }
}