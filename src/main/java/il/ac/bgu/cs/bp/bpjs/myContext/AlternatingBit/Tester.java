package il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit;

import java.io.*;
import java.util.PriorityQueue;

import il.ac.bgu.cs.bp.bpjs.myContext.Main;


public class Tester
{
    // private static Queue<TestElement> q = new LinkedList<>();
    // static PriorityQueue<TestElement> q = new PriorityQueue<>();

    public final static void main(String[] argv)
    {
        StudentNetworkSimulator simulator;
        
        int nsim = -1;
        double loss = -1.0;
        double corrupt = -1.0;
        double delay = 1000;
        int trace = 3;
        long seed = -1;
        String buffer = "";
        String[] test = new String[2];
    
        BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
                         
        System.out.println("Network Our Tester v1.0");
        
        while (!buffer.equals("q"))
        {
            System.out.print("Enter tests events 'L'-Lost mag, '[CT | CC | CS]'-Corrupt msg, 'T'-Timeout or 'q'-quit [q]: ");
            test[0] = "q";
            try
            {
                test = stdIn.readLine().split(" ");   
                // buffer = stdIn.readLine();   
                System.out.println("Buffer-"+test[0]);
                buffer = test[0];
            }
            catch (IOException ioe)
            {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }
            
            if (buffer.equals("L") || buffer.equals("CT") || buffer.equals("CC") || buffer.equals("CS") || buffer.equals("T"))
            {
                TestElement t = new TestElement(test[0], Integer.parseInt(test[1]));
                Main.q.add(t);
            }  
            else if (buffer.equals(""))
                    break;
            }

        for (TestElement item: Main.q) {
            System.out.println(item);
        }
    

        while (nsim < 1)
        {
            System.out.print("Enter number of messages to simulate (> 0): " +
                             "[10] ");
            try
            {
                buffer = stdIn.readLine();
            }
            catch (IOException ioe)
            {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }
            
            if (buffer.equals(""))
            {
                nsim = 10;
            }
            else
            {            
                try
                {
                    nsim = Integer.parseInt(buffer);
                }
                catch (NumberFormatException nfe)
                {
                    nsim = -1;
                }
            }
        }
        
        // while (loss < 0.0)
        // {
        //     System.out.print("Enter the packet loss probability (0.0 for no " +
        //                      "loss): [0.0] ");
        //     try
        //     {
        //         buffer = stdIn.readLine();
        //     }
        //     catch (IOException ioe)
        //     {
        //         System.out.println("IOError reading your input!");
        //         System.exit(1);
        //     }
            
        //     if (buffer.equals(""))
        //     {
        //         loss = 0.0;
        //     }
        //     else
        //     {            
        //         try
        //         {
        //             loss = (Double.valueOf(buffer)).doubleValue();
        //         }
        //         catch (NumberFormatException nfe)
        //         {
        //             loss = -1.0;
        //         }
        //     }
        // }            

        // while (corrupt < 0.0)
        // {
        //     System.out.print("Enter the packet corruption probability (0.0 " +
        //                      "for no corruption): [0.0] ");
        //     try
        //     {
        //         buffer = stdIn.readLine();
        //     }
        //     catch (IOException ioe)
        //     {
        //         System.out.println("IOError reading your input!");
        //         System.exit(1);
        //     }
            
        //     if (buffer.equals(""))
        //     {
        //         corrupt = 0.0;
        //     }
        //     else
        //     {            
        //         try
        //         {
        //             corrupt = (Double.valueOf(buffer)).doubleValue();
        //         }
        //         catch (NumberFormatException nfe)
        //         {
        //             corrupt = -1.0;
        //         }
        //     }
        // }            

        // while (delay <= 0.0)
        // {
        //     System.out.print("Enter the average time between messages from " +
        //                      "sender's layer 5 (> 0.0): [1000] ");
        //     try
        //     {
        //         buffer = stdIn.readLine();
        //     }
        //     catch (IOException ioe)
        //     {
        //         System.out.println("IOError reading your input!");
        //         System.exit(1);
        //     }
            
        //     if (buffer.equals(""))
        //     {
        //         delay = 1000.0;
        //     }
        //     else
        //     {            
        //         try
        //         {
        //             delay = (Double.valueOf(buffer)).doubleValue();
        //         }
        //         catch (NumberFormatException nfe)
        //         {
        //             delay = -1.0;
        //         }
        //     }
        // }            

        // while (trace < 0)
        // {
        //     System.out.print("Enter trace level (>= 0): [0] ");
        //     try
        //     {
        //         buffer = stdIn.readLine();
        //     }
        //     catch (IOException ioe)
        //     {
        //         System.out.println("IOError reading your input!");
        //         System.exit(1);
        //     }
            
        //     if (buffer.equals(""))
        //     {
        //         trace = 0;
        //     }
        //     else
        //     {            
        //         try
        //         {
        //             trace = Integer.parseInt(buffer);
        //         }
        //         catch (NumberFormatException nfe)
        //         {
        //             trace = -1;
        //         }
        //     }
        // }

        while (seed < 1)
        {
            System.out.print("Enter random seed: [random] ");
            try
            {
                buffer = stdIn.readLine();
            }
            catch (IOException ioe)
            {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }
            
            if (buffer.equals(""))
            {
                seed = System.currentTimeMillis();
            }
            else
            {            
                try
                {
                    seed = (Long.valueOf(buffer)).longValue();
                }
                catch (NumberFormatException nfe)
                {
                    seed = -1;
                }
            }
        }
         
        simulator = new StudentNetworkSimulator(nsim, loss, corrupt, delay,
                                                trace, seed, Main.q);
                                                
        simulator.runSimulator();
    }
}
