#include "../include/Party.h"
using std::vector;
#include "../include/JoinPolicy.h"
#include "Simulation.h"
#include <iostream>
using namespace std;




Party::Party(int id, string name, int mandates, JoinPolicy *jp) : mId(id), mName(name), mMandates(mandates), mJoinPolicy(jp), mState(Waiting), timer(0), Offers(vector<int>())
{

    // You can change the implementation of the constructor, but not the signature!
    
}
Party::  ~Party(){
    if (mJoinPolicy) 
        delete mJoinPolicy;
}
//copy constructor
Party::Party(const Party& other): mId(other.mId), mName(other.mName), mMandates(other.mMandates), mJoinPolicy(other.mJoinPolicy->clone()), mState(other.mState), timer(other.timer), Offers(vector<int>()){}
//move constructor
Party::Party( Party&& other): mId(other.mId), mName(other.mName), mMandates(other.mMandates), mJoinPolicy(other.mJoinPolicy), mState(other.mState), timer(other.timer), Offers(vector<int>())
{
    other.mJoinPolicy=nullptr;
 }
//copy assimnt operator
Party& Party::operator=(const Party& other){
    if(this != &other){
        if (mJoinPolicy)delete  mJoinPolicy;
        mId=other.mId; 
        mName=other.mName; 
        mMandates=other.mMandates;
        mJoinPolicy=other.mJoinPolicy;
        mState=other.mState;
    }
    return(*this);
}
Party& Party::operator=( Party &&other) //move operator
{
    if (mJoinPolicy)delete  mJoinPolicy;
    mId=other.mId; 
    mName=other.mName; 
    mMandates=other.mMandates;
    mJoinPolicy=other.mJoinPolicy;
    other.mJoinPolicy=nullptr;
    mState=other.mState;
    return *this;

}

State Party::getState() const
{
    return mState;
    
}

void Party::setState(State state)
{
    mState = state;
}

int Party::getMandates() const
{
    return mMandates;
}

const string & Party::getName() const
{
    return mName;
}
const int & Party::getId() const
{
    return mId;
}


void Party::step(Simulation &s)
{   
    // TODO: implement this method
    
    
    if(mState==CollectingOffers)
      timer++;

    if(timer==3)
     { 
        int agentID= mJoinPolicy->chooseParty(Offers,s);
       setState(Joined);
    s.setCoalition(s.getAgent(agentID).getCoId(),getId());
    s.setCoMandates(s.getAgent(agentID).getCoId(),getMandates());
//     //copy constructor for Agent
   Agent cloneAgent(s.getAgent(agentID));
   cloneAgent.setId(s.getAgents().size());
   cloneAgent.setPId(getId());
    s.addAgent(cloneAgent);
    timer++;

     }
}
//extra functions


void Party::addoffer(int offer)
{
    Offers.push_back(offer);
}
vector<int> Party::getOffers() const
{
    return Offers;
}