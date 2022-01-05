package il.ac.bgu.cs.bp.bpjs.beresheet;

import il.ac.bgu.cs.bp.bpjs.analysis.DfsBProgramVerifier;
import il.ac.bgu.cs.bp.bpjs.analysis.listeners.PrintDfsVerifierListener;
import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Verification {

  public static void main(final String[] args) throws URISyntaxException {
    List<String> files =
        Arrays.stream(Objects.requireNonNull(Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("Beresheet")).toURI()).toFile().listFiles()))
            .map(f -> String.join("/", "Beresheet", f.getName()))
            .collect(Collectors.toList());
    BProgram bprog = new ContextBProgram(files);
    DfsBProgramVerifier vfr = new DfsBProgramVerifier();
    vfr.setMaxTraceLength(2000);
    vfr.setProgressListener(new PrintDfsVerifierListener());
//    vfr.setDebugMode(true);
    try {
      var res = vfr.verify(bprog);
      System.out.println(MessageFormat.format(
          "States = {0}\n" +
              "Edges = {1}\n" +
              "Time = {2}",
              res.getScannedStatesCount(), res.getScannedEdgesCount(), res.getTimeMillies()));
      if (res.isViolationFound())
        System.out.println(MessageFormat.format("Found violation: {0}", res.getViolation().get()));
      else
        System.out.println("No violation found");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
