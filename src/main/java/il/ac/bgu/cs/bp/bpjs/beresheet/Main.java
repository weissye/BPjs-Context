package il.ac.bgu.cs.bp.bpjs.beresheet;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;


public class Main {
  public static void main(final String[] args) throws Exception {

    System.out.println("// start");

    runBPModel();

    System.out.println("// done");
  }

  private static void runBPModel() throws URISyntaxException {

    List<String> files =
        Arrays.stream(Objects.requireNonNull(Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("Beresheet")).toURI()).toFile().listFiles()))
            .map(f -> String.join("/", "Beresheet", f.getName()))
            .collect(Collectors.toList());
    System.out.println("files-" + files);
    BProgram bprog = new ContextBProgram(files);
    final BProgramRunner rnr = new BProgramRunner(bprog);
    rnr.addListener(new PrintBProgramRunnerListener());
    rnr.addListener(new BeresheetHardwareActuator());
    rnr.run();
  }
}
