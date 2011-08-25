package com.nutiteq.kml;

import java.util.Hashtable;

public class KmlStylesCache {
  private final Hashtable stylesTable;
  private final Hashtable styleMapsTable;

  //TODO jaanus : size limit
  public KmlStylesCache() {
    stylesTable = new Hashtable();
    styleMapsTable = new Hashtable();
  }

  public void addStyle(final KmlStyle style) {
    stylesTable.put(style.getStyleId(), style);
  }

  public void addStyleMap(final KmlStyleMap styleMap) {
    styleMapsTable.put(styleMap.getStyleMapId(), styleMap);
  }

  public KmlStyle getStyle(final String style) {
    if (style == null) {
      return null;
    }

    String usedStyleId = null;
    if (styleMapsTable.containsKey(style)) {
      final String normalStyle = ((KmlStyleMap) styleMapsTable.get(style)).getNormal();
      usedStyleId = normalStyle == null ? null : normalStyle.substring(1);
    }

    if (usedStyleId == null) {
      usedStyleId = style;
    }

    return (KmlStyle) stylesTable.get(usedStyleId);
  }

  // test methods removed by obfuscator
  protected int numberOfStyles() {
    return stylesTable.size();
  }

  public int numberOfStyleMaps() {
    return styleMapsTable.size();
  }

  public String resolveImageUrl(final String url) {
    if (url == null) {
      return null;
    }
    
    String imageUrl = null;
    if (!url.startsWith("#")) {
      imageUrl = url;
    }

    if (imageUrl == null) {
      final KmlStyle style = getStyle(url.substring(1));
      if (style != null) {
        imageUrl = style.getIconUrl();
      }
    }

    return imageUrl;
  }
}
