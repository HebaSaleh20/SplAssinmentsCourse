#pragma once

#include <vector>

#include "Graph.h"
#include "Agent.h"

using std::vector;

class Simulation
{
public:
    Simulation(Graph g, vector<Agent> agents);

    void step();
    bool shouldTerminate() const;

    const Graph &getGraph() const;
    const vector<Agent> &getAgents() const;
    const Party &getParty(int partyId) const;
    const vector<vector<int>> getPartiesByCoalitions() const;
     Graph &getGraph2();
     Party & getParty2(int partyId);
    int getMandates(int partyID);
    int getPartyId(int agentID);
    int getCoalId(int agentID);
     void setCoalition(int index,int pId);
     void setCoMandates(int index,int numMandates);
     vector<int> getcoMandates();
     void setAgents(Agent g);
     Agent & getAgent(int AgentID);
     void addAgent(Agent g);
private:
    Graph mGraph;
    vector<Agent> mAgents;
    vector<vector<int>> coalition;
    vector<int> coMandates;
};
