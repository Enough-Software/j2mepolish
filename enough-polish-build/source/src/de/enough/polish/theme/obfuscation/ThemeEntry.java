package de.enough.polish.theme.obfuscation;

import java.util.ArrayList;

public class ThemeEntry
{
	public static final int ROOT = 0;
	public static final int CLASS = 1;
	public static final int METHOD = 2;
	public static final int FIELD = 3;
	
	private int group;
	
	private String name;
	private String obfuscated;
	private String type;
	
	private ArrayList children;
	
	public ThemeEntry()
	{
		this.children = new ArrayList();
	}

	public String getName() {
		return name;
	}

	public void setName(String normal) {
		this.name = normal;
	}

	public String getObfuscated() {
		return obfuscated;
	}

	public void setObfuscated(String obfuscated) {
		this.obfuscated = obfuscated;
	}

	public ArrayList getChildren() {
		return children;
	}

	public void setChildren(ArrayList children) {
		this.children = children;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
