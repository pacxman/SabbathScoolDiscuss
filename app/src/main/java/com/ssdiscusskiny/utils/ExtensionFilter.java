package com.ssdiscusskiny.utils;

import java.io.FilenameFilter;
import java.io.File;

public class ExtensionFilter implements FilenameFilter
{
	private String extension;
	
	public ExtensionFilter(String extension){
		this.extension = extension;
	}

	@Override
	public boolean accept(File dir, String name)
	{
		// TODO: Implement this method
		return name.endsWith(extension);
	}
	
	
}
