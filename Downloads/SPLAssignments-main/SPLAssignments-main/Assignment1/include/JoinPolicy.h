#pragma once
#include "Simulation.h"
#include <vector>
#include <iostream>
using namespace std;


class JoinPolicy {
    public:
        JoinPolicy(){};
        virtual int chooseParty( vector<int> Offers,Simulation &sim) = 0;
        virtual ~JoinPolicy(){};
        virtual JoinPolicy* clone()=0;
};

class MandatesJoinPolicy : public JoinPolicy {
    public:
        MandatesJoinPolicy(){};
        int chooseParty( vector<int> Offers,Simulation &sim) override;
        ~MandatesJoinPolicy(){} ;
        JoinPolicy* clone();
};

class LastOfferJoinPolicy : public JoinPolicy {
    public:
        LastOfferJoinPolicy(){};
        int chooseParty( vector<int> Offers,Simulation &sim) override;
        ~LastOfferJoinPolicy(){};
        JoinPolicy* clone();
};