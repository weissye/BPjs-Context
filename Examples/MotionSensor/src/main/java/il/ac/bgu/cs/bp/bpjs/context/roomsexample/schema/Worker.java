package il.ac.bgu.cs.bp.bpjs.context.roomsexample.schema;

import il.ac.bgu.cs.bp.bpjs.context.roomsexample.schema.rooms.Room;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Worker extends BasicEntity {
    @Column
    private String name = "";

    @OneToOne
    private Room room = null;

    public Worker() {
        super();
    }

    public Worker(String id, String name) {
        super(id);
        this.name = name;
    }

    public Worker(String id, String name, Room room) {
        this(id, name);
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public String getName() {
        return name;
    }
}
