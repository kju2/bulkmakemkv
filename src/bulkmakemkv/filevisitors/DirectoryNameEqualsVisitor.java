package bulkmakemkv.filevisitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

import bulkmakemkv.Tools;

public class DirectoryNameEqualsVisitor extends SimpleFileVisitor<Path> {

	private List<Path>		cache	= new ArrayList<Path>();
	private List<String>	result	= new ArrayList<String>();
	private String			dirName;

	public DirectoryNameEqualsVisitor(String dirName) {
		this.dirName = Tools.normalizeDirectory(dirName).replace("/", "\\");
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		cache.add(dir);
		String curr = Tools.normalizeDirectory(dir.getFileName().toString()).replace("/", "\\");
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
