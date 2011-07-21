/*
 * Created on Jul 23, 2007 at 10:38:28 AM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.enough.polish.util.ZipUtil;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * <pre>
 * history
 *        Jul 23, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public class ZipTest extends TestCase {

/**
 * Perform some more tests ;-)
 * 	this is usefull since we deal with random data and
 * 	there have to be several hader combinations to be 
 * 	checked.
 *
 */
	public void testMltInputStream(){
		//long tm=System.currentTimeMillis();
		try {
			for (int i = 0; i < 30; i++) {
				testInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("mltTest elapsed time: " + (System.currentTimeMillis()-tm)+"ms");
	}
	/**
	 * This test compares the ZipInputStream to Javas native GZIPInputstream
	 * using random, but redundant data.
	 * 
	 * @throws IOException
	 */
	public void testInputStream() throws IOException{
		Random rnd = new Random(System.currentTimeMillis());
		byte[] uncompressed = new byte[1024*100];
		byte[] random = new byte[1024];
		
		byte[] compressed;
		byte[] compare = new byte[uncompressed.length];
		
		ByteArrayInputStream byteIn;
		
		for (int i = 0; i < 100; i++) {
			nextBytes(rnd,random , (int) (1024*rnd.nextFloat()));
			System.arraycopy(random, 0, uncompressed, 1024*i, 1024);
		}
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream( byteOut );
		out.write( uncompressed, 0, uncompressed.length );
		out.close();
		compressed = byteOut.toByteArray(); 
		
		byteIn = new ByteArrayInputStream( compressed ); 
		GZipInputStream zip = new GZipInputStream( byteIn, 1024, GZipInputStream.TYPE_GZIP , true );
		DataInputStream in = new DataInputStream( zip ); 
		in.readFully( compare ); 
		
		for (int j = 0; j < uncompressed.length; j++) {
			assertEquals( uncompressed[j], compare[j] );
		}
		assertEquals(1,zip.validData());
		//System.out.println( " vaild Data == " + zip.validData() );
		
		
	}
	
	public void testDecompress() throws IOException{
		Random rnd = new Random(System.currentTimeMillis());
		byte[] uncompressed = new byte[1024*100];
		byte[] random = new byte[1024];
		
		byte[] compressed;
		byte[] compare;
		
		for (int i = 0; i < 100; i++) {
			nextBytes(rnd,random , (int) (1024*rnd.nextFloat()));
			System.arraycopy(random, 0, uncompressed, 1024*i, 1024);
		}
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream( byteOut );
		out.write( uncompressed, 0, uncompressed.length );
		out.close();
		compressed = byteOut.toByteArray(); 
		
		compare = ZipUtil.decompress(compressed, GZipInputStream.TYPE_GZIP);
		
		for (int j = 0; j < uncompressed.length; j++) {
			assertEquals( uncompressed[j], compare[j] );
		}
		
	}
	
	public void testMltOutPutStream(){
		//long tm=System.currentTimeMillis();
		try {
			for (int i = 0; i < 10; i++) {
				outputStreamTest(1<<15, 1<<15);
				outputStreamTest(0, 1<<15);
				outputStreamTest(1<<15, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("mltTest elapsed time: " + (System.currentTimeMillis()-tm)+"ms");
	}
	
	public void outputStreamTest(int huffSize, int lz77Size) throws Exception{
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");	
	
		Random rnd = new Random(System.currentTimeMillis());
		int L=100;
		byte[] uncompressed = new byte[1024*L];
		byte[] random = new byte[1024];
		
		byte[] compressed;
		byte[] compare = new byte[uncompressed.length];
		
		ByteArrayInputStream byteIn;
		
		for (int i = 0; i < random.length; i++) {
			random[i]=1;
		}
		
		for (int i = 0; i < L; i++) {
			nextBytes(rnd,random , (int) (1024*rnd.nextFloat()));
			System.arraycopy(random, 0, uncompressed, 1024*i, 1024);
		}
	
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		//GZIPOutputStream out = new GZIPOutputStream( byteOut );
		GZipOutputStream out = new GZipOutputStream( byteOut , 1024, GZipOutputStream.TYPE_GZIP, huffSize ,lz77Size);
		
		out.write( uncompressed, 0, uncompressed.length-1 );
		out.write(uncompressed[uncompressed.length-1]);
		out.close();
		compressed = byteOut.toByteArray(); 
		
		byteIn = new ByteArrayInputStream( compressed ); 
		
		GZIPInputStream zip = new GZIPInputStream(byteIn, 1024);
		
		//ZipInputStream zip = new ZipInputStream(byteIn, 1024, ZipUtil.TYPE_GZIP, true);
		
		DataInputStream in = new DataInputStream( zip );
		/*int count=0;
		while(in.available()>0){
			compare[count++]=(byte)in.read();
		}*/
		
		in.readFully( compare );
		
		for (int j = 1; j < compare.length; j++) {
			assertEquals( uncompressed[j], compare[j] );
		}
	}
	
	
	public void testCompress() throws IOException{
		Random rnd = new Random(System.currentTimeMillis());
		byte[] uncompressed = new byte[1024*100];
		byte[] random = new byte[1024];
		
		byte[] compressed;
		byte[] compare = new byte[uncompressed.length];
		
		for (int i = 0; i < 100; i++) {
			nextBytes(rnd,random , (int) (1024*rnd.nextFloat()));
			System.arraycopy(random, 0, uncompressed, 1024*i, 1024);
		}
		
		compressed = ZipUtil.compress(uncompressed, GZipOutputStream.TYPE_GZIP);
		
		ByteArrayInputStream compressedDataStream = new ByteArrayInputStream(compressed);
		DataInputStream in = new DataInputStream( new GZIPInputStream(compressedDataStream)); 
		
		in.readFully(compare);
		
		for (int j = 0; j < uncompressed.length; j++) {
			assertEquals( uncompressed[j], compare[j] );
		}
		
	}
	
	
	// extended Random.nextBytes function
    public void nextBytes(Random rand, byte[] bytes,int len) {
    	for (int i = 0; i < len; )
    	    //for (int rnd = rand.nextInt(),  n = Math.min(len - i, Integer.SIZE/Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE)
    		for (int rnd = rand.nextInt(),  n = Math.min(len - i, 32/8); n-- > 0; rnd >>= 8)
    	    	bytes[i++] = (byte)rnd;
    }
	
    public void testCRC32(){
	   byte[] data={ 0x12, 0x34, 0x45, 0x67, 0x12, 0x34, 0x45, 0x67, 0x12, 0x34, 0x45, 0x67, 0x12, 0x34, 0x45, 0x67};
	   assertEquals( 0xd4e3a082 , ZipHelper.crc32(new int[256],0,data,0,data.length));
    }
	
}
