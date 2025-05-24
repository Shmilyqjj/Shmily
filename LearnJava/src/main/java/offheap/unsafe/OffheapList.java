package offheap.unsafe;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

/**
 * 使用unSafe实现堆外内存分配 将数据存储在堆外内存中，从而提高性能
 * 使用unSafe存储数组
 */

public class OffheapList {
    private static final Unsafe unsafe;
    private long address;
    private long size;

    /**
     * 每个元素的大小（假设存储long类型 大小：8）
     */
    private static final int ELEMENT_SIZE = 8;

    static {
        try {
            // 通过反射获取 Unsafe 实例
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("无法获取 Unsafe 实例", e);
        }
    }

    public OffheapList(long size) {
        this.size = size * ELEMENT_SIZE;
        // 分配堆外内存
        this.address = unsafe.allocateMemory(this.size);
        // 内存初始化为 0
        unsafe.setMemory(address, this.size, (byte) 0);
    }

    // 写入数据
    public void putLong(long offset, long value) {
        if (offset * ELEMENT_SIZE > size) {
            throw new IndexOutOfBoundsException("max offheap size exceed.");
        }
        unsafe.putLong(address + offset * ELEMENT_SIZE, value);
    }

    // 读取数据
    public long getLong(long offset) {
        if (offset * ELEMENT_SIZE > size) {
            throw new IndexOutOfBoundsException("invalid offset address.");
        }
        return unsafe.getLong(address + offset * ELEMENT_SIZE);
    }

    // 释放内存
    public void free() {
        unsafe.freeMemory(address);
    }

    public static void main(String[] args) {
        OffheapList memory = new OffheapList(1024000);
        for (int i = 0; i < 1024000; i++) {
            memory.putLong(i, i * 2);
        }

        System.out.println("读取数据: " + memory.getLong(1024));
        // 手动释放内存
        memory.free();
    }
}