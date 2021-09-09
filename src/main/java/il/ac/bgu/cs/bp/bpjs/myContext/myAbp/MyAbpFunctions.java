package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MyAbpFunctions {

    Queue<L3Msg> t2r = new LinkedList<L3Msg>();
    Queue<L3Msg> r2t = new LinkedList<L3Msg>();
    Queue<String> received = new LinkedList<String>();


    private int tSeq = 0;
    private int rSeq = 0;
    private int SEQ_MAX = 2;
    private int CHN_SIZE = 2;


    private boolean CHN_LOSS = true;
    private boolean CHN_REORDER = true;

    private int sendNext = 0;


    public Queue<String> getReceived() {
        return received;
    }
    public void setReceived(Queue<String> received) {
        this.received = received;
    }
    public int gettSeq() {
        return tSeq;
    }
    public void settSeq(int tSeq) {
        this.tSeq = tSeq;
    }
    public int getrSeq() {
        return rSeq;
    }
    public void setrSeq(int rSeq) {
        this.rSeq = rSeq;
    }
    public int getSEQ_MAX() {
        return SEQ_MAX;
    }
    public void setSEQ_MAX(int SEQ_MAX) {
        this.SEQ_MAX = SEQ_MAX;
    }
    public int getCHN_SIZE() {
        return CHN_SIZE;
    }
    public void setCHN_SIZE(int CHN_SIZE) {
        this.CHN_SIZE = CHN_SIZE;
    }
    public int getSendNext() {
        return sendNext;
    }
    public void setSendNext(int sendNext) {
        this.sendNext = sendNext;
    }
    public boolean getCHN_LOSS() {
        return CHN_LOSS;
    }
    public void setCHN_LOSS(boolean CHN_LOSS) {this.CHN_LOSS = CHN_LOSS; }
    public boolean getCHN_REORDER() {
        return CHN_REORDER;
    }

    public void setCHN_REORDER(boolean CHN_REORDER) {
        this.CHN_REORDER = CHN_REORDER;
    }


    public void sendData(String data) {
        L3Msg t = new L3Msg(tSeq, data);
        t2r.add(t);
        System.out.println(t2r.toString());
    }
    public void receivedAckOk(){
        r2t.remove();
        tSeq = (tSeq + 1) % SEQ_MAX;
        sendNext += 1;
    }
    public void receiveAckNok(){
        r2t.remove();
    }

    public void sentAckMsg(){
        L3Msg x = t2r.remove();
        String payload = x.getData();
        rSeq = (rSeq + 1) % SEQ_MAX;
        L3Msg t = new L3Msg(rSeq);
        r2t.add(t);
        received.add(payload);
    }
        public void sendNakMsg(){
            t2r.remove();
            L3Msg t = new L3Msg(rSeq);
            r2t.add(t);
        }
        public void lostT2r(){
            t2r.remove();
        }
        public void lostR2t(){
            r2t.remove();
        }

        public void reorderT2r() {
            reversequeue(t2r);
        }

    public void reorderR2t() {
        reversequeue(r2t);
    }

    static void reversequeue(Queue <L3Msg> queue)
    {
        Stack<L3Msg> stack = new Stack<>();
        while (!queue.isEmpty()) {
            stack.add(queue.peek());
            queue.remove();
        }
        while (!stack.isEmpty()) {
            queue.add(stack.peek());
            stack.pop();
        }
    }

}
