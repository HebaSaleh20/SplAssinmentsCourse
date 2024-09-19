#include "../include/StompProtocol.h"
#include <iostream>
#include <fstream>

vector<string>  StompProtocol::split (string s, string delimiter) {     //splitting the string line with delimiter between words into a vector.
    size_t pos_start = 0, pos_end, delim_len = delimiter.length();
    string token;
    vector<string> res;

    while ((pos_end = s.find (delimiter, pos_start)) != string::npos) {
        token = s.substr (pos_start, pos_end - pos_start);
        pos_start = pos_end + delim_len;
        res.push_back (token);
    }

    res.push_back (s.substr (pos_start));
    return res;
}


string StompProtocol::MessageToServer(string msg){     //gets a msg from the keyboard, converts it to a proper frame(to send it to the server).
vector<string> splitmsg = split(msg," ");
string cases=splitmsg[0];
string frame="";
if(cases=="login"){
  if(!loggedIn)
   {frame="CONNECT\naccept-version:1.2\nhost:stomp.cs.bgu.ac.il\nlogin:"+splitmsg[2]+"\npasscode:"+splitmsg[3]+"\n";
   myUser=splitmsg[2];
   host=splitmsg[1].substr(0,splitmsg[1].find(":"));
   string strport=splitmsg[1].substr(splitmsg[1].find(":")+1);
   port=stoi(strport);
   }
 }
 if(cases=="join")
 {
  frame="SUBSCRIBE\ndestination:"+splitmsg[1]+"\nid:"+to_string(subId)+"\nreceipt:"+to_string(receiptId);
  receiptidtoFrame[receiptId]=frame;
  ChanneltoSubid[splitmsg[1]]=subId;
  SubidtoChannel[subId]=splitmsg[1];
  subId++;
  receiptId++;
 }
if(cases=="exit")
{ bool isSubscribed=false;
  for(map<string,int>::iterator ii=ChanneltoSubid.begin();ii!=ChanneltoSubid.end();++ii)
  {
    if((*ii).first==splitmsg[1])
       isSubscribed=true;
  }
int id=-1;
if(isSubscribed)
  {
   id=ChanneltoSubid[splitmsg[1]];
  }
frame="UNSUBSCRIBE\nid:"+to_string(id)+"\nreceipt:"+to_string(receiptId)+"\n";
receiptidtoFrame[receiptId]=frame;
receiptId++;
}


if (cases == "summary")
  {
    string gamename = splitmsg[1];
        bool isfound=false;
    
    string team_a = gamename.substr(0, gamename.find("_"));
    string team_b = gamename.substr(gamename.find("_") + 1, gamename.size());
    string username = splitmsg[2];
    string filePath=splitmsg[3];
    int num =-1;
  for (map<string,int>::iterator ii=ChanneltoSubid.begin();ii!=ChanneltoSubid.end()&&!isfound;++ii){
    if ((*ii).first==gamename)
      num=0;
    }
    if (num==0){
  for(auto &pair: usersEvents)
  {
    if (pair.first==username)
      isfound=true;
  }
  if(isfound){
    map<string, vector<Event>> userevent1 = usersEvents[username];
    vector<Event> events = userevent1[gamename];
    frame = team_a + " vs " + team_b + "\nGame State:\n";
    map<string, string> Game;
    map<string, string> TeamA;
    map<string, string> TeamB;
    string c1="";
    string c2="";
    string c3="";
    for (Event E : events)
    {
      map<string, string> E_game_updates = E.get_game_updates();
      std::map<std::string, string>::iterator it = E_game_updates.begin();
      while (it != E_game_updates.end())
      {
          Game[it->first] = it->second;
           it++;
      }
      map<string, string> E_team_a_update = E.get_team_a_updates();
      std::map<std::string, string>::iterator it1 = E_team_a_update.begin();
      while (it1 != E_team_a_update.end())
      {
          TeamA[it1->first] = it1->second;
          it1++;
      }
      map<string, string> E_team_b_update = E.get_team_b_updates();
      std::map<std::string, string>::iterator it2 = E_team_b_update.begin();
      while (it2 != E_team_b_update.end())
      {
          TeamB[it2->first] = it2->second;
        it2++;
      }
    }
    c1 = printHash(Game);
    c2 = printHash(TeamA);
    c3 = printHash(TeamB);

    frame = frame + "General state:\n" + c1 + "\n"  + team_a + " state:\n" + c2 + "\n" + team_b + " state:\n" + c3 + "Game event reports:\n\n";
    for (Event E : events)
    {
      frame = frame + to_string(E.get_time()) + " - " + E.get_name() + ":\n\n\n" + E.get_discription() + "\n\n\n";
    }

  
  ofstream f(filePath);
  f << frame;
  f.close();
  frame="summary";
  }
  else{
      cout<< "error ---->  "<< username <<" has not reported regarding this game."<<endl;
  }
  }
  else
  cout<< "error ----> you are not subscribed to this channel."<<endl;
}

if (cases =="logout"){
  if(loggedIn){
frame="DISCONNECT\nreceipt:"+to_string(receiptId)+"\n";
receiptidtoFrame[receiptId]=frame;
receiptId++;
  }
  else
  {
    frame="login before you do logout";
  }
}
return frame;
}

string StompProtocol::printHash(map<string, string> msg)
{
  string frame = "";
  std::map<std::string, string>::iterator it = msg.begin();
  for (it = msg.begin(); it != msg.end(); it++)
  {
    frame = frame + it->first + ": " + it->second + " \n";
  }
  return frame;
}


