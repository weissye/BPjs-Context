package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

import java.util.*;

import static il.ac.bgu.cs.bp.bpjs.myContext.myAbp.AbpInfra.states.*;

public class AbpSender {

    private String newDate;
//    private AbpInfra.states senderState = AbpInfra.states.NONE ;
    private int tSeq = 0;
    private int sendNext = 0;

    private AbpInfra infra = new AbpInfra();

    List<String> TO_BE_SENT = new ArrayList<String>();

    public AbpSender() {
        super();
        this.newDate = "";
        this.tSeq = 0;
        this.sendNext = 0;
    }

    public String getNewDate() { return newDate; }
    public void setNewDate(String newDate) { this.newDate = newDate; }
    public boolean haveToSend() {
        return (infra.t2r.size() < infra.getCHN_SIZE() && sendNext < TO_BE_SENT.size());
    }
    public boolean getAckOk(){
        return (!infra.r2t.isEmpty() && infra.r2t.peek().getSecNo() == (tSeq+1)%infra.getSEQ_MAX());
    }
    public boolean getAckNok(){
        return (!infra.r2t.isEmpty() && infra.r2t.peek().getSecNo() != (tSeq+1)%infra.getSEQ_MAX());
    }
    public boolean ifLostT2r(){
        return (!infra.t2r.isEmpty() && infra.getCHN_LOSS());
    }
    public boolean ifReorderT2r(){
        return (!infra.t2r.isEmpty() && infra.getCHN_REORDER());
    }
    public boolean chckEndingStatus() {
        return Arrays.equals(infra.received.toArray(), TO_BE_SENT.toArray());
    }

    public void runSender(AbpInfra getInfra, AbpInfra.states senderState) {

        infra = getInfra;

        switch (senderState){
            case SEND:
                if (haveToSend())  {
                    sendData(TO_BE_SENT.get(sendNext));
                    System.out.println("//send-" + TO_BE_SENT.get(sendNext));
                }
                break;
            case ACKOK:
                if (getAckOk()) {
                    receivedAckOk();
                    System.out.println("//receivedAckOk");
                }
                break;
            case ACKNOK:
                if (getAckNok()) {
                    receiveAckNok();
                    System.out.println("//receiveAckNok");
                }
                break;
            case T2RLOSS:
                if (ifLostT2r()) {
                    lostT2r();
                    System.out.println("//T2RLOSS");
                }
                break;
            case T2RREORDER:
                if (ifReorderT2r()) {
                    reorderT2r();
                    System.out.println("//T2RREORDER");
                }
                break;
            case FINISH:
                if (sendNext == TO_BE_SENT.size()){
                    if (chckEndingStatus()) {
                        System.out.println("//Success - TO_BE_SEND-"+TO_BE_SENT+" Received-"+infra.received);
                        senderState = FINISH;
                    }
                    else
                        System.out.println("//Failed- "+infra.received);
                }
                break;

        }

    }

    public void sendData(String data) {
        System.out.println("//Sent-"+ data);
        L3Msg t = new L3Msg(tSeq, data);
        infra.t2r.add(t);
    }
    public void receivedAckOk(){
        System.out.println("//receivedAckOk");
        infra.r2t.remove();
        tSeq = (tSeq + 1) % infra.getSEQ_MAX();
        sendNext += 1;
    }
    public void receiveAckNok(){
        System.out.println("//receiveAckNok");
        infra.r2t.remove();
    }

    public void lostT2r(){
        System.out.println("//lostT2r");
        infra.t2r.remove();
    }
    public void reorderT2r() {
        System.out.println("//reorderT2r");
        infra.reversequeue(infra.t2r);
    }






}
