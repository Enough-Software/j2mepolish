//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

public class TrieUtils {
	
	static byte[] charBuffer = new byte[2];
	static byte[] integerBuffer = new byte[4];
	static byte[] byteBuffer = new byte[1];
		
	private TrieUtils(){}
	
	public static int byteToInt(byte[] bytes, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i + offset] & 0x000000FF) << shift;
		}
		return value;
    }
	
	public static char byteToChar(byte[] bytes, int offset)
	{
		int high = bytes[offset] & 0xff;
		int low = bytes[offset+1] & 0xff;
		return (char)((int)( high << 8 | low ));
	}
		
	public static byte byteToByte(byte[] bytes, int offset)
	{
		return bytes[offset];
	}
	
	public static byte[] intToByte (final int value) 
	{
		integerBuffer[0] = (byte) ((value >> 24) & 0x000000FF);
		integerBuffer[1] = (byte) ((value >> 16) & 0x000000FF);
		integerBuffer[2] = (byte) ((value >> 8) & 0x000000FF);
		integerBuffer[3] = (byte) (value & 0x00FF);
		return integerBuffer;
	} 
	
	public static byte[] byteToByte (final byte value) 
	{
		byteBuffer[0] = value;
		return byteBuffer;
	}   
	
	public static byte[] charToByte (final char value)
	{
		charBuffer[0] = (byte) ((value >> 8) & 0x000000FF);
		charBuffer[1] = (byte) (value & 0x00FF);
		return charBuffer;
	}
	
}
