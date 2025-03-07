class SharedResource {
    synchronized void criticalSection() {
        System.out.println(Thread.currentThread().getName() + " đã vào");
        for (int i = 0; i < 10; i++) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " phát hiện bị interrupt, thoát!");
                return;
            }
            System.out.println(Thread.currentThread().getName() + " đang xử lý...");
        }
        System.out.println(Thread.currentThread().getName() + " thoát");
    }
}

public class TestInterrupt {
    public static void main(String[] args) throws InterruptedException {
        SharedResource resource = new SharedResource();
        Thread t1 = new Thread(() -> resource.criticalSection(), "T1");

        t1.start();
        Thread.sleep(2000);
        System.out.println("Gửi interrupt cho T1");
        t1.interrupt();
    }
}
