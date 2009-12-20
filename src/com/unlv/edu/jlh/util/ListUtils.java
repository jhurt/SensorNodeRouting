package com.unlv.edu.jlh.util;

import java.util.List;

/**
 * static list utility methods
 */
public class ListUtils {

	/**
	 * Copy the contents of the source list to the destination list
	 * Any items in the destination list before the copy will be removed
	 * @param source
	 * @param destination
	 */
	public static synchronized void copyList(final List source, final List destination) {
		if(source!=null && destination!=null) {
			destination.clear();
			for(Object obj : source) {
				destination.add(obj);
			}
		}
	}
}
