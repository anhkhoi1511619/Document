
import java.util.HashMap;


public interface SendFactory {
    Send fill(int command, int sequence, HashMap obj);
}
