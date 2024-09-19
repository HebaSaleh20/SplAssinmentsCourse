package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{

    public ConcurrentHashMap<Integer,ConnectionHandler<T>> myClients;    //holds the clients' handlers with the key: client id
    // hashMap usernametoPassword
    public ConcurrentHashMap<String,String> UsernametoPassword; 
    //hashMap topictoClients(channel)
    public ConcurrentHashMap<String,LinkedList<int[]>> ChanneltoClients; 
    
    //hashMap usernametoLoggedin
    public ConcurrentHashMap<String,Boolean> UsernametoLoggedin;
    int messageid;
    public ConnectionsImpl(){
        myClients = new ConcurrentHashMap<>();
        UsernametoPassword=new ConcurrentHashMap<>();
        ChanneltoClients=new ConcurrentHashMap<>();
        UsernametoLoggedin=new ConcurrentHashMap<>();
        messageid=0;
    }

    public void addConnection(int connecionId, ConnectionHandler<T> handler){
        myClients.put(connecionId, handler);
    }

    public boolean send(int connectionId, String msg){
        myClients.get(connectionId).send(msg);
        return true;
    }

    public void send(String channel, String msg){
        
       LinkedList<int[]> mylist= ChanneltoClients.get(channel);
       for(int[]  clientinfo : mylist)
       {
        String message="MESSAGE\nsubscription:"+clientinfo[1]+"\nmessage-id:"+messageid+"\ndestination:/"+channel+"\n\n"+msg;
        messageid++;
        send(clientinfo[0],message);//should check if it returns false then send an Error frame.
       }
    
    }
    public void disconnect(int connectionId){
        
        ConnectionHandler<T> myhandler= myClients.remove(connectionId);
        for( LinkedList<int[]>  connectionId1  : ChanneltoClients.values())
        { 
            for(int[] curr : connectionId1)
            {
                if(curr[0]==connectionId)
                connectionId1.remove(curr);
            }
        }
        
   
    }

    public boolean unsubscribeCheck(int connectionId,int subId)
    {
        for( LinkedList<int[]>  channelSub  : ChanneltoClients.values())
        { 
            for(int[] curr : channelSub)
            {
                if(curr[0]==connectionId & curr[1]==subId){
                channelSub.remove(curr);
                return true;
            }
        }



        }
       return false; 
    }
    public boolean subscribeCheck(int connectionId,int subId)
    {
        for( LinkedList<int[]>  channelSub  : ChanneltoClients.values())
        { 
            for(int[] curr : channelSub)
            {
                if(curr[0]==connectionId & curr[1]==subId)
                return true;
            }



        }
       return false; 
    }
}
