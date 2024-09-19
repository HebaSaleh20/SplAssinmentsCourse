package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
       // TODO: implement this
       
      if(args[1].equals("tpc")){
        Server.threadPerClient(
            Integer.parseInt(args[0]), //port
            () -> new StompProtocol<String>(), //protocol factory
            () -> new StompEncoderDecoder(),new ConnectionsImpl<String>())//message encoder decoder factory
    .serve();
      }
    if(args[1].equals("reactor")){
        Server.reactor(4,
            Integer.parseInt(args[0]), //port
            () -> new StompProtocol<String>(), //protocol factory
            () -> new StompEncoderDecoder(),new ConnectionsImpl<String>())//message encoder decoder factory
        .serve();
    }
    }

}
