package edu.buptsse.youchat.util.communication

import java.io.*

object SerializeUtil {
    /**
     *
     * @param object 转换成字节数组的对象
     * @return 对象对应的字节数组，异常发生返回null
     */
    @JvmStatic
    fun object2Bytes(`object`: Any?): ByteArray? {
        try {
            ByteArrayOutputStream().use { bos ->
                ObjectOutputStream(bos).use { oos ->
                    oos.writeObject(`object`)
                    oos.flush()
                    return bos.toByteArray()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     *
     * @param bytes 转换成对象的字节数组
     * @return 字节数组对应的对象，异常发生返回null
     */
    @JvmStatic
    fun bytes2Object(bytes: ByteArray?): Any? {
        try {
            ByteArrayInputStream(bytes).use { bis -> ObjectInputStream(bis).use { ois -> return ois.readObject() } }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}
