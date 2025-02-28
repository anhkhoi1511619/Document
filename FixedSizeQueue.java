import java.util.*;

public class FixedSizeQueue {
    private static final int MAX_SIZE = 10;
    private Deque<String> queue = new ArrayDeque<>(MAX_SIZE);

    public void add(String element) {
        if (queue.size() >= MAX_SIZE) {
            queue.pollFirst(); // Xóa phần tử đầu tiên nếu đã đủ 10 phần tử
        }
        queue.addLast(element); // Thêm phần tử mới vào cuối
    }

    public void printQueue() {
        System.out.println(queue);
    }

    public static void main(String[] args) {
        FixedSizeQueue myQueue = new FixedSizeQueue();
        for (int i = 1; i <= 12; i++) { // Thêm 12 phần tử để kiểm tra
            myQueue.add("Item " + i);
            myQueue.printQueue();
        }
    }
}
