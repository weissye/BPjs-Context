package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.NetworkSimulator;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.StudentNetworkSimulator;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.TestElement;
import il.ac.bgu.cs.bp.bpjs.myContext.Main;


public class abpActuator extends BProgramRunnerListenerAdapter {
    static StudentNetworkSimulator simulator;
    int nsim = 10;
    double loss = -1.0;
    double corrupt = -1.0;
    double delay = 1000;
    int trace = 3;
    long seed = -1;

  @Override
  public void eventSelected(BProgram bp, BEvent e) {
    if(e.name.equals("doNothing"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
    } else  if(e.name.equals("timeOut"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      TestElement t = new TestElement("T", NetworkSimulator.getStep()+10);
      Main.q.add(t);
      for (TestElement item: Main.q) {
        System.out.println(item);
    }

    } 
    else if(e.name.equals("dataCorrupt"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      TestElement t = new TestElement("CT", NetworkSimulator.getStep()+10);
      Main.q.add(t);
      for (TestElement item: Main.q) {
        System.out.println(item);
    }

    }
    else if(e.name.equals("cksmCorrupt"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      TestElement t = new TestElement("CC", NetworkSimulator.getStep()+10);
      Main.q.add(t);
    }
    else if(e.name.equals("seqCorrupt"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      TestElement t = new TestElement("CS", NetworkSimulator.getStep()+10);
      Main.q.add(t);
    }
    else if(e.name.equals("lost"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      TestElement t = new TestElement("L", NetworkSimulator.getStep()+10);
      Main.q.add(t);
    }  
    else if(e.name.equals("startSimulator"))  {
      System.out.println("**********:" + bp.getName() + " Actuator pour " + e.name );
      simulator = new StudentNetworkSimulator(nsim, loss, corrupt, delay, trace, seed, Main.q);
      
      simulator.runSimulator();
      System.out.println("//simulator start running");

  }

  }
}
