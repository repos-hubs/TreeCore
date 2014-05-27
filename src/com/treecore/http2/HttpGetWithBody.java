package com.treecore.http2;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpGetWithBody extends HttpEntityEnclosingRequestBase {
	public static final String METHOD_NAME = "GET";

	public String getMethod() {
		return "GET";
	}

	public HttpGetWithBody(String uri) {
		setURI(URI.create(uri));
	}

	public HttpGetWithBody(URI uri) {
		setURI(uri);
	}

	public HttpGetWithBody() {
	}
}