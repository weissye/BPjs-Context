package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.myContext.Events.DataToBeSend;
import org.mozilla.javascript.NativeObject;


public class AbpActuator extends BProgramRunnerListenerAdapter {
  AbpTester simulator = new AbpTester();
  AbpInfra.externalInput nextInput = AbpInfra.externalInput.NONE;
  String nextData;

  @Override
  public void eventSelected(BProgram bp, BEvent e) {
    AbpInfra.externalInput nextEvent = AbpInfra.externalInput.NONE;

    if (e.name.equals("doNothing")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
    } else if (e.name.equals("dataToBeSend")) {
      //DataToBeSend dataToBeSend = (DataToBeSend)e;
      NativeObject t = (NativeObject) e.getData();
      System.out.println("---*****:" + bp.getName() + " Name-" + e.name + " Actuator pour " + t.get("info"));
      simulator.abpSimulator(AbpInfra.externalInput.TOSEND, (String) t.get("info"));
    } else if (e.name.equals("send")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.SEND;
    } else if (e.name.equals("ackOk")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.ACKOK;
    } else if (e.name.equals("ackNok")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.ACKNOK;
    } else if (e.name.equals("recAck")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.RECACK;
    } else if (e.name.equals("recNak")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.RECNAK;
    } else if (e.name.equals("t2rLoss")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.T2RLOSS;
    } else if (e.name.equals("r2tLoss")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.R2TLOSS;
    } else if (e.name.equals("t2rReordered")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.T2RREORDER;
    } else if (e.name.equals("r2tReorder")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.R2TREORDER;
    } else if (e.name.equals("success")) {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name);
      nextEvent = AbpInfra.externalInput.FINISH;
    } else
        nextEvent = AbpInfra.externalInput.NONE;

    simulator.abpSimulator(nextEvent);
  }
}

