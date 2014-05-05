package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import junit.framework.TestCase;

public class ChunkedStorageCollectionTest extends TestCase 
{
	
	public void testChunking()
	{
		TestChunkedStorageCollection collection = new TestChunkedStorageCollection();
		int testSize = 1003;
		for (int i=0; i<testSize; i++)
		{
			collection.add( new TestData(Integer.toString(i)));
		}
		assertEquals(testSize, collection.size());
		assertEquals(testSize % 20 + 20, collection.sizeTail());
		ArrayList list = (ArrayList) ChunkedStorageMemorySystem.listsPerIdentifier.get("test");
		assertNotNull(list);
		assertTrue(list.size() > 0);
		assertEquals(list.size(), testSize / 20);
		assertTrue(collection.isDirty());
		try {
			collection.saveCollection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("for " + e.toString());
		}
		assertFalse(collection.isDirty());
		
		for (int i=testSize-1; i>=0; i--)
		{
			//System.out.println("testing " + i);
			assertEquals(Integer.toString(i), ((TestData)collection.get(i)).data);
		}
		
		collection.remove(1000);
		assertTrue(collection.isDirty());
		assertEquals(testSize - 1, collection.size());
		
		int toAdd = 23;
		for (int i=0; i<toAdd; i++)
		{
			collection.add( new TestData(Integer.toString(i + testSize)));
		}
	
		assertEquals(testSize + toAdd - 1, collection.size() );
		assertEquals( (testSize + toAdd - 1) % 20 + 20, collection.sizeTail() );
	}
	
	public void testRemove()
	{
		TestChunkedStorageCollection collection = new TestChunkedStorageCollection();
		int testSize = 1003;
		for (int i=0; i<testSize; i++)
		{
			collection.add( new TestData(Integer.toString(i)));
		}
		assertEquals(testSize, collection.size());
		System.out.println("------ before removal ------");
		//System.out.println("now: internal of 9: " + collection.getInternalIndex(9) + " -> " + collection.get(9));
		for (int i=0; i<30; i++)
		{
			System.out.print( collection.getInternalIndex(i) + ": " + collection.get(i) + ", ");
		}
		System.out.println();
		int[] removeIndeces = new int[]{ 10, 0, 0, 999, 0, 26, 1, 767, 400, 0,  14, 7 };
		for (int i = 0; i < removeIndeces.length; i++)
		{
			int removeIndex = removeIndeces[i];
			System.out.print("removing " + removeIndex + ", internal=" + collection.getInternalIndex(removeIndex) + " -> " );
			Object previous = collection.remove(removeIndex);
			System.out.println(previous);
			System.out.println("now: internal of 2: " + collection.getInternalIndex(2) + " -> " + collection.get(2));
			System.out.println("now: internal of 3: " + collection.getInternalIndex(3) + " -> " + collection.get(3));
			System.out.println("now: internal of 4: " + collection.getInternalIndex(4) + " -> " + collection.get(4));
			assertNotNull(previous);
			testSize--;
			assertEquals( testSize, collection.size() );
		}
		System.out.println("------ after removal ------");
		for (int i=0; i<30; i++)
		{
			System.out.print( i + "(" + collection.getInternalIndex(i) + "): " + collection.get(i) + ", ");
		}
		System.out.println();
		for (int i=0; i<collection.size(); i++)
		{
			if (collection.get(i) == null)
			{
				fail("got null for index " + i + "->" + collection.getInternalIndex(i));
			}
		}
	}
	
	static class TestData implements Mutable
	{
		private String data;
		private boolean isDirty;

		public TestData(String data)
		{
			this.data = data;
		}

		public void write(DataOutputStream out) throws IOException {
			Serializer.writeUtfNullable(this.data, out);
			this.isDirty = false;
		}

		public void read(DataInputStream in) throws IOException {
			this.data = Serializer.readUtfNullable(in);
		}
		
		public void setData(String data)
		{
			this.data = data; 
			this.isDirty = true;
		}

		public boolean isDirty() {
			return this.isDirty;
		}
		
		public String toString()
		{
			return this.data;
		}
		
	}
	
	static class TestChunkedStorageCollection extends ChunkedStorageCollection
	{
		public TestChunkedStorageCollection()
		{
			super("test", 20, new ChunkedStorageMemorySystem(), ChunkedStorageCollection.STORAGE_STRATEGY_CHUNKED);
		}
		public Mutable createCollectionObject() {
			return new TestData(null);
		}
	}
	
	static class ChunkedStorageMemorySystem implements ChunkedStorageSystem
	{
		
		private static Hashtable listsPerIdentifier;
		
		public ChunkedStorageMemorySystem()
		{
			listsPerIdentifier = new Hashtable();
		}

		public byte[] loadTailData(String identifier) throws IOException {
			return loadData(-1, identifier);
		}

		public byte[] loadData(int chunkIndex, String identifier)
				throws IOException 
		{
			ArrayList list = (ArrayList) listsPerIdentifier.get(identifier);
			if (list == null)
			{
				throw new IOException();
			}
			int index = chunkIndex + 1;
			return (byte[]) list.get(index);
		}

		public void saveTailData(String identifier, byte[] data)
				throws IOException 
		{
			saveChunkData(-1, identifier, data);
		}

		public void saveChunkData(int chunkIndex, String identifier, byte[] data)
				throws IOException 
		{
			int index = chunkIndex + 1;
			ArrayList list = (ArrayList) listsPerIdentifier.get(identifier);
			if (list == null)
			{
				list = new ArrayList();
				listsPerIdentifier.put(identifier, list);
			}
			if (index < list.size())
			{
				list.set(index, data);
			}
			else
			{
				list.add(data);
				if (list.size() < index + 1)
				{
					throw new IOException("inconsistent state: expected size=" + (index + 1) + ", actual size=" + list.size());
				}				
			}
		}

		public void delete(String identifier) throws IOException {
			listsPerIdentifier.remove(identifier);
		}
		
	}

}
