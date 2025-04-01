package Messages;

import java.io.Serializable;

public class DownloadRequest implements Serializable {
    private boolean isOneMessage = false;

    public DownloadRequest(boolean _isOneMessage)
    {
        this.isOneMessage = _isOneMessage;
    }

    public boolean isOneMessage() {
        return isOneMessage;
    }

}
