import java.util.HashMap;
//javac Send.java SendFactory.java StopStation.java TrainCommPackageDTO.java MainFactory.java
//java MainFactory
public class MainFactory implements SendFactory {
    StopStation stopStation = new StopStation(); // CẦN khởi tạo

    Send stopStation(int command, int sequence, HashMap bundle) {
        if (bundle == null) bundle = new HashMap();
        stopStation.stopSeq = (int) bundle.getOrDefault("stopSeq", 0);
        try {
            stopStation.operationNum = (int) bundle.getOrDefault("operationNum", 0);
        } catch (Exception e) {
        }
        stopStation.setCommand(command);
        stopStation.setSequenceNum(sequence);

        return stopStation;
    }

    @Override
    public Send fill(int command, int sequence, HashMap obj) {
        return stopStation(command, sequence, (HashMap) obj);
    }
}

