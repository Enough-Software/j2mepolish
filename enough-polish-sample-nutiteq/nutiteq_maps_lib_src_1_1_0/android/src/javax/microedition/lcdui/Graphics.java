package javax.microedition.lcdui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Bitmap.Config;

import com.nutiteq.log.Log;

public class Graphics {
  public static final int BASELINE = 0x01;
  public static final int BOTTOM = 0x02;
  public static final int LEFT = 0x04;
  public static final int RIGHT = 0x08;
  public static final int TOP = 0x10;
  public static final int VCENTER = 0x20;
  public static final int HCENTER = 0x40;

  public static final int DOTTED = 0x01;
  public static final int SOLID = 0x02;
  private final Canvas canvas;
  private Font font;
  private final Paint paint;

  public Graphics(final Canvas wrapped) {
    canvas = wrapped;
    font = Font.getDefaultFont();
    paint = new Paint(font.getTypefacePaint());
    paint.setAntiAlias(true);
  }

  public void drawImage(final Image image, final int x, final int y, final int anchor) {
    int ax;
    int ay;
    if ((anchor & LEFT) != 0) {
      ax = x;
    } else if ((anchor & HCENTER) != 0) {
      ax = x - image.getWidth() / 2;
    } else {
      ax = x - image.getWidth();
    }
    if ((anchor & TOP) != 0) {
      ay = y;
    } else if ((anchor & VCENTER) != 0) {
      ay = y - image.getHeight() / 2;
    } else {
      ay = y - image.getHeight();
    }

    //TODO jaanus : check this. not really sure why this sometimes happens
    try {
      this.canvas.drawBitmap(image.getBitmap(), ax, ay, null);
    } catch (final NullPointerException e) {
      Log.error("NPE in G");
    }
  }

  public void drawLine(final int x1, final int y1, final int x2, final int y2) {
    canvas.drawLine(x1, y1, x2, y2, paint);
  }

  public void drawRect(final int x, final int y, final int width, final int height) {
    paint.setStyle(Paint.Style.STROKE);
    canvas.drawRect(x, y, x + width, y + height, paint);
  }

  public void drawRGB(final int[] rgbData, final int offset, final int scanlength, final int x,
      final int y, final int width, final int height, final boolean processAlpha) {
    final Bitmap drawn = Bitmap.createBitmap(rgbData, width, height, Config.ARGB_8888);
    canvas.drawBitmap(drawn, x, y, null);
  }

  public void drawString(final String str, final int x, final int y, final int anchor) {
    int paintX = x;
    int paintY = y;
    if ((anchor & TOP) != 0) {
      paintY += font.getSize();
    } else if ((anchor & BOTTOM) != 0) {
      paintY -= font.getDescent();
    }

    final int stringWidth = font.stringWidth(str);
    if ((anchor & RIGHT) != 0) {
      paintX -= stringWidth;
    } else if ((anchor & HCENTER) != 0) {
      paintX -= stringWidth / 2;
    }

    canvas.drawText(str, paintX, paintY, paint);
  }

  public void fillRect(final int x, final int y, final int width, final int height) {
    paint.setStyle(Paint.Style.FILL);
    canvas.drawRect(x, y, x + width, y + height, paint);
  }

  public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3,
      final int y3) {
    paint.setStyle(Paint.Style.FILL);
    final Path triangle = new Path();
    triangle.moveTo(x1, y1);
    triangle.lineTo(x2, y2);
    triangle.lineTo(x3, y3);
    triangle.close();
    canvas.drawPath(triangle, paint);
  }

  public int getClipHeight() {
    return canvas.getClipBounds().height();
  }

  public int getClipWidth() {
    return canvas.getClipBounds().width();
  }

  public int getClipX() {
    return canvas.getClipBounds().left;
  }

  public int getClipY() {
    return canvas.getClipBounds().top;
  }

  public void setClip(final int x, final int y, final int width, final int height) {
    canvas.clipRect(x, y, x + width, y + height, Region.Op.REPLACE);
  }

  public void setColor(final int rgb) {
    paint.setColor(rgb);
  }

  public void setFont(final Font font) {
    this.font = font;
    paint.setTypeface(font.getTypefacePaint().getTypeface());
    paint.setTextSize(font.getSize());
  }

  public void fillRoundRect(final int x, final int y, final int width, final int height,
      final int arcWidth, final int arcHeight) {
    paint.setStyle(Paint.Style.FILL);
    canvas.drawRoundRect(new RectF(x, y, x + width, y + height), arcWidth, arcHeight, paint);
  }
}
