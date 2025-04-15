import java.util.HashMap;
//javac Send.java SendFactory.java StopStation.java TrainCommPackageDTO.java MainFactory.java
//java MainFactory
public class MainFactory implements SendFactory {
    StopStation stopStation = new StopStation(); // CẦN khởi tạo

    CommPackageDTO stopStation(int command, int sequence, HashMap bundle) {
        if (bundle == null) bundle = new HashMap();
        stopStation.stopSeq = (int) bundle.getOrDefault("stopSeq", 0);
        int currentOperationNum = 9;
//        try {
//            int ret = (int)bundle.getOrDefault("operationNum", currentOperationNum);
//            stopStation.operationNum =  (ret < 0) ? currentOperationNum : ret;
//        } catch (Exception e) {
//        }
        stopStation.operationNum = (int)bundle.getOrDefault("operationNum", currentOperationNum);
        stopStation.setCommand(command);
        stopStation.setSequenceNum(sequence);

        return stopStation;
    }

    @Override
    public byte[] fill(int command, int sequence, HashMap obj) {
        return stopStation(command, sequence, (HashMap) obj).serialize();
    }
}

