import java.io.ByteArrayOutputStream;

public class StopStation extends TrainCommPackageDTO {
    int stopSeq;
    int operationNum;
    int command;
    int sequenceNum;

    public void setCommand(int cmd) {
        command = cmd;
    }

    public void setSequenceNum(int seq) {
        sequenceNum = seq;
    }

    @Override
    public byte[] serialize() {
        // Mã hóa thành byte[]
        //return new byte[]{(byte) command, (byte) sequenceNum, (byte) stopSeq, (byte)operationNum};
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(toBytes(stopSeq,1));
            stream.write(toBytes(operationNum,1));
            data = stream.toByteArray();
            return super.serialize();
        } catch (Exception e) {
            return new byte[]{0};
        }
    }

    @Override
    public void deserialize(byte[] data) {
        // Giải mã từ byte[]
        //command = data[0];
        //sequenceNum = data[1];

        if(data == null) return;
        super.deserialize(data);
        data = super.data;
        //Log.d(TAG, "MainStopStationRequest Data Ready to deserialize");
        int offset = 0;
        stopSeq = (data[offset] & 0xFF);
        //Log.d(TAG, "stopSeq: "+stopSeq);
        offset++;
        operationNum = (data[offset] & 0xFF);
    }
}
