#pragma once
#include <string>
#include <vector>

using std::string;

class JoinPolicy;
class Simulation;

enum State
{
    Waiting,
    CollectingOffers,
    Joined
};

class Party
{
public:
    Party(int id, string name, int mandates, JoinPolicy *); 
    ~Party(); //distructor
   Party(const Party& other);//copy constructor
    Party( Party&& other);//move constructor
    Party& operator=(const Party& other);//assigmnet operator
    Party& operator=( Party&& other);//move assigmnet operator
    State getState() const;
    void setState(State state);
    int getMandates() const;
    void step(Simulation &s);
    const string &getName() const;
    const int & getId() const;
    void addoffer(int offer);
    std::vector<int> getOffers() const;
    

private:
    int mId;
    string mName;
    int mMandates;
    JoinPolicy *mJoinPolicy;
    State mState;
    int timer;
    std:: vector<int> Offers;
};
