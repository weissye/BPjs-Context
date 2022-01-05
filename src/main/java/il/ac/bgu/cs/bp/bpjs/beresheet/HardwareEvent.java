package il.ac.bgu.cs.bp.bpjs.beresheet;

import il.ac.bgu.cs.bp.bpjs.internal.ScriptableUtils;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;

import java.text.MessageFormat;

public class HardwareEvent extends BEvent {
  public final boolean isStart;

  public HardwareEvent(String name, Object data, boolean isStart) {
    super(name, data);
    this.isStart = isStart;
  }

  public HardwareEvent(HardwareEvent o, boolean isStart) {
    this(o.name, o.maybeData, isStart);
  }

  @Override
  public String toString() {
    return MessageFormat.format("BEvent name:{0} data:{1} isStart:{2}", name, ScriptableUtils.stringify(maybeData), isStart);
  }
}
