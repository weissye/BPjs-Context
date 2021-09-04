package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit.TestElement;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class abpProcess {

    myAbpFunctions abp = new myAbpFunctions();
    private enum states {
        NONE, SEND, ACKOK, ACKNOK, RECACK, RECNAK, R2TLOST, R2TREORDER, T2RLOST, T2RREORDER, SUCCESS, LOSTERROR, DUPERROR
    }
    private states senderState = states.NONE ;
    private states receiverState = states.NONE;


    private boolean toggle = true;
    private String[] TO_BE_SENT = new String[] {"a", "b", "c"};

    public boolean haveToSend() {
        return (abp.t2r.size() < abp.getCHN_SIZE() && abp.getSendNext() < TO_BE_SENT.length);
    }
    public boolean getAckOk(){
        return (!abp.r2t.isEmpty() && abp.r2t.peek().getSecNo() == (abp.gettSeq()+1)%abp.getSEQ_MAX());
    }
    public boolean getAckNok(){
        return (!abp.r2t.isEmpty() && abp.r2t.peek().getSecNo() != (abp.gettSeq()+1)%abp.getSEQ_MAX());
    }
    public boolean sendAck(){
        return (!abp.t2r.isEmpty() && abp.t2r.peek().getSecNo() == abp.getrSeq() && abp.r2t.size() < abp.getCHN_SIZE());
    }
    public boolean sendNak(){
        return (!abp.t2r.isEmpty() && abp.t2r.peek().getSecNo() != abp.getrSeq() && abp.r2t.size() < abp.getCHN_SIZE());
    }
    public boolean ifLostT2r(){
        return (!abp.t2r.isEmpty() && abp.getCHN_LOSS());
    }
    public boolean ifLostR2t(){
        return (!abp.r2t.isEmpty() && abp.getCHN_LOSS());
    }
    public boolean ifReorderT2r(){
        return (!abp.t2r.isEmpty() && abp.getCHN_REORDER());
    }
    public boolean ifReorderR2t(){
        return (!abp.r2t.isEmpty() && abp.getCHN_REORDER());
    }
    public boolean chckEndingStatus() {
        return Arrays.equals(abp.received.toArray(), TO_BE_SENT);
    }

    public void runSimulator() {
        while (true) {
            if (haveToSend() && toggle){
                abp.sendData(TO_BE_SENT[abp.getSendNext()]);
                System.out.println("//send-"+TO_BE_SENT[abp.getSendNext()]);
                continue;
            } else if (getAckOk()) {
                abp.receivedAckOk();
                System.out.println("//receivedAckOk");
                continue;
            } else if (getAckNok()) {
                abp.receiveAckNok();
                System.out.println("//receiveAckNok");
                continue;
            } else if (sendAck()) {
                abp.sentAckMsg();
                System.out.println("//sentAckMsg");
                continue;
            } else if (sendNak()) {
                abp.sendNakMsg();
                System.out.println("//sendNakMsg");
                continue;
//            } else if (ifLostT2r()) {
//                abp.lostT2r();
//                continue;
//            } else if (ifLostR2t()) {
//                abp.lostR2t();
//                continue;
//            } else if (ifReorderT2r()) {
//                abp.reorderT2r();
//                continue;
//            } else if (ifReorderR2t()) {
//                abp.reorderR2t();
//                continue;
            } else if (abp.getSendNext() == TO_BE_SENT.length){
                if (chckEndingStatus())
                    System.out.println("//Success - "+abp.received.toString());
                else
                    System.out.println("//Failed- "+abp.received.toString());
                break;
            }
            toggle = !toggle;

        }
    }


}
