package il.ac.bgu.cs.bp.bpjs.context.AlternatingBit;

public interface EventList
{
    boolean add(Event e);
    Event removeNext();
    String toString();
    Event removeTimer(int entity);
    double getLastPacketTime(int entityTo);
}
