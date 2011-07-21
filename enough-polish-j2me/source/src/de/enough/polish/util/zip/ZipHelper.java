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
 * <p>Provides some helper methods for ZipInputStream and ZipOutputStream.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jun 25, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public final class ZipHelper {
	
	// constants:
	public static final int[] LENGTH_CODE=
	{
	0	,	3	,
	0	,	4	,
	0	,	5	,
	0	,	6	,
	0	,	7	,
	0	,	8	,
	0	,	9	,
	0	,	10	,
	1	,	11	,
	1	,	13	,
	1	,	15	,
	1	,	17	,
	2	,	19	,
	2	,	23	,
	2	,	27	,
	2	,	31	,
	3	,	35	,
	3	,	43	,
	3	,	51	,
	3	,	59	,
	4	,	67	,
	4	,	83	,
	4	,	99	,
	4	,	115	,
	5	,	131	,
	5	,	163	,
	5	,	195	,
	5	,	227	,
	0	,	258	};
	public static final int[] DISTANCE_CODE=
	{
	0	,	1	,
	0	,	2	,
	0	,	3	,
	0	,	4	,
	1	,	5	,
	1	,	7	,
	2	,	9	,
	2	,	13	,
	3	,	17	,
	3	,	25	,
	4	,	33	,
	4	,	49	,
	5	,	65	,
	5	,	97	,
	6	,	129	,
	6	,	193	,
	7	,	257	,
	7	,	385	,
	8	,	513	,
	8	,	769	,
	9	,	1025	,
	9	,	1537	,
	10	,	2049	,
	10	,	3073	,
	11	,	4097	,
	11	,	6145	,
	12	,	8193	,
	12	,	12289	,
	13	,	16385	,
	13	,	24577	};
	
	public static int encodeCode(int[] Code , int distance){
		int i=0;
		//while(i<Code.length/2 && distance>=Code[i*2+1]){i++;}
		while( i < (Code.length>>1) && distance >= Code[(i<<1)+1]){
			i++;
		}
		// now: distance < Code[i*2+1]
		
		return i-1;
	}
	
	
    /**
     * This function generates the huffmann codes according to a given set of bitlegth.
     * 
     * @param huffmanCode			will be the resulting code pattern
     * @param huffmanCodeLength		has to be the bitlegth for each code
     */
    public static void genHuffTree(int[] huffmanCode, byte[] huffmanCodeLength){
    	int maxbits=0;
    	
    	// generate bitlen_count
    	for (int i = 0; i < huffmanCodeLength.length; i++) {
    		int length = huffmanCodeLength[i];
    		maxbits= maxbits > length ? maxbits : length;
		}
    	maxbits++;
    	
    	short[] bitlen_count = new short[maxbits];
    	for (int i = 0; i < huffmanCodeLength.length; i++) {
    		bitlen_count[huffmanCodeLength[i]]++;
		}
    	
    	int code=0;
    	int[] next_code=new int[maxbits]; 
    	bitlen_count[0]=0;
    	
    	// find the Codes for each smallest Code within all of the same bitlength
    	for (int bits = 1; bits < maxbits; bits++) {
    		//code=(code + bitlen_count[bits-1])*2;
    		code=(code + bitlen_count[bits-1])<<1;
			next_code[bits]=code;
		}
    	// generate all codes by adding 1 to the predecessor
    	for (int i = 0; i < huffmanCode.length; i++) {
    		byte length = huffmanCodeLength[i];
			if (length!=0){
	    		huffmanCode[i]=next_code[length];
	    		next_code[length]++;
    		}
		}
    	
    }
    
    public static void revHuffTree(int[] huffmanCode, byte[] huffmanCodeLength){
    	// reverse all:
    	int tmp;
    	int reversed;
    	for (int i = 0; i < huffmanCode.length; i++) {
    		tmp=huffmanCode[i];
    		reversed=0;
    		for (int j = 0; j < huffmanCodeLength[i]; j++) {
    			reversed|=((tmp>>>j)&1);
    			reversed<<=1;
			}
    		huffmanCode[i] = reversed>>>1;
		}
    }


    /**
     * Generate the fixed huffman trees as described in the rfc 
     * 
     * @param huffmanCode - pointer to where the code shall be inserted
     * @param huffmanCodeLength
     * @param distHuffCode
     * @param distHuffCodeLength
     */
    public static void genFixedTree(int[] huffmanCode, byte[] huffmanCodeLength,
    		int[] distHuffCode, byte[] distHuffCodeLength){
    	
    	int i;
    	// huffmanCodes
		for (i= 0; i <= 143; i++) {
			huffmanCode[i]=48 + i; //48==00110000
			huffmanCodeLength[i]=8;
		}
		for (i = 144; i <= 255; i++) {
			huffmanCode[i]=400 + i-144; //400==110010000
			huffmanCodeLength[i]=9;
		}
		for (i = 256; i <= 279; i++) {
			huffmanCode[i]=i-256; //0==0
			huffmanCodeLength[i]=7;
		}
		for (i = 280; i < 286; i++) {
			huffmanCode[i]=192+i-280; //192==11000000
			huffmanCodeLength[i]=8;
		}
		// reverse all:
		ZipHelper.revHuffTree(huffmanCode, huffmanCodeLength);
		
		// distHuffCode for non fixed DISTANCE tree
		for (int j = 0; j < distHuffCode.length; j++) {
			distHuffCode[j]=j;
			distHuffCodeLength[j]=5;
		}
		// reverse all:
		ZipHelper.revHuffTree(distHuffCode, distHuffCodeLength);
	}
    


    /**
     * Compute the tree length according to the frequency of each code 
     * 
     * @param count 
     * @param huffmanCodeLength
     * @param max_len 
     */
    public static void genTreeLength(int[] count, byte[] huffmanCodeLength, int max_len){
    	int[] knotCount = new int[count.length];
    	int[] knotPointer = new int[count.length];
    	
    	// initialise the knots
    	for (short i = 0; i < count.length; i++) {
    		if (count[i]!=0){
    			knotCount[i]=count[i];
    		}else{
    			knotCount[i]=Integer.MAX_VALUE;
    		}
    		knotPointer[i]= i;
		}
    	
    	// look for the smalest two knots
    	int s1=0, s2=0;
    	while(true){
    		
	    	// find Min1 and Min2
	    	if(knotCount[0]<knotCount[1]){
	    		s1=0;
	    		s2=1;
	    	} else {
	    		s1=1;
	    		s2=0;
	    	}
	    	for (int i = 2; i < count.length; i++) {
				if (knotCount[i]<knotCount[s1]){
					s2=s1;
					s1=i;
				} else if (knotCount[i]<knotCount[s2]){
					s2=i;
				}
			}
	    	
			// if we got all Minima -> exit
			if(knotCount[s2]== Integer.MAX_VALUE){
				break;
			}
	    	
			// merge the knots
			knotCount[s1]+=knotCount[s2];
			int tmp=knotPointer[s2];
			knotCount[s2]=Integer.MAX_VALUE;
			
			// set all knot pointer of Min2 -> Min1,   add one to CodeLength, and remove old knots from search
			for (int i = 0; i <count.length; i++) {
				if(knotPointer[i]==tmp){
					knotPointer[i]=knotPointer[s1];
					huffmanCodeLength[i]++;
				} else if (knotPointer[i]==knotPointer[s1]){
					huffmanCodeLength[i]++;
				}
			}
			
    	}
    	
    	
    	// check for overflow
    	int overflowCount=0;
    	for (int i = 0; i < huffmanCodeLength.length; i++) {
    		if (huffmanCodeLength[i]>max_len) {
    			overflowCount++;
    		}
		}
    	// fix the overflow
    	if (overflowCount!=0){
    		//System.out.println(" fixing " + overflowCount + " overflows  because of max=" + max_len);
	    	
    		// list the overflows
    		short[] overflows = new short[overflowCount];
	    	overflowCount=0;
	    	for (short i = 0; i < huffmanCodeLength.length; i++) {
	    		if (huffmanCodeLength[i]>max_len) {
	    			overflows[overflowCount++]=i;
	    		}
			}
	    	// find the index of the smalest node
	    	int minNode=0;
	    	for (int i = 0; i < huffmanCodeLength.length; i++) {
				if (huffmanCodeLength[i]!=0 && huffmanCodeLength[minNode]>huffmanCodeLength[i]){
					minNode=i;
				}
			}
	    	
	    	// ok lets go and fix it....
	    	int exendableNode;
	    	int overflow1, overflow2;
	    	while(overflowCount!=0){
	    		exendableNode=minNode;
	    		// find the largest expandable length
	    		for (int i = 0; i < huffmanCodeLength.length; i++) {
	    			if(huffmanCodeLength[i]<max_len && huffmanCodeLength[exendableNode]<huffmanCodeLength[i]){
	    				exendableNode=i;
	    			}
				}
	    		
	    		// find the deepest two overflows
	    		overflow1=0;
	    		overflow2=0;
	    		for (int i = 0; i < overflows.length; i++) {
	    			if(huffmanCodeLength[overflows[i]]> huffmanCodeLength[overflow1]){
	    				overflow1=overflows[i];
	    				
	    			} else if(huffmanCodeLength[overflows[i]]== huffmanCodeLength[overflow1]) {
	    				overflow2=overflows[i];
	    			}
				}
	    		
	    		// insert one of them at the best exendableNode
	    		huffmanCodeLength[exendableNode]++;
	    		huffmanCodeLength[overflow1]=huffmanCodeLength[exendableNode];
	    		
	    		// lift the other one
	    		huffmanCodeLength[overflow2]--;
	    		
	        	// refresh the overflow count
	        	overflowCount--;
	        	if (huffmanCodeLength[overflow2]==max_len){
	        		overflowCount--;
	        	}
	    	}
    	}
    	
    }
    
    
    /**
     *  This function converts the representation of a huffman tree from
     *  table to tree structure.
     *  
     * @param huffmanCode	input
     * @param huffmanCodeLength		input
     * @param huffmanData	input
     * @param huffmanTree   resulting tree
     */
    public static void convertTable2Tree(int[] huffmanCode, byte[] huffmanCodeLength, int[] huffmanData, short[] huffmanTree){
    	
    	for (int i = 0; i < huffmanTree.length; i++) {
    		huffmanTree[i]=0;
		}
    	
    	short pointer;
    	short nextNode=1;
    	// fill each code in
    	short j;
    	for (short i = 0; i < huffmanCode.length; i++) {
			if (huffmanCodeLength[i]!=0){
				pointer=0;
				for (j = 0; j < huffmanCodeLength[i]; j++) {
					
					if(huffmanTree[pointer*2]==0){
						// undefined internal NODE
						huffmanTree[pointer*2]=nextNode++;
						huffmanTree[pointer*2+1]=nextNode++;
					} 
					// go to known internal NODE
					pointer=huffmanTree[pointer*2+ ((huffmanCode[i]>>>j)&1)];
					
				}
				
				// set empty LEAF to data
				if (pointer<0){
					//#debug error
					System.out.println("error pointer=-1");
				}
				huffmanTree[pointer*2]=-1;
				huffmanTree[pointer*2+1]=(short)huffmanData[i];
			}
		}
    }
    
	/**
	 * This function parses and pops one huffmancode out of the given buffer
     * and returs the associated value.
	 * 
	 * @param smallCodeBuffer the buffer that is parsed
	 * @param huffmanTree is the representation of the tree
	 * @return the data represented by the parsed huffman code
	 * @throws IOException
	 */
    public static int deHuffNext(long[] smallCodeBuffer, short[] huffmanTree) throws IOException{
//    	//#debug
//    	System.out.println("deHuffNext(long[], short[])");
    	//#if polish.debug.error
    	if (smallCodeBuffer[1]<15){
    		//#debug error
    		System.out.println("smallCodebuffer is too small");
    	}
    	//#endif
    	
    	short pointer=0;
    	
    	while(huffmanTree[pointer*2]!=-1){
			// go to next internal NODE
			pointer=huffmanTree[pointer*2+ (int)(smallCodeBuffer[0]&1)];
			smallCodeBuffer[0]>>>=1;
			smallCodeBuffer[1]--;
			
			if (pointer==0){
		    	//System.out.println("small code buffer DAta "+smallCodeBuffer[0] + "\n");
		    	// error: in walking tree no leaf found
		    	throw new IOException("5");
			}
			
		}
    	// return the data
    	return huffmanTree[pointer*2+1];

    }

    /**
     * This function skips the Gzip Header of a given stream.
     * @param in the stream
     * @throws IOException ocurrs if the stream is not readable
     */
	public static void skipheader(InputStream in) throws IOException{
		// check magic number and compression algorithm
		if (in.read()!=31 || in.read()!=139 ||  in.read()!=8){
			// wrong gzip header
			throw new IOException("0");
		}
		
		int flag = in.read();
		in.skip(6);
		
		// skip FEXTRA
		if((flag & 4)==4){
			 in.skip( in.read() | in.read()<<8);
		}
		// skip FNAME
		if((flag&8)==8){
			while(in.read()!=0){
				//
			}
		}
		// skip FCOMMENT
		if((flag&16)==16){
			while(in.read()!=0){
				//
			}
		}
		// skip FHCRC
		if((flag & 2)==2){
			in.skip(2);
		}
		
	}

	/**
	 * In order to compute the CRC checksum fast it is crucial to 
	 * have a precomputed table.
	 * 
	 * @param table store the table here
	 */
	private static void initCrc32Table(int[] table){
		int c;
		
		int n,k;
		for(n=0; n<256; n++){
			c=n;
			for(k=0; k<8;k++){
				if((c&1)==1){
					c=0xedb88320 ^ (c >>> 1);
				} else{
					c>>>=1;
				}
			}
			table[n]=c;
		}
	}
	
	/**
	 *  This function computes / refreshes a crc32 checksum. 
	 *  
	 * @param table this is the precomputed crc32 table
	 * @param crc set this value to zero if starting the computation
	 * 				or to the last retrieved value when processing 
	 * 				further data.
	 * @param buffer the buffer to be walked
	 * @param off the offset to start
	 * @param len the amount of bytes to process
	 * @return the new/refreshed crc checksum
	 */
	public static int crc32(int[] table, int crc, byte[] buffer, int off, int len){
		if (table[2]==0){
			initCrc32Table(table);
		}
		
		crc = crc ^ 0xffffffff;
		
		for(int n=0; n<len;n++){
			crc=table[(crc^buffer[n+off]) & 0xff]^(crc>>>8);
		}
		
		return 0xffffffff ^ crc ;
		
	}
	
}
