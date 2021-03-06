package anderson.assignment3.battleship.http.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import anderson.assignment3.battleship.http.models.GameSummary;
import anderson.assignment3.battleship.http.models.OnTaskFinishedListener;

/**
 * Created by anderson on 11/2/15.
 */
public class GetGameListTask extends AsyncTask<URL, Void, GameSummary[]> {

    OnTaskFinishedListener<GameSummary[]> onTaskFinishedListener = null;

    private boolean result = false;

    @Override
    protected GameSummary[] doInBackground(URL... params) {
        GameSummary[] games = null;
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
            games = gson.fromJson(response, GameSummary[].class);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return games;
    }

    @Override
    protected void onPostExecute(GameSummary[] games) {
        if(onTaskFinishedListener != null){
            onTaskFinishedListener.onTaskFinished(result, games);
        }
    }

    public void setOnTaskFinishedListener(OnTaskFinishedListener listener){
        onTaskFinishedListener = listener;
    }

}
