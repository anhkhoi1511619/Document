import java.io.ByteArrayOutputStream;

public class StopStation extends TrainCommPackageDTO {
    int stopSeq;
    int operationNum;

    @Override
    public byte[] serialize() {
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
