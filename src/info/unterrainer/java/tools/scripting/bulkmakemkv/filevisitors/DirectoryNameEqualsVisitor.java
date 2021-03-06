/**************************************************************************
 * <pre>
 *
 * Copyright (c) Unterrainer Informatik OG.
 * This source is subject to the Microsoft Public License.
 *
 * See http://www.microsoft.com/opensource/licenses.mspx#Ms-PL.
 * All other rights reserved.
 *
 * (In other words you may copy, use, change and redistribute it without
 * any restrictions except for not suing me because it broke something.)
 *
 * THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * </pre>
 ***************************************************************************/
package info.unterrainer.java.tools.scripting.bulkmakemkv.filevisitors;

import info.unterrainer.java.tools.scripting.bulkmakemkv.Utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.ParametersAreNonnullByDefault({})
public class DirectoryNameEqualsVisitor extends SimpleFileVisitor<Path> {

	private List<Path> cache = new ArrayList<Path>();
	private List<String> result = new ArrayList<String>();
	private String dirName;

	public DirectoryNameEqualsVisitor(String dirName) {
		this.dirName = Utils.normalizeDirectory(dirName).replace("/", "\\");
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		cache.add(dir);
		String curr = Utils.normalizeDirectory(dir.getFileName().toString()).replace("/", "\\");
		if (dirName.toLowerCase().equals(curr.toLowerCase())) {
			result.add(dir.toString());
		}
		return FileVisitResult.CONTINUE;
	}

	// If there is some error accessing the file, let the user know. If you don't override this method and an error
	// occurs, an IOException is thrown.
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return FileVisitResult.CONTINUE;
	}

	public List<String> getResult() {
		return result;
	}

	public List<Path> getCache() {
		return cache;
	}
}
