/*
* Simulate a single server queue with:
*  - a normally distributed inter-arrival time with mean 30 and SD of 5
*  - a normally distributed service time with mean 24 and SD of 4
*  - run for first 15 customers
*/

import java.util.*; 
import java.util.LinkedList;
import java.util.Queue;

public class SingleServer {

    static int count=0;                                     //count of clients having entered the system
    static Random arrivTime = new Random();                  
    static Random servTime = new Random();                  
    static double nextArriv = 0.0;                          //time step of next arrival
    static double nextServ = servTime.nextGaussian()*4+24;  //time step of next service
    static Queue<Client> Q = new LinkedList<>();            //queue of clients waiting for service

    public static void main(String[] args) throws Exception {
        //based on an event-scheduling world view
        while(count<15){                                    //loop until all 15 have arrived
            if(nextArriv<=nextServ){                        //if next step is an arrival:
                arrive();                                   //update state from arrival
            }
            else if(nextArriv>nextServ){                    //if next step serve
                if(Q.size()==0){                            //if no one to serve
                    double temp = nextServ;                 //temporarily store nextServ, since arrive() decrements it
                    arrive();                               //next step has to be an arrive
                    nextServ=temp;                          //reassign nextServ its original value
                }
                serve();                                    //update state from serve
            }
        }
        while(Q.size() > 0){                //loop until everyone left in the queue is served
            serve();
        }
    }


    public static void arrive(){
        count++;                                        //increment client count
        Q.add(new Client(count,0,0));             //add new client object to Q
        nextServ-=nextArriv;                            //time spent towards next task
        for(Client c:Q){ c.waitTime += nextArriv;}      //update waitTime of Qers
        nextArriv = arrivTime.nextGaussian()*5+30;      //get new arrival time step from distribution
    }

    public static void serve(){
        Client out;                                     //temp variable for client being served
        out=Q.poll();                                   //remove first client from Q
        nextArriv-=nextServ;                            //time spent towards next task
        for(Client c:Q){ c.waitTime += nextServ;}       //update waitTime of Qers
        out.systTime=out.waitTime + nextServ;           //assign system time to client on exit
        nextServ = servTime.nextGaussian()*4+24;        //get new service time step from distribution
        System.out.println(out.toString());             //print results
    }
}

class Client{
    int num;
    double waitTime;
    double systTime;

    Client(int n, double t1, double t2){
        num = n;
        waitTime=t1;
        systTime=t2;
    }

    @Override
    public String toString() {
        return "Waiting time for customer " + num +": "+ waitTime +
        " sec \nTotal system time for customer "+ num + ": " + systTime + " sec \n";
    }
}

