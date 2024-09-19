
#include "../include/JoinPolicy.h"



int MandatesJoinPolicy::chooseParty(vector<int> Offers,Simulation &sim)
{ 
vector<int> coMandates=sim.getcoMandates();
   
    int agentID=Offers[0];
    
    int max=coMandates[sim.getAgent(agentID).getCoId()];
    int currMandates=-1;
    for(unsigned int i=1;i<Offers.size();i++)
    {
        
        currMandates=coMandates[sim.getAgent(i).getCoId()];
        if(currMandates>max)
        {
            agentID=Offers[i];
            max=currMandates;

        }
    }
    return agentID; 
}
int LastOfferJoinPolicy::chooseParty( vector<int> Offers,Simulation &sim)
{
int agentID=Offers[Offers.size()-1];
return agentID;
}



JoinPolicy* LastOfferJoinPolicy::clone(){
    return new LastOfferJoinPolicy();
}

JoinPolicy* MandatesJoinPolicy::clone(){
    return new MandatesJoinPolicy();
}