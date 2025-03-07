import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadRecovery {
    static final int THREAD_POOL_SIZE_FOR_EACH_IP = 50;
    static final HashMap<String, ScheduledThreadPoolExecutor> commExecutor = new HashMap<>();
    static List<ScheduledFuture<?>> runningTasks = new ArrayList<>();
    static ScheduledThreadPoolExecutor executor= new ScheduledThreadPoolExecutor(50, new ThreadFactory() {
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
    private static final Map<String, Thread> threadMap = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        System.out.println("Application starting....");
        
        // Định kỳ kiểm tra trạng thái thread
        //executor.scheduleAtFixedRate(ThreadRecovery::checkThreadStatus, 0, 1, TimeUnit.SECONDS);
        // Định kỳ kiểm tra trạng thái thread
        executor.scheduleAtFixedRate(ThreadRecovery::run, 0, 100, TimeUnit.MILLISECONDS);
        
    }
    
    
    private static void checkThreadStatus() {
        StringBuilder statusLog = new StringBuilder("==== CHECK READER THREAD STATUS ====\n");
        StringBuilder interruptedLog = new StringBuilder("==== THREADS REQUESTED FOR INTERRUPT ====\n");
    
        // Sắp xếp danh sách thread theo thứ tự Thread-1, Thread-2, ..., Thread-50
        List<Map.Entry<String, Thread>> sortedThreads = new ArrayList<>(threadMap.entrySet());
        sortedThreads.sort(Comparator.comparing(entry -> Integer.parseInt(entry.getKey().replace("Thread-", ""))));
    
        for (Map.Entry<String, Thread> entry : sortedThreads) {
            String threadName = entry.getKey();
            Thread thread = entry.getValue();
            Thread.State state = thread.getState();
    
            // Xử lý trạng thái
            if (state == Thread.State.BLOCKED) {
                statusLog.append(String.format("[Thread-%s: BLOCKED], ", threadName.replace("Thread-", "")));
                interruptedLog.append(String.format("[Thread-%s], ", threadName.replace("Thread-", ""))); // Ghi log riêng
                thread.interrupt(); // Yêu cầu interrupt nhưng không xác nhận nó đã bị interrupt thành công
            } else {
                statusLog.append(String.format("[Thread-%s: %s], ", threadName.replace("Thread-", ""), state));
            }
        }
    
        // In ra log trạng thái thread, loại bỏ dấu phẩy cuối cùng nếu có
        System.out.println(statusLog.toString().replaceAll(", $", ""));
    
        // Nếu có thread bị BLOCKED và được yêu cầu interrupt, thì in dòng 2
        if (interruptedLog.length() > "==== THREADS REQUESTED FOR INTERRUPT ====\n".length()) {
            System.out.println(interruptedLog.toString().replaceAll(", $", ""));
        }
    }
    
    private static void run() {
        for (int i = 0; i < 200; i++) {
            sendAsync(true, "192.168.254.45", 50001, true, 1);
            sendAsync(true, "192.168.254.46", 50001, true, 1);
        }
    }
    
    public static Future<Boolean> sendAsync(boolean lcpComSrv, String address, int port, boolean critical, int count) {
        if (!commExecutor.containsKey(address)) {
            System.out.println("Adding " + address + " to connection pool ");
            commExecutor.put(address, executor); // Sử dụng chung một pool
        }
        var executor = commExecutor.get(address);
        System.out.println( "About to send to "+address+", sending queue is "+executor.getQueue().size() + " core pool size is "+executor.getCorePoolSize());
        // if(executor.getActiveCount() >= 27) {
		// 	System.out.println( "About to send to "+address+", connection pool is 40 "+executor.getActiveCount()+", check all thread status and reset if BLOCKED");
        //     checkThreadStatus();
        //     return null;
		// }
		if(executor.getActiveCount() > executor.getCorePoolSize()/2 && critical) {
			System.out.println( "About to send to "+address+", connection pool is half way full "+executor.getActiveCount()+", drop this package");
            checkThreadStatus();
			return null;
		}
		if(executor.getQueue().size() > 20) {
			System.out.println( "About to send to "+address+", connection queue is full "+executor.getQueue().size());
		}
		if(executor.getActiveCount() >= executor.getCorePoolSize() && critical) {
			System.out.println( "About to send to "+address+", connection pool is full "+executor.getActiveCount()+", this package may be stuck");
		}
        return executor.submit(() -> send(lcpComSrv, address, port, critical, count));
    }
    
    private static final Object lock = new Object();
    public static boolean send(boolean lcpComSrv, String address, int port, boolean critical, int count) {
        Thread currentThread = Thread.currentThread();
        var random = (int)(Math.random() * 50000);
        System.out.println("[Start] Send throughtout "+random+"s to " + address + ":" + port + " in Thread: " + currentThread.getName());
        threadMap.put(currentThread.getName(), currentThread);  // Cập nhật danh sách thread đang chạy
        
        synchronized (lock) { // Chặn thread khác vào đây
            System.out.println(Thread.currentThread().getName() + " is running");
            if (currentThread.isInterrupted()) {
                System.out.println(currentThread.getName()+ " is received request interrupt!. "+"[Ignore] Transmit command, " + lcpComSrv+ " -> " + address+" and receive command, " + lcpComSrv + " <-"+ address);
				return false;
			}
            System.out.println("Communicating "+random+"s to " + address + ":" + port + " in Thread: " + currentThread.getName());
            while (random-- != 0) {
                //System.out.println("Communicating "+random+"s to " + address + ":" + port + " in Thread: " + currentThread.getName());
            }
            System.out.println("Successfully communicated with " + address+ " in Thread: " + currentThread.getName());
            System.out.println(Thread.currentThread().getName() + " finish");
        }
        System.out.println("[End] Send throughtout "+(int)random/1000+"s to " + address + ":" + port + " in Thread: " + currentThread.getName());
        return true;
    }
}
