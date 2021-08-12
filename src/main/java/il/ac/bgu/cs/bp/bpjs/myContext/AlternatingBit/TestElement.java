package il.ac.bgu.cs.bp.bpjs.myContext.AlternatingBit;

public class TestElement {
    
    private String testEvent;
    private int data;

    public TestElement(String testEvent, int data) {
        this.setTestEvent(testEvent);
        this.setData(data);
    }

    public TestElement(String testEvent) {
        this.setTestEvent(testEvent);
        this.setData(-1);
    }

    public TestElement(TestElement t) {
        this.testEvent = t.getTestEvent();
        this.data = t.getData();
    }


    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getTestEvent() {
        return testEvent;
    }

    public void setTestEvent(String testEvent) {
        this.testEvent = testEvent;
    }

}
