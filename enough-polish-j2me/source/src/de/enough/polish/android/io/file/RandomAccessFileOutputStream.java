//#condition polish.android
package de.enough.polish.android.io.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileOutputStream extends OutputStream {
	
	RandomAccessFile raf;
	
	RandomAccessFileOutputStream(RandomAccessFile raf) {
		this.raf = raf;
	}

	@Override
	public void close() throws IOException {
		this.raf.close();
	}

	@Override
	public void flush() throws IOException {
		//TODO ???
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		this.raf.write(buffer, offset, count);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		this.raf.write(buffer);
	}

	@Override
	public void write(int oneByte) throws IOException {
		this.raf.writeByte(oneByte);
		
	}

}
