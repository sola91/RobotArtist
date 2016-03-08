package org.robotartist.util;


public class Loader {
	public static void loadOpenCV() {
		for (OpencvNativeLibs lib : OpencvNativeLibs.values()) {
			try {
				System.loadLibrary(lib.getName());
			} catch (UnsatisfiedLinkError e) {
				//e.printStackTrace();
			}
		}
	}
}
