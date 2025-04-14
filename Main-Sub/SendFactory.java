
import java.util.HashMap;


public interface SendFactory {
    byte[] fill(int command, int sequence, HashMap obj);
}
