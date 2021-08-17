package il.ac.bgu.cs.bp.bpjs.myContext;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import il.ac.bgu.cs.bp.bpjs.internal.ScriptableUtils;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgramSyncSnapshot;
import il.ac.bgu.cs.bp.bpjs.model.BThreadSyncSnapshot;
import il.ac.bgu.cs.bp.bpjs.model.SyncStatement;
import il.ac.bgu.cs.bp.statespacemapper.GenerateAllTracesInspection;


public class utilsMapper {

    public static List<List<BEvent>> getAllPaths2(GenerateAllTracesInspection.MapperResult res) {
        System.out.println("// Generated paths:");
        boolean findSimplePathsOnly = true; // acyclic paths
        int maxPathLength = Integer.MAX_VALUE;
        var paths = res.generatePaths(findSimplePathsOnly, maxPathLength);
        System.out.println(paths);
        return paths;
      }
    
      public static void exportGraph(String runName, GenerateAllTracesInspection.MapperResult res) throws IOException {
        Function<GenerateAllTracesInspection.MapperEdge, Map<String, Attribute>> edgeAttributeProvider = e -> Map.of(
            "label", DefaultAttribute.createAttribute(e.event.name)
            // "Event", DefaultAttribute.createAttribute(e.event.toString()),
            // "Event_name", DefaultAttribute.createAttribute(e.event.name),
            // "Event_value", DefaultAttribute.createAttribute(Objects.toString(e.event.maybeData))
        );
        Function<GenerateAllTracesInspection.MapperVertex, Map<String, Attribute>> vertexAttributeProvider = v -> {
          boolean startNode = v.equals(res.startNode);
          boolean acceptingNode = res.acceptingStates.contains(v);
          return Map.of(
              // "hash", DefaultAttribute.createAttribute(v.hashCode()),
              // "store", DefaultAttribute.createAttribute(getStore(v.bpss)),
              // "statements", DefaultAttribute.createAttribute(getStatments(v.bpss)),
              // "bthreads", DefaultAttribute.createAttribute(getBThreads(v.bpss)),
              "shape", DefaultAttribute.createAttribute(startNode ? "none " : acceptingNode? "doublecircle" : "circle")
          );
        };
        Supplier<Map<String, Attribute>> graphAttributeProvider = () -> Map.of(
            "name", DefaultAttribute.createAttribute("\"" + runName + "\""),
            "run_date", DefaultAttribute.createAttribute("\"" + DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()) + "\""),
            "num_of_vertices", DefaultAttribute.createAttribute(res.states().size()),
            "num_of_edges", DefaultAttribute.createAttribute(res.edges().size()),
            "num_of_events", DefaultAttribute.createAttribute(res.events.size())
        );
    
        System.out.println("// Export to GraphViz...");
        var outputDir = "exports";
        Files.createDirectories(Paths.get(outputDir));
        var path = Paths.get(outputDir, runName + ".dot");
        var dotExporter = new DOTExporter<GenerateAllTracesInspection.MapperVertex, GenerateAllTracesInspection.MapperEdge>();
        dotExporter.setEdgeAttributeProvider(edgeAttributeProvider);
        dotExporter.setVertexAttributeProvider(vertexAttributeProvider);
        dotExporter.setGraphAttributeProvider(graphAttributeProvider);
        try (var out = new PrintStream(path.toString())) {
          dotExporter.exportGraph(res.graph, out);
        }
      }
    
      public static String getBThreads(BProgramSyncSnapshot bpss) {
        return bpss.getBThreadSnapshots().stream().map(BThreadSyncSnapshot::getName).collect(joining(","));
      }
    
      public static String sanitize(String in) {
        return in
            .replace("\r\n", "")
            .replace("\n", "")
            .replace("\"", "\\\"")
            .replace("JS_Obj ", "")
            .replaceAll("[\\. \\-+]", "_");
      }
    
      public static String getStore(BProgramSyncSnapshot bpss) {
        return bpss.getDataStore().entrySet().stream()
            .map(entry -> "{" + sanitize(ScriptableUtils.stringify(entry.getKey())) + "," + sanitize(ScriptableUtils.stringify(entry.getValue())) + "}")
            .collect(joining(",", "[", "]"));
      }
    
      public static String getStatments(BProgramSyncSnapshot bpss) {
        return bpss.getBThreadSnapshots().stream()
            .map(btss -> {
              SyncStatement syst = btss.getSyncStatement();
              return
                  "{name: " + sanitize(btss.getName()) + ", " +
                      "isHot: " + syst.isHot() + ", " +
                      "request: " + syst.getRequest().stream().map(e -> sanitize(e.toString())).collect(joining(",", "[", "]")) + ", " +
                      "waitFor: " + sanitize(syst.getWaitFor().toString()) + ", " +
                      "block: " + sanitize(syst.getBlock().toString()) + ", " +
                      "interrupt: " + sanitize(syst.getInterrupt().toString()) + "}";
            })
            .collect(joining(",\n", "[", "]"));
      }
    
 
}
