package edu.buptsse.youchat.util.communication;

import java.io.*;

public class SerializeUtil {
    private SerializeUtil() {}

    /**
     *
     * @param object 转换成字节数组的对象
     * @return 对象对应的字节数组，异常发生返回null
     */
    public static byte[] object2Bytes(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param bytes 转换成对象的字节数组
     * @return 字节数组对应的对象，异常发生返回null
     */
    public static Object bytes2Object(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)){
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
