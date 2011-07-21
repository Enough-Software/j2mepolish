/*
 * Created on Jun 28, 2007 at 8:01:42 AM.
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
import java.io.OutputStream;

/**
 * <p>Generates GZIP or DEFLATE encoded input streams from an InputStream.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 28, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public class GZipOutputStream extends OutputStream {
	/**
	 * This constant triggers the normal deflate compression as described in rfc 1951.
	 */
	public static final int TYPE_DEFLATE=0;
	/**
	 * This constant triggers the gzip compression that is the same as deflate with 
	 * some extra header information (see rfc 1952).
	 */
	public static final int TYPE_GZIP=1;
	
	private OutputStream outStream;
	
	private byte[] outputWindow;
	private byte[] plainDataWindow;
	private int outProcessed;
	private int plainPointer;
	
	private final static int HASHMAP_COUNT=4;
	ZipIntMultShortHashMap[] HM	= new ZipIntMultShortHashMap[HASHMAP_COUNT+1];
	
	private byte[] inputBuffer;	// the input data is read in chunks from the inputStream
	private int inEnd;
	private int inStart;
	
	private int[] smallCodeBuffer; // we have to buffer the output, because it comes in different bitlength
	
	int[] huffmanCode;			// all necessary informations to store the trees
	byte[] huffmanCodeLength;
	int[] distHuffCode;
	byte[] distHuffCodeLength;
	
	private int[] litCount;		// the frequency of all literal symbols
	private int[] distCount;	// the frequency of all distance symbols
	
	private int isize;			// # of all processed bytes from inputStream
	private int crc32;			// crc32 checksum of  "
	private int[] crc32Table=new int[256];
	
	private int status;
	private final static int STREAM_INIT=0;
	private final static int STREAMING=4;
	
	private boolean lastBlock;	// needed to set the lastBlock bit in the header
	private boolean lz77active; // activates the lz77 compression
	
	private int BTYPE;	// 	=1 for fixed Tree this happens in case of huffmanwindowsize=0
						//	=2 in case of dynamix Trees
	
	/**
	 * 
	 * @param outputStream 		stream to write the compressed data in 
	 * @param size	prefered 	size of the internal buffer
	 * @param compressionType	TYPE_GZIP or TYPE_DEFLATE
	 * @param plainWindowSize	this size is important for the lz77 search. Larger values
	 * 		will result in better compression. Maximum is 32768.
	 * @param huffmanWindowSize	this size is important for the huffmanencoding. A large
	 * 		value will result to a better frequency statistic and therefore to a better compression.
	 * @throws IOException 		might be thrown in case that the inputStream can not be read, 
	 * 		the outputStream can not be written into or in case of wrong arguments
	 * 
	 * @see #TYPE_DEFLATE
	 * @see #TYPE_GZIP
	 */
	public GZipOutputStream(OutputStream outputStream, int size, int compressionType, int plainWindowSize, int huffmanWindowSize) throws IOException {
		this.outStream = outputStream;

		this.inputBuffer=new byte[size+300];
		this.litCount=new int[286];
		this.distCount=new int[30];
		this.smallCodeBuffer = new int[2];
		
		// check plainWindowSize; this triggers the LZ77 compression
		if (plainWindowSize > 32768){
			throw new IllegalArgumentException("plainWindowSize > 32768");
		}
		if (plainWindowSize>=100){
			this.plainDataWindow = new byte[(plainWindowSize/HASHMAP_COUNT)*HASHMAP_COUNT];
			this.lz77active=true;
		} else {
			this.plainDataWindow=null;
			this.lz77active=false;
		}
		
		// check the huffmanWindowSize; this also triggers dynamic/fixed trees
		if (huffmanWindowSize > 32768){
			throw new IllegalArgumentException("plainWindowSize > 32768");
		}
		if (huffmanWindowSize<1024 && huffmanWindowSize>0){
			huffmanWindowSize=1024;
		}
		this.outputWindow = new byte[huffmanWindowSize];
		if(huffmanWindowSize==0){
			this.lastBlock=true;
			// fixed tree: write header, generate huffman codes
			this.BTYPE=1;
			newBlock();
			this.status=GZipOutputStream.STREAMING;
		} else {
			this.BTYPE=2;
			this.status=GZipOutputStream.STREAM_INIT;
		}
		
		for (int i = 0; i < HASHMAP_COUNT; i++) {
			this.HM[i] = new ZipIntMultShortHashMap(2*1024);
		}
		
		// write GZIP header, if wanted 
		if (compressionType==GZipOutputStream.TYPE_GZIP){
			/*
	         +---+---+---+---+---+---+---+---+---+---+
	         |ID1|ID2|CM |FLG|     MTIME     |XFL|OS | 
	         +---+---+---+---+---+---+---+---+---+---+*/
			this.outStream.write(31);
			this.outStream.write(139);
			this.outStream.write(8);
			this.outStream.write(new byte[6]);
			this.outStream.write(255);
		}
		
	}
	public void close() throws IOException{
		
		this.flush();
		
		// append the final tree, in case of dynamic trees
		if (this.BTYPE==2){
			
			// empty the huffmanwindow and force a new block on purpose, if there might
			// 		occur a new block before the last one. This is to make sure that the 
			//		final flag is set only for the "very" last one
			if ((this.outProcessed+8 +(this.inEnd-this.inStart)*8/3 >this.outputWindow.length)){
				compileOutput();
			}
			
			// compile the few remaining bits into the final block
			LZ77(true);
			this.lastBlock=true;
			compileOutput();
		} else {
			// no final tree, just flush since there is just one single 
			//		and therefore final block
			LZ77(true);
		}
		
		writeFooter();
		
		this.outStream=null;
		
		this.outputWindow=null;
		this.inputBuffer=null;
		this.litCount=null;
		
	}
	/**
	 * It is strongly recomended NOT to call flush before close() since 
	 *  close() is able to handle the flushing better itself.
	 */
	public void flush() throws IOException{
		// flush inputBuffer -> LZ77
		LZ77(false); // do not set to true, since we still need sth. for the last block
		
	}
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		
		if(this.inputBuffer.length == this.inEnd ){
    		// process the inputBuffer if we need space 
    		LZ77(false);
		}
		
		// append the byte to the input buffer
		this.inputBuffer[this.inEnd++]=(byte) b;

		// ... and refresh the isize as well as crc32
		this.isize++;
		byte[] bb = new byte[1];
		bb[0]=(byte)b;
		this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, bb, 0, 1);
		
	}
    public void write(byte[] b) throws IOException {
    	write(b, 0, b.length);
    }
    public void write(byte[] b, int off, int len) throws IOException {
    	int processed=0; 

    	// refresh checksum
    	this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, b, off, len);
    	this.isize+=len;
    	
    	while (processed!=len){
    		
    		// fill data in 
    		if(this.inputBuffer.length - this.inEnd>=len-processed){
    			// if more than necessary fits in
    			System.arraycopy(b, processed+off, this.inputBuffer, this.inEnd, len-processed);
    			this.inEnd+=len-processed;
    			processed=len;
    		} else{
    			System.arraycopy(b, processed+off, this.inputBuffer, this.inEnd, this.inputBuffer.length-this.inEnd);
    			processed+=this.inputBuffer.length-this.inEnd;
    			this.inEnd=this.inputBuffer.length;
    		}
    		
    		// LZ77 the inputBuffer
    		LZ77(false);
    	}
    }
    
    /**
     * This function searches through all hashmaps and returns the best pointer
     * 		that was found.
     * 
     * @param bestPointer	this will hold the resulting distance and lenth pair {distance, length}
     * @param position		position to look at in the inputbuffer
     * @return				if there was a pointer found
     */
    private boolean search4LZ77(int[] bestPointer, int position){
    	ZipIntMultShortHashMap.Element found = null;

    	int[] pointer = new int[2];
    	bestPointer[1]=0;
    	
    	for (int i = 0; i < HASHMAP_COUNT; i++) {
    		
    		// retrieve and compare the best pointers out of each hashmap
    		found=null;
    		found = this.HM[i].get( (128+this.inputBuffer[position])<<16 | (128+this.inputBuffer[position+1])<<8 | (128+this.inputBuffer[position+2]) );
    		
    		if (found!=null &&  found.size!=0){
    			searchHM4LZ77(found, pointer, position);
    			
    			if (pointer[1]> bestPointer[1]){
    				bestPointer[0]=pointer[0];
    				bestPointer[1]=pointer[1];
    			}
    			
    		}
    	}
    	
    	// was a pointer found?
    	return bestPointer[1]!=0;
    }
    
    /**
     *  This function takes the result element of a hashmap to look for previous reoccurrences of the data in the inputbuffer
     *  starting at position. The best matching distance and length pair is return in
     *  pointer. 
     *  
     * @param found the hashmap result of the thee chars
     * @param pointer  distance and lenth pair {distance, length}
     * @param position  position in the inputbuffer
     */
    private void searchHM4LZ77(ZipIntMultShortHashMap.Element found, int[] pointer, int position) {
    	int length;
    	
		// check this.plainPointer-found0.values[k] from the end since shortest pointers are at the end
		int bestK=0, bestLength=0;
		
		for (int k = found.size-1; k >=0 ;k--) {
			
			length=3;
			
			int comparePointer=100000;
			
			// deal with index out of bounds in case of finish
			while(length<258 && position+length < this.inputBuffer.length){
				if (found.values[k]<this.plainPointer){
					comparePointer=(found.values[k]+ ( length % (this.plainPointer - found.values[k]) ) ) % this.plainDataWindow.length;
				} else {
					comparePointer=(found.values[k]+ ( length % (this.plainPointer + this.plainDataWindow.length - found.values[k]) ) ) % this.plainDataWindow.length;
				}
				
				// break, if the compared chars differ
				if (this.inputBuffer[position+length] == this.plainDataWindow[ comparePointer ] ){
					length++;
				} else {
					break;
				}
				
			}
			
			// compare the recently found pointer pair with the currently best
			if (length>bestLength){
				bestK=k;
				bestLength=length;
				if (length==258){
					break;
				}
			}
		}
		
		// encode the pointer
		pointer[0]= /*distance=*/ (this.plainPointer-found.values[bestK] + this.plainDataWindow.length )% this.plainDataWindow.length;
		pointer[1]=/*length=*/bestLength;

    }
    
    /**
     * This function puts the pointer into the outputwindow where it will be taken off
     * when the huffman compression takes place. 
     * 
     * @param distance		the distance information of the pointer
     * @param length		the length information of the pointer
     * @throws IOException 	this might happen, when there occurr errors while writing into the outputStream
     */
    private void encodePointer(int distance, int length) throws IOException{
    	int litlen;
    	byte litextra;
    	int di;
    	int distExtra;
    	
    	//#debug info
		//# System.out.println(length +  "-Tupel found  at " + distance + "back");
		
		// compute length information
		di = ZipHelper.encodeCode(ZipHelper.LENGTH_CODE, length);
		litlen=257+di;
		litextra=(byte) (length - ZipHelper.LENGTH_CODE[di*2+1]);
		
		//compute distance information
		di = ZipHelper.encodeCode(ZipHelper.DISTANCE_CODE, distance);
		distExtra = distance -ZipHelper.DISTANCE_CODE[di*2+1];					
		
		// write buffer information for compiler
		if(this.outputWindow.length!=0){
			
			this.outputWindow[this.outProcessed]=(byte)255; // special distance stuff
			this.outputWindow[this.outProcessed+1]= (byte) (litlen -255); // 0 is reserved!!
			
			this.outputWindow[this.outProcessed+2]= litextra;// value of extra bits
			this.outputWindow[this.outProcessed+3]=(byte)(di);// id of Table == code
			this.outputWindow[this.outProcessed+4]=(byte)(distExtra&0xFF);
			this.outputWindow[this.outProcessed+5]=(byte)(distExtra>>8 &0xFF);
			this.outputWindow[this.outProcessed+6]=(byte)(distExtra>>16 &0xFF);
			
			this.outProcessed+=6; // 7-1
			
			// increase distCount
			this.litCount[litlen]++;
			this.distCount[di]++;
			
		} else { // write direct when dynamic huffman is switched off
			
			// write litlen + extra bytes
			pushSmallBuffer(this.huffmanCode[litlen],this.huffmanCodeLength[litlen]);
			pushSmallBuffer(litextra, (byte) ZipHelper.LENGTH_CODE[2*(litlen-257)]);
			
			// write distance code + extra info
			pushSmallBuffer(this.distHuffCode[di],this.distHuffCodeLength[di]);
			pushSmallBuffer(distExtra, (byte) ZipHelper.DISTANCE_CODE[di*2]);
			
		}
		
    }
    /**
     * This method fills a single char into the huffmanwindow or in the outputStream
     * 		depending on the huffmanmode
     *  
     * @param position		take the char from inputbuffer at this postion
     * @throws IOException	
     */
    private void encodeChar(int position) throws IOException {
    	int val = (this.inputBuffer[position] + 256)&255;
    	
    	if(this.outputWindow.length!=0){
			// and increase the literal Count
			this.litCount[val]++;
	
			// fill the plain char in the output window ... encode 255
			this.outputWindow[this.outProcessed] = (byte)val;
			if(val==255){
				//special encoding for 255
				this.outProcessed++;
				this.outputWindow[this.outProcessed]=(byte)0;
			}
    	} else{
    		pushSmallBuffer(this.huffmanCode[val],this.huffmanCodeLength[val]);
    	}
    }
    
    
    /**
     * This function applies the LZ77 algorithm on the inputBuffer.
     * The inputBuffer is cleared completely and filled into 
     * outputWindow. If outputWindow is full, compileOutput is called.
     * @throws IOException 
     */
    //public int l=0;
    private void LZ77(boolean finish) throws IOException{
    	
    	// prepate the inputbuffer
    	if (this.inStart!=0){
			System.arraycopy(this.inputBuffer, this.inStart, this.inputBuffer, 0, this.inEnd-this.inStart);
			this.inEnd-=this.inStart;
			this.inStart=0;
		}
    	// do not leave sth. behind if finish is enabled
    	int upTo;
    	if(finish){
    		upTo=this.inEnd;
    	} else {
    		upTo=this.inEnd-300;
    	}
    	
    	// process the inputbuffer
    	int[] pointer = new int[2];
    	int[] lastpointer = new int[2];
    	
    	int distance;
    	int length;
    	
    	int i;
    	for (i = 0; i < upTo; ) { 
    		
    		length=1;
    		distance=0;
    		
    		// TODO what about lazy matching??
    		
    		// search for reoccurrence, if there is enough input data left
    		if(this.lz77active && i < upTo-2 && search4LZ77(pointer, i)){
    			
    			if (pointer[1]>lastpointer[1]){
    				// put single char of (i-1) and keep searching
    				lastpointer[0]=pointer[0];
    				lastpointer[1]=pointer[1];
    			} else {
    				distance=pointer[0];
    				length=pointer[1];
    			}
    			
    		}
    		
    		// the pointer is not allowed to exceed the inputBuffer!!
    		if (finish && upTo-i<length){
    			length=upTo-i;
    		}
    		
    		// encode pointer or char
    		if( length>2 ){
    			encodePointer(distance, length);
    		}else{
    			encodeChar(i);
    		}
    		
    		
    		// check if Huffman compression is requested
    		if (this.outputWindow.length!=0){
	    		// check, if we have to compile the output
	    		this.outProcessed++;
	    		// there has to be enough space for a length, distance pair
	    		if (this.outProcessed+8>this.outputWindow.length){
	    			// if outputWindow full : call compileOutput
	    			compileOutput();
	    		}
	    		
    		}
    		
			// refresh the plain window, if lz77 is active
    		if (this.lz77active){
				for (int k = 0; k < length; k++) {
					this.plainDataWindow[this.plainPointer] = this.inputBuffer[i+k];
					
					// add the bytes to the hashmap
		    		this.HM[this.plainPointer/(this.plainDataWindow.length/HASHMAP_COUNT)].put( (128+this.inputBuffer[i+k])<<16 | (128+this.inputBuffer[i+k+1])<<8 | (128+this.inputBuffer[i+k+2]) , (short) this.plainPointer);
					
					// clear hashmap
					if (++this.plainPointer%(this.plainDataWindow.length/HASHMAP_COUNT)==0){
						// wrap around
						if (this.plainPointer==this.plainDataWindow.length){
							this.plainPointer=0;
						}
						
						this.HM[(this.plainPointer/(this.plainDataWindow.length/HASHMAP_COUNT)) % HASHMAP_COUNT].clear();
					}
					
				}
    		}
			// i is just increased here
			i+=length;
    		
		}
    	
    	this.inStart=i;
    }
    
    /**
     * This function generates and writes the header for a new block.
     * Therefore it has to calculate and compress the huffman trees.
     * 
     * @throws IOException
     */
    private void newBlock() throws IOException{
    	if(this.status==GZipOutputStream.STREAM_INIT){
			this.status=GZipOutputStream.STREAMING;
		} else {
			pushSmallBuffer(this.huffmanCode[256],this.huffmanCodeLength[256]);
			// write old 256, write blockheader
		}
		if(this.lastBlock){
			pushSmallBuffer(1, (byte)1);
			//#debug
			System.out.println("final block");
		} else{
			pushSmallBuffer(0, (byte)1);
		}
		
		pushSmallBuffer(this.BTYPE, (byte)2);
		
		this.huffmanCode = new int[286];
		this.huffmanCodeLength  = new byte[286];
		this.distHuffCode  = new int[30];
		this.distHuffCodeLength  = new byte[30];
		
		if (this.BTYPE==1){
			ZipHelper.genFixedTree(this.huffmanCode, this.huffmanCodeLength, this.distHuffCode, this.distHuffCodeLength);
		} else if (this.BTYPE==2) {
			
			
	    	// fake two distance codes, if there are none
	    	for (int i = 0; i < 2; i++) {
	    		if (this.distCount[i]==0){
	    			this.distCount[i]=1;
				}
			}
			
			// generate dynamic Huffmantree for Literals + Length
			this.litCount[256]=1; // theoretisch kann man es auch auf 2 setzen, was das Problem jedoch nicht loest
			ZipHelper.genTreeLength(this.litCount, this.huffmanCodeLength,15);
			ZipHelper.genHuffTree(this.huffmanCode, this.huffmanCodeLength);
			ZipHelper.revHuffTree(this.huffmanCode, this.huffmanCodeLength);
			
			// generate dynamic Huffmantree for Distances
			ZipHelper.genTreeLength(this.distCount, this.distHuffCodeLength,15);
			ZipHelper.genHuffTree(this.distHuffCode, this.distHuffCodeLength);
			ZipHelper.revHuffTree(this.distHuffCode, this.distHuffCodeLength);
			
			// save tree
			compressTree(this.huffmanCodeLength, this.distHuffCodeLength);
			
			// clear the counter
			for (int i = 0; i < 286; i++) {
				this.litCount[i]=0;
			}
			for (int i = 0; i < 30; i++) {
				this.distCount[i]=0;
			}
			
		}

    }
    
	/**
	 * This function applies the huffmanencoding on the collected data in outputWindow.
	 * The huffman encoded data is then written into the outputStream.
	 * 
	 * @throws IOException 
	 */
	private void compileOutput() throws IOException{
		//#debug
		System.out.println("  compile Output; new Block");

		// generate & store the tree
		newBlock();			
		
		int litlen,	litextra,di,distExtra;
		
		// write the data
		int val=0;
		for (int i = 0; i < this.outProcessed; i++) {
			
			val=this.outputWindow[i];
			if(val<0){
				val+=256;
			} 
			if(val!=255){
				pushSmallBuffer(this.huffmanCode[val],this.huffmanCodeLength[val]);
			} else {
				if (val==255){
					i++;
					if (this.outputWindow[i]==0){
						// 255 char
						pushSmallBuffer(this.huffmanCode[255],this.huffmanCodeLength[255]);
						
					} else if(this.outputWindow[i]>0) {
						// compile encoded pointer
						
						// length information
						litlen=255+this.outputWindow[i];
						i++;
						litextra=this.outputWindow[i];
						i++;
						
						// distance information
						di=this.outputWindow[i];
						i++;
						distExtra= ((this.outputWindow[i]+256) & 255) | ((this.outputWindow[i+1]+256) &255)<<8 | ((this.outputWindow[i+2]+256) &255) <<16;
						i+=3;
						//distance=distExtra + ZipUtil.DISTANCE_CODE[di*2+1];

						// write litlen + extra bytes
						pushSmallBuffer(this.huffmanCode[litlen],this.huffmanCodeLength[litlen]);
						pushSmallBuffer(litextra, (byte) ZipHelper.LENGTH_CODE[2*(litlen-257)]);
						
						// write distance code + extra info
						pushSmallBuffer(this.distHuffCode[di],this.distHuffCodeLength[di]);
						pushSmallBuffer(distExtra, (byte) ZipHelper.DISTANCE_CODE[di*2]);
						
						i--;
					} else {
						throw new IOException("illegal code decoded");
					}
					
				}
				
			}
		}
		this.outProcessed=0;
	}
	
	/**
	 * This function finishes the last block and writes the checksums.
	 * 
	 * @throws IOException
	 */
	private void writeFooter() throws IOException{
		// write current 256
		pushSmallBuffer(this.huffmanCode[256],this.huffmanCodeLength[256]);
		//#debug
		System.out.println(" wrote final 256;");
		
		// flush the upto the byte boundrary
		if ((this.smallCodeBuffer[1]&7) != 0){
			pushSmallBuffer(0, (byte) (8-(this.smallCodeBuffer[1]&7)));
		}
		
		//write CRC, count
		this.outStream.write(this.crc32 & 255);
		this.outStream.write((this.crc32>>>8) & 255);
		this.outStream.write((this.crc32>>>16) & 255);
		this.outStream.write((this.crc32>>>24) & 255);
		
		this.outStream.write(this.isize & 255);
		this.outStream.write((this.isize>>>8) & 255);
		this.outStream.write((this.isize>>>16) & 255);
		this.outStream.write((this.isize>>>24) & 255);
		
		// close the stream
		this.outStream.flush();
		this.outStream.close();
		
		//#debug
		System.out.println(" output finished");
	}

	/**
	 * This function compresses and writes the literal/length as well as the distance trees. 
	 * At first repeatcodes are determined, then the miniTree is generated and after all they
	 * are written into the outputStream.
	 * 
	 * @param huffmanCodeLength
	 * @param distHuffCodeLength
	 * @throws IOException
	 */
    private void compressTree(byte[] huffmanCodeLength, byte[] distHuffCodeLength) throws IOException{
    	
    	int HLIT=285;
    	int HDIST=29;
    	
    	while(huffmanCodeLength[HLIT]==0 && HLIT>29){HLIT--;}
    	HLIT++;
    	
    	// dont worry: empty distance tree workaround is in newBlock();
    	while(distHuffCodeLength[HDIST]==0 && HDIST>0){HDIST--;}
    	HDIST++; // # of all Distance Symbols
    	
    	// merge Hlit + Hdist
    	byte[] len=new byte[HLIT+HDIST];
    	int j=0;
    	for (int i = 0; i < HLIT; i++) {
    		len[j]=huffmanCodeLength[i];
    		//System.out.println("  " + j + " " + huffmanCode[j] + ", " + huffmanCodeLength[j]);
    		j++;
		}
    	for (int i = 0; i < HDIST; i++) {
    		len[j]=distHuffCodeLength[i];
    		//System.out.println("  " + j + " " + distHuffCode[i] + ", " + distHuffCodeLength[i]);
    		j++;
		}
    	
    	int[] miniHuffData= { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    	
    	// fill repeatcodes in and count them
    	byte[] outLitLenDist=new byte[HLIT+HDIST];
    	int outCount=0;
    	int[] miniCodeCount=new int[19]; // count of used miniCodes
		short k;
    	for (int i = 0; i < len.length; i++) {
    		
			if( i+3< len.length && len[i]==len[i+1] && len[i]==len[i+2] && len[i]==len[i+3]){
				if(len[i]==0){
					outLitLenDist[outCount]=0; // ausgliedern
					//System.out.println("wrinting outCount="+outCount + "   =  0");
					k=4;
					
					while(i+k<len.length && len[i]==len[i+k] && k<139){k++;}

					
					if (k<11+1) {
						//System.out.println("writing outCount="+outCount + "   =  17");
						outLitLenDist[outCount+1]=17;
						outLitLenDist[outCount+2]=(byte) (k-3-1);
					} else{
						//System.out.println("writing outCount="+outCount + "   =  18");
						//System.out.println(" repeat count = " + (k-1));
						outLitLenDist[outCount+1]=18;
						outLitLenDist[outCount+2]=(byte) (k-11-1);
					}
					
					i+=k-1;
				}else{
					// encode repeat code ; char * n -> char, 16, n
					//System.out.println("writing outCount="+outCount + "   =  "+len[i]);
					outLitLenDist[outCount]=len[i];
					outLitLenDist[outCount+1]=16;
					//System.out.println("writing outCount="+outCount + "   =  16");
					
					// repeat count
					k=4;
					while(i+k<len.length && len[i]==len[i+k] && k<7){k++;}
					outLitLenDist[outCount+2]= (byte) (k-4);
					i+=k-1;
				}
				// add value to statistic
				miniCodeCount[outLitLenDist[outCount]]++;
				// add repeat code to statistic
				miniCodeCount[outLitLenDist[outCount+1]]++;
				// +3  == 2 +1
				outCount+=2;
				
			} else{
				//System.out.println("wrinting outCount="+outCount + "   =  "+ len[i]);
				outLitLenDist[outCount]=len[i];
				miniCodeCount[outLitLenDist[outCount]]++;
			}

			outCount++;
		}
    	
    	
    	// generate mini Tree
		byte[] miniHuffCodeLength=new byte[19];
		int[] miniHuffCode=new int[19];
		
		int i=0;
		
		ZipHelper.genTreeLength(miniCodeCount, miniHuffCodeLength,7);
		
		ZipHelper.genHuffTree(miniHuffCode, miniHuffCodeLength);
		ZipHelper.revHuffTree(miniHuffCode, miniHuffCodeLength);
		
		// write Header-Header
    	pushSmallBuffer(HLIT-257, (byte)5);
    	pushSmallBuffer(HDIST-1, (byte)5); 
    	
    	
    	int HCLEN=19-1;
    	while(miniHuffCodeLength[miniHuffData[HCLEN]]==0 && HCLEN>0){HCLEN--;}
    	HCLEN++;
    	
       	pushSmallBuffer(HCLEN-4, (byte)4);
		
       	// write Mini-Tree
       	for (i = 0; i < HCLEN; i++) {
       		//System.out.println("" + i + "=" +miniHuffData[i] + ":\t" + miniHuffCodeLength[miniHuffData[i]]);
       		pushSmallBuffer(miniHuffCodeLength[miniHuffData[i]], (byte)3);
       		
		}
       	//#mdebug
    	System.out.println(" HLIT: " + HLIT);
    	System.out.println(" HDIST: " + HDIST);
    	System.out.println(" HCLEN: " + HCLEN);
    	//#enddebug
       	
		// write Huffmanntree
    	for (i = 0; i < outCount; i++) {
    		// write code
    		//System.out.println("wrinting i="+i + "   =  " + outLitLenDist[i]);
    		pushSmallBuffer(miniHuffCode[outLitLenDist[i]], miniHuffCodeLength[outLitLenDist[i]]);

    		// add special treatment
    		if (outLitLenDist[i]>15){
    			switch (outLitLenDist[i]) {
				case 16:
					pushSmallBuffer(outLitLenDist[i+1], (byte)2);
					i++;
					break;
				case 17:
					pushSmallBuffer(outLitLenDist[i+1], (byte)3);
					i++;
					break;
				default: // 18
					pushSmallBuffer(outLitLenDist[i+1], (byte)7);
					i++;
					break;
				}
    			
    		}
    		
    		//System.out.println(outLitLenDist[i] + " == code: " + miniHuffCode[outLitLenDist[i]]);
		}
    	
    }
	
	
	/**
	 * This function is able to write bits into the outStream. Please
	 * mind that is uses a buffer. You might have to flush it, by giving zeros.
	 *   
	 * @param val The bitsequence as an integer.
	 * @param len The number of bits to process.
	 * @throws IOException in case Errors within the outputStream ocurred
	 */
	private void pushSmallBuffer(int val, byte len) throws IOException{
		int smallBuffer0 = this.smallCodeBuffer[0];
		int smallBuffer1 = this.smallCodeBuffer[1];
		
		// add the given data
		smallBuffer0 &= ~( ((1<<len)-1) << smallBuffer1);
		smallBuffer0|= (val<<smallBuffer1);
		smallBuffer1+=len;
		
		// clear the buffer except for a fraction of a reamining byte
		while(smallBuffer1>=8){
			this.outStream.write(smallBuffer0&255);
			smallBuffer0>>>=8;
			smallBuffer1-=8;
		}
		
		this.smallCodeBuffer[0]=smallBuffer0;
		this.smallCodeBuffer[1]=smallBuffer1;
	}
	
}
