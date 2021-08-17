package il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class StudentNetworkSimulator extends NetworkSimulator
{
    /*
     * Predefined Constants (static member variables):
     *
     *   int MAXDATASIZE : the maximum size of the Message data and
     *                     Packet payload
     *
     *   int A           : a predefined integer that represents entity A
     *   int B           : a predefined integer that represents entity B
     *
     *
     * Predefined Member Methods:
     *
     *  void stopTimer(int entity):
     *       Stops the timer running at "entity" [A or B]
     *  void startTimer(int entity, double increment):
     *       Starts a timer running at "entity" [A or B], which will expire in
     *       "increment" time units, causing the interrupt handler to be
     *       called.  You should only call this with A.
     *  void toLayer3(int callingEntity, Packet p)
     *       Puts the packet "p" into the network from "callingEntity" [A or B]
     *  void toLayer5(int entity, String dataSent)
     *       Passes "dataSent" up to layer 5 from "entity" [A or B]
     *  double getTime()
     *       Returns the current time in the simulator.  Might be useful for
     *       debugging.
     *  void printEventList()
     *       Prints the current event list to stdout.  Might be useful for
     *       debugging, but probably not.
     *
     *
     *  Predefined Classes:
     *
     *  Message: Used to encapsulate a message coming from layer 5
     *    Constructor:
     *      Message(String inputData):
     *          creates a new Message containing "inputData"
     *    Methods:
     *      boolean setData(String inputData):
     *          sets an existing Message's data to "inputData"
     *          returns true on success, false otherwise
     *      String getData():
     *          returns the data contained in the message
     *  Packet: Used to encapsulate a packet
     *    Constructors:
     *      Packet (Packet p):
     *          creates a new Packet that is a copy of "p"
     *      Packet (int seq, int ack, int check, String newPayload)
     *          creates a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and a
     *          payload of "newPayload"
     *      Packet (int seq, int ack, int check)
     *          create a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and
     *          an empty payload
     *    Methods:
     *      boolean setSeqnum(int n)
     *          sets the Packet's sequence field to "n"
     *          returns true on success, false otherwise
     *      boolean setAcknum(int n)
     *          sets the Packet's ack field to "n"
     *          returns true on success, false otherwise
     *      boolean setChecksum(int n)
     *          sets the Packet's checksum to "n"
     *          returns true on success, false otherwise
     *      boolean setPayload(String newPayload)
     *          sets the Packet's payload to "newPayload"
     *          returns true on success, false otherwise
     *      int getSeqnum()
     *          returns the contents of the Packet's sequence field
     *      int getAcknum()
     *          returns the contents of the Packet's ack field
     *      int getChecksum()
     *          returns the checksum of the Packet
     *      int getPayload()
     *          returns the Packet's payload
     *
     */

    // Add any necessary class variables here.  Remember, you cannot use
    // these variables to send messages error free!  They can only hold
    // state information for A or B.
    // Also add any necessary methods (e.g. checksum of a String)

    private final int ACK = 1;
    private final int WINDOW_SIZE = 8;
    private final int BUFFER_SIZE = 50;
    private final double TIME_UNITS = 50.0;

    private int mSequence;                  // Sequence number of individual packets
    private int mBase;                      // The sequence number of the last unacknowledged packet
    private int mNextSequence;              // The next sequence number outside of the current window
    private int mExpectedSequenceNumber;    // The sequence number the receiver expects to receive
    private int mLastACKSequence;           // The last sequence that the receiver gave an ACK
    private boolean mTimerAvailable;        // Used to coordinate whether the timer is currently in use
    private Queue<Packet> mPacketBuffer;    // Buffer to hold the messages that have no received ACK

    // Variables used for gathering statistics
    private int mPacketsTransmitted;        // Packets transmitted
    private int mNumberOfACK;               // Packets that received an ACK
    private int mRetransmissions;           // Packets that have been re-transmitted
    private int mCorruptPacketsReceived;    // Corrupt packets received
    private int mLostORCorrupt;             // How many times a re-transmit was necessary due to lost or corrupt packets
    private int mTotalRTT;                  // A sum of all RTTs
    private int mRTTCount;                  // Number of RTTs to calculate for average
    private HashMap<Integer, Double>
            mStartTimes;                    // Keeps track of start times for individual packets
    private boolean mComments = false;       // For my use: turn on and off comments for debugging.



    // This is the constructor.  Don't touch!
    public StudentNetworkSimulator(int numMessages,
                                   double loss,
                                   double corrupt,
                                   double avgDelay,
                                   int trace,
                                   long seed, PriorityQueue<TestElement> q)
    {
        super(numMessages, loss, corrupt, avgDelay, trace, seed, q);
    }

    @Override
    public void runSimulator()
    {
        super.runSimulator();

        // Print statistics when program stops
        printStatistics();
    }

    /**
     * Creates checksum by adding the sequence, ack, and each
     * character of the message.
     */
    private int createChecksum(int sequence, int ack, String message)
    {
        int checksum = 0;
        checksum+= sequence;
        checksum+= ack;

        for(int i = 0; i < message.length(); i++)
            checksum+= message.charAt(i);

        return checksum;
    }

    /**
     * Helper method to create packets so code re-use can be cut down.
     */
    private Packet createPacket(int sequence, String payload)
    {
        int checksum = createChecksum(sequence, ACK, payload);
        return new Packet(sequence, ACK, checksum, payload);
    }

    /**
     * Helper method to add Packets to queue and deal with state variables as well as exceptions
     */
    private boolean addToQueue(Packet packet)
    {
        try
        {
            mPacketBuffer.add(packet);
            return true;
        }
        catch(IllegalStateException e)
        {
            return false;
        }
    }

    /**
     * Determines whether the mUnsentMessages checksum is the same as
     * the expected checksum.
     */
    private boolean isPacketCorrupt(Packet packet)
    {
        int checksum = createChecksum(packet.getSeqnum(),
                packet.getAcknum(),
                packet.getPayload());

        return checksum != packet.getChecksum();
    }

    /**
     * Calculates the average RTT and displays all the statistics gathered
     */
    private void printStatistics()
    {
        double averageTime = mRTTCount > 0 ? (mTotalRTT / (double) mRTTCount) : 0.0;
        System.out.println("Statistics\n" +
                "Number of packets transmitted: " + mPacketsTransmitted + "\n" +
                "Number of re-transmissions: " + mRetransmissions + "\n" +
                "Number of ACK packets: " + mNumberOfACK + "\n" +
                "Number of corrupt packets: " + mCorruptPacketsReceived + "\n" +
                "Re-transmits due to corrupt or lost packets: " + (mLostORCorrupt) + "\n" +
                "Average RTT: " + averageTime);
    }

    // This routine will be called whenever the upper layer at the sender [A]
    // has a message to send.  It is the job of your protocol to insure that
    // the data in such a message is delivered in-order, and correctly, to
    // the receiving upper layer.
    protected void aOutput(Message message)
    {
        if((mNextSequence - mBase) < WINDOW_SIZE)
        {
            // Create packet and send it to side B
            Packet packet = createPacket(mSequence++, message.getData());

            if(addToQueue(packet))
            {
                toLayer3(A, packet);
                mStartTimes.put(packet.getSeqnum(), getTime());

                // Update state and statistics counter
                ++mNextSequence;
                mPacketsTransmitted++;

                if(mComments)
                    System.out.println("aOutput sent packet: " + packet.toString());
                if(mComments)
                    System.out.println("Window size: " + (mNextSequence - mBase) + "\n");

                if(mTimerAvailable)
                {
                    startTimer(A, TIME_UNITS);
//                    System.out.println("aOutput: started timer\n");
                    mTimerAvailable = false;
                }
            }
            else
            {
                if(mComments)
                    System.out.println("aOutput: Buffer is full, dropping message");
            }
        }
        else
        {
            if(mComments)
                System.out.println("aOutput: Window is full, dropping message");
        }
    }

    // This routine will be called whenever a packet sent from the B-side
    // (i.e. as a result of a toLayer3() being done by a B-side procedure)
    // arrives at the A-side.  "packet" is the (possibly corrupted) packet
    // sent from the B-side.
    protected void aInput(Packet packet)
    {
        if(mComments)
            System.out.println("aInput received packet: " + packet.toString() + " base: " + mBase);

        double arrivalTime = getTime();
        int sequenceNumber = packet.getSeqnum();
        Double startTime = mStartTimes.get(sequenceNumber);

        // Aggregate time round trips for each packet sent/received
        if(startTime != null)
        {
            mTotalRTT += arrivalTime - startTime.doubleValue();
            mRTTCount++;
        }

        boolean outOfOrder = sequenceNumber <= mBase || sequenceNumber >= mNextSequence;

        // Let timer expire for corrupt packets
        if (isPacketCorrupt(packet))
        {
            mCorruptPacketsReceived++;

            if(mComments)
                System.out.println("aInput found corrupt packet, let timer expire.\n");
        }
        else if (outOfOrder)
        {
            if(mComments)
                System.out.println("aInput found out of order or duplicate ACK, let timer expire.\n");
        }
        // Handle packets that are ACK
        else
        {
            if(!mTimerAvailable)
            {
                stopTimer(A);
                mTimerAvailable = true;
            }

            // Need to pop off all sequence numbers that have been acknowledged
            while(!mPacketBuffer.isEmpty())
            {
                if(mPacketBuffer.peek().getSeqnum() <= sequenceNumber)
                {
                    Packet evictedPacket = mPacketBuffer.poll();

                    if (mComments)
                        System.out.println("aInput: Popped off " + evictedPacket.toString());

                    mStartTimes.remove(evictedPacket.getSeqnum());
                }
                else
                {
                    break;
                }
            }

            mBase = packet.getSeqnum();

            if(mComments)
                System.out.println("aInput: cumulative ACK received, stopping timer. Next Sequence: "
                        + mNextSequence + "\n");
        }
    }

    // This routine will be called when A's timer expires (thus generating a
    // timer interrupt). You'll probably want to use this routine to control
    // the retransmission of mUnsentMessages. See startTimer() and stopTimer(), above,
    // for how the timer is started and stopped.
    protected void aTimerInterrupt()
    {
        mTimerAvailable = true;
        mLostORCorrupt++;
        if(mComments)
            System.out.println("Timer expired, re-transmitting window.");

        // Re-transmit all unacknowledged packets and restart timer
        for(Packet packet : mPacketBuffer)
        {
            toLayer3(A, packet);
            mRetransmissions++;
            mStartTimes.put(packet.getSeqnum(), getTime());

            if(mComments)
                System.out.println("Re-sending: " + packet.toString());

            if(mTimerAvailable)
            {
                startTimer(A, TIME_UNITS);
                mTimerAvailable = false;
            }
        }
    }

    // This routine will be called once, before any of your other A-side
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity A).
    protected void aInit()
    {
        // Initialize state variables
        mSequence = 0;
        mNextSequence = 0;
        mBase = -1;
        mTimerAvailable = true;

        mPacketBuffer = new ArrayBlockingQueue<Packet>(BUFFER_SIZE);

        // Initialize statistics variables
        mPacketsTransmitted = 0;
        mNumberOfACK = 0;
        mLostORCorrupt = 0;
        mTotalRTT = 0;
        mRTTCount = 0;
        mStartTimes = new HashMap<Integer, Double>();
    }

    // This routine will be called whenever a packet sent from the B-side
    // (i.e. as a result of a toLayer3() being done by an A-side procedure)
    // arrives at the B-side.  "packet" is the (possibly corrupted) packet
    // sent from the A-side.
    protected void bInput(Packet packet)
    {
        if(mComments)
            System.out.println("bInput received packet: " + packet.toString());

        String payload = "data";
        Packet responsePacket;

        boolean corrupt = isPacketCorrupt(packet);
        boolean retransmission = packet.getSeqnum() != mExpectedSequenceNumber;

        if(corrupt || retransmission)
        {
            responsePacket = createPacket(mLastACKSequence, payload);

            if(corrupt)
                mCorruptPacketsReceived++;
            if(mComments)
                System.out.println("bInput: detected corrupt or out of order packet, sending sequence of lastACK\n");
        }
        else
        {
            // Data is good, send it up
            toLayer5(B, packet.getPayload());

            // Create ACK packet
            responsePacket = createPacket(mExpectedSequenceNumber, payload);

            // Update state and statistics
            mLastACKSequence = mExpectedSequenceNumber++;
            mNumberOfACK++;
            mPacketsTransmitted++;

            if(mComments)
                System.out.println("bInput: packet is error free, sending ACK packet.\n");
        }

        toLayer3(B, responsePacket);
        mPacketsTransmitted++;
    }

    // This routine will be called once, before any of your other B-side
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity B).
    protected void bInit()
    {
        mExpectedSequenceNumber = 0;
        mLastACKSequence = -1;
        mRetransmissions = 0;
        mCorruptPacketsReceived = 0;
    }
}
