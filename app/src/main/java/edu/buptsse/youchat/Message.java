package edu.buptsse.youchat;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class Message implements Serializable {
    private String from;
    private String to;
    private byte[] data;
    private Date time;
    private Type dataType;

    private static final long serialVersionUID = 500382L;

    public Message(String from, String to, byte[] data, Date time, Type dataType) {
        this.from = from;
        this.to = to;
        this.data = data;
        this.time = time;
        this.dataType = dataType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Type getDataType() {
        return dataType;
    }

    public void setDataType(Type dataType) {
        this.dataType = dataType;
    }

    public String getText() {
        return new String(data);
    }

    public enum Type {
        TEXT,
        IMAGE,
        VIDEO,
        AUDIO,
        FILE,
        TCP_PORT_INFO,
        UDP_PORT_INFO,
        CALL
    }
}
