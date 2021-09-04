package il.ac.bgu.cs.bp.bpjs.myContext.myAbp;

public class l3Channel {
    private int secNo;
    private String data;

    public l3Channel(int secNo, String data) {
        this.secNo = secNo;
        this.data = data;
    }
    public l3Channel(int secNo) {
        this.secNo = secNo;
        this.data = "Dummy";
    }
    public int getSecNo() {
        return secNo;
    }

    public String getData() {
        return data;
    }

    public void setSecNo(int secNo) {
        this.secNo = secNo;
    }

    public void setData(String data) {
        this.data = data;
    }
}
