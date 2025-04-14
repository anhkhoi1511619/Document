import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KillProcessByPort {
    public static void main(String[] args) {
        int port = 52102;
        try {
            killProcessesByPort(port);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void killProcessesByPort(int port) throws IOException {
        Process process;
        process = Runtime.getRuntime().exec("lsof -i :" + port);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Found: " + line); // Debugging

                String[] parts = line.trim().split("\\s+");
                int pid = -1;
                try {
                    pid = Integer.parseInt(parts[1]); // Linux/macOS: PID ở cột thứ 2
                } catch (NumberFormatException ignored) {
                }

                if (pid > 0) {
                    killProcess(pid);
                }
            }
        }
    }

    private static void killProcess(int pid) {
        try {
            System.out.println("Killing process PID: " + pid);
            Runtime.getRuntime().exec("kill -9 " + pid);
        } catch (IOException e) {
            System.err.println("Failed to kill PID " + pid + ": " + e.getMessage());
        }
    }
}
