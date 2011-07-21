//#condition polish.api.amms

package de.enough.polish.camera;

import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.amms.control.camera.SnapshotControl;
import javax.microedition.amms.control.camera.ZoomControl;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

import de.enough.polish.snapshot.SnapshotUtil;
import de.enough.polish.util.DeviceInfo;

public class CameraUtil extends SnapshotUtil {
	private static final long PLAYER_TIMEOUT = 300;

	public static final int Feature_NOT_SUPPORTED = -1004;
	public static final long FOCUS_TIME_MS = 750L;

	//#if polish.api.advancedmultimedia
	public static final int FLASH_AUTO = FlashControl.AUTO;
	public static final int FLASH_AUTO_WITH_REDEYEREDUCE = FlashControl.AUTO_WITH_REDEYEREDUCE;
	public static final int FLASH_FILLIN = FlashControl.FILLIN;
	public static final int FLASH_FORCE = FlashControl.FORCE;
	public static final int FLASH_FORCE_WITH_REDEYEREDUCE = FlashControl.FORCE_WITH_REDEYEREDUCE;
	public static final int FLASH_OFF = FlashControl.OFF;
	public static final int ZOOM_NEXT_LEVEL = ZoomControl.NEXT;
	public static final int ZOOM_PREVIOUS_LEVEL = ZoomControl.PREVIOUS;
	public static final int FOCUS_AUTO = FocusControl.AUTO;
	public static final int FOCUS_AUTO_LOCK = FocusControl.AUTO_LOCK;
	public static final int FOCUS_NEXT_LEVEL = FocusControl.NEXT;
	public static final int FOCUS_PREVIOUS_LEVEL = FocusControl.PREVIOUS;
	public static final int ROTATE_LEFT = CameraControl.ROTATE_LEFT;
	public static final int ROTATE_RIGHT = CameraControl.ROTATE_RIGHT;
	public static final int ROTATE_NONE = CameraControl.ROTATE_NONE;
	public static final int BURST_FREEZE = SnapshotControl.FREEZE;
	public static final int BURST_FREEZE_AND_CONFIRM = SnapshotControl.FREEZE_AND_CONFIRM;

	//#endif

	/**
	 * Returns the protocol to capture an video
	 * 
	 * @return the protocol string
	 * @throws MediaException
	 *             if capture is not supported
	 */
	public static String getVideoProtocol() throws MediaException {
		String[] contentTypes = Manager.getSupportedContentTypes("capture");
		if (contentTypes == null || contentTypes.length == 0) {
			throw new MediaException("capture not supported");
		}
		String protocol;
		//#if polish.identifier.motorola/v3xx
		protocol = "capture://camera";
		//#elif polish.group.series60e3
		protocol = "capture://devcam0";
		//#elif polish.vendor == Sony-Ericsson
		protocol = "capture://audio_video";
		//#elif polish.vendor == Generic
		if (DeviceInfo.getVendor() == DeviceInfo.VENDOR_MOTOROLA)
			protocol = "capture://camera";
		else if (DeviceInfo.getVendor() == DeviceInfo.VENDOR_SONY_ERICSSON)
			protocol = "capture://audio_video";
		else
			protocol = "capture://video";
		//#else
		protocol = "capture://video";
		//#endif
		return protocol;
	}

}
