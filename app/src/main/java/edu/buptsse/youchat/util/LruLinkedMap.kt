package edu.buptsse.youxuancheng.util

class LruLinkedMap<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(maxSize, 0.75f, false) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return this.size > maxSize
    }
}