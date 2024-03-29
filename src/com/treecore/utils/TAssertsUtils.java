package com.treecore.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class TAssertsUtils {
	private static Pattern _whiteSpace = Pattern.compile("\\s+", 40);

	public static void assertTrue(boolean b) {
		if (!b)
			throw new MyAssert("false");
	}

	public static void assertTrue(boolean b, String msg) {
		if (!b)
			throw new MyAssert("false : " + msg);
	}

	public static void assertFalse(boolean b) {
		if (b)
			throw new MyAssert("true");
	}

	public static void assertEquals(int a, int b) {
		if (a != b)
			throw new MyAssert(a + " != " + b);
	}

	public static void assertEquals(long a, long b) {
		if (a != b)
			throw new MyAssert(a + " != " + b);
	}

	public static void assertEquals(char a, char b) {
		if (a != b)
			throw new MyAssert(a + " != " + b);
	}

	public static void assertEquals(short a, short b) {
		if (a != b)
			throw new MyAssert(a + " != " + b);
	}

	public static void assertEquals(byte expected, byte result) {
		if (expected != result)
			throw new MyAssert(expected + " != " + result);
	}

	public static void assertEquals(double a, double b, double diff) {
		if (Math.abs(a - b) > diff)
			throw new MyAssert(a + " != " + b);
	}

	public static void assertEquals(String a, Object b) {
		_assertEquals(a, b == null ? null : b.toString());
	}

	public static void assertSame(Object a, Object b) {
		if (a != b)
			throw new MyAssert(a + " != " + b);
	}

	public static void assertEquals(Object a, Object b) {
		_assertEquals(a, b);
	}

	public static void _assertEquals(Object a, Object b) {
		if (a == null) {
			if (b == null)
				return;
			throw new MyAssert("left null, right not");
		}

		if (a.equals(b)) {
			return;
		}
		throw new MyAssert("[" + a + "] != [" + b + "] ");
	}

	public static void assertEquals(String a, String b, String msg) {
		if (a.equals(b)) {
			return;
		}
		throw new MyAssert("[" + a + "] != [" + b + "] " + msg);
	}

	public static void assertArrayEquals(byte[] expected, byte[] result) {
		if (Arrays.equals(expected, result)) {
			return;
		}
		throw new MyAssert(
				"These arrays are different, but they might be big so not printing them here");
	}

	public static void assertArrayEquals(char[] expected, char[] result) {
		if (Arrays.equals(expected, result)) {
			return;
		}
		throw new MyAssert(
				"These arrays are different, but they might be big so not printing them here");
	}

	public static void assertNotEquals(Object a, Object b) {
		if (a == null) {
			if (b != null)
				return;
			throw new MyAssert("left null, right null");
		}

		if (!a.equals(b)) {
			return;
		}
		throw new MyAssert("[" + a + "] == [" + b + "] ");
	}

	public static void assertClose(String a, Object o) {
		assertClose(a, o == null ? "null" : o.toString());
	}

	public static void assertClose(String a, String b) {
		assertClose(a, b, "");
	}

	public static void assertClose(String a, String b, String tag) {
		if (isClose(a, b)) {
			return;
		}

		throw new MyAssert(tag + "[" + a + "] != [" + b + "]");
	}

	public static boolean isClose(String a, String b) {
		a = _simplify(a);
		b = _simplify(b);
		return a.equalsIgnoreCase(b);
	}

	private static String _simplify(String s) {
		s = s.trim();
		s = _whiteSpace.matcher(s).replaceAll("");
		return s;
	}

	public static void assertNull(Object foo) {
		if (foo == null) {
			return;
		}
		throw new MyAssert("not null [" + foo + "]");
	}

	public static void assertNotNull(Object foo) {
		if (foo != null) {
			return;
		}
		throw new MyAssert("null");
	}

	public static void assertLess(long lower, long higher) {
		if (lower < higher) {
			return;
		}
		throw new MyAssert(lower + " is higher than " + higher);
	}

	public static void assertLess(double lower, double higher) {
		if (lower < higher) {
			return;
		}
		throw new MyAssert(lower + " is higher than " + higher);
	}

	public static void assertEmptyString(String s) {
		if (!s.equals(""))
			throw new MyAssert(s);
	}

	public static void fail(String errorMessage) {
		throw new MyAssert(errorMessage);
	}

	@Deprecated
	public static class MyAssert extends RuntimeException {
		private static final long serialVersionUID = -4415279469780082174L;
		final String _s;

		MyAssert(String s) {
			super();
			this._s = s;
		}

		public String toString() {
			return this._s;
		}
	}
}