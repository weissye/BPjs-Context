package il.ac.bgu.cs.bp.bpjs.myContext.Events;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;


public class DataToBeSend extends BEvent
{


    public DataToBeSend(String data) {
        super();
        this.data = data;
    }
    private String data;

    @Override
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataToBeSend{" +
                "data='" + data + '\'' +
                '}';
    }


}



