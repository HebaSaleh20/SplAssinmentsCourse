#pragma once

#include <vector>
#include "Graph.h"


class SelectionPolicy;

class Agent
{
public:
    Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy);
    ~Agent();   //destructor
    Agent (const Agent& other);     //copy constructor
    Agent( Agent&& other1);//move constructor
    Agent& operator=(const Agent& other);//assigmnt operator
    Agent& operator=( Agent &&other); //move operator
    int getPartyId() const;
    int getId() const;
    void step(Simulation &);
    void setCoId(int coId);
    int getCoId();
    void setId(int newId);
    void setPId(int newPId);




private:
    int mAgentId;
    int mPartyId;
    SelectionPolicy *mSelectionPolicy;
    int coalitionId;
};
