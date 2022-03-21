package il.ac.bgu.cs.bp.bpjs.beresheet;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;

public class BeresheetHardwareActuator extends BProgramRunnerListenerAdapter {
  @Override
  public void eventSelected(BProgram bp, BEvent e) {
    if(e instanceof HardwareEvent) {
      bp.enqueueExternalEvent(new HardwareEvent((HardwareEvent) e, false));
    }
  }
}
