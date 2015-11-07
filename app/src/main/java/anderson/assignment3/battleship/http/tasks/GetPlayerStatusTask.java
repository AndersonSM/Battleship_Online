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
public class GetPlayerStatusTask extends AsyncTask<String, Void, GetPlayerStatusTask.StatusResponse> {
    public class StatusResponse {
        public boolean isYourTurn;
        public String winner;

        public StatusResponse(){}
    }

    OnTaskFinishedListener<StatusResponse> onTaskFinishedListener = null;

    private boolean result = false;

    @Override
    protected StatusResponse doInBackground(String... params) {
        StatusResponse statusResponse = null;
        String playerId = params[1];
        String requestStr = "{\"playerId\": \"" + playerId + "\"}";

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
            statusResponse = gson.fromJson(response, StatusResponse.class);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return statusResponse;
    }

    @Override
    protected void onPostExecute(StatusResponse statusResponse) {
        if(onTaskFinishedListener != null){
            onTaskFinishedListener.onTaskFinished(result, statusResponse);
        }
    }

    public void setOnTaskFinishedListener(OnTaskFinishedListener listener){
        onTaskFinishedListener = listener;
    }

}
