package il.ac.bgu.cs.bp.bpjs.contextold;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgramSyncSnapshot;
import il.ac.bgu.cs.bp.bpjs.model.SyncStatement;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.AbstractEventSelectionStrategy;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.EventSelectionResult;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.EventSelectionStrategy;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.SimpleEventSelectionStrategy;
import il.ac.bgu.cs.bp.bpjs.model.eventsets.EventSet;
import il.ac.bgu.cs.bp.bpjs.model.eventsets.EventSets;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public final class CtxEventSelectionStrategy extends AbstractEventSelectionStrategy {

    private EventSelectionStrategy strategy;

    public CtxEventSelectionStrategy(long seed) {
        super(seed);
        strategy = new SimpleEventSelectionStrategy(seed);
    }

    public CtxEventSelectionStrategy() {
        this(new SimpleEventSelectionStrategy());
    }

    public CtxEventSelectionStrategy(EventSelectionStrategy eventSelectionStrategy) {
        strategy = eventSelectionStrategy;
    }

    public void setEventSelectionStrategy(EventSelectionStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Set<BEvent> selectableEvents(BProgramSyncSnapshot bpss) {

        Set<SyncStatement> statements = bpss.getStatements();
        List<BEvent> externalEvents = bpss.getExternalEvents();

        EventSet blocked = EventSets.anyOf(statements.stream()
            .filter(Objects::nonNull)
            .map(SyncStatement::getBlock)
            .filter(r -> r != EventSets.none)
            .collect(Collectors.toSet()));

        Set<BEvent> ctxEvents = statements.parallelStream()
            .filter(Objects::nonNull)
            .filter(s -> !getRequestedAndNotBlocked(s, blocked).isEmpty())
            .flatMap(s -> getRequestedAndNotBlocked(s, blocked).stream())
            .filter(e -> ContextProxy.CtxEvents.contains(e.name))
            .collect(toSet());
        if (ctxEvents.size() > 0) {
            for (BEvent e : ctxEvents) {
                if (e.name.equals("CTX.Changed"))
                    return new HashSet<>() {{
                        add(e);
                    }};
                if (e.name.equals("Context population completed"))
                    return new HashSet<>() {{
                        add(e);
                    }};
            }
            return ctxEvents;
        }

        return strategy.selectableEvents(bpss);
    }

    @Override
    public Optional<EventSelectionResult> select(BProgramSyncSnapshot bpss, Set<BEvent> selectableEvents) {
        return strategy.select(bpss, selectableEvents);
    }
}