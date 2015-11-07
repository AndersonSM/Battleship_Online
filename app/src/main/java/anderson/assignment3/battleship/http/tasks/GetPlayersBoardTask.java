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
public class GetPlayersBoardTask extends AsyncTask<String, Void, GetPlayersBoardTask.BoardsResponse> {
    public class BoardSpace {
        public final String HIT = "HIT";
        public final String MISS = "MISS";
        public final String SHIP = "SHIP";
        public final String NONE = "NONE";

        public int xPos, yPos;
        public String status;

        public BoardSpace(){}
    }

    public class BoardsResponse {
        public BoardSpace[] playerBoard, opponentBoard;

        public BoardsResponse(){}
    }

    OnTaskFinishedListener<BoardsResponse> onTaskFinishedListener = null;

    private boolean result = false;

    @Override
    protected BoardsResponse doInBackground(String... params) {
        BoardsResponse boardsResponse = null;
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
            boardsResponse = gson.fromJson(response, BoardsResponse.class);
            this.result = true;
        } catch (IOException e) {
            e.printStackTrace();
            this.result = false;
        }

        return boardsResponse;
    }

    @Override
    protected void onPostExecute(BoardsResponse boardsResponse) {
        if(onTaskFinishedListener != null){
            onTaskFinishedListener.onTaskFinished(this.result, boardsResponse);
        }
    }

    public void setOnTaskFinishedListener(OnTaskFinishedListener listener){
        onTaskFinishedListener = listener;
    }

}
