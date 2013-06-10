package de.enough.polish.io;

import java.io.IOException;

public interface ChunkedStorageSystem {

	byte[] loadTailData(String identifier) throws IOException;

	byte[] loadData(int chunkIndex, String identifier) throws IOException;
	
	void saveTailData(String identifier, byte[] data) throws IOException;
	
	void saveChunkData(int chunkIndex, String identifier, byte[] data) throws IOException;

}
