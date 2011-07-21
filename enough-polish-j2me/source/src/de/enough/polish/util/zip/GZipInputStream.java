/*
 * Created on Jun 25, 2007 at 11:12:23 AM.
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

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Reads and uncompresses GZIP or DEFLATE encoded input streams.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 25, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public class GZipInputStream extends InputStream {
	/**
	 * This constant triggers the normal deflate compression as described in rfc 1951.
	 */
	public static final int TYPE_DEFLATE=0;
	/**
	 * This constant triggers the gzip compression that is the same as deflate with 
	 * some extra header information (see rfc 1952).
	 */
	public static final int TYPE_GZIP=1;
	
	private InputStream inStream;
	private boolean inStreamEnded;
	
	private byte status;
	private static final byte EXPECTING_HEADER=0;
	private static final byte EXPECTING_DATA=1;
	private static final byte EXPECTING_CHECK=2;
	private static final byte FINISHED=3;
	
	private boolean hash;
	/**
	 * The data seems to be decompressed sucessfull if vaildData
	 *  is true after processing the whole stream. This is determinded
	 *  via counting the processed bytes and depending on the
	 *  on the parameters given to the constructor also by using
	 *  a CRC32 checksum.
	 */
	private boolean validData;
	
	private int crc32;
	private int[] crc32Table=new int[256];
	
	private int type;
	
	// Flags
	private boolean BFINAL;//indicates last block
	private int BTYPE;// type of compression
	
	// Buffer stuff:
	private byte[] window=new byte[32 * 1024]; // every decoder has to support windows up to 32k
	private int pProcessed=0;				// data pointer = one after the last processed
	private long allPocessed=0;				// amount of processed data mod 2^32
	
	byte[] outBuff;			// the plain data will be stored here before being requested
	private int buffsize;
	int outEnd=0;						// the position AFTER the last byte of data
	int lastEnd=0;						// 		"		the point up to the crc32 was computed
	int outStart=0;						// the position of the first bit of data
	
	private int B0len;				// length of remaining plain bytes to process
	
	long[] smallCodeBuffer=new long[2];// (1) contains the merged bitcode and (2) contains the count of those bits
	static final byte BL=8; 

	
	// Compression stuff
	short[] huffmanTree;
	
	short[] distHuffTree;
	
	/**
	 * Creates an input stream capable of GZIP and Deflate with a buffer of 1024 bytes.
	 * 
	 * @param inputStream 	the stream that contains the compressed data.
	 * @param compressionType	TYPE_GZIP or TYPE_DEFLATE
	 * @param hash 		set true for data checking, set false for speed reading
	 * @throws IOException when the header of a GZIP stream cannot be skipped
	 * @see #TYPE_DEFLATE
	 * @see #TYPE_GZIP
	 */
	public GZipInputStream(InputStream inputStream, int compressionType, boolean hash) 
	throws IOException
	{
		this(inputStream, 1024, compressionType, hash);
	}
	
	/**
	 * Creates an input stream capable of GZIP and Deflate.
	 * 
	 * @param inputStream 	the stream that contains the compressed data.
	 * @param size the size of the internally used buffer
	 * @param compressionType	TYPE_GZIP or TYPE_DEFLATE
	 * @param hash 		set true for data checking, set false for speed reading
	 * @throws IOException when the header of a GZIP stream cannot be skipped
	 * @see #TYPE_DEFLATE
	 * @see #TYPE_GZIP
	 */
	public GZipInputStream(InputStream inputStream, int size, int compressionType, boolean hash) 
	throws IOException 
	{
		//System.out.println("creating GZipInputStream with hash=" + hash);
		this.inStream=inputStream;
		
		this.inStreamEnded=false;
		this.status=GZipInputStream.EXPECTING_HEADER;
		
		this.hash=hash;
		this.type=compressionType;
		
		this.smallCodeBuffer=new long[2];
		this.huffmanTree=new short[288*4];
		
		
		this.distHuffTree=new short[32*4];
		
		this.buffsize=size;
		this.outBuff=new byte[size+300];
		//#debug
		System.out.println("creating outbuff, size=" + size + ", actual lenth=" + this.outBuff.length) ;
		
		if (this.type==GZipInputStream.TYPE_GZIP){
			ZipHelper.skipheader(inputStream);
		}
		
		this.crc32=0;
	}
	

    public void close() throws IOException{
    	this.inStream.close();
    	
    	this.smallCodeBuffer=null;

		this.huffmanTree=null;
		this.distHuffTree=null;
    }
	
    /**
     * This function hides the fact that 'this.window' is a ring buffer
     * 		so just pass 'start' and 'len' of data in the window as well
     *  	as a destination and it will be copied there.
     * @param start
     * @param len
     * @param dest
     */
    private void copyFromWindow(int start, int len, byte[] dest, int destoff){
    	//#debug
    	System.out.println( "copyFromWindow(start=" + start + ", len=" + len + ", dest.length=" + dest.length + ", destoff="  + destoff + ") - window.length=" + this.window.length );
    	if (start + len < this.window.length) {
    		System.arraycopy(this.window, start, dest, 0+destoff, len);
    	} else {
    		System.arraycopy(this.window, start, dest, 0+destoff, this.window.length - start);
    		System.arraycopy(this.window, 0, dest, this.window.length - start + destoff, len - (this.window.length - start) );
    		
    	}
    	//#debug
    	System.out.println("end of copyFromWindow");
    }
    private void copyIntoWindow(int start, int len, byte[] src, int srcOff){
    	//#debug
    	System.out.println( "copyIntoWindow()" );
    	if(len + start < this.window.length) {
			System.arraycopy(src, srcOff, this.window, start, len);
		} else {
			System.arraycopy(src, srcOff, this.window, start, this.window.length-start);
			System.arraycopy(src, srcOff+(this.window.length-start), this.window, 0, len - (this.window.length-start));
		}
    	
    }
    
    
    /**
     * This function fills the internal outputbuffer reading data form the this.inputStream 
     *		and inflating it.
     */
    
    private void inflate() throws IOException{
    	//#debug
    	System.out.println("inflate - outbuff.length=" + this.outBuff.length);
    	int val=0;
    	
    	int rep;
    	int rem;
    	
    	int cLen;
    	int cPos;
    	int aPos;
    	
    	int copyBytes;
    	
    	byte[] myWindow=this.window;
    	byte[] myOutBuff=this.outBuff;
    	
    	// shift outputbuffer to the beginning
    	System.arraycopy(myOutBuff, this.outStart, myOutBuff, 0, this.outEnd-this.outStart);
    	this.outEnd-=this.outStart;
    	this.outStart=0;
    	
    	this.lastEnd = this.outEnd;
    	
    	if (this.B0len==0){
    		if (this.smallCodeBuffer[1]<15){
    			refillSmallCodeBuffer();
    		}
    	}
    	
    	
    	// and fill it by parsing the input-stream
    	while ((myOutBuff.length-this.outEnd>300 && (this.smallCodeBuffer[1]>0  || this.B0len>0)) && this.status!=GZipInputStream.FINISHED){
    		
    		// parse block header
    		if (this.status == GZipInputStream.EXPECTING_HEADER){
    			processHeader();
    		}
    		// deal with the data
    		
    		if (this.status==GZipInputStream.EXPECTING_DATA){
	    		// just copy data
	    		if (this.BTYPE==0){
	    			
	    			if (this.B0len>0){
		    			// copy directly
	    				copyBytes=(myOutBuff.length-this.outEnd)>this.B0len ? this.B0len : myOutBuff.length-this.outEnd;
	    				
		    			//at most myOutBuff.length-this.outEnd
		    			
	    				copyBytes=this.inStream.read(myOutBuff, this.outEnd, copyBytes);
		    			copyIntoWindow(this.pProcessed, copyBytes, myOutBuff, this.outEnd);
		    			
		    			this.outEnd+=copyBytes;
		    			this.pProcessed=(this.pProcessed +copyBytes) & 32767;// % (1<<15);
		    			
		    			this.B0len-=copyBytes;

	    			}else{
		    			if(this.BFINAL){
		    				this.status=GZipInputStream.EXPECTING_CHECK;
		    			} else {
		    				this.status=GZipInputStream.EXPECTING_HEADER;
		    			}
		    			if (this.smallCodeBuffer[1]<15){
		    				refillSmallCodeBuffer();
		    			}
	    			}
	    			
	    		}// inflate
	    		else {
	    			
	    			if (this.smallCodeBuffer[1]<15){
	    				refillSmallCodeBuffer();
	    			}
		    		
	    			val=ZipHelper.deHuffNext(this.smallCodeBuffer,this.huffmanTree);
	    			
		    		
		    		// normal single byte
		    		if (val<256){
		    			
		    			//this.window[this.pProcessed]=(byte)val;
		    			myWindow[this.pProcessed]=(byte)val;
		    			this.pProcessed=(this.pProcessed +1) &  32767;// % (1<<15);
		    			
		    			myOutBuff[this.outEnd]=(byte)val;
		    			this.outEnd++;
		    			
		    			
		    		}// copy: pointer + len 
		    		else if (val!=256) {
						if (val>285){
							//ERROR: data > 285 was decoded. This is invalid data.;
							throw new IOException("1");
						}
						
		    			// parse the pointer 
						
		    			// cLen
		    			// 		read some bits
		    			cLen=popSmallBuffer(ZipHelper.LENGTH_CODE[(val-257)<<1]);
		    			//		add the offset
		    			cLen+=ZipHelper.LENGTH_CODE[((val-257)<<1)+1];
		    			
		    			// cPos
		    			//    	resolve the index
		    			if (this.smallCodeBuffer[1]<15){
		    				refillSmallCodeBuffer();
		    			}
		    			// DISTANCE
		    			val=ZipHelper.deHuffNext(this.smallCodeBuffer, this.distHuffTree);
		    			
		    			//	 	resolve the value
		    			cPos=popSmallBuffer(ZipHelper.DISTANCE_CODE[val<<1]);
						
		    			cPos+=ZipHelper.DISTANCE_CODE[(val<<1)+1];
		    			
		    			
		    			// process the pointer (the data does always fit)
		    			
		    			// the absolute starting position for copying data
		    			aPos=this.pProcessed - cPos;
		    			aPos+=aPos<0 ? myWindow.length:0;
		    			
		    			// how often will the data be copied?
		    			rep=cLen/cPos;
		    			rem=cLen-cPos*rep;
		    			
		    			for (int j = 0; j < rep; j++) {
		    				// cPos < cLen
		    				copyFromWindow(aPos, cPos, myOutBuff,this.outEnd);
			    			copyIntoWindow(this.pProcessed, cPos, myOutBuff, this.outEnd);
		    				this.outEnd+=cPos;
		    				this.pProcessed=(this.pProcessed +cPos) &  32767;//% (1<<15);
		    			}
		    			
		    			// cPos > cLen OR remainder 
		    			copyFromWindow(aPos, rem, myOutBuff,this.outEnd);// from window into buffer, and again into window
		    			copyIntoWindow(this.pProcessed, rem, myOutBuff, this.outEnd);
		    			this.outEnd+=rem;
		    			this.pProcessed=(this.pProcessed +rem) &  32767;// % (1<<15);
		    			
		    			
		    		}// val=256
		    		else {
		    			if(this.BFINAL){
		    				this.status=GZipInputStream.EXPECTING_CHECK;
		    			} else {
		    				this.status=GZipInputStream.EXPECTING_HEADER;
		    			}
		    		}
		    		if (this.smallCodeBuffer[1]<15){
		    			refillSmallCodeBuffer();
		    		}
		    	}
	    	}
    		if (this.status == GZipInputStream.EXPECTING_CHECK){
    			//System.out.println(this.allPocessed + " data check");
    			
    			this.status=GZipInputStream.FINISHED;
    			
    			this.allPocessed=(this.allPocessed+this.outEnd-this.lastEnd) & 4294967295L;
    			if (this.hash){
    		    	// lastEnd -> End in CRC32 einbeziehen
    				this.crc32=ZipHelper.crc32(this.crc32Table, this.crc32, myOutBuff, this.lastEnd, this.outEnd-this.lastEnd);
    			}
    			
    			// skip till next byte boundary, read CRC , isize
    			popSmallBuffer(this.smallCodeBuffer[1]&7);
    			int cCrc=popSmallBuffer(8)|(popSmallBuffer(8)<<8)|(popSmallBuffer(8)<<16)|(popSmallBuffer(8)<<24);
    			int iSize=popSmallBuffer(8)|(popSmallBuffer(8)<<8)|(popSmallBuffer(8)<<16)|(popSmallBuffer(8)<<24);
    			
    			this.validData=(iSize==this.allPocessed);
    			if (this.hash){
    				this.validData &= (this.crc32==cCrc);
    			}
    			
    			if (!this.validData){
    				//ERROR: the data check (size & hash) are wrong
    				throw new IOException("2");
    			}
    			
    		}
    		
    	}
    	
    	// refresh the checksum at once
    	if (this.status!=GZipInputStream.FINISHED){
	    	this.allPocessed=(this.allPocessed+this.outEnd-this.lastEnd) & 4294967295L;
	    	if (this.hash){
		    	// lastEnd -> End in CRC32 einbeziehen
	    		this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, myOutBuff, this.lastEnd, this.outEnd-this.lastEnd);
	    	}
    	}
    	
    }
    
    private void processHeader() throws IOException{
    	//#debug
    	System.out.println("processHeader()");
    	int val;
    	
		int HLIT; // number of miniHuff fragments
		int HDIST;// number of distance codes (should somehow lead to the same)
		int HCLEN;// number of length codes

		int[] distHuffCode=new int[30];
		int[] distHuffData=new int[30];
		byte[] distHuffCodeLength=new byte[30];
		
		int[] huffmanCode=new int[286];				// this contains the codes according to the huffman tree/mapping
		int[] huffmanData=new int[286];				// this contains the data referring to the code.
		byte[] huffmanCodeLength=new byte[286];
		
		this.BFINAL= (popSmallBuffer(1)==1);
	
		this.BTYPE=popSmallBuffer(2);
		
		if (this.BTYPE==3){
			throw new IllegalArgumentException();
		} else if (this.BTYPE==1){
			//System.out.println(this.allPocessed +  ": fixed tree");
			
			ZipHelper.genFixedTree(huffmanCode, huffmanCodeLength, distHuffCode, distHuffCodeLength);
			for (int i = 0; i < 286; i++) {
				huffmanData[i]=i;
			}
			for (int i = 0; i < 30; i++) {
				distHuffData[i]=i;
			}
			
			// convert literal table to tree
			ZipHelper.convertTable2Tree(huffmanCode, huffmanCodeLength, huffmanData, this.huffmanTree);
			
	    	// convert distance table to tree
			ZipHelper.convertTable2Tree(distHuffCode, distHuffCodeLength, distHuffData, this.distHuffTree);
			
		} else if(this.BTYPE==2) {
			//System.out.println(this.allPocessed + ": dynamic tree");
			
			// read/parse the length codes
			
			HLIT=popSmallBuffer(5);
				HDIST=popSmallBuffer(5);
			HCLEN=popSmallBuffer(4);
			
			// miniTree
			int[] miniHuffData=     { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
			int[] seq={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
			
			byte[] miniHuffCodeLength=new byte[19];
			int[] miniHuffCode=new int[19];
			
			// read the miniHuffCodeLength
			for (int i = 0; i < HCLEN+4; i++) {
				miniHuffCodeLength[ miniHuffData[i] ]=(byte) popSmallBuffer(3);
			}
			
			ZipHelper.genHuffTree(miniHuffCode, miniHuffCodeLength);
			ZipHelper.revHuffTree(miniHuffCode, miniHuffCodeLength);

			short[] miniTree = new short[19*4];
	    	ZipHelper.convertTable2Tree(miniHuffCode, miniHuffCodeLength, seq, miniTree);
	    	
			// parse the length code for the normal Tree and the distance Tree using the miniTree
			for (int i = 0; i < huffmanCodeLength.length; i++) {
				huffmanCodeLength[i]=0;
			}
			for (int i = 0; i < distHuffCodeLength.length; i++) {
				distHuffCodeLength[i]=0;
			}
			byte lastVal=0;
			for (int j = 0; j < HLIT + 257 + HDIST +1;) {
				
				if (this.smallCodeBuffer[1]<15){
					refillSmallCodeBuffer();
				}
				val=ZipHelper.deHuffNext(this.smallCodeBuffer, miniTree);
				
				// data
				if (val<16){
					lastVal=(byte)val;
					val=1;
				} else{
				// repeat code
					if (val==16){
						val=popSmallBuffer(2)+3;
					} else if (val==17){
						lastVal=0;
						val=popSmallBuffer(3)+3;
					} else if (val==18){
						lastVal=0;
						val=popSmallBuffer(7)+11;
					}
				}
				// fill the value in
				for (int k = 0; k < val; k++,j++) {
					if (j<HLIT + 257){
						huffmanCodeLength[j]=lastVal;
					} else {
						distHuffCodeLength[j-(HLIT + 257)]=lastVal;
					}
				}
				
			}
			
			// final tree:		 fill this.huffmanCode this.huffmanData
			ZipHelper.genHuffTree(huffmanCode, huffmanCodeLength);
			for (int i = 0; i < huffmanData.length; i++) {
				huffmanData[i]=i;
			}
			// converting literal table to tree
			ZipHelper.revHuffTree(huffmanCode, huffmanCodeLength);
			ZipHelper.convertTable2Tree(huffmanCode, huffmanCodeLength, huffmanData, this.huffmanTree);
			
			// Distance Tree
	    	// distHuffData for non fixed distance tree
			// this.distHuffCodeLength is read together with this.huffmanCodeLength
	    	for (int j = 0; j < distHuffCode.length; j++) {
	    		distHuffData[j]=j;
			}
	    	ZipHelper.genHuffTree(distHuffCode, distHuffCodeLength);
	    	ZipHelper.revHuffTree(distHuffCode, distHuffCodeLength);
	    	ZipHelper.convertTable2Tree(distHuffCode, distHuffCodeLength, distHuffData, this.distHuffTree);
	    	
		} else{
			// just skip bits up to the next boundary
			popSmallBuffer(this.smallCodeBuffer[1]&7); //&7 == %8
			
			// read and check the header 
			this.B0len=popSmallBuffer(8)|popSmallBuffer(8)<<8;
			if (this.smallCodeBuffer[1]<15){
				refillSmallCodeBuffer();
			}
			if (this.B0len + (popSmallBuffer(8)|popSmallBuffer(8)<<8) != 0xffff){
				//Error:  the header for the uncompressed is wrong;
				throw new IOException("3");
				
			}
			
			// clear the buffer
			while(this.smallCodeBuffer[1]!=0 && this.B0len>0){
				val = popSmallBuffer(8);
				this.window[this.pProcessed]=(byte)val;
				this.pProcessed=(this.pProcessed +1) & 32767; // == % (1<<15);
				
				this.outBuff[this.outEnd]=(byte)val;
				this.outEnd++;
				
				this.B0len--;
			}
			
		}
		
		this.status=GZipInputStream.EXPECTING_DATA;
		
		distHuffCode=null;
		distHuffData=null;
		distHuffCodeLength=null;
		
		huffmanCodeLength=null;
		huffmanCode=null;
		huffmanData=null;
	} 
    
    /**
     * Checks if the current status is valid
     * 
     * @return -1 if the stream is finished, 1 if the current data is valid, 0 if not
     * @throws IOException
     */
    public int validData() throws IOException{
    	//#debug
    	System.out.println("validData()");
    	inflate();
    	if (this.status!=GZipInputStream.FINISHED){
    		return -1;
    	}  else {
    		if (this.validData){
    			return 1;
    		} else {
    			return 0;
    		}
    	}
    }
    
    private int popSmallBuffer(long len) throws IOException{
    	//#debug
    	System.out.println("popSmallBuffer(" + len + ")");
    	if (len==0) return 0;
    	
		if (this.smallCodeBuffer[1]<len){
			refillSmallCodeBuffer();
		}
		
    	int ret= (int) (this.smallCodeBuffer[0] & ((1<<len)-1));
		this.smallCodeBuffer[0]>>>=len;
		this.smallCodeBuffer[1]-=len;
    	
		return ret;    	
    	
    }
    /**
     * This function refills the smallCodeBuffer with data from the input
     * stream.
     *
     */
    byte[] tmpRef = new byte[8]; // just one allocation
    private void refillSmallCodeBuffer() throws IOException{
    	//#debug
    	System.out.println("refillSmallCodeBuffer");
    	
    	// (re)fill this.smallBuffer reading this.inStream
    	if (!this.inStreamEnded){
	    	
	    	int wanted=(int)(BL-this.smallCodeBuffer[1]/8-1);
	    	int count= this.inStream.read(this.tmpRef,0,wanted);
	    	
	    	if (count == -1){
	    		this.inStreamEnded=true;
	    	}
	    	
			for (int i = 0; i < count; i++) {
				this.smallCodeBuffer[0] &= ~( (long)0xff << this.smallCodeBuffer[1]);
				if (this.tmpRef[i]<0){
					this.smallCodeBuffer[0] |= (long)(this.tmpRef[i]+256) << this.smallCodeBuffer[1];
				}else{
					this.smallCodeBuffer[0] |= (long)this.tmpRef[i] << this.smallCodeBuffer[1];
				}
				this.smallCodeBuffer[1] += 8;
			}
    	}
    }
    
    /**
     * This function fills the buffer and returns the amount of 
     * avialable data. Therefore it will always return the buffer
     * size that was given to the constructor.
     */
    public int available() throws IOException {
    	//#debug
    	System.out.println("available()");
    
    	if ((this.outEnd-this.outStart)<this.outBuff.length-300){
    		inflate();
    		//#debug
    		System.out.println("inflated to " + (this.outEnd-this.outStart));
    	}
    	return this.outEnd-this.outStart;
    }
    
    public long skip(long n) throws IOException{
    	//#debug
    	System.out.println("skip(" + n + ")");
    	long skipped=0;
    	byte[] b=new byte[this.buffsize];
    	
		while(skipped<n && this.status != GZipInputStream.FINISHED){
			skipped+=this.read(b);
		}
    	
    	return skipped;
    }
    
	public int read() throws IOException {
    	if ((this.outEnd-this.outStart)==0){
    		inflate();
    	}
    	if(this.outEnd-this.outStart==0 && this.inStreamEnded){
    		// the input stream ended
    		return -1;
		} else {
			return (this.outBuff[this.outStart++] + 256) & 255;
		}
	}
	
    public int read(byte[] b) throws IOException {
    	return read(b, 0, b.length);
    }
    
    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException  {
    	//#debug
    	System.out.println("read[byte[],off,len)");
    	// inflate as much as possible
    	if ((this.outEnd-this.outStart)<this.outBuff.length-300){
    		inflate();
    	}
    	
    	// we can process just min(b.length, this.availableBytes) Bytes
    	int av=this.available();
    	int copyBytes= len > av ? av: len;
    	
    	// copy available data from the ouputBuffer to 'b'
    	// here ocurred an error once
    	System.arraycopy(this.outBuff, this.outStart, b, off, copyBytes);
    	
    	this.outStart+=copyBytes;
    	
    	// return the number of copied bytes
    	if(copyBytes!=0){
    		return copyBytes;
    	} else {
    		return -1;
    	}
    }
    
}
