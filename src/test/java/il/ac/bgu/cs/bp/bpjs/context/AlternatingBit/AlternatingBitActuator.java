package il.ac.bgu.cs.bp.bpjs.context.AlternatingBit;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;

public class AlternatingBitActuator extends BProgramRunnerListenerAdapter {
  @Override
  public void eventSelected(BProgram bp, BEvent e) {
    if(e.name.equals("send"))  {
      System.out.println("---*****:" + bp.getName() + " Actuator pour " + e.name + " water.");
    }
  }
}
