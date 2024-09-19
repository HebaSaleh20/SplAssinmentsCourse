#include "../include/SelectionPolicy.h"
#include <iostream>
using namespace std;



void MandatesSelectionPolicy::select(int AgentId,Simulation & s)
{ 
  int Id=-1; int max=0;
  vector<int> toOffer= s.getGraph2().toOffer(s.getPartyId(AgentId), s.getCoalId(AgentId),s);
  for (unsigned int i=0; i<toOffer.size(); i++){
   
    int num = s.getMandates(toOffer[i]);
    if(num>max)
    {
      max=num;
      Id=toOffer[i];
    }
  }
  if(Id!=-1){//it's not final should add more conditions
    if(s.getParty2(Id).getState()==Waiting){
      s.getParty2(Id).setState(CollectingOffers);
    }
      s.getParty2(Id).addoffer(AgentId);

  }
  

}

void EdgeWeightSelectionPolicy::select(int AgentId,Simulation & s)
{
int Id=-1;
int max = 0;
  vector<int> toOffer= s.getGraph2().toOffer(s.getPartyId(AgentId), s.getCoalId(AgentId),s);//fix
  for (unsigned int i=0; i<toOffer.size(); i++){
    int num = s.getGraph2().getEdgeWeight(s.getPartyId(AgentId),toOffer[i]);
    if(num>max)
    {
      max=num;
      Id=toOffer[i];
    }
  }
  if(Id!=-1){
    if(s.getParty2(Id).getState()==Waiting){
      s.getParty2(Id).setState(CollectingOffers);
    }
      s.getParty2(Id).addoffer(AgentId);
  }

  
}
    


SelectionPolicy* EdgeWeightSelectionPolicy::clone(){
  return new EdgeWeightSelectionPolicy();
}

SelectionPolicy* MandatesSelectionPolicy::clone(){
  return new MandatesSelectionPolicy();
}