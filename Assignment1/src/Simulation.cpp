#include "../include/Simulation.h"

#include <iostream>
using namespace std;

Simulation::Simulation(Graph graph, vector<Agent> agents) : mGraph(graph), mAgents(agents) ,coalition(vector<vector<int>>()), coMandates(vector<int>())
{
    // You can change the implementation of the constructor, but not the signature!
    
    for(unsigned int i=0;i<mAgents.size();i++)
    {int currId=mAgents[i].getPartyId();
     vector<int> co;
     co.push_back(currId);
     coalition.push_back(co);
     mAgents[i].setCoId(coalition.size()-1);
     coMandates.push_back(mGraph.getMandates(currId));
    }
    
    
}

void Simulation::step()
{
    // TODO: implement this method
    for (int i = 0; i <mGraph.getNumVertices() ; i++)
    {
        
        mGraph.getParty2(i).step(*this);
    }
   
    for(unsigned int i=0;i<mAgents.size();i++)
{
    mAgents[i].step(*this);
}


}


bool Simulation::shouldTerminate() const
{
    // TODO implement this method
    for(unsigned i=0;i<coMandates.size();i++)
    {if(coMandates[i]>=61)
        return true;
    }
    for( int i=0;i<mGraph.getNumVertices();i++)
    {
        if(mGraph.getParty(i).getState()!=Joined)
        return false;
    }
    return true;
}

const Graph &Simulation::getGraph() const
{
    return mGraph;
}

const vector<Agent> &Simulation::getAgents() const
{
    return mAgents;
}

const Party &Simulation::getParty(int partyId) const
{
    return mGraph.getParty(partyId);
}

/// This method returns a "coalition" vector, where each element is a vector of party IDs in the coalition.
/// At the simulation initialization - the result will be [[agent0.partyId], [agent1.partyId], ...]
const vector<vector<int>> Simulation::getPartiesByCoalitions() const
{
    // TODO: you MUST implement this method for getting proper output, read the documentation above.
    return coalition;
}
//extra functions

Graph & Simulation::getGraph2() 
{ 
    return mGraph;
}
Party & Simulation::getParty2(int partyId) 
{ 
    return mGraph.getParty2(partyId);
}
 void Simulation::setCoalition(int index,int pId)
 {
  coalition[index].push_back(pId);
 }
 void Simulation::setCoMandates(int index,int numMandates)
 {
coMandates[index]=coMandates[index]+numMandates;
 }
vector<int> Simulation::getcoMandates()
 {
return coMandates;
 }
void Simulation::setAgents(Agent g)
{
    mAgents.push_back(g);
}

int Simulation::getMandates(int partyID){
    return mGraph.getParty(partyID).getMandates();
}

int Simulation::getPartyId(int agentID){
    return mAgents[agentID].getPartyId();
}
int Simulation::getCoalId(int agentID){
    return mAgents[agentID].getCoId();
}

Agent & Simulation::getAgent(int AgentID)
{
    return mAgents[AgentID];
}

void Simulation::addAgent(Agent g)
{
    mAgents.push_back(g);
}