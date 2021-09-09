package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import java.util.List;
import java.util.*;

public class AbpProcess {

    MyAbpFunctions abp = new MyAbpFunctions();
    private enum states {
        NONE, SEND, ACKOK, ACKNOK, RECACK, RECNAK, R2TLOST, R2TREORDER, T2RLOST, T2RREORDER, SUCCESS, LOSTERROR, DUPERROR;
        static
        public final states[] values = values();
        public states prev() { return values[(ordinal() - 1  + values.length) % values.length]; }
        public states next() { return values[(ordinal() + 1) % values.length]; }
    }
    public enum externatInput {
        NONE, SEND, R2TLOST, R2TREORDER, T2RLOST, T2RREORDER
    }

    private externatInput nextInput = externatInput.NONE;
    public externatInput getNextInput() {
        return nextInput;
    }
    public void setNextInput(externatInput nextInput) {
        this.nextInput = nextInput;
    }

    private String newDate;
    public String getNewDate() {
        return newDate;
    }
    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }

    private states protocolState = states.NONE ;
    private states receiverState = states.NONE;


    private boolean toggle = true;
//    private String[] TO_BE_SENT;
    List<String> TO_BE_SENT = new ArrayList<String>() ;

    public boolean haveToSend() {
        return (abp.t2r.size() < abp.getCHN_SIZE() && abp.getSendNext() < TO_BE_SENT.size());
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
        return Arrays.equals(abp.received.toArray(), TO_BE_SENT.toArray());
    }

    public void runSimulator() {
        if (nextInput != externatInput.NONE) {
            switch (nextInput) {
                case SEND:
                    TO_BE_SENT.add(newDate);
                    protocolState = states.SEND;
                    break;
                case R2TLOST:
                    protocolState = states.R2TLOST;
                    break;
                case T2RLOST:
                    protocolState = states.T2RLOST;
                    break;
                case T2RREORDER:
                    protocolState = states.T2RREORDER;
                    break;
                case R2TREORDER:
                    protocolState = states.R2TREORDER;
                    break;
            }
            nextInput = externatInput.NONE;
        }
        else
            protocolState = protocolState.next();
        switch (protocolState){
            case SEND:
                if (haveToSend() && toggle) {
                    abp.sendData(TO_BE_SENT.get(abp.getSendNext()));
                    System.out.println("//send-" + TO_BE_SENT.get(abp.getSendNext()));
                }
                break;
            case ACKOK:
                if (getAckOk()) {
                    abp.receivedAckOk();
                    System.out.println("//receivedAckOk");
                }
                break;
            case ACKNOK:
                if (getAckNok()) {
                    abp.receiveAckNok();
                    System.out.println("//receiveAckNok");
                }
                break;
            case RECACK:
                if (sendAck()) {
                    abp.sentAckMsg();
                    System.out.println("//sentAckMsg");
                }
                break;
            case RECNAK:
                if (sendNak()) {
                    abp.sendNakMsg();
                    System.out.println("//sendNakMsg");
                }
                break;
            case T2RLOST:
                if (ifLostT2r()) {
                    abp.lostT2r();
                }
                break;
            case R2TLOST:
                if (ifLostR2t()) {
                    abp.lostR2t();
                }
                break;
            case T2RREORDER:
                if (ifReorderT2r()) {
                    abp.reorderT2r();
                }
                break;
            case R2TREORDER:
                if (ifReorderR2t()) {
                    abp.reorderR2t();
                }
                break;
            }
            if (abp.getSendNext() == TO_BE_SENT.size()){
                if (chckEndingStatus())
                    System.out.println("//Success - "+abp.received.toString());
                else
                    System.out.println("//Failed- "+abp.received.toString());
            }
            toggle = !toggle;

    }


}
