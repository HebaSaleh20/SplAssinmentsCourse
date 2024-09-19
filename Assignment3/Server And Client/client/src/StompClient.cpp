#include <iostream>
using namespace std;
#include "../include/ConnectionHandler.h"

#include <vector>
#include "../include/StompProtocol.h"
#include <thread>

void ReadFromSocket(StompProtocol &protocol, ConnectionHandler &handler,bool &terminate){
    
    string frameFromServer;
    while(!terminate){
        frameFromServer ="";
		handler.getLine(frameFromServer);
		string output = protocol.MessageFromServer(frameFromServer);
        cout << output << endl;
        if(output=="Logout successful")
        {
            terminate=true;
            handler.close();
        }
    }
    

}

int main(int argc, char *argv[]) {
	// TODO: implement the STOMP client

   while(true){
 
    bool terminate=false;
    
    std::string line;
    bool loggedIn = false;
    StompProtocol protocol;
    ConnectionHandler connectionHandler(protocol.host, protocol.port);

    while(!loggedIn){
		getline(cin, line);
        string frame = protocol.MessageToServer(line);
        string frameFromServer="";
        connectionHandler.host_ = protocol.host;
        connectionHandler.port_ = protocol.port;
	    if(protocol.split(line," ")[0]=="login"){	
        if (!connectionHandler.connect()) {
            std::cerr << "Cannot connect to " << protocol.host << ":" << protocol.port << std::endl;
            continue;
        }
        connectionHandler.sendLine(frame);
        connectionHandler.getLine(frameFromServer);
		string output = protocol.MessageFromServer(frameFromServer);
        cout << output << endl;
        if(output!="Login successful"){
            connectionHandler.close();
            continue;
        }
        loggedIn = true;

    }else cout<<"should log in first"<<endl;
    thread reader(&ReadFromSocket, ref(protocol), ref(connectionHandler),ref(terminate));

    while(!terminate){
		getline(cin, line);
        
        if((protocol.split(line," ")[0]=="login") & (protocol.loggedIn))
        {cout << "The client is already logged in,log out before trying again" << endl;
        continue;
        }
        string frame = protocol.MessageToServer(line);

        if(protocol.split(line," ")[0]=="report")
		{
            string jsonfile=protocol.split(line," ")[1];
            vector<string> myEvents=protocol.sendEvents(jsonfile);
            for(string event : myEvents)
            {
                connectionHandler.sendLine(event);
	        
                
            }
            continue;
        }

        if(frame=="summary")
          continue;
		

		if(!connectionHandler.sendLine(frame))
        {cout << "Disconnected. Exiting...\n" << endl;
            break;
        }
        if(line=="logout")
        {  reader.join();
            break;
        }
	}

   }
	return 0;
    
   
}
}