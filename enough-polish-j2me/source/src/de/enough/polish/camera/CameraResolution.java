package de.enough.polish.camera;

import de.enough.polish.io.Serializable;


public class CameraResolution implements Serializable{
	public int width;
	public int height;

	public CameraResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public CameraResolution() {

	}

	public boolean equals(Object o) {
		if (o instanceof CameraResolution) {
			CameraResolution d = (CameraResolution) o;
			return d.width == this.width && d.height == this.height;
		}
		return false;
	}

	public int hashCode() {
		return (this.width ^ this.height);
	}
}
