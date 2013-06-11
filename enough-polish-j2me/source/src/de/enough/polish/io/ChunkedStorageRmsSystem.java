//#condition polish.midp || polish.usePolishGui

/*
 * Copyright (c) 2013 Robert Virkus / Enough Software
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

import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;


public class ChunkedStorageRmsSystem implements ChunkedStorageSystem {

	public byte[] loadTailData(String identifier) throws IOException
	{
		return loadData(-1, identifier);
	}

	public byte[] loadData(int chunkIndex, String identifier) throws IOException {
		try { throw new RuntimeException("load: for " + chunkIndex + " of " + identifier); } catch (Exception e) { e.printStackTrace(); }
		RecordStore store = null;
		try
		{
			store = RecordStore.openRecordStore(identifier, false);
			int recordSetId = chunkIndex + 2;
			if (store.getNumRecords() == 0)
			{
				throw new IOException("no records in " + identifier);
			}
			//#debug
			System.out.println(identifier + ": loading " + recordSetId);
			byte[] data = store.getRecord(recordSetId);
			return data;
		} 
//		catch (IOException e)
//		{
//			throw e;
//		}
		catch (Exception e2)
		{
			throw new IOException(e2.toString());
		}
		finally
		{
			if (store != null)
			{
				try { store.closeRecordStore(); } catch (Exception e){}
			}
		}
	}

	public void saveTailData(String identifier, byte[] data) throws IOException {
		saveChunkData(-1, identifier, data);
	}

	public void saveChunkData(int chunkIndex, String identifier, byte[] data)
			throws IOException 
	{
		try { throw new RuntimeException("save: for " + chunkIndex + " of " + identifier); } catch (Exception e) { e.printStackTrace(); }
		RecordStore store = null;
		try
		{
			store = RecordStore.openRecordStore(identifier, true);
			int recordSetId = chunkIndex + 2;
			if (store.getNumRecords() >= recordSetId)
			{
				store.setRecord(recordSetId, data, 0, data.length);
			}
			else
			{
				int id = store.addRecord(data, 0, data.length);
				if (id != recordSetId)
				{
					throw new IOException("Invalid state: expected to store record " + recordSetId + ", but got " + id);
				}
			}
		} 
//		catch (IOException e)
//		{
//			throw e;
//		}
		catch (Exception e2)
		{
			throw new IOException(e2.toString());
		}
		finally
		{
			if (store != null)
			{
				try { store.closeRecordStore(); } catch (Exception e){}
			}
		}
	}

	public void delete(String identifier) throws IOException {
		try {
			RecordStore.deleteRecordStore(identifier);
		} catch (RecordStoreNotFoundException e) {
			//#debug info
			System.out.println("record store not found: " + identifier);
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("Unable to delete record store" + e);
			throw new IOException(e.toString());
		}
	}
}
