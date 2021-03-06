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
package info.unterrainer.java.tools.scripting.bulkmakemkv;

import info.unterrainer.java.tools.utils.NullUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ NullUtils.class })
public class FileName {

	public final static String regExRoundBrackets = "\\([^(]*?\\)";
	public final static String regExSquareBrackets = "\\[[^\\]]*?\\]";
	public final static String regExEpisodesLong = "[sS](\\d+)[eE](\\d+)-[eE](\\d+)";
	public final static String regExEpisodesShort = "[sS](\\d+)[eE](\\d+)";

	private File file;
	private long size;
	private String name;
	private String extension;
	@Nullable
	private String calculatedCleanName;

	private boolean bonusDisc;
	@Nullable
	private Integer year;

	private List<Match> roundBracketContents = new ArrayList<Match>();
	private List<Match> squareBracketContents = new ArrayList<Match>();
	private List<Match> episodesLongContents = new ArrayList<Match>();
	private List<Match> episodesShortContents = new ArrayList<Match>();

	public FileName(File file) {
		name = file.getName().substring(0, file.getName().lastIndexOf('.'));
		extension = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
		try {
			size = Files.size(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.file = file;
		calculate(name);
	}

	private void calculate(String name) {
		squareBracketContents = Utils.getPattern(name, regExSquareBrackets, 1);
		roundBracketContents = Utils.getPattern(name, regExRoundBrackets, 1);
		calculatedCleanName = name.replaceAll(regExSquareBrackets, "");
		calculatedCleanName = calculatedCleanName.noNull().replaceAll(regExRoundBrackets, "");
		calculatedCleanName = calculatedCleanName.noNull().trim();

		episodesLongContents = Utils.getPattern(calculatedCleanName.noNull(), regExEpisodesLong, 0);
		calculatedCleanName = calculatedCleanName.noNull().replaceAll(regExEpisodesLong, "");
		calculatedCleanName = Utils.removeDashTrim(calculatedCleanName);

		episodesShortContents = Utils.getPattern(calculatedCleanName.noNull(), regExEpisodesShort, 0);
		calculatedCleanName = calculatedCleanName.noNull().replaceAll(regExEpisodesShort, "");
		calculatedCleanName = Utils.removeDashTrim(calculatedCleanName);

		Calendar now = Calendar.getInstance();
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().equals("bonus")) {
				bonusDisc = true;
			}
			try {
				year = Integer.parseInt(s);
				if (year.noNull() < 1800 || year.noNull() > now.get(Calendar.YEAR)) {
					// Plausibility-check.
					year = null;
				}
			} catch (NumberFormatException e) {
			}
		}

		if (calculatedCleanName.noNull().toLowerCase().endsWith("bonus")) {
			bonusDisc = true;
		}
	}

	@Override
	public String toString() {
		return calculatedCleanName
				+ " ex:["
				+ extension
				+ "] sz:["
				+ size
				+ "]"
				+ " rb:"
				+ roundBracketContents
				+ " sb:"
				+ squareBracketContents
				+ " el:"
				+ episodesLongContents
				+ " es:"
				+ episodesShortContents
				+ " yr:["
				+ year
				+ "] bd:["
				+ bonusDisc
				+ "]";
	}

	@Nullable
	public String getFolderName() {
		String result = calculatedCleanName;
		if (!squareBracketContents.isEmpty()) {
			result += " [" + squareBracketContents.get(0) + "]";
		}
		if (year != null) {
			result += " (" + year + ")";
		}
		if (!episodesLongContents.isEmpty() || !episodesShortContents.isEmpty()) {
			// This is a TV-series.
			String h = " - ";
			if (!episodesLongContents.isEmpty()) {
				h += episodesLongContents.get(0);
			}
			if (!episodesShortContents.isEmpty()) {
				h += episodesShortContents.get(0);
			}
			result += h;
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("side")) {
				result += " (" + s + ")";
			}
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("part")) {
				result += " (" + s + ")";
			}
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("english")) {
				result += " (" + s + ")";
			}
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("german")) {
				result += " (" + s + ")";
			}
		}
		return result;
	}

	@Nullable
	public String getFileName() {
		String result = calculatedCleanName;
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("side")) {
				result += " (" + s + ")";
			}
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("part")) {
				result += " (" + s + ")";
			}
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("english")) {
				result += " (" + s + ")";
			}
		}
		for (Match m : roundBracketContents) {
			String s = m.getMatch();
			if (s.toLowerCase().startsWith("german")) {
				result += " (" + s + ")";
			}
		}
		return result;
	}

	public File getFile() {
		return file;
	}

	public long getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	@Nullable
	public String getCalculatedCleanName() {
		return calculatedCleanName;
	}

	public List<Match> getRoundBracketContents() {
		return roundBracketContents;
	}

	public List<Match> getSquareBracketContents() {
		return squareBracketContents;
	}

	public List<Match> getEpisodesLongContents() {
		return episodesLongContents;
	}

	public List<Match> getEpisodesShortContents() {
		return episodesShortContents;
	}

	public boolean isBonusDisc() {
		return bonusDisc;
	}

	@Nullable
	public Integer getYear() {
		return year;
	}
}
