package android.util

/**
 * This class is needed to mock android.util.LongSparseArray for the tests
 */
class LongSparseArray<E> {

    private val mHashMap: HashMap<Long, E> = HashMap()

    fun put(key: Long, value: E) {
        mHashMap[key] = value
    }

    operator fun get(key: Long): E? {
        return mHashMap[key]
    }

    fun size(): Int {
        return mHashMap.size
    }

    fun valueAt(position: Int): E? {

        mHashMap.entries.forEachIndexed { index, mutableEntry -> if (index == position) return mutableEntry.value }

        return null
    }
}