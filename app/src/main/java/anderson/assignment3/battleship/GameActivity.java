package anderson.assignment3.battleship;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import anderson.assignment3.battleship.http.models.GameSummary;
import anderson.assignment3.battleship.models.OnGameUpdatedListeners;

public class GameActivity extends Activity implements OnGameUpdatedListeners {
    private String GAME_FRAGMENT_TAG = "GAME_FRAGMENT_TAG";
    private String GAMES_LIST_FRAGMENT_TAG = "GAMES_LIST_FRAGMENT_TAG";
    private String FILE_NAME = "games.txt";

    private GameController controller;
    private ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        controller = GameController.getInstance(this, this);

        //loadGames();

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(rootLayout);

        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);


        FrameLayout masterFrameLayout = new FrameLayout(this);
        masterFrameLayout.setId(10);
        //if(isTablet(this)) {
            horizontalLayout.addView(masterFrameLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        //}

        FrameLayout detailFrameLayout = new FrameLayout(this);
        detailFrameLayout.setId(11);
        horizontalLayout.addView(detailFrameLayout, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 4));

        rootLayout.addView(horizontalLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        GamesListFragment gamesListFragment = (GamesListFragment) getFragmentManager().findFragmentByTag(GAMES_LIST_FRAGMENT_TAG);
        if (gamesListFragment == null) {
            gamesListFragment = GamesListFragment.newInstance(controller.getGames());
            //
            transaction.add(masterFrameLayout.getId(), gamesListFragment, GAMES_LIST_FRAGMENT_TAG);
        }

        GameFragment gameFragment = (GameFragment)getFragmentManager().findFragmentByTag(GAME_FRAGMENT_TAG);
        if(gameFragment == null){
            gameFragment = GameFragment.newInstance(null, null);
            transaction.add(detailFrameLayout.getId(), gameFragment, GAME_FRAGMENT_TAG);
        }

        transaction.commit();

        spinner = new ProgressDialog(this);
        spinner.setTitle("Loading");
        spinner.setMessage("Retrieving data from the server");
        spinner.setCancelable(false);

        try {
            controller.updateGames();
        } catch (Exception e) {
            Log.i("Update Games Exception", e.getMessage());
        }

        gameFragment.setOnSpaceSelectedListener(new GameFragment.OnSpaceSelectedListener() {
            @Override
            public void onSpaceSelected(int[] points) {
                if (!controller.isPlayersTurn()) {
                    return;
                }

                final GameFragment gameFragment = (GameFragment) getFragmentManager().findFragmentByTag(GAME_FRAGMENT_TAG);

                int x = points[0];
                int y = points[1];

                /*if (!controller.isCurrentGameOver() && controller.attackSpace(x, y)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle(controller.getCurrentPlayerName() + "'s turn!");
                    builder.setMessage("Give the device to your enemy.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gameFragment.setPlayerGrids(controller.getCurrentPlayerGrid(), controller.getCurrentEnemyGrid());
                        }
                    });
                    builder.show();

                } else {
                    Toast.makeText(GameActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                }*/
                /*if (controller.isCurrentGameOver()) {
                    new AlertDialog.Builder(GameActivity.this)
                            .setTitle("Game Over!")
                            .setMessage("The winner is " + controller.getCurrentGame().getWinnerName())
                            .setPositiveButton("Ok", null)
                            .show();
                    saveGames();
                    return;
                }*/

                //saveGames();

                GamesListFragment gamesListFragment = (GamesListFragment) getFragmentManager().findFragmentByTag(GAMES_LIST_FRAGMENT_TAG);
                gamesListFragment.setGamesList(controller.getGames());
            }
        });

        gamesListFragment.setOnNewGameButtonPressedListener(new GamesListFragment.OnNewGameButtonPressedListener() {
            @Override
            public void onNewGameButtonPressed(String gameName, String playerName) {
                if(gameName == null || playerName == null || gameName.isEmpty() || playerName.isEmpty()){
                    Toast.makeText(GameActivity.this, "Please, input a valid game name and player name.", Toast.LENGTH_LONG).show();
                    return;
                }

                try{
                    spinner.show();
                    controller.createNewGame(gameName, playerName);
                } catch(Exception e) {
                    Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        gamesListFragment.setOnItemSelectedListener(new GamesListFragment.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                GamesListFragment gamesListFragment = (GamesListFragment) getFragmentManager().findFragmentByTag(GAMES_LIST_FRAGMENT_TAG);
                GameFragment gameFragment = (GameFragment) getFragmentManager().findFragmentByTag(GAME_FRAGMENT_TAG);

                //controller.setCurrentGame(position);

                /*gamesListFragment.setGamesList(controller.getGamesList());
                gamesListFragment.setCurrentGame(controller.getCurrentGame());
                gameFragment.setPlayerGrids(controller.getCurrentPlayerGrid(), controller.getCurrentEnemyGrid());

                Toast.makeText(GameActivity.this, controller.getCurrentPlayerName() + "'s turn", Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        //saveGames();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPlayerGridsUpdated(boolean result, int[][] playerGrid, int[][] enemyGrid) {
        GamesListFragment gamesListFragment = (GamesListFragment) getFragmentManager().findFragmentByTag(GAMES_LIST_FRAGMENT_TAG);
        GameFragment gameFragment = (GameFragment) getFragmentManager().findFragmentByTag(GAME_FRAGMENT_TAG);

        if(result) {
            gameFragment.setPlayerGrids(playerGrid, enemyGrid);

            try {
                controller.updateGames();
            } catch (Exception e) {
                Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(GameActivity.this, "Some error occurred while getting the grids.", Toast.LENGTH_LONG).show();
        }

        spinner.dismiss();
    }

    @Override
    public void onGameListUpdated(boolean result, GameSummary[] games) {
        GamesListFragment gamesListFragment = (GamesListFragment) getFragmentManager().findFragmentByTag(GAMES_LIST_FRAGMENT_TAG);

        if(result){
            gamesListFragment.setGamesList(games);
        } else {
            Toast.makeText(GameActivity.this, "Some error occurred while getting games.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNewGameCreated(boolean result, String gameId) {
        GamesListFragment gamesListFragment = (GamesListFragment) getFragmentManager().findFragmentByTag(GAMES_LIST_FRAGMENT_TAG);

        gamesListFragment.setCurrentGame(gameId);

        try{
            controller.updateGames();
            //TODO: Update grids only when the game is started (when someone joins)
            //controller.updatePlayersBoard();
        } catch (Exception e){
            Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        spinner.dismiss();
    }

    public void saveGames(){
        try {
            //controller.saveGames(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void loadGames(){
        try {
            //controller.loadGames(new File(getFilesDir(), FILE_NAME).getPath());
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
