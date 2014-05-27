package com.treecore.http;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

public class TSyncHttpClient extends TAsyncHttpClient {
	public TSyncHttpClient() {
		super(false, 80, 443);
	}

	public TSyncHttpClient(int httpPort) {
		super(false, httpPort, 443);
	}

	public TSyncHttpClient(int httpPort, int httpsPort) {
		super(false, httpPort, httpsPort);
	}

	public TSyncHttpClient(boolean fixNoHttpResponseException, int httpPort,
			int httpsPort) {
		super(fixNoHttpResponseException, httpPort, httpsPort);
	}

	public TSyncHttpClient(SchemeRegistry schemeRegistry) {
		super(schemeRegistry);
	}

	protected RequestHandle sendRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, ResponseHandlerInterface responseHandler,
			Context context) {
		if (contentType != null) {
			uriRequest.addHeader("Content-Type", contentType);
		}

		responseHandler.setUseSynchronousMode(true);

		new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler).run();

		return new RequestHandle(null);
	}
}