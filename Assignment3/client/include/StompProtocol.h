#pragma once

#include "../include/ConnectionHandler.h"
#include <iostream>
#include <string>
#include <boost/asio.hpp>
using boost::asio::ip::tcp;
using namespace std;
#include <map>
#include "event.h"
// TODO: implement the STOMP protocol
class StompProtocol
{
private:

public:
    map<int,string> receiptidtoFrame;
    map<string,int> ChanneltoSubid;
    map<int,string> SubidtoChannel;
    map<string,map<string,vector<Event>>> usersEvents;
    StompProtocol();
    string MessageToServer(string msg);
    string MessageFromServer(string frame);
    vector<string> split (string s, string delimiter);
    vector<string> sendEvents(string file);
    string printHash(map<string,string> msg);

    int subId;
    int receiptId;
    bool loggedIn;
    string myUser;
    string currChannel;
    string host;
    short port;
};
