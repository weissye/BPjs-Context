package il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit;

public class TestElement implements Comparable<TestElement> {
    
    private String testEvent;
    private int stepNo;

    public TestElement(String testEvent, int stepNo) {
        this.setTestEvent(testEvent);
        this.setStepNo(stepNo);
    }

    public TestElement(String testEvent) {
        this.setTestEvent(testEvent);
        this.setStepNo(-1);
    }

    public TestElement(TestElement t) {
        this.testEvent = t.getTestEvent();
        this.stepNo = t.getStepNo();
    }


    public int getStepNo() {
        return stepNo;
    }

    public void setStepNo(int stepNo) {
        this.stepNo = stepNo;
    }

    public String getTestEvent() {
        return testEvent;
    }

    public void setTestEvent(String testEvent) {
        this.testEvent = testEvent;
    }

    @Override
    public int compareTo(TestElement t) {
        return t.stepNo < this.stepNo ? 1 : -1;
    }

    @Override
    public String toString() {
        return "testEvent: " + this.testEvent + ", stepNo: " + this.stepNo;
    }

}
