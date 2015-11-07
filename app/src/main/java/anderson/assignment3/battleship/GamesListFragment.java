package anderson.assignment3.battleship;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;

import anderson.assignment3.battleship.http.models.GameSummary;

/**
 * Created by anderson on 10/26/15.
 */
public class GamesListFragment extends Fragment implements ListAdapter{
    public interface OnNewGameButtonPressedListener {
        void onNewGameButtonPressed(String gameName, String playerName);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private static final String GAMES_LIST_KEY = "GAMES_LIST_KEY";
    private OnNewGameButtonPressedListener onNewGameButtonPressedListener = null;
    private OnItemSelectedListener onItemSelectedListener = null;
    private GameSummary[] games;
    private String currentGameId;
    private ListView listView;

    public static GamesListFragment newInstance(GameSummary[] games){
        GamesListFragment fragment = new GamesListFragment();

        if(games != null) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(GAMES_LIST_KEY, (Serializable) games);
            fragment.setArguments(arguments);
        }

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootLayout = new LinearLayout(getActivity());
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        Button newGameButton = new Button(getActivity());
        newGameButton.setText("Create new game");
        newGameButton.setTextSize(5 * getResources().getDisplayMetrics().density);
        rootLayout.addView(newGameButton);

        games = new GameSummary[0];
        if(getArguments() != null && getArguments().containsKey(GAMES_LIST_KEY)){
            games = (GameSummary[]) getArguments().getSerializable(GAMES_LIST_KEY);
        }

        listView = new ListView(getActivity());
        listView.setAdapter(this);
        rootLayout.addView(listView);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create a new game:");

                LinearLayout inputsView = new LinearLayout(getActivity());
                inputsView.setOrientation(LinearLayout.VERTICAL);

                final EditText gameNameInput = new EditText(getActivity());
                gameNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                gameNameInput.setHint("Game's name");
                inputsView.addView(gameNameInput);

                final EditText playerNameInput = new EditText(getActivity());
                playerNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                playerNameInput.setHint("Player's name");
                inputsView.addView(playerNameInput);

                builder.setView(inputsView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onNewGameButtonPressedListener != null) {
                            onNewGameButtonPressedListener.onNewGameButtonPressed(gameNameInput.getText().toString(), playerNameInput.getText().toString());
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(games != null && currentGameId != null && games[position].id.equals(currentGameId)){
                    return;
                }

                if(onItemSelectedListener != null){
                    onItemSelectedListener.onItemSelected(position);
                }
            }
        });

        return rootLayout;
    }

    public void setGamesList(GameSummary[] games){
        if(games != null) {
            this.games = games;
            listView.invalidateViews();
        }
    }

    public void setCurrentGame(String gameId){
        if(gameId != null && !gameId.isEmpty()){
            currentGameId = gameId;
        }
    }

    public void setOnNewGameButtonPressedListener(OnNewGameButtonPressedListener listener){
        onNewGameButtonPressedListener = listener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener){
        onItemSelectedListener = listener;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !games[position].status.equals(GameSummary.DONE);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return games.length;
    }

    @Override
    public Object getItem(int position) {
        return games[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(getActivity());

        GameSummary game = games[position];

        String text = game.name + "\n";
        text += "Status: " + game.status;

        textView.setText(text);
        textView.setTextSize(4 * getResources().getDisplayMetrics().density);

        if(currentGameId != null && game.id.equals(currentGameId)){
            textView.setBackgroundColor(Color.DKGRAY);
        }

        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }
}
