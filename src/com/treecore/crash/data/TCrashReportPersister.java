package com.treecore.crash.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import com.treecore.crash.TCrash;

public class TCrashReportPersister {
	private static final int NONE = 0;
	private static final int SLASH = 1;
	private static final int UNICODE = 2;
	private static final int CONTINUE = 3;
	private static final int KEY_DONE = 4;
	private static final int IGNORE = 5;
	private static final String LINE_SEPARATOR = "\n";

	public CrashReportData load(String fileName) throws IOException {
		FileInputStream in = TCrash.getInstance().getContext()
				.openFileInput(fileName);
		if (in == null) {
			throw new IllegalArgumentException(
					"Invalid crash report fileName : " + fileName);
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(in, 8192);
			bis.mark(2147483647);
			boolean isEbcdic = isEbcdic(bis);
			bis.reset();
			CrashReportData localCrashReportData;
			if (!isEbcdic) {
				return load(new InputStreamReader(bis, "ISO8859-1"));
			}
			return load(new InputStreamReader(bis));
		} finally {
			in.close();
		}
	}

	public void store(CrashReportData crashData, String fileName)
			throws IOException {
		OutputStream out = TCrash.getInstance().getContext()
				.openFileOutput(fileName, 0);
		try {
			StringBuilder buffer = new StringBuilder(200);
			OutputStreamWriter writer = new OutputStreamWriter(out, "ISO8859_1");

			Iterator localIterator = crashData.entrySet().iterator();

			while (localIterator.hasNext()) {
				Map.Entry entry = (Map.Entry) localIterator.next();
				String key = ((ReportField) entry.getKey()).toString();
				dumpString(buffer, key, true);
				buffer.append('=');
				dumpString(buffer, (String) entry.getValue(), false);
				buffer.append("\n");
				writer.write(buffer.toString());
				buffer.setLength(0);
			}
			writer.flush();
		} finally {
			out.close();
		}
	}

	private boolean isEbcdic(BufferedInputStream in) throws IOException {
		byte b;
		while ((b = (byte) in.read()) != -1) {
			if ((b == 35) || (b == 10) || (b == 61)) {
				return false;
			}
			if (b == 21) {
				return true;
			}

		}

		return false;
	}

	private synchronized CrashReportData load(Reader reader) throws IOException {
		int mode = 0;
		int unicode = 0;
		int count = 0;
		char[] buf = new char[40];
		int offset = 0;
		int keyLength = -1;
		boolean firstChar = true;

		CrashReportData crashData = new CrashReportData();
		BufferedReader br = new BufferedReader(reader, 8192);
		while (true) {
			int intVal = br.read();
			if (intVal == -1) {
				break;
			}
			char nextChar = (char) intVal;

			if (offset == buf.length) {
				char[] newBuf = new char[buf.length * 2];
				System.arraycopy(buf, 0, newBuf, 0, offset);
				buf = newBuf;
			}
			if (mode == 2) {
				int digit = Character.digit(nextChar, 16);
				if (digit >= 0) {
					unicode = (unicode << 4) + digit;
					count++;
					if (count >= 4)
						;
				} else {
					if (count <= 4) {
						throw new IllegalArgumentException("luni.09");
					}
					mode = 0;
					buf[(offset++)] = (char) unicode;
					if ((nextChar != '\n') && (nextChar != ''))
						;
				}
			} else if (mode == 1) {
				mode = 0;
				switch (nextChar) {
				case '\r':
					mode = 3;
					break;
				case '\n':
				case '':
					mode = 5;
					break;
				case 'b':
					nextChar = '\b';
					break;
				case 'f':
					nextChar = '\f';
					break;
				case 'n':
					nextChar = '\n';
					break;
				case 'r':
					nextChar = '\r';
					break;
				case 't':
					nextChar = '\t';
					break;
				case 'u':
					mode = 2;
					unicode = count = 0;
					break;
				default:
					break;
				}
			} else {
				switch (nextChar) {
				case '!':
				case '#':
					if (firstChar) {
						do {
							intVal = br.read();
							if (intVal == -1) {
								break;
							}
							nextChar = (char) intVal;

							if ((nextChar == '\r') || (nextChar == '\n'))
								break;
						} while (nextChar != '');
					}

					break;
				case '\n':
					if (mode == 3)
						mode = 5;
					break;
				case '\r':
				case '':
					mode = 0;
					firstChar = true;
					if ((offset > 0) || ((offset == 0) && (keyLength == 0))) {
						if (keyLength == -1) {
							keyLength = offset;
						}
						String temp = new String(buf, 0, offset);
						crashData.put(
								(ReportField) Enum.valueOf(ReportField.class,
										temp.substring(0, keyLength)), temp
										.substring(keyLength));
					}
					keyLength = -1;
					offset = 0;
					break;
				case '\\':
					if (mode == 4) {
						keyLength = offset;
					}
					mode = 1;
					break;
				case ':':
				case '=':
					if (keyLength == -1) {
						mode = 0;
						keyLength = offset;
					}
					break;
				default:
					if (Character.isWhitespace(nextChar)) {
						if (mode == 3) {
							mode = 5;
						}

						if ((offset != 0) && (offset != keyLength)
								&& (mode != 5)) {
							if (keyLength == -1)
								mode = 4;
						}
					} else {
						if ((mode == 5) || (mode == 3)) {
							mode = 0;
						}

						firstChar = false;
						if (mode == 4) {
							keyLength = offset;
							mode = 0;
						}
						buf[(offset++)] = nextChar;
					}
					break;
				}
			}
		}
		int intVal;
		if ((mode == 2) && (count <= 4)) {
			throw new IllegalArgumentException("luni.08");
		}
		if ((keyLength == -1) && (offset > 0)) {
			keyLength = offset;
		}
		if (keyLength >= 0) {
			String temp = new String(buf, 0, offset);
			ReportField key = (ReportField) Enum.valueOf(ReportField.class,
					temp.substring(0, keyLength));
			String value = temp.substring(keyLength);
			if (mode == 1) {
				value = value + "";
			}
			crashData.put(key, value);
		}

		return crashData;
	}

	private void dumpString(StringBuilder buffer, String string, boolean key) {
		int i = 0;
		if ((!key) && (i < string.length()) && (string.charAt(i) == ' ')) {
			buffer.append("\\ ");
			i++;
		}

		for (; i < string.length(); i++) {
			char ch = string.charAt(i);
			switch (ch) {
			case '\t':
				buffer.append("\\t");
				break;
			case '\n':
				buffer.append("\\n");
				break;
			case '\f':
				buffer.append("\\f");
				break;
			case '\r':
				buffer.append("\\r");
				break;
			case '\013':
			default:
				if (("\\#!=:".indexOf(ch) >= 0) || ((key) && (ch == ' '))) {
					buffer.append('\\');
				}
				if ((ch >= ' ') && (ch <= '~')) {
					buffer.append(ch);
				} else {
					String hex = Integer.toHexString(ch);
					buffer.append("\\u");
					for (int j = 0; j < 4 - hex.length(); j++) {
						buffer.append("0");
					}
					buffer.append(hex);
				}
				break;
			}
		}
	}
}