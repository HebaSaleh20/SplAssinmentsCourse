package bgu.spl.net.srv;

import bgu.spl.net.impl.stomp.StompEncoderDecoder;
import bgu.spl.net.impl.stomp.StompProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompProtocol<T> protocol;
    private final StompEncoderDecoder encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, StompEncoderDecoder reader, StompProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                String nextMessage =  encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                 protocol.process(nextMessage);
                
            }

        }} catch (IOException ex) {
            ex.printStackTrace();
        }

        }
        public void startProtocol(int connectionId,ConnectionsImpl<T> connections)
        {
            protocol.start(connectionId, connections);
        }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(String msg) {
        //IMPLEMENT IF NEEDED
        try {
         System.out.println("\nmessage to send to client:\n"+msg);
        byte[] response=encdec.encode(msg); 
        if (response != null) {
        out.write(response);
        out.flush();
        if(msg.split("\n")[0].equals("ERROR"))
        {
            try{
                close();
            }catch(IOException ex){}
        }

        }
    }
        catch(IOException ex){
           System.out.println("client is disconnected");
        }
    }
}

