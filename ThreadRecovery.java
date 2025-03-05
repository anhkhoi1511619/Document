import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadRecovery {
    static final int THREAD_POOL_SIZE_FOR_EACH_IP = 50;
    static final HashMap<String, ScheduledThreadPoolExecutor> commExecutor = new HashMap<>();
    static List<ScheduledFuture<?>> runningTasks = new ArrayList<>();
    static ScheduledThreadPoolExecutor executor;
    private static final Map<String, Thread> threadMap = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        System.out.println("Application starting....");
        
        executor = new ScheduledThreadPoolExecutor(50, new ThreadFactory() {
            private int count = 0;
            
            @Override
            public Thread newThread(Runnable r) {
                count++;
                String threadName = "Thread-" + count;
                Thread thread = new Thread(r, threadName);
                threadMap.put(threadName, thread);  // Lưu vào danh sách theo dõi
                return thread;
            }
        });
        
        for (int i = 0; i < 100; i++) {
            System.out.println("Loop: "+i+"times");
            sendAsync(true, "192.168.254.45", 50001, true, i);
            sendAsync(true, "192.168.254.46", 50001, true, i);
        }
        
        // Định kỳ kiểm tra trạng thái thread
        executor.scheduleAtFixedRate(ThreadRecovery::checkThreadStatus, 5, 2, TimeUnit.SECONDS);
    }
    
    private static void checkThreadStatus() {
        System.out.println("==== Kiểm tra trạng thái thread ====");
        for (Map.Entry<String, Thread> entry : threadMap.entrySet()) {
            Thread thread = entry.getValue();
            Thread.State state = thread.getState();
            
            if (state == Thread.State.TERMINATED) {
                System.out.println(entry.getKey() + " đã kết thúc.");
            } else if (state == Thread.State.WAITING) {
                System.out.println(entry.getKey() + " đang chờ (" + state + ").");
            }else if (state == Thread.State.TIMED_WAITING) {
                System.out.println(entry.getKey() + " đang tạm dừng (" + state + "). Interuptted Now");
                thread.interrupt();
                System.out.println(entry.getKey() + " đang tạm dừng (" + state + "). Interuptted Done");
            }
        }
    }
    
    public static Future<Boolean> sendAsync(boolean lcpComSrv, String address, int port, boolean critical, int count) {
        if (!commExecutor.containsKey(address)) {
            System.out.println("Adding " + address + " to connection pool ");
            commExecutor.put(address, executor); // Sử dụng chung một pool
        }
        var executor = commExecutor.get(address);
        return executor.submit(() -> send(lcpComSrv, address, port, critical, count));
    }
    
    public static boolean send(boolean lcpComSrv, String address, int port, boolean critical, int count) {
        Thread currentThread = Thread.currentThread();
        threadMap.put(currentThread.getName(), currentThread);  // Cập nhật danh sách thread đang chạy
        var random = Math.random()* 5000;
        System.out.println("Communicating "+(int)random/1000+"s to " + address + ":" + port + " in Thread: " + currentThread.getName());
        
        try {
            Thread.sleep((long) (random)); // Mô phỏng độ trễ xử lý
        } catch (InterruptedException e) {
            System.out.println(currentThread.getName() + " nhận được interrupt!");
            return false;
        }
        
        System.out.println("Successfully communicated with " + address+ " in Thread: " + currentThread.getName());
        return true;
    }
}
