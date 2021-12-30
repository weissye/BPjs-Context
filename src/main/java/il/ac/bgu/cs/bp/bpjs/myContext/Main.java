package il.ac.bgu.cs.bp.bpjs.myContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.InMemoryEventLoggingListener;
import il.ac.bgu.cs.bp.bpjs.myContext.myAbp.AbpActuator;
import il.ac.bgu.cs.bp.statespacemapper.jgrapht.exports.DotExporter;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;


import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.context.PrintCOBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.context.PrintCOBProgramRunnerListener.Level;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.AlternatingBitActuator;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.StudentNetworkSimulator;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.TestElement;
import il.ac.bgu.cs.bp.bpjs.myContext.TicTacToe.TicTacToeGameMain;
import il.ac.bgu.cs.bp.statespacemapper.StateSpaceMapper;
import jdk.jfr.Timestamp;



public class Main {
  /**
   * Choose the desired COBP program...
   */

  private static final Example example =
//  Example.abpStudents;
//          Example.abp;
    Example.Bereshit;
  //  Example.HotCold;
  //  Example.SampleProgram;
//      Example.SampleProgram;
    //  Example.TicTacToe;

  /**
   * internal context events are: "CTX.Changed", "_____CTX_LOCK_____", "_____CTX_RELEASE_____"
   * You can filter these event from printing on console using the Level:
   * Level.ALL : print all
   * Level.NONE : print none
   * Level.CtxChanged: print only CTX.Changed events (i.e., filter the transaction lock/release events)
   */
  //private static final Level logLevel = Level.CtxChanged;
  // private static final Level logLevel = Level.ALL;
  private static final Level logLevel = Level.ALL;
    // private static Queue<TestElement> q = new LinkedList<>();
  public static PriorityQueue<TestElement> q = new PriorityQueue<>();

  
  public static void main(final String[] args) throws Exception {

      Set<List<BEvent>> samples = new HashSet<>();

      List<String> files =
          Arrays.stream(Objects.requireNonNull(Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(example.name())).toURI()).toFile().listFiles()))
              .map(f -> String.join("/", example.name(), f.getName()))
              .collect(Collectors.toList());
      System.out.println("files-"+files);
      BProgram bprog = new ContextBProgram(files);
//      BProgram bprog = new ContextBProgram("abp/abp.js");
//      BProgram bprog = new ContextBProgram("abp/dal.js","abp/bl.js");
      final BProgramRunner rnr = new BProgramRunner(bprog);
      rnr.addListener(new PrintCOBProgramRunnerListener(logLevel, new PrintBProgramRunnerListener()));

      if (example == Example.TicTacToe) {
        boolean useUI = true;
        TicTacToeGameMain.main(bprog, rnr, useUI);
        return;
      } if (example == Example.HotCold) {
        rnr.addListener(new HotColdActuator());
      } if (example == Example.abp) {
        rnr.addListener(new AbpActuator());
      } if (example == Example.abpStudents) {
        rnr.addListener(new AlternatingBitActuator());
      }
      var eventLogger = rnr.addListener(new InMemoryEventLoggingListener());
      rnr.run();
      samples.add(eventLogger.getEvents());

//
//       System.out.println("// start");
////      String name = "abp";
//      String name = "Bereshit";
//     // This will load the program file  <Project>/src/main/resources/HelloBPjsWorld.js
//       BProgram bprog = new ContextBProgram(name + "/dal.js", name + "/bl.js");
////       BProgram bprog = new ContextBProgram(name + "/abp.js");
//
//       // You can use a different EventSelectionStrategy, for example:
//       /* var ess = new PrioritizedBSyncEventSelectionStrategy();
//       bprog.setEventSelectionStrategy(ess); */
//       StateSpaceMapper mpr = new StateSpaceMapper();
//       mpr.setMaxTraceLength(15Bereshit
//
//
//
//       );
//       var res = mpr.mapSpace(bprog);
//       System.out.println("// completed mapping the states graph");
//       System.out.println(res.toString());
//
//       System.out.println("// Export to GraphViz...");
//       var outputDir = "exports";
//       var path = Paths.get(outputDir, name + ".dot").toString();
//       new DotExporter(res,path,name).export();
//
//       System.out.println("// done");

      System.exit(0);
  }

  
    // public static List<List<BEvent>> getAllPaths(GenerateAllTracesInspection.MapperResult res) {
    //   System.out.println("// Generated paths:");
    //   boolean findSimplePathsOnly = true; // acyclic paths
    //   int maxPathLength = Integer.MAX_VALUE;
    //   var paths = res.generatePaths(findSimplePathsOnly, maxPathLength);
    //   System.out.println(paths);
    //   return paths;
    // }
  
}
