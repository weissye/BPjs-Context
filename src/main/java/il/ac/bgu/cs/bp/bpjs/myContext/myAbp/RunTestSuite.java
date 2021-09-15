package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunTestSuite {


    public static void main(final String[] args) throws Exception {

        AbpTester abpTester = new AbpTester();

        List<String> list = new ArrayList<>();

        abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "A");
        abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "B");
        abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "C");
        abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "D");
        abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "E");
        abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "V");


        try (BufferedReader br = new BufferedReader(new FileReader("BestTestSuite.txt"))) {
            list = br.lines().collect(Collectors.toList());
            ListIterator<String> iterator = list.listIterator(1);

            // Printing the iterated value
            System.out.println("\nUsing ListIterator"
                    + " from Index 1:\n");
            while (iterator.hasNext()) {
                String events = iterator.next();

                List<String> eventsList = Stream.of(events.split(",", -1))
                                          .collect(Collectors.toList());
                ListIterator<String> iterator2 = eventsList.listIterator(1);
                while (iterator2.hasNext()) {
                    String eventName = iterator2.next();
                    if (! eventName.startsWith("Goal")){
                        abpTester.abpSimulator( AbpInfra.externalInput.valueOf(eventName.toUpperCase()));
                    }
                }
                abpTester.resetInfra();
                abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "A");
                abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "B");
                abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "C");
                abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "D");
                abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "E");
                abpTester.abpSimulator( AbpInfra.externalInput.TOSEND, "V");

            }

        }

    }
}
