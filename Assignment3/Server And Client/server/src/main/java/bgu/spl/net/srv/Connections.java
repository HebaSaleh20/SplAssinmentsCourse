package bgu.spl.net.srv;

import java.io.IOException;

public interface Connections<T> {

    
    boolean send(int connectionId, String msg);

    void send(String channel, String msg);

    void disconnect(int connectionId);

    void addConnection(int connecionId, ConnectionHandler<T> handler);
    
    public boolean unsubscribeCheck(int connectionId,int subId);
    public boolean subscribeCheck(int connectionId,int subId);
}