vector<string> StompProtocol::sendEvents(string file)
{
  vector<string> myEvents;
  names_and_events eventsStruct=parseEventsFile(file);
  vector<Event> v=eventsStruct.events;
  string team1=eventsStruct.team_a_name;
  string team2=eventsStruct.team_b_name;
  for(Event event : v)
  {
    string teams="team a:"+event.get_team_a_name()+"\nteam b:"+event.get_team_b_name()+"\n";
    string eventnameandtime="event name:"+event.get_name()+"\ntime:"+to_string(event.get_time())+"\n";
    map<string,string> updates=event.get_game_updates();
    map<string,string> teamAupdate=event.get_team_a_updates();
    map<string,string> teamBupdate=event.get_team_b_updates();
    string generalupdates="general game updates:";
    string Aupdates="team a updates:";
    string Bupdates="team b updates:";
  for(map<string,string>::iterator ii=updates.begin();ii!=updates.end();++ii)
  {
    generalupdates+= "\n     "+(*ii).first+ ": "+(*ii).second;
  }
  generalupdates+="\n";

  for(map<string,string>::iterator ii=teamAupdate.begin();ii!=teamAupdate.end();++ii)
  {
    Aupdates+= "\n     "+(*ii).first+ ": "+(*ii).second;
  }
  Aupdates+="\n";

  for(map<string,string>::iterator ii=teamBupdate.begin();ii!=teamBupdate.end();++ii)
  {
    Bupdates+= "\n     "+(*ii).first+ ": "+(*ii).second;
  }
  Bupdates+="\n";
  string description=event.get_discription();
  string frame="SEND\ndestination:/"+team1+"_"+team2+"\n"+"  "+"\nuser:"+myUser+"\n"+teams+eventnameandtime+generalupdates+Aupdates+Bupdates+"description:\n"+description;
   myEvents.push_back(frame);
  }
  return myEvents;
}

string StompProtocol::MessageFromServer(string frame){ //gets a frame from the server, converts it to a proper msg(to prints it).
  string msg="";
  vector<string> splitframe = split(frame,"\n");
  string cases=splitframe[0];
  
  if(cases=="CONNECTED")
  if(!loggedIn)
  {loggedIn=true;
    msg="Login successful";
  }

  if(cases=="RECEIPT")
  { string receipt=splitframe[1].substr(splitframe[1].find(":")+1);
    int receipt_id=stoi(receipt);
    string myframe=receiptidtoFrame[receipt_id];
    vector<string> splitted = split(myframe,"\n");
    string mycase=splitted[0];
    if(mycase=="SUBSCRIBE")
    {
      string id=splitted[2].substr(splitted[2].find(":")+1);
      int sub_id=stoi(id);
      string mychannel=SubidtoChannel[sub_id];
      msg="Joined channel "+mychannel;
      currChannel=mychannel;
      vector<Event> myevents;
      usersEvents[myUser][mychannel]=myevents;
    }
    if(mycase=="UNSUBSCRIBE")
    {
      string id=splitted[1].substr(splitted[1].find(":")+1);
      int sub_id=stoi(id);
      string mychannel=SubidtoChannel[sub_id];
      SubidtoChannel.erase(sub_id);
      ChanneltoSubid.erase(mychannel);
      msg="Exited channel "+mychannel;
    }
    if(mycase=="DISCONNECT")
    {
      
      msg="Logout successful";
      loggedIn=false;
     
    }

}

 if (cases=="MESSAGE")
    {
      
      vector<string> myEvents=split(frame,"\n");
      string chanel = myEvents[3].substr(13);
      string user = (split(myEvents[5],":"))[1];
      map<string, string> team_a_update;
      map<string, string> team_b_update;
      map<string, string> General_update;
      int i=10;
      int cuur=6;
      string team_a = (split(myEvents[6],":"))[1];
      string team_b = (split(myEvents[7],":"))[1];
      string event_name=(split(myEvents[8],":"))[1];
      int time =stoi((split(myEvents[9],":"))[1]);
      string discription;
     
      if ((split(myEvents[i],":"))[0]=="general game updates" ){
        if((split(myEvents[i+1],":"))[0]!="team a updates"){
            i++;
            do{
              General_update[(split(myEvents[i],":"))[0]]=(split(myEvents[i],":"))[1];
              i++;
              }while((split(myEvents[i],":"))[0]!="team a updates");
        }
        else
          i++;
      }
        if ((split(myEvents[i],":"))[0]=="team a updates" ){
         if( (split(myEvents[i+1],":"))[0]!="team b updates"){
          i++;
            do{
              team_a_update[(split(myEvents[i],":"))[0]]=(split(myEvents[i],":"))[1];
              i++;
              }while((split(myEvents[i],":"))[0]!="team b updates");
        } 
        else{
          i++;
        }
        
        }
         if ((split(myEvents[i],":"))[0]=="team b updates" )
        {
          if( (split(myEvents[i+1],":"))[0]!="description"){
            i++;
            do{
              team_b_update[(split(myEvents[i],":"))[0]]=(split(myEvents[i],":"))[1];
              i++;
              }while((split(myEvents[i],":"))[0]!="description");
        }
        else{
          i++;
        }
        } 
         if((split(myEvents[i],":"))[0]=="description"){
          discription=myEvents[i+1];
          cuur=i+1;
         
        
      } 
       Event event(team_a,team_b,event_name,time,General_update,team_a_update,team_b_update,discription);
          
            usersEvents[user][chanel].push_back(event);
          
            
    }
    if(cases=="ERROR")
    {
      string errorname=splitframe[1].substr(9);
      if(errorname=="User already logged in")
        cout<<errorname;
      if(errorname=="Wrong password")
        cout<<errorname;
    }


return msg;

}


StompProtocol::StompProtocol() : subId(0),receiptId(0),loggedIn(false), myUser(""),currChannel(""),host(""),port(0)  {

}
