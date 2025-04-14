
import java.io.ByteArrayOutputStream;
import java.util.Arrays;


public class CommPackageDTO {
    static final String TAG = CommPackageDTO.class.getSimpleName();
    byte stx =  0x02;
    short dataSize;
    byte dataSizeSum;
    int command = 0;
    int sequenceNum = 1; // 1 byte
    byte[] data;
    byte dataSum;
    byte etx = 0x03;

    public int getCommand() {
        return command;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public byte[] getData() {
        return data;
    }
    public boolean shouldRun() {
        return isCorrectData();
    }



    //For UnitTest
    public byte getStx() {
        return stx;
    }

    public short getDataSize() {
        return dataSize;
    }

    public byte getDataSizeSum() {
        return dataSizeSum;
    }

    public byte getDataSum() {
        return dataSum;
    }

    public byte getEtx() {
        return etx;
    }
    //End for Unit Test

    public byte[] serialize() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] dataBuffer = (data == null) ? new byte[]{} : data;//TODO: Maybe this is BUG
            byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
            cmdSeqDataBuffer[0] = (byte) command;
            cmdSeqDataBuffer[1] = (byte) sequenceNum;
            System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
            dataSize = (short) (cmdSeqDataBuffer.length);
            dataSizeSum = sum(toBytes(dataSize,2));
            dataSum = sum(cmdSeqDataBuffer);
            stream.write(stx);
            stream.write(toBytes(dataSize,2));
            stream.write(dataSizeSum);
            stream.write(command);
            stream.write(sequenceNum);
            if(data != null) stream.write(data);
            stream.write(dataSum);
            stream.write(etx);
    
            return stream.toByteArray();
        } catch (Exception e) {
            return new byte[]{0};
        }
    }
    public void deserialize(byte[] ret) {
        if(ret.length<=0) return;
        int offset = 0;
        stx = ret[offset];
        System.out.println( "stx: "+stx);
        offset++;
        byte[] dataSizeArr = Arrays.copyOfRange(ret, offset, offset+2);
        System.out.println( "dataSizeArr: "+Arrays.toString(dataSizeArr));
        dataSize = (short) castInt(dataSizeArr);
        System.out.println( "dataSize: "+dataSize);
        offset+=2;
        dataSizeSum = ret[offset];
        System.out.println( "dataSizeSum: "+dataSizeSum);
        offset++;
        command = (ret[offset] & 0xFF);
        System.err.println( "command: "+command);
        offset++;
        sequenceNum = ret[offset];
        System.out.println( "sequenceNum: "+sequenceNum);
        offset++;
        data = Arrays.copyOfRange(ret, offset, ret.length-2);
        System.out.println( "dataArr: "+Arrays.toString(data));
        dataSum = ret[ret.length-2];
         System.out.println("dataSum: "+dataSum);
        etx = ret[ret.length-1];
        System.out.println( "etx: "+etx);
    }

    public boolean isCorrectData() {
        if(stx != 0x02) {
            System.out.println( "Can not parse due to STX incorrectly");
            return false;
        }
        if(etx != 0x03) {
            System.out.println( "Can not parse due to ETX incorrectly");
            return false;
        }
        byte[] dataBuffer = (data==null) ? new byte[]{} : data;
        byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
        cmdSeqDataBuffer[0] = (byte) command;
        cmdSeqDataBuffer[1] = (byte) sequenceNum;
        System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
        System.out.println( "cmdSeqDataBuffer is prepared: "+Arrays.toString(cmdSeqDataBuffer));
        System.out.println( "Size of cmdSeqDataBuffer: "+cmdSeqDataBuffer.length);
        byte sum = sum(cmdSeqDataBuffer);
        System.out.println( "Sum by user: "+sum);
        System.out.println( "Sum is received: "+dataSum);
        if(Math.abs(dataSize) != Math.abs(dataSizeSum)){
            System.out.println( "Can not parse due to DataSize is not equal with Data Size Sum");
            return false;
        }
        if((cmdSeqDataBuffer.length != Math.abs(dataSize))) {
            System.out.println( "Data Size is not equal with size of data size");
            return false;
        }
        if(Math.abs(sum) != Math.abs(dataSum)) {
            System.out.println( "Can not parse due to Data Size incorrectly");
            return false;
        }
        return true;
    }

    protected byte sum(byte[] input) {
        byte ret = 0;
        for(byte x: input) ret += x;
        return ret;
    }
    protected byte[] toBytes(int input, int size) {
        byte[] ret = new byte[size];
        for (int i = 0; i < size; i++) {
            ret[size-i-1] = (byte) ((input>>(i*8)) & 0xff);
        }
        return ret;
    }    
    protected int castInt(byte[] input) {
        return castInt(input, 0, input.length);
    }

    protected int castInt(byte[] input, int offset, int length) {
        int ret = 0;
        int n = Math.min(offset+length, input.length);
        for (int i = offset; i < n; i++) {
            ret = ret<<8 | (0xff & (int)input[i]);
        }
        return ret;
    }
}
