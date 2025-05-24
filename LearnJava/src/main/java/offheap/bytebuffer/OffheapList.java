package offheap.bytebuffer;
import java.nio.ByteBuffer;

/**
 * NIO 非直接内存（堆外内存） ： 将数据存储在堆外内存中，从而提高性能
 * 使用ByteBuffer存储数组
 */

public class OffheapList {
    // 每个元素的大小（假设存储int类型）
    private static final int ELEMENT_SIZE = 4;
    // 初始容量
    private static final int INITIAL_CAPACITY = 1000;
    // 堆外内存缓冲区
    private ByteBuffer buffer;
    // 当前元素数量
    private int size;
    // 容量
    private int capacity;

    public OffheapList() {
        this.capacity = INITIAL_CAPACITY;
        // 分配直接内存（堆外内存）
        this.buffer = ByteBuffer.allocateDirect(capacity * ELEMENT_SIZE);
        this.size = 0;
    }

    // 添加元素
    public void add(int value) {
        if (size >= capacity) {
            // 扩容
            resize();
        }
        // 将元素写入堆外内存
        buffer.putInt(size * ELEMENT_SIZE, value);
        size++;
    }

    // 获取元素
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        // 从堆外内存读取元素
        return buffer.getInt(index * ELEMENT_SIZE);
    }

    // 获取大小
    public int size() {
        return size;
    }


    /**
     * 释放堆外内存
     */
    public void free() {
        if (buffer.isDirect()) {
            try {
                // 通过反射获取 Cleaner
                java.lang.reflect.Field cleanerField = buffer.getClass().getDeclaredField("cleaner");
                cleanerField.setAccessible(true);
                sun.misc.Cleaner cleaner = (sun.misc.Cleaner) cleanerField.get(buffer);
                if (cleaner != null) {
                    cleaner.clean();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扩容
     */
    private void resize() {
        int newCapacity = capacity * 2;
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity * ELEMENT_SIZE);

        // 复制原有数据
        buffer.position(0);
        newBuffer.put(buffer);

        // 释放原内存
        free();

        // 更新引用
        buffer = newBuffer;
        capacity = newCapacity;
    }

    public static void main(String[] args) {
        OffheapList list = new OffheapList();

        // 添加元素
        for (int i = 0; i < 100000; i++) {
            list.add(i);
        }

        // 获取元素
        System.out.println("Element at index 500: " + list.get(500));

        // 释放堆外内存
        list.free();
    }
}