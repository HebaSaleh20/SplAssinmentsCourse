#include "../include/Graph.h"
#include "Party.h"
#include "Simulation.h"
#include <iostream>
using namespace std;

Graph::Graph(vector<Party> vertices, vector<vector<int>> edges) : mVertices(vertices), mEdges(edges) 
{
    // You can change the implementation of the constructor, but not the signature!
    
}

int Graph::getMandates(int partyId) const
{
    return mVertices[partyId].getMandates();
}

int Graph::getEdgeWeight(int v1, int v2) const
{
    return mEdges[v1][v2];
}

int Graph::getNumVertices() const
{
    return mVertices.size();
}

const Party &Graph::getParty(int partyId) const
{
    return mVertices[partyId];
}



 Party &Graph::getParty2(int partyId) 
{
    return mVertices[partyId];
}

//extra functions
vector<int> Graph::toOffer(int agentPartyId, int coalitionID , Simulation & s) {
    vector<int> toOffer; 
    for(int i=0;i<getNumVertices();i++)
    { 
        if(agentPartyId != i){
            bool isDiffColition=true;
            if(getEdgeWeight(agentPartyId,i)>0){
                if(!(mVertices[i].getState() == Joined)) //(mVertices[i].getState()==Waiting) | (mVertices[i].getState() == CollectingOffers)) 
                {
                    
                    vector<int> offers=mVertices[i].getOffers();
                    for(unsigned int i=0;(i<offers.size()) & isDiffColition;i++)
                    {
                        if(coalitionID==s.getAgent(offers[i]).getCoId())
                            isDiffColition=false;
                    }
                    if(isDiffColition == true){
                    toOffer.push_back(i);
                }
                }
            
            }
        }
    }
    return toOffer;
}