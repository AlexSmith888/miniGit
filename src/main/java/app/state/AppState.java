package app.state;

import java.io.IOException;

public interface AppState {
    void saveCurrentState() throws IOException;
    void recoverPreviousState() throws IOException;
}
