#pragma once
#include "Simulation.h"
class SelectionPolicy { 
     public:
     SelectionPolicy(){};
     virtual ~SelectionPolicy(){};
     virtual void select(int AgentId, Simulation &s)=0;
     virtual SelectionPolicy* clone()=0;
};

class MandatesSelectionPolicy: public SelectionPolicy{ 
public:
     MandatesSelectionPolicy(){};
     ~MandatesSelectionPolicy(){};
     void select(int AgentId, Simulation &s) override;
     SelectionPolicy* clone();
};


class EdgeWeightSelectionPolicy: public SelectionPolicy{ 
public:
     EdgeWeightSelectionPolicy(){};
     ~EdgeWeightSelectionPolicy(){};
     void select(int AgentId, Simulation &s) override;
     SelectionPolicy* clone();
};