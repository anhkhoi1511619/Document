import java.util.HashMap;
//javac Send.java SendFactory.java StopStation.java TrainCommPackageDTO.java MainFactory.java
//java MainFactory
public class Test {
    public static void main(String[] args) {
        System.err.println("Khoi");
        byte[] data;
        var dto = new CommPackageDTO();
        var  factory = new MainFactory();
        var bundle = new HashMap<>();
        bundle.put("stopSeq", 0x03);
        bundle.put("operationNum", 0xE8);
        data = factory.fill(0x30, 1, bundle);
        
        System.out.println("Serialized: " + format(data));

        try {
            dto.deserialize(data);
        } catch (Exception e) {
            System.out.println( "Error occurred when parsing package of server controller"+e);
        }
        System.out.println("Data is correctly "+(dto.isCorrectData() ? "YES" : "NO"));

        bundle.put("stopSeq", 3);
        bundle.put("operationNum", 5);
        data = factory.fill(0x20, 2, bundle);
        
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
