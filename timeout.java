import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Executors;

public class timeout {
	static List<ScheduledFuture<?>> runningTasks = new ArrayList<>();
	static ScheduledThreadPoolExecutor executor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(30);
	public static void main(String[] args)
	{
		System.out.println("Application starting....");
		startTimeout();					
		executor.schedule(() -> {
                        System.out.println("Download from FTP starting...");
			dowloadFTP();
                }, 9000, TimeUnit.MILLISECONDS);
	}

	static void dowloadFTP()
	{
		try {
			stopTimeout();
			throw new IOException("File crash");
		}catch (IOException e)
		{
			System.out.println("Error while downloading file from FTP, reset timeout now " + e.getMessage());
			startTimeout();
		}

	}

	static void startTimeout()
	{
		System.out.println("Waiting to check status of event DownloadUpdatePackageFTP by timeout 50s");
		ScheduledFuture<?> task = executor.schedule(() -> {
			System.out.println("timed out, download ftp task failed");
		}, 10000, TimeUnit.MILLISECONDS);
		runningTasks.add(task);
	}
	    
	static void stopTimeout() {
        	System.out.println("Stop Timeout (50s) now");
        	for(ScheduledFuture<?> task: runningTasks) {
            		task.cancel(true);
        	}
        	runningTasks.clear();
    	}
}
