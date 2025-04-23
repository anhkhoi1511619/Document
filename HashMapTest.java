import java.util.HashMap;
import java.util.Map;
public class HashMapTest {
  private static final Map<Integer, Integer> lastOperations = new HashMap<>();
  public static void main(String[] args) {
    lastOperations.put(5, null);
    int operationNo = getOperationNo (5);
    System.out.println("Hello World " + operationNo);
  }


    /**
     * 概要：車両タイプで車両運用を取得
     *
     * @param sysNo 車両タイプ
     * @return 車両運用
     */
    public static int getOperationNo(int sysNo) {
        return lastOperations.getOrDefault(sysNo, -1);
    }
}
