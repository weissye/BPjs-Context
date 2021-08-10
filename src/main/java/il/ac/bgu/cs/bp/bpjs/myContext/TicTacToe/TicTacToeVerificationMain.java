package il.ac.bgu.cs.bp.bpjs.myContext.TicTacToe;

import il.ac.bgu.cs.bp.bpjs.analysis.BThreadSnapshotVisitedStateStore;
import il.ac.bgu.cs.bp.bpjs.analysis.DfsBProgramVerifier;
import il.ac.bgu.cs.bp.bpjs.analysis.ExecutionTraceInspections;
import il.ac.bgu.cs.bp.bpjs.analysis.VerificationResult;
import il.ac.bgu.cs.bp.bpjs.analysis.listeners.PrintDfsVerifierListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.PrioritizedBSyncEventSelectionStrategy;

/**
 * Verification of the TicTacToe strategy.
 * 
 * @author reututy
 */
public class TicTacToeVerificationMain  {

	public static void main(String[] args) throws InterruptedException {

		// Create a program
		BProgram bprog = new ResourceBProgram("BPJSTicTacToe.js");

		bprog.setEventSelectionStrategy(new PrioritizedBSyncEventSelectionStrategy());
		bprog.setWaitForExternalEvents(true);
		
		String simulatedPlayer =   "bp.registerBThread('XMoves', function() {\n" +
									"while (true) {\n" +
										"bp.sync({ request:[ X(0, 0), X(0, 1), X(0, 2), X(1, 0), \n" +
										"X(1, 1), X(1, 2), X(2, 0), X(2, 1), X(2, 2) ] }, 10); \n" +
										"}\n" +
									"});\n";
		
		// This bthread models the requirement that X never wins.
         String xCantWinRequirementBThread = "bp.registerBThread( \"req:NoXWin\", function(){\n" +
                                            "	bp.sync({waitFor:StaticEvents.XWin});\n" +
                                            "	bp.ASSERT(false, \"Found a trace where X wins.\");\n" +
                                            "});";
        
		bprog.appendSource(simulatedPlayer);
		bprog.appendSource(xCantWinRequirementBThread);
//		bprog.appendSource(infiBThread);
        try {
            DfsBProgramVerifier vfr = new DfsBProgramVerifier();
            vfr.addInspection(ExecutionTraceInspections.FAILED_ASSERTIONS);

            vfr.setMaxTraceLength(70);
//            vfr.setDebugMode(true);
            vfr.setVisitedStateStore(new BThreadSnapshotVisitedStateStore());
            vfr.setProgressListener(new PrintDfsVerifierListener() );

            final VerificationResult res = vfr.verify(bprog);
            if (res.isViolationFound()) {
                System.out.println("Found a counterexample");
                res.getViolation().get()
                    .getCounterExampleTrace()
                    .getNodes()
                    .forEach(nd -> System.out.println(" " + nd.getEvent()));

            } else {
                System.out.println("No counterexample found.");
            }
            System.out.printf("Scanned %,d states\n", res.getScannedStatesCount());
            System.out.printf("Time: %,d milliseconds\n", res.getTimeMillies());

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }

		System.out.println("end of run");
	}

}