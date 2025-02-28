import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class CheckMSControllerEnum {
	static enum MSError {
		ERROR_MAIN_CONTROLLER_COM,
		ERROR_SUB_CONTROLLER_1_COM,
		ERROR_SUB_CONTROLLER_2_COM,
		ERROR_SUB_CONTROLLER_3_COM
	}
	public static final List<MSError> controllerComError = Arrays.asList(MSError.ERROR_MAIN_CONTROLLER_COM
			,MSError.ERROR_SUB_CONTROLLER_1_COM, MSError.ERROR_SUB_CONTROLLER_2_COM, MSError.ERROR_SUB_CONTROLLER_3_COM
	);
        public static void main(String[] args)
        {	
		int controllerID = 4;
		MSError input = MSError.ERROR_SUB_CONTROLLER_3_COM;		
                System.out.println(controllerComError.get(controllerID - 1));
		if(input.equals(controllerComError.get(controllerID - 1)))
		{
			 System.out.println("Equal.. SKIP");
		}
        }
}
