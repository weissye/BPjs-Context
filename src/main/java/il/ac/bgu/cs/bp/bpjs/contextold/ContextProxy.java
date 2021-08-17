package il.ac.bgu.cs.bp.bpjs.contextold;

import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import org.mozilla.javascript.BaseFunction;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextProxy implements Serializable {
  public static List<String> CtxEvents =
      List.of("CTX.Changed", "_____CTX_LOCK_____", "_____CTX_RELEASE_____", "Context population completed");
  public final Map<String, BaseFunction> queries = new HashMap<>();
  public final Map<String, BaseFunction> effectFunctions = new HashMap<>();

  public static ContextProxy proxy;
  private static ContextProxySer proxySer;
  public static ScriptableObjectCloner cloner;

  private ContextProxy() {
  }

  public static ContextProxy Create(BProgram bprog) {
    proxy = new ContextProxy();
    proxySer = new ContextProxySer();
    cloner = new ScriptableObjectCloner(bprog);
    return proxy;
  }

  private static class ContextProxySer implements Serializable {
    private Object readResolve() throws ObjectStreamException {
      return proxy;
    }
  }

  private Object writeReplace() throws ObjectStreamException {
    return proxySer;
  }
}
