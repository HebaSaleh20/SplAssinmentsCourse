#include "Agent.h"
#include "SelectionPolicy.h"
#include <iostream>
using namespace std;

Agent::Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy) : mAgentId(agentId), mPartyId(partyId), mSelectionPolicy(selectionPolicy), coalitionId(-1)
{
    // You can change the implementation of the constructor, but not the signature
}
Agent::~Agent()
{
    if(mSelectionPolicy) 
        delete mSelectionPolicy;
}
//copy constructor
Agent::Agent( const Agent& other):mAgentId(other.mAgentId), mPartyId(other.mPartyId), mSelectionPolicy(other.mSelectionPolicy->clone()), coalitionId(other.coalitionId) {
   
 
}
Agent::Agent( Agent&& other):mAgentId(other.mAgentId), mPartyId(other.mPartyId), mSelectionPolicy(other.mSelectionPolicy), coalitionId(other.coalitionId) {
   //move constructor
    other.mSelectionPolicy=nullptr;
}
   
Agent& Agent::operator=(const Agent& other)//assigmnt operator
{
    if(this != &other){
        if(mSelectionPolicy)
            delete mSelectionPolicy;
        mAgentId = (other.mAgentId);
        mPartyId = (other.mPartyId);
        mSelectionPolicy = (other.mSelectionPolicy);
        coalitionId = (other.coalitionId);
    }
    return *this;
}
Agent& Agent::operator=( Agent &&other) //move operator
{
    if (mSelectionPolicy) delete  mSelectionPolicy;
    mAgentId=other.mAgentId;
    mPartyId=other.mPartyId;
    mSelectionPolicy=other.mSelectionPolicy;
    other.mSelectionPolicy=nullptr;
    return *this;
}
int Agent::getId() const
{
    return mAgentId;
}

int Agent::getPartyId() const
{
    return mPartyId;
}

void Agent::step(Simulation &sim)
{
    // TODO: implement this method
mSelectionPolicy->select(getId(),sim);
}
//extra functions

void Agent::setCoId(int coId)
{
    coalitionId=coId;
}
int Agent::getCoId()
{
return coalitionId;
}
void Agent::setId(int newId)
{
    mAgentId=newId;
}
void Agent::setPId(int newPId)
{
    mPartyId=newPId;
}


