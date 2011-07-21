//#condition polish.midp || polish.usePolishGui

//PATCHED VERSION for delete function (ask fabio)

/*
 * Created on 13-Mar-2006 at 21:34:23.
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
package de.enough.polish.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

/**
 * <p>Stores serializable objects in the record store system.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        13-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Begin Fabio (delete functions)
 * @param <K> when you use the enough-polish-client-java5.jar you can parameterize the RmsStorage, e.g. RmsStorage&lt;Vector&lt;Note&gt;&gt;.
 */
public class RmsStorage
//#if polish.java5
	<K>
	implements Storage<K> 
//#else
	 //# implements Storage 
//#endif
{
	
	private RecordStore masterRecordStore;
	private final HashMap masterRecordSetIdsByName;
	private final HashMap masterRecordSetNameById;
	private final int indexRecordId;
	private boolean isClosed;
	private String mastRecordStoreName;
	
	static ArrayList masterRecordStores = new ArrayList();

	/**
	 * Creates a new RmsStorage that uses different record stores for each stored object
	 */
	public RmsStorage() 
	{
		this.masterRecordStore = null;
		this.masterRecordSetIdsByName = null;
		this.masterRecordSetNameById = null;
		this.indexRecordId = -1;
	}
	
	
	/**
	 * Creates a new RmsStorage that uses a single record store for several stored objects
	 * 
	 * @param singleRecordStoreName the name of the record store, when one record store 
	 *        should be used for all entries - or null when for each name a new record store
	 *        should be created.  
	 * @throws IOException when the singleRecordStore is not null and the corresponding recordstore could not be opened or created.
	 */
	public RmsStorage( String singleRecordStoreName ) 
	throws IOException 
	{
		if ( singleRecordStoreName != null ) {
			try {
				this.mastRecordStoreName = singleRecordStoreName;
				this.masterRecordStore = RecordStore.openRecordStore(singleRecordStoreName, true);
				this.masterRecordSetIdsByName = new HashMap();
				this.masterRecordSetNameById = new HashMap();
				
				// add master recordstore to list
				if(!masterRecordStores.contains(this.masterRecordStore))
				{
					masterRecordStores.add(this.masterRecordStore);
				}
				
				// now read index record set:
				RecordEnumeration enumeration = this.masterRecordStore.enumerateRecords( null, null, false );
				int firstId = Integer.MAX_VALUE;
				while ( enumeration.hasNextElement() ) {
					// read index:	
					int id = enumeration.nextRecordId();
					if ( id < firstId ) {
						firstId = id;
					}
				}
				enumeration.destroy();
				if (firstId == Integer.MAX_VALUE) {
					// ok, this record store has just been created, so add a dumny 
					// recordset for now:
					ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream( byteOut );
					out.writeInt( 0 );
					byte[] data = byteOut.toByteArray();
					this.indexRecordId = this.masterRecordStore.addRecord(data, 0, data.length );
					out.close();
					byteOut.close();
				} else {
					this.indexRecordId = firstId;
					DataInputStream in = new DataInputStream( 
							new ByteArrayInputStream( this.masterRecordStore.getRecord(firstId) ));
					int numberOfEntries = in.readInt();
					Integer inte = null;
					for (int i = 0; i < numberOfEntries; i++) {
						String name = in.readUTF();
						int recordId = in.readInt();
						inte =  new Integer( recordId );
						this.masterRecordSetIdsByName.put( name,inte );
						this.masterRecordSetNameById.put( inte, name );
						inte = null;
					}
					in.close();
				}
			} catch (RecordStoreException e) {
				throw new IOException( e.toString() );
			}
		} else {
			this.masterRecordStore = null;			
			this.masterRecordSetIdsByName = null;
			this.masterRecordSetNameById = null;
			this.indexRecordId = -1;
		}
	}
	
	/**
	 * Closes all master recordstores. Should be called at the end of an 
	 * application.
	 */
	public static void shutdown()
	{
		for (int i = 0; i < masterRecordStores.size(); i++) {
			RecordStore store = (RecordStore)masterRecordStores.get(i);
			try {
				//#debug debug
				System.out.println("closing master recordstore " + store.getName());
				
				store.closeRecordStore();
			} catch (RecordStoreNotOpenException e) {
				// its already closed
			} 
			catch (RecordStoreException e) {
				//#debug error
				System.out.println("unable to close master recordstore " + e.toString());
			}
		}
	}
	

	/**
	 * Retrieves the record set ID for the given name.
	 * This method can only be used when a master record store is used.
	 * 
	 * @param name the name of the set
	 * @return either the set ID or -1 when the name is not yet used 
	 */
	public int getRecordSetId( String name ) {
		Integer id = (Integer) this.masterRecordSetIdsByName.get(name);
		if (id != null) {
			return id.intValue();
		} else {
			return -1;
		}
	}
	
	/**
	 * F.Beghin
	 * 
	 * Retrieves the logic key by the record set ID
	 * 
	 * @param recordSetId the ID of the set
	 * @return the name of the record set 
	 */
	public String getRecordLogicKey( int recordSetId ) {
		
		String logicalKey = (String) this.masterRecordSetNameById.get(new Integer(recordSetId));
		 
		return logicalKey;
		 
	}
	
	
	
	/**
	 * Registers a new record set ID in the index record set of the master record store.
	 * 
	 * @param id the ID of the new record set
	 * @param name the name for the set
	 * @throws IOException when the index record set could not be prepared
	 * @throws RecordStoreException when the index record set could not be written
	 */
	private void registerRecordSetId( int id, String name )
	throws IOException, RecordStoreException
	{
		Integer idInt = new Integer(id);
		this.masterRecordSetIdsByName.put( name,  idInt  );
		this.masterRecordSetNameById.put(idInt, name);
		idInt = null;
		Object[] keys = this.masterRecordSetIdsByName.keys();
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		out.writeInt( this.masterRecordSetIdsByName.size() );
		for (int i = 0; i < keys.length; i++) {
			String key = (String)keys[i];
			idInt = (Integer) this.masterRecordSetIdsByName.get( key );
			out.writeUTF( key );
			out.writeInt( idInt.intValue() );
		}
		byte[] data = byteOut.toByteArray();
		this.masterRecordStore.setRecord( this.indexRecordId, data, 0, data.length );
		out.close();
		byteOut.close();		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#save(de.enough.polish.io.Serializable, java.lang.String)
	 */
	//#if polish.java5
	//#	public void save(K object, String name) throws IOException  {
	//#else
		 public void save(Object object, String name) throws IOException  {
	//#endif

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		Serializer.serialize(object, out);
		byte[] data = byteOut.toByteArray();
		out.close();
		byteOut.close();
		saveData( name, data);
	}

		 
	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#save(de.enough.polish.io.Serializable, java.lang.String)
	 */
	//#if polish.java5
	//#	public void update(K object, String newKey, String oldKey) throws IOException {
	//#else
		 public void update(Object object, String newKey, String oldKey) throws IOException {
	//#endif

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		Serializer.serialize(object, out);
		byte[] data = byteOut.toByteArray();
		out.close();
		byteOut.close();
		saveData( newKey, oldKey, data);
	}	 
	 
			 
	/**
	 * Stores the data under the given name.
	 * 
	 * @param name the name
	 * @param data the data
	 * @throws IOException when storage fails
	 */
	private void saveData(String name, byte[] data) throws IOException {
		saveData(name, null, data);
	}
	
	/**
	 * Saves the data under a possibly new name
	 * 
	 * @param newKey the current name
	 * @param oldKey the old name
	 * @param data the data
	 * @throws IOException when the storage fails 
	 */
	private void saveData(String newKey, String oldKey, byte[] data)throws IOException {
		try {
			if (this.masterRecordStore != null) {
				ensureOpen();
				int recordSetId = -1;
				if (oldKey != null){
					recordSetId = getRecordSetId( oldKey );
					Integer recordInt = new Integer(recordSetId);
					if (recordSetId != -1 ) {
					
						if ((getRecordSetId(newKey) != -1) && (!(newKey.equals(oldKey)))){
							throw new IOException ("key already used");
						}
						this.masterRecordSetIdsByName.remove(oldKey);
						//this.masterRecordSetIdsByName.put(newKey, recordInt);
						this.masterRecordSetNameById.remove(recordInt);
						//this.masterRecordSetNameById.put(recordInt, newKey);
						recordInt = null;
						this.masterRecordStore.setRecord(recordSetId, data, 0, data.length );
						registerRecordSetId(recordSetId, newKey);
					}
				} else {
					
					if (getRecordSetId(newKey) != -1){
						throw new IOException ("key already used");
					}
					recordSetId = this.masterRecordStore.addRecord( data, 0, data.length );
					registerRecordSetId(recordSetId, newKey);
				
				}
			} else {
				RecordStore store = RecordStore.openRecordStore(newKey, true);
				int recordSetId = -1;
				RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
				if (enumeration.hasNextElement()) {
					recordSetId = enumeration.nextRecordId();
				}
				enumeration.destroy();
				if (recordSetId == -1) {
					// new record set:
					store.addRecord(data, 0, data.length);
				} else {
					// existing record set:
					store.setRecord(recordSetId, data, 0, data.length);
				}
				store.closeRecordStore();
			}
		} catch (RecordStoreException e ) {
			//#debug error
			System.out.println("Unable to store object under name [" + newKey + "]" + e );
			throw new IOException( e.toString() );
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#read(java.lang.String)
	 */
	//#if polish.java5
		public K read( String name )
	//#else
		//# public Object read( String name )
	//#endif
	throws IOException {
		byte[] data;
		RecordStore store = null;
		try {
			if (this.masterRecordStore != null) {
				ensureOpen();
				int recordId = getRecordSetId(name);
				if (recordId == -1) {
					throw new IOException( name + " is unknown");
				}
				data = this.masterRecordStore.getRecord(recordId);
			} else {
				store = RecordStore.openRecordStore(name, false);
				RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
				data = enumeration.nextRecord();
				enumeration.destroy();
			}
		} catch (RecordStoreException e) {
			throw new IOException( e.toString() );
		}
		finally {
			try {
				if (store != null) {
					store.closeRecordStore();
				}
			} catch (RecordStoreNotOpenException e) {
				// Ignore.
			} catch (RecordStoreException e) {
				throw new IOException( e.toString() );
			}
		}
		DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ));
		//#if polish.java5
			return (K)Serializer.deserialize( in );
		//#else
			 //# return Serializer.deserialize( in );
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#enumerate(java.lang.String)
	 */
	public Enumeration enumerate(String name) throws IOException {
		throw new IOException("Sorry, not supported - might drop this method altogether");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#list()
	 */
	public String[] list() throws IOException {
		if (this.masterRecordStore == null) {
			//#if polish.debug.verbose
				throw new IllegalStateException("need  to use a single-name RmsStorage configuration for being able to list entries.");
			//#else
				//# throw new IllegalStateException();
			//#endif
		}
		return (String[]) this.masterRecordSetIdsByName.keys( new String[ this.masterRecordSetIdsByName.size() ]);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#delete(java.lang.String)
	 */
	public void delete(String name)
	throws IOException
	{
		try {
			if (this.masterRecordStore == null) {
				RecordStore.deleteRecordStore(name);
			} else if (this.masterRecordSetIdsByName != null
	               && !this.masterRecordSetIdsByName.isEmpty())
			{
				ensureOpen();
				Integer id = (Integer) this.masterRecordSetIdsByName.remove(name);
				this.masterRecordSetNameById.remove( id);
				if (id != null) {
					this.masterRecordStore.deleteRecord(id.intValue());
				}
				registerAfterDeletingAKeyRecordSetId();
				
			}
		} catch (RecordStoreException e) {
			throw new IOException(e.toString());
		}
	}
	
	
	/**
	 * Delete all record store to improve performance.
	 * This method should be used only 
	 * with RmsStorage that uses a single record store for several stored objects
	 * @throws IOException  when the operation fails
	 */
	public void deleteAll() throws IOException {
		if (this.masterRecordSetIdsByName != null
	               && !this.masterRecordSetIdsByName.isEmpty()){
			this.masterRecordSetIdsByName.clear();
			this.masterRecordSetNameById.clear();
			try {
				if (this.masterRecordStore.getName() != null){
					String temp = this.masterRecordStore.getName(); 
					this.masterRecordStore.closeRecordStore();
					RecordStore.deleteRecordStore(temp);
				}
			} catch (RecordStoreException e) {
				throw new IOException( e.toString() );
			}
		}
	}
	
	
	/**
	 * Persist hashtable after delete object operation
	 * 
	 * @param id the ID of the new record set
	 * @param name the name for the set
	 * @throws IOException when the index record set could not be prepared
	 * @throws RecordStoreException when the index record set could not be written
	 */
	private void registerAfterDeletingAKeyRecordSetId( )
	throws IOException, RecordStoreException
	{
		Object[] keys = this.masterRecordSetIdsByName.keys();
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		out.writeInt( this.masterRecordSetIdsByName.size() );
		for (int i = 0; i < keys.length; i++) {
			String key = (String)keys[i];
			Integer idInt = (Integer) this.masterRecordSetIdsByName.get( key );
			out.writeUTF( key );
			out.writeInt( idInt.intValue() );
		}
		byte[] data = byteOut.toByteArray();
		this.masterRecordStore.setRecord( this.indexRecordId, data, 0, data.length );
		out.close();
		byteOut.close();		
	}
	
	/**
	 * Retrieves the used size of the underlying record store
	 * @return the used size (in bytes), -1 when an error occurred
	 */
	public int getSize(){
		if (this.masterRecordStore == null) {
			return -1;
		}
		try {
			return this.masterRecordStore.getSize();
		} catch (RecordStoreNotOpenException e) {
			//#debug warn
			System.out.println("Unable to retrieve masterStoreSize" + e );
		}
		return -1;
	}
	
	/**
	 * Retrieves the available size of the underlying record store
	 * @return the available size (in bytes), -1 when an error occurred
	 */
	public int getSizeAvailable(){
		if (this.masterRecordStore == null) {
			return -1;
		}
		try {
			return this.masterRecordStore.getSizeAvailable();
		} catch (RecordStoreNotOpenException e) {
			//#debug warn
			System.out.println("Unable to retrieve available masterStoreSize" + e );
		}
		return -1;
	}
	
	private void ensureOpen() throws IOException {
		if (this.isClosed && this.masterRecordStore != null) {
			try {
				this.masterRecordStore = RecordStore.openRecordStore(this.mastRecordStoreName, true);
			} catch (Exception e) {
				throw new IOException(e.toString());
			}
		}
	}

	/**
	 * Closes the underlying RecordStore - only for cases when one RmsStorage is used for several stores.
	 */
	public void close() {
		try {
			this.masterRecordStore.closeRecordStore();
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to close record store" + e);
		}
		this.isClosed = true;
	}


	/**
	 * Checks if the given store exists
	 * @param name the name of the store
	 * @return true when the store exists
	 */
	public boolean exists(String name) {
		if (this.masterRecordStore != null) {
			return (getRecordSetId(name) != -1);
		}
		String[] names = RecordStore.listRecordStores();
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				String rcdstName = names[i];
				if (name.equals(rcdstName)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
