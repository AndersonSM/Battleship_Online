package anderson.assignment3.battleship.http.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import anderson.assignment3.battleship.http.models.OnTaskFinishedListener;
import anderson.assignment3.battleship.http.models.Player;

/**
 * Created by anderson on 11/2/15.
 */
public class JoinGameTask extends AsyncTask<String, Void, Player> {

    OnTaskFinishedListener<Player> onTaskFinishedListener = null;

    private boolean result = false;

    @Override
    protected Player doInBackground(String... params) {
        Player player = null;
        String playerName = params[1];
        String requestStr = "{\"playerName\": \"" + playerName + "\"}";

        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStreamWriter payloadStream = new OutputStreamWriter(connection.getOutputStream());
            payloadStream.write(requestStr);
            payloadStream.flush();
            payloadStream.close();

            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_ACCEPTED){
                return null;
            }

            InputStream responseStream = connection.getInputStream();
            Scanner responseScanner = new Scanner(responseStream);
            StringBuilder responseString = new StringBuilder();
            while(responseScanner.hasNext()){
                responseString.append(responseScanner.nextLine());
            }
            String response = responseString.toString();

            Gson gson = new Gson();
            player = gson.fromJson(response, Player.class);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        if(player != null) {
            player.name = playerName;
        }

        return player;
    }

    @Override
    protected void onPostExecute(Player player) {
        if(onTaskFinishedListener != null){
            onTaskFinishedListener.onTaskFinished(result, player);
        }
    }

    public void setOnTaskFinishedListener(OnTaskFinishedListener listener){
        onTaskFinishedListener = listener;
    }

}
