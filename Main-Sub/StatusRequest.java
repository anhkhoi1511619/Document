
public class StatusRequest extends CommPackageDTO {
    int data1;
    //byte[] ret = new byte[] {0x00,0x00,0x00};
    @Override
    public byte[] serialize() {
        try {
            data = new byte[]{(byte)data1};
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
        //stopSeq = (data[offset] & 0xFF);
        //Log.d(TAG, "stopSeq: "+stopSeq);
        offset++;
        //operationNum = (data[offset] & 0xFF);
    }
}
