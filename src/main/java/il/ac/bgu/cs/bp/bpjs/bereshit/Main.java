package il.ac.bgu.cs.bp.bpjs.bereshit;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.InMemoryEventLoggingListener;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.statespacemapper.StateSpaceMapper;
import il.ac.bgu.cs.bp.statespacemapper.jgrapht.exports.DotExporter;


import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;


public class Main {
  /**
   * Choose the desired COBP program...
   */

  private static final Example example =
    Example.Bereshit;
  
  public static void main(final String[] args) throws Exception {

      System.out.println("// start");

      // run runBPModel() function for Bereshit BP model or runMapper() function for model mapper (Bereshit.dot)
      runBPModel();
//      runMapper();

      System.out.println("// done");

      System.exit(0);
  }

  private static void runBPModel() throws URISyntaxException {

      Set<List<BEvent>> samples = new HashSet<>();

      List<String> files =
              Arrays.stream(Objects.requireNonNull(Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(example.name())).toURI()).toFile().listFiles()))
                      .map(f -> String.join("/", example.name(), f.getName()))
                      .collect(Collectors.toList());
      System.out.println("files-"+files);
      BProgram bprog = new ContextBProgram(files);
      final BProgramRunner rnr = new BProgramRunner(bprog);
      rnr.addListener(new PrintBProgramRunnerListener());

      if (example == Example.TicTacToe) {
//        boolean useUI = true;
//        TicTacToeGameMain.main(bprog, rnr, useUI);
          return;
      }
      var eventLogger = rnr.addListener(new InMemoryEventLoggingListener());
      rnr.run();
      samples.add(eventLogger.getEvents());

  }

  static private void runMapper() throws Exception {
      String name = "Bereshit";
      BProgram bprog = new ContextBProgram(name + "/dal.js", name + "/bl.js");

      StateSpaceMapper mpr = new StateSpaceMapper();
      mpr.setMaxTraceLength(10);
      var res = mpr.mapSpace(bprog);
      System.out.println("// completed mapping the states graph");
      System.out.println(res.toString());

      System.out.println("// Export to GraphViz...");
      var outputDir = "exports";
      var path = Paths.get(outputDir, name + ".dot").toString();
      new DotExporter(res,path,name).export();

  }
}
