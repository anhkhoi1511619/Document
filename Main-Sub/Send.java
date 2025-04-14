
public interface Send {
    byte[] serialize();
    void deserialize(byte[] data);
}
