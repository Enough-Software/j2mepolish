package de.enough.polish.postcompile.renaming;

import java.util.Iterator;
import java.util.Map;

public final class ClassRenamingHelper
{
  public static String doRenaming(String str, Map renamingMap)
  {
    if (str == null)
      {
        return null;
      }

    // Try a shortcut.
    if (renamingMap.get(str) != null) {
    	return renamingMap.get(str).toString();
    }
    
    Iterator it = renamingMap.entrySet().iterator();
    
    // Loop through all remappings.
    while (it.hasNext())
      {
        Map.Entry entry = (Map.Entry) it.next();
        String key = "L" + entry.getKey() + ";";
        String value = "L" + entry.getValue() + ";";
        str = str.replaceAll(key, value);
      }
    
    return str;
  }

  public static String[] doRenaming(String[] array, Map renamingMap)
  {
    if (array == null)
      {
        return null;
      }
    
    for (int i = 0; i < array.length; i++)
      {
        array[i] = doRenaming(array[i], renamingMap);
      }
    
    return array;
  }
}
