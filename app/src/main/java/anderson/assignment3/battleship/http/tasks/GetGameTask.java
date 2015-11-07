package anderson.assignment3.battleship.http.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import anderson.assignment3.battleship.http.models.Game;
import anderson.assignment3.battleship.http.models.OnTaskFinishedListener;

/**
 * Created by anderson on 11/2/15.
 */
public class GetGameTask extends AsyncTask<URL, Void, Game> {

    OnTaskFinishedListener<Game> onTaskFinishedListener = null;

    private boolean result = false;

    @Override
    protected Game doInBackground(URL... params) {
        Game game = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) params[0].openConnection();
            InputStream responseStream = connection.getInputStream();
            Scanner responseScanner = new Scanner(responseStream);
            StringBuilder responseString = new StringBuilder();
            while(responseScanner.hasNext()){
                responseString.append(responseScanner.nextLine());
            }
            String response = responseString.toString();

            Gson gson = new Gson();
            game = gson.fromJson(response, Game.class);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return game;
    }

    @Override
    protected void onPostExecute(Game game) {
        if(onTaskFinishedListener != null){
            onTaskFinishedListener.onTaskFinished(result, game);
        }
    }

    public void setOnTaskFinishedListener(OnTaskFinishedListener listener){
        onTaskFinishedListener = listener;
    }

}
