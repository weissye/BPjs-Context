package il.ac.bgu.cs.bp.bpjs.myContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.context.PrintCOBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.context.PrintCOBProgramRunnerListener.Level;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.AlternatingBitActuator;
import il.ac.bgu.cs.bp.bpjs.myContext.TicTacToe.TicTacToeGameMain;
import il.ac.bgu.cs.bp.statespacemapper.StateSpaceMapper;
import jdk.jfr.Timestamp;



public class Main {
  /**
   * Choose the desired COBP program...
   */

  private static final Example example =
  Example.abp;
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

  
  public static void main(final String[] args) throws Exception {
    // List<String> files =
    //     Arrays.stream(Objects.requireNonNull(Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(example.name())).toURI()).toFile().listFiles()))
    //         .map(f -> String.join("/", example.name(), f.getName()))
    //         .collect(Collectors.toList());
    // System.out.println("files-"+files);
    // BProgram bprog = new ContextBProgram(files);
    // final BProgramRunner rnr = new BProgramRunner(bprog);
    // rnr.addListener(new PrintCOBProgramRunnerListener(logLevel, new PrintBProgramRunnerListener()));

    // if (example == Example.TicTacToe) {
    //   boolean useUI = true;
    //   TicTacToeGameMain.main(bprog, rnr, useUI);
    //   return;
    // } if (example == Example.HotCold) {
    //   rnr.addListener(new HotColdActuator());
    // } if (example == Example.abp) {
    //   rnr.addListener(new AlternatingBitActuator()); }
    // rnr.run();

    System.out.println("// start");
    // This will load the program file  <Project>/src/main/resources/HelloBPjsWorld.js
    BProgram bprog = new ContextBProgram("abp/dal.js","abp/bl.js");
    String name = "abp";
    StateSpaceMapper mpr = new StateSpaceMapper();

    var res = mpr.mapSpace(bprog);
    System.out.println("// completed mapping the states graph");
    System.out.println(res.toString());

    utilsMapper.getAllPaths(res);

    utilsMapper.exportGraph(name, res);
    System.out.println("// done");
    System.exit(0);
  }

}
