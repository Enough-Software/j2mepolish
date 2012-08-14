package de.enough.polish.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class CachedInputStreamTest extends TestCase {
	
	public void testBufferInputStreamSingleRead() throws IOException {
		byte[] data = new byte[]{ 127, 1, 2, 3, 4, 77, -8, 89, 7, 8, 9, 10 };
		ByteArrayInputStream outerIn = new ByteArrayInputStream(data);
		CachedInputStream bufferIn = new CachedInputStream(outerIn);
		
		for (int i = 0; i < data.length; i++) {
			bufferIn.read();
		}
		
		byte[] readData = bufferIn.getBufferedData();
		assertNotNull(readData);
		assertEquals(data.length, readData.length);
		for (int i = 0; i < readData.length; i++) {
			assertEquals(data[i], readData[i]);
		}
	}
	
	public void testBufferInputStreamBufferRead() throws IOException {
		byte[] data = new byte[]{ 127, 1, 2, 3, 4, 77, -8, 89, 7, 8, 9, 10, 11, 12, 13, 15, 17, 19, 23, 43 };
		ByteArrayInputStream outerIn = new ByteArrayInputStream(data);
		CachedInputStream bufferIn = new CachedInputStream(outerIn);
		
		byte[] readData = new byte[5];
		while ( (bufferIn.read(readData)) > 0) {
			// continue reading
		}
		
		readData = bufferIn.getBufferedData();
		assertNotNull(readData);
		assertEquals(data.length, readData.length);
		for (int i = 0; i < readData.length; i++) {
			assertEquals(data[i], readData[i]);
		}
	}

}
