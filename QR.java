public class QR {
        public static void main(String[] args)
        {
		String ret = "02050000000000000000000000000000000000000000000000000000104";
		System.out.println("Data: "+rwMediaData(ret));
		ret = "0201";
		System.out.println("Data: "+rwMediaData(ret));
		ret = "02050000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000045";
		System.out.println("Data: "+rwMediaData(ret));
	}

	static String rwMediaData(String rawData) {
		final int    QR_IC_DATA_LEN_MAX = 61;
		final int    BYTE_OFFSET     	= 2;
		final String APPEND_DATA   		= "30";
		final int 	 maxLen 			= QR_IC_DATA_LEN_MAX * BYTE_OFFSET;
		String 		 ret 				= rawData;
		if (rawData.length() == maxLen) {
			
			return rawData;
		}
		if (rawData.length() > maxLen) {
			ret = rawData.substring(0, maxLen);
		} else {
			StringBuilder sb = new StringBuilder();
			int counter = maxLen - rawData.length();
			while (counter > 0) {
				sb.append(APPEND_DATA);
				counter -= 2;
			}
			ret = rawData + sb;
		}

		return ret.substring(0, maxLen);
	}
}
