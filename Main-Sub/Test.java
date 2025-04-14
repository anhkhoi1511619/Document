import java.util.HashMap;
//javac Send.java SendFactory.java StopStation.java TrainCommPackageDTO.java MainFactory.java
//java MainFactory
public class Test {
    public static void main(String[] args) {
        System.err.println("Khoi");
        var dto = new TrainCommPackageDTO();
        MainFactory factory = new MainFactory();
        HashMap<String, Integer> bundle = new HashMap<>();
        bundle.put("stopSeq", 1);
        bundle.put("operationNum", 2);
        Send send = factory.fill(20, 1, bundle);

        byte[] data = send.serialize();
        
        System.out.println("Serialized: " + format(data));

        try {
            dto.deserialize(data);
        } catch (Exception e) {
            System.out.println( "Error occurred when parsing package of server controller"+e);
        }
        System.out.println("Data is correctly "+(dto.isCorrectData() ? "YES" : "NO"));

        bundle.put("stopSeq", 3);
        bundle.put("operationNum", -1);
        send = factory.fill(20, 2, bundle);

        data = send.serialize();
        
        System.out.println("Serialized: " + format(data));


        try {
            dto.deserialize(data);
        } catch (Exception e) {
            System.out.println( "Error occurred when parsing package of server controller"+e);
        }
        System.out.println("Data is correctly "+(dto.isCorrectData() ? "YES" : "NO"));
    }

    static String format(byte[] input) {
		if(input == null) {
			return "";
		}
		var ret = new StringBuilder();
		for (byte x: input) {
			ret.append(format(x));
		}
		return ret.toString();
	}
    static String format(byte input){
		return String.format("%02X", input);
	}

}
