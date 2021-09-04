package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import java.util.LinkedList;
import java.util.Queue;

public class myAbpFunctions {

    Queue<l3Channel> t2r = new LinkedList<l3Channel>();
    Queue<l3Channel> r2t = new LinkedList<l3Channel>();
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



//    ##########################################
//            # Transmitter actions
//# send:    send next message that has not been acknowledged yet:
//            #          Append the message to the transmitter-to-receiver channel.
//    input "t:send" {
//        guard { return len(t2r) < CHN_SIZE and send_next < len(TO_BE_SENT) }
//        body { t2r.append((t_seq, TO_BE_SENT[send_next])) }
//    }
//# ack ok:  receive expected acknowledgement from the receiver.
//            #          Consider acknowledged message successfully received.
//#          Increase sequence number for next messages.
//    input "t:ack ok" {
//        guard { return r2t and r2t[0] == (t_seq + 1 if t_seq + 1 <= SEQ_MAX else 0) }
//        body {
//            r2t.pop(0)
//            t_seq += 1
//            if t_seq > SEQ_MAX:
//            t_seq = 0
//            send_next += 1
//        }
//    }
//    input "t:ack nok" {
//        guard { return r2t and r2t[0] != (t_seq + 1 if t_seq + 1 <= SEQ_MAX else 0) }
//        body {
//            r2t.pop(0)
//        }
//    }

    public void sendData(String data) {
        l3Channel t = new l3Channel(tSeq, data);
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
        l3Channel x = t2r.remove();
        String payload = x.getData();
        rSeq = (rSeq + 1) % SEQ_MAX;
        l3Channel t = new l3Channel(rSeq);
        r2t.add(t);
        received.add(payload);
    }
        public void sendNakMsg(){
            t2r.remove();
            l3Channel t = new l3Channel(rSeq);
            r2t.add(t);
        }
//        abp.lostT2r();
//        continue;
//        } else if (ifLostR2t()) {
//        abp.lostR2t();
//        continue;
//        } else if (ifReorderT2r()) {
//        abp.reorderT2r();
//        continue;
//        } else if (ifReorderR2t()) {
//        abp.reorderR2t();
//        continue;


}
