package edu.buptsse.youchat;


import java.io.*;
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

    public File saveImage() {
        return null;
    }

    public File saveVideo() {
        return null;
    }

    public File saveAudio() {
        return null;
    }

    public File saveFile() {
        return null;
    }

    private void saveData(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum Type {
        TEXT,
        IMAGE,
        VIDEO,
        AUDIO,
        FILE,
        TCP_PORT_INFO,
        UDP_PORT_INFO,
        CALL,
        USER_SYSTEM         //data中的内容是由UserSystemMessage转字节而来
    }
}