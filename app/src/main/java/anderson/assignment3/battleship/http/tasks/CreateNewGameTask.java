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

/**
 * Created by anderson on 11/2/15.
 */
public class CreateNewGameTask extends AsyncTask<String, Void, CreateNewGameTask.NewGameResponse> {

    public class NewGameResponse {
        public String playerId, gameId;

        public NewGameResponse(){}
    }

    OnTaskFinishedListener<NewGameResponse> onTaskFinishedListener = null;

    private boolean result = false;

    @Override
    protected NewGameResponse doInBackground(String... params) {
        NewGameResponse gameResponse = null;
        String gameName = params[1];
        String playerName = params[2];
        String requestStr = "{\"gameName\": \"" + gameName + "\", \"playerName\": \"" + playerName + "\"}";

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
            if(responseCode < 200 || responseCode > 299){
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
            gameResponse = gson.fromJson(response, NewGameResponse.class);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return gameResponse;
    }

    @Override
    protected void onPostExecute(NewGameResponse newGameResponse) {
        if(onTaskFinishedListener != null){
            onTaskFinishedListener.onTaskFinished(result, newGameResponse);
        }
    }

    public void setOnTaskFinishedListener(OnTaskFinishedListener listener){
        onTaskFinishedListener = listener;
    }

}
