package asu.tool.util;

/**
 * 16位UUID生成器
 * @date 2016年3月25日 上午10:10:46
 * @author darren.qiu
 * @version 1.0.0  
 *
 */
public class UUID16Generator {

    /**
     * 产生一个16位的UUID
	 *
     * @return  String  产生一个16位的UUID
     */
	public static String generate() {
		return new StringBuilder(16).append(format(getHiTime())).append(
				format(getLoTime())).append(format(getCount())).toString();
	}
	
	/**
	 * 随机生成8位
	 *
	 * @return 随机字符串
	 */
	public static String getRandom8Dights(){
		String str = UUID16Generator.generate();
		return str.substring(8);
	}

	private static short counter = (short) 0;

	private final static String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuilder buf = new StringBuilder("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	private final static String format(short shortval) {
		String formatted = Integer.toHexString(shortval);   //转换为16进制
		StringBuilder buf = new StringBuilder("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	private final static short getCount() {
		synchronized (UUID16Generator.class) {
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	/**
	 * Unique down to millisecond
	 * @return short value
	 */
	private final static short getHiTime() {
		return (short) (System.currentTimeMillis() >>> 32);
	}

	private final static int getLoTime() {
		return (int) System.currentTimeMillis();
	}
	
	public static void main(String[] args) {
		for(int i=0;i<10000;i++){
			System.out.println(getRandom8Dights());
		}
	}
}
