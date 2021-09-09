package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.myContext.Events.DataToBeSend;


public class AbpActuator extends BProgramRunnerListenerAdapter {
  AbpProcess simulator = new AbpProcess();


  @Override
  public void eventSelected(BProgram bp, BEvent e) {
    if(e.name.equals("doNothing"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
    } else  if(e.name.equals("dataToBeSend"))  {
      //DataToBeSend dataToBeSend = (DataToBeSend)e;
      System.out.println("---*****:" + bp.getName()+ " Name-" + e.name + " Actuator pour " + e.getData()  );
 //     simulator.TO_BE_SENT.add(dataToBeSend.getData());
      simulator.setNextInput(AbpProcess.externatInput.SEND);
    }  else if(e.name.equals("doT2rLost"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      simulator.abp.setCHN_LOSS(true);
      simulator.setNextInput(AbpProcess.externatInput.T2RLOST);
    }  else if(e.name.equals("doR2tLost"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name );
      simulator.abp.setCHN_LOSS(true);
      simulator.setNextInput(AbpProcess.externatInput.R2TLOST);
    } else if(e.name.equals("startSimulator"))  {
      System.out.println("**********:" + bp.getName() + " Actuator pour " + e.name );
      simulator.setNextInput(AbpProcess.externatInput.NONE);
      simulator.runSimulator();
      System.out.println("//simulator start running");
    }
    simulator.runSimulator();
    simulator.setNextInput(AbpProcess.externatInput.NONE);
  }
}
