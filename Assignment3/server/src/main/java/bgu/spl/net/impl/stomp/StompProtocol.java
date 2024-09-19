package bgu.spl.net.impl.stomp;

import java.util.LinkedList;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.ConnectionsImpl;
public class StompProtocol<T> implements StompMessagingProtocol<T> {
    private int connectionId;
    String currusername;
    private ConnectionsImpl<T> connections;
    public void start(int connectionId, ConnectionsImpl<T> connections){
        this.connectionId=connectionId;
        this.connections=connections;
        this.currusername="";
 
    }
    
    public void process(String message){
 
       
        System.out.print("\nmessage from client:\n"+message);
        String response="";
        String[] frameToLines = message.split("\n");
        String cases=frameToLines[0];
        //check command
            //case 1 
            if(cases.equals("CONNECT"))
            {
            String username=frameToLines[3].substring(frameToLines[3].indexOf("")+1); //without login:
            currusername=username;
            String password=frameToLines[4].substring(frameToLines[4].indexOf(":")+1);//without passcode:
            if(!connections.UsernametoPassword.containsKey(username))   // New user
            {
                connections.UsernametoPassword.put(username,password);
                connections.UsernametoLoggedin.put(username,true);
                response = "CONNECTED\nversion:1.2\n";
            }
            else if(connections.UsernametoLoggedin.get(username))   //User is already logged in 
            response=ERRORframe(message, 0, cases);//should change this as an error frame
            else {                                                  //User exists
                if(!connections.UsernametoPassword.get(username).equals(password)) //password is wrong
                response=ERRORframe(message, 0, "Wrong password");//should change this as an error frame
                else  {                                             //password is correct, CONNECTING
                
                connections.UsernametoLoggedin.replace(username,true);
                response = "CONNECTED\nversion:1.2\n";
                }    

            }
            connections.send(connectionId,response);
            }
            //case 2
            if(cases.equals("SUBSCRIBE"))
            {
            String channel=frameToLines[1].substring(12);//without destination:/
            int subId=Integer.parseInt(frameToLines[2].substring(frameToLines[2].indexOf(":")+1));//without id:
            int receiptId=Integer.parseInt(frameToLines[3].substring(frameToLines[3].indexOf(":")+1));
            int[] array = {connectionId,subId};
            if(connections.ChanneltoClients.containsKey(channel))
            {   if(connections.subscribeCheck(connectionId,subId))
                {
                    response=ERRORframe(message, receiptId, cases);
                }
                else{
                connections.ChanneltoClients.get(channel).addLast(array);
                response="RECEIPT\nreceipt-id:"+receiptId;
                }
            }
            else {
                LinkedList<int[]> subscribers=new LinkedList<>();
                subscribers.addLast(array);
                connections.ChanneltoClients.put(channel,subscribers);
                response="RECEIPT\nreceipt-id:"+receiptId;
            }

            
            connections.send(connectionId,response);
            }
            //case 3

            if(cases.equals("UNSUBSCRIBE"))
            {
            int subId=Integer.parseInt(frameToLines[1].substring(frameToLines[1].indexOf(":")+1));
            int receiptId=Integer.parseInt(frameToLines[2].substring(frameToLines[2].indexOf(":")+1));
            if(connections.unsubscribeCheck(connectionId, subId))
            {
                response="RECEIPT\nreceipt-id:"+receiptId;
            }
            else{
                response=ERRORframe(message, receiptId, cases);
            }
            connections.send(connectionId,response);
            }
            //case 4
            if(cases.equals("SEND"))
            {
                String channel=frameToLines[1].substring(13);
                boolean isSubscribed=false;
                for( LinkedList<int[]>  channelSub  : connections.ChanneltoClients.values())
                { 
                for(int[] curr : channelSub)
                {
                if(curr[0]==connectionId)
                isSubscribed=true;
                }
                }
                if(isSubscribed)
                {response=message.substring(message.indexOf("user:")); 
                connections.send(channel,response);
                }
                else {
                    response=ERRORframe(message,0, cases);
                    connections.send(connectionId,response);
                }
               
            }
            //case 5
            if(cases.equals("DISCONNECT"))
            {
            if(connections.myClients.containsKey(connectionId))
            {int receiptId=Integer.parseInt(frameToLines[1].substring(frameToLines[1].indexOf(":")+1));
            response="RECEIPT\nreceipt-id:"+receiptId;
            connections.send(connectionId,response);
            connections.UsernametoLoggedin.put(currusername,false);
            currusername="";
            connections.disconnect(connectionId);
            } 
        }  
            
            

        }   
        
       

    private String ERRORframe(String message,int receipt,String cases){
        String errorframe="ERROR\n";
        //connect twice
        if(cases.equals("CONNECT"))
        {
           errorframe+="message: User already logged in\n\nThe Message:\n----\n"+message+"\n----";
        }
        //wrong password
        if(cases.equals("Wrong password"))
        {
            errorframe+="message: Wrong password\n\nThe Message:\n----\n"+message+"\n----\n The password is wrong, please try again"; 
        }
        //sub to channel twice 
        if(cases.equals("SUBSCRIBE"))
        {
        String channel=message.split("\n")[1].substring(12);
        errorframe+="repceipt-id:"+receipt+"\nmessage: already joined to "+channel+"\n\nThe Message:\n----\n"+message+"\n----";
        }
        //unsub to channel that doesn't joined before
        if(cases.equals("UNSUBSCRIBE"))
        {  
        errorframe+="repceipt-id:"+receipt+"\nmessage: user is not subscribed to the channel\n\nThe Message:\n----\n"+message+"\n----\nthe user is not subscribed to the channel, please subscribe first";
        }
        //send frame
        if(cases.equals("SEND"))
        {
            errorframe+="\nmessage: user is not subscribed to the channel\n\nThe Message:\n----\n"+message+"\n----\nthe user is not subscribed to the channel, please subscribe first";
        }
    return errorframe;
    }
    
	
	/**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){return false;}

}