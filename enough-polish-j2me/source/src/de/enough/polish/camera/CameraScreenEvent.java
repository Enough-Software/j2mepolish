package de.enough.polish.camera;

public class CameraScreenEvent {
	public final static byte START_AUTO_FOUCS = 1;
	public final static byte END_AUTO_FOUCS = 2;
	private byte typ;

	public CameraScreenEvent(byte typ) {
		this.typ = typ;
	}

	public byte getTyp() {
		return typ;
	}

}
