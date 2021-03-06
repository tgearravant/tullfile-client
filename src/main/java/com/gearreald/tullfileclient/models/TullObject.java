package com.gearreald.tullfileclient.models;

public abstract interface TullObject {
	public String getLocalPath();
	public String getName();
	public boolean isTullFile();
	public boolean isTullFolder();
	public boolean delete();
	public boolean equals(Object o);
	public int hashCode();
	public TullFolder getParent();
}
