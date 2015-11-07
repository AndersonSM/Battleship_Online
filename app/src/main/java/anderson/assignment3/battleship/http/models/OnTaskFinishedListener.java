package anderson.assignment3.battleship.http.models;

/**
 * Created by anderson on 11/3/15.
 */
public interface OnTaskFinishedListener<T> {
    void onTaskFinished(boolean result, T response);
}
