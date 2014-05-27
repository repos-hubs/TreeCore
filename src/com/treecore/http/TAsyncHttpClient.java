package com.treecore.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import android.content.Context;
import android.os.Looper;

import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.treecore.utils.log.TLog;

public class TAsyncHttpClient {
	public static final String TAG = TAsyncHttpClient.class.getSimpleName();
	public static final int DEFAULT_MAX_CONNECTIONS = 10;
	public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
	public static final int DEFAULT_MAX_RETRIES = 5;
	public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
	public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ENCODING_GZIP = "gzip";
	private int mMaxConnections = 10;
	private int mTimeout = 10000;
	private final DefaultHttpClient mHttpClient;
	private final HttpContext mHttpContext;
	private ExecutorService mThreadPool;
	private final Map<Context, List<RequestHandle>> mRequestMap;
	private final Map<String, String> mClientHeaderMap;
	private boolean isUrlEncodingEnabled = true;

	public TAsyncHttpClient() {
		this(false, 80, 443);
	}

	public TAsyncHttpClient(int httpPort) {
		this(false, httpPort, 443);
	}

	public TAsyncHttpClient(int httpPort, int httpsPort) {
		this(false, httpPort, httpsPort);
	}

	public TAsyncHttpClient(boolean fixNoHttpResponseException, int httpPort,
			int httpsPort) {
		this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort,
				httpsPort));
	}

	private static SchemeRegistry getDefaultSchemeRegistry(
			boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
		if (fixNoHttpResponseException) {
			TLog.i(TAG,
					"Beware! Using the fix is insecure, as it doesn't verify SSL certificates.");
		}

		if (httpPort < 1) {
			httpPort = 80;
			TLog.i(TAG, "Invalid HTTP port number specified, defaulting to 80");
		}

		if (httpsPort < 1) {
			httpsPort = 443;
			TLog.i(TAG,
					"Invalid HTTPS port number specified, defaulting to 443");
		}
		SSLSocketFactory sslSocketFactory;
		SSLSocketFactory sslSocketFactory;
		if (fixNoHttpResponseException)
			sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
		else {
			sslSocketFactory = SSLSocketFactory.getSocketFactory();
		}
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), httpPort));
		schemeRegistry
				.register(new Scheme("https", sslSocketFactory, httpsPort));

		return schemeRegistry;
	}

	public TAsyncHttpClient(SchemeRegistry schemeRegistry) {
		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, this.mTimeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(this.mMaxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams, 10);

		HttpConnectionParams.setSoTimeout(httpParams, this.mTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, this.mTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);

		this.mThreadPool = getDefaultThreadPool();
		this.mRequestMap = new WeakHashMap();
		this.mClientHeaderMap = new HashMap();

		this.mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
		this.mHttpClient = new DefaultHttpClient(cm, httpParams);
		this.mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
				for (String header : TAsyncHttpClient.this.mClientHeaderMap
						.keySet()) {
					if (request.containsHeader(header)) {
						Header overwritten = request.getFirstHeader(header);
						TLog.i(TAsyncHttpClient.TAG,
								String.format(
										"Headers were overwritten! (%s | %s) overwrites (%s | %s)",
										new Object[] {
												header,
												TAsyncHttpClient.this.mClientHeaderMap
														.get(header),
												overwritten.getName(),
												overwritten.getValue() }));
					}
					request.addHeader(header,
							(String) TAsyncHttpClient.this.mClientHeaderMap
									.get(header));
				}
			}
		});
		this.mHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				Header encoding = entity.getContentEncoding();
				if (encoding != null)
					for (HeaderElement element : encoding.getElements())
						if (element.getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new TAsyncHttpClient.InflatingEntity(
									entity));
							break;
						}
			}
		});
		this.mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				AuthState authState = (AuthState) context
						.getAttribute("http.auth.target-scope");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute("http.auth.credentials-provider");
				HttpHost targetHost = (HttpHost) context
						.getAttribute("http.target_host");

				if (authState.getAuthScheme() == null) {
					AuthScope authScope = new AuthScope(targetHost
							.getHostName(), targetHost.getPort());
					Credentials creds = credsProvider.getCredentials(authScope);
					if (creds != null) {
						authState.setAuthScheme(new BasicScheme());
						authState.setCredentials(creds);
					}
				}
			}
		}, 0);

		this.mHttpClient.setHttpRequestRetryHandler(new RetryHandler(5, 1500));
	}

	public static void allowRetryExceptionClass(Class<?> cls) {
		if (cls != null)
			RetryHandler.addClassToWhitelist(cls);
	}

	public static void blockRetryExceptionClass(Class<?> cls) {
		if (cls != null)
			RetryHandler.addClassToBlacklist(cls);
	}

	public HttpClient getHttpClient() {
		return this.mHttpClient;
	}

	public HttpContext getHttpContext() {
		return this.mHttpContext;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.mHttpContext.setAttribute("http.cookie-store", cookieStore);
	}

	public void setThreadPool(ExecutorService mThreadPool) {
		this.mThreadPool = mThreadPool;
	}

	public ExecutorService getThreadPool() {
		return this.mThreadPool;
	}

	protected ExecutorService getDefaultThreadPool() {
		return Executors.newCachedThreadPool();
	}

	public void setEnableRedirects(boolean enableRedirects,
			boolean enableRelativeRedirects, boolean enableCircularRedirects) {
		this.mHttpClient.getParams().setBooleanParameter(
				"http.protocol.reject-relative-redirect",
				!enableRelativeRedirects);
		this.mHttpClient.getParams().setBooleanParameter(
				"http.protocol.allow-circular-redirects",
				enableCircularRedirects);
		this.mHttpClient.setRedirectHandler(new MyRedirectHandler(
				enableRedirects));
	}

	public void setEnableRedirects(boolean enableRedirects,
			boolean enableRelativeRedirects) {
		setEnableRedirects(enableRedirects, enableRelativeRedirects, true);
	}

	public void setEnableRedirects(boolean enableRedirects) {
		setEnableRedirects(enableRedirects, enableRedirects, enableRedirects);
	}

	public void setRedirectHandler(RedirectHandler customRedirectHandler) {
		this.mHttpClient.setRedirectHandler(customRedirectHandler);
	}

	public void setUserAgent(String userAgent) {
		HttpProtocolParams
				.setUserAgent(this.mHttpClient.getParams(), userAgent);
	}

	public int getMaxConnections() {
		return this.mMaxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		if (maxConnections < 1)
			maxConnections = 10;
		this.mMaxConnections = maxConnections;
		HttpParams httpParams = this.mHttpClient.getParams();
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(this.mMaxConnections));
	}

	public int getTimeout() {
		return this.mTimeout;
	}

	public void setTimeout(int timeout) {
		if (timeout < 1000)
			timeout = 10000;
		this.mTimeout = timeout;
		HttpParams httpParams = this.mHttpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, this.mTimeout);
		HttpConnectionParams.setSoTimeout(httpParams, this.mTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, this.mTimeout);
	}

	public void setProxy(String hostname, int port) {
		HttpHost proxy = new HttpHost(hostname, port);
		HttpParams httpParams = this.mHttpClient.getParams();
		httpParams.setParameter("http.route.default-proxy", proxy);
	}

	public void setProxy(String hostname, int port, String username,
			String password) {
		this.mHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(hostname, port),
				new UsernamePasswordCredentials(username, password));
		HttpHost proxy = new HttpHost(hostname, port);
		HttpParams httpParams = this.mHttpClient.getParams();
		httpParams.setParameter("http.route.default-proxy", proxy);
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.mHttpClient.getConnectionManager().getSchemeRegistry()
				.register(new Scheme("https", sslSocketFactory, 443));
	}

	public void setMaxRetriesAndTimeout(int retries, int timeout) {
		this.mHttpClient.setHttpRequestRetryHandler(new RetryHandler(retries,
				timeout));
	}

	public void addHeader(String header, String value) {
		this.mClientHeaderMap.put(header, value);
	}

	public void removeHeader(String header) {
		this.mClientHeaderMap.remove(header);
	}

	public void setBasicAuth(String username, String password) {
		setBasicAuth(username, password, false);
	}

	public void setBasicAuth(String username, String password, boolean preemtive) {
		setBasicAuth(username, password, null, preemtive);
	}

	public void setBasicAuth(String username, String password, AuthScope scope) {
		setBasicAuth(username, password, scope, false);
	}

	public void setBasicAuth(String username, String password, AuthScope scope,
			boolean preemtive) {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		this.mHttpClient.getCredentialsProvider().setCredentials(
				scope == null ? AuthScope.ANY : scope, credentials);
		setAuthenticationPreemptive(preemtive);
	}

	public void setAuthenticationPreemptive(boolean isPreemtive) {
		if (isPreemtive)
			this.mHttpClient.addRequestInterceptor(
					new PreemtiveAuthorizationHttpRequestInterceptor(), 0);
		else
			this.mHttpClient
					.removeRequestInterceptorByClass(PreemtiveAuthorizationHttpRequestInterceptor.class);
	}

	public void clearBasicAuth() {
		this.mHttpClient.getCredentialsProvider().clear();
	}

	public void cancelRequests(final Context context,
			final boolean mayInterruptIfRunning) {
		if (context == null) {
			TLog.e(TAG, "Passed null Context to cancelRequests");
			return;
		}
		Runnable r = new Runnable() {
			public void run() {
				List requestList = (List) TAsyncHttpClient.this.mRequestMap
						.get(context);
				if (requestList != null) {
					for (RequestHandle requestHandle : requestList) {
						requestHandle.cancel(mayInterruptIfRunning);
					}
					TAsyncHttpClient.this.mRequestMap.remove(context);
				}
			}
		};
		if (Looper.myLooper() == Looper.getMainLooper())
			new Thread(r).start();
		else
			r.run();
	}

	public void cancelAllRequests(boolean mayInterruptIfRunning) {
		for (List requestList : this.mRequestMap.values()) {
			if (requestList != null) {
				for (RequestHandle requestHandle : requestList) {
					requestHandle.cancel(mayInterruptIfRunning);
				}
			}
		}
		this.mRequestMap.clear();
	}

	public RequestHandle head(String url,
			ResponseHandlerInterface responseHandler) {
		return head(null, url, null, responseHandler);
	}

	public RequestHandle head(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return head(null, url, params, responseHandler);
	}

	public RequestHandle head(Context context, String url,
			ResponseHandlerInterface responseHandler) {
		return head(context, url, null, responseHandler);
	}

	public RequestHandle head(Context context, String url,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		return sendRequest(this.mHttpClient, this.mHttpContext, new HttpHead(
				getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)),
				null, responseHandler, context);
	}

	public RequestHandle head(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpUriRequest request = new HttpHead(getUrlWithQueryString(
				this.isUrlEncodingEnabled, url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, request, null,
				responseHandler, context);
	}

	public RequestHandle get(String url,
			ResponseHandlerInterface responseHandler) {
		return get(null, url, null, responseHandler);
	}

	public RequestHandle get(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return get(null, url, params, responseHandler);
	}

	public RequestHandle get(Context context, String url,
			ResponseHandlerInterface responseHandler) {
		return get(context, url, null, responseHandler);
	}

	public RequestHandle get(Context context, String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return sendRequest(this.mHttpClient, this.mHttpContext, new HttpGet(
				getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)),
				null, responseHandler, context);
	}

	public RequestHandle get(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(
				this.isUrlEncodingEnabled, url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, request, null,
				responseHandler, context);
	}

	public RequestHandle post(String url,
			ResponseHandlerInterface responseHandler) {
		return post(null, url, null, responseHandler);
	}

	public RequestHandle post(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return post(null, url, params, responseHandler);
	}

	public RequestHandle post(Context context, String url,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		return post(context, url, paramsToEntity(params, responseHandler),
				null, responseHandler);
	}

	public RequestHandle post(Context context, String url, HttpEntity entity,
			String contentType, ResponseHandlerInterface responseHandler) {
		return sendRequest(
				this.mHttpClient,
				this.mHttpContext,
				addEntityToRequestBase(
						new HttpPost(URI.create(url).normalize()), entity),
				contentType, responseHandler, context);
	}

	public RequestHandle post(Context context, String url, Header[] headers,
			RequestParams params, String contentType,
			ResponseHandlerInterface responseHandler) {
		HttpEntityEnclosingRequestBase request = new HttpPost(URI.create(url)
				.normalize());
		if (params != null)
			request.setEntity(paramsToEntity(params, responseHandler));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, request,
				contentType, responseHandler, context);
	}

	public RequestHandle post(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			ResponseHandlerInterface responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPost(URI.create(url).normalize()), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, request,
				contentType, responseHandler, context);
	}

	public RequestHandle put(String url,
			ResponseHandlerInterface responseHandler) {
		return put(null, url, null, responseHandler);
	}

	public RequestHandle put(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return put(null, url, params, responseHandler);
	}

	public RequestHandle put(Context context, String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return put(context, url, paramsToEntity(params, responseHandler), null,
				responseHandler);
	}

	public RequestHandle put(Context context, String url, HttpEntity entity,
			String contentType, ResponseHandlerInterface responseHandler) {
		return sendRequest(
				this.mHttpClient,
				this.mHttpContext,
				addEntityToRequestBase(
						new HttpPut(URI.create(url).normalize()), entity),
				contentType, responseHandler, context);
	}

	public RequestHandle put(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			ResponseHandlerInterface responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPut(URI.create(url).normalize()), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, request,
				contentType, responseHandler, context);
	}

	public RequestHandle delete(String url,
			ResponseHandlerInterface responseHandler) {
		return delete(null, url, responseHandler);
	}

	public RequestHandle delete(Context context, String url,
			ResponseHandlerInterface responseHandler) {
		HttpDelete delete = new HttpDelete(URI.create(url).normalize());
		return sendRequest(this.mHttpClient, this.mHttpContext, delete, null,
				responseHandler, context);
	}

	public RequestHandle delete(Context context, String url, Header[] headers,
			ResponseHandlerInterface responseHandler) {
		HttpDelete delete = new HttpDelete(URI.create(url).normalize());
		if (headers != null)
			delete.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, delete, null,
				responseHandler, context);
	}

	public RequestHandle delete(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpDelete httpDelete = new HttpDelete(getUrlWithQueryString(
				this.isUrlEncodingEnabled, url, params));
		if (headers != null)
			httpDelete.setHeaders(headers);
		return sendRequest(this.mHttpClient, this.mHttpContext, httpDelete,
				null, responseHandler, context);
	}

	protected RequestHandle sendRequest(DefaultHttpClient client,
			HttpContext mHttpContext, HttpUriRequest uriRequest,
			String contentType, ResponseHandlerInterface responseHandler,
			Context context) {
		if (uriRequest == null) {
			throw new IllegalArgumentException(
					"HttpUriRequest must not be null");
		}

		if (responseHandler == null) {
			throw new IllegalArgumentException(
					"ResponseHandler must not be null");
		}

		if (responseHandler.getUseSynchronousMode()) {
			throw new IllegalArgumentException(
					"Synchronous ResponseHandler used in AsyncHttpClient. You should create your response handler in a looper thread or use SyncHttpClient instead.");
		}

		if (contentType != null) {
			uriRequest.setHeader("Content-Type", contentType);
		}

		responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
		responseHandler.setRequestURI(uriRequest.getURI());

		AsyncHttpRequest request = new AsyncHttpRequest(client, mHttpContext,
				uriRequest, responseHandler);
		this.mThreadPool.submit(request);
		RequestHandle requestHandle = new RequestHandle(request);

		if (context != null) {
			List requestList = (List) this.mRequestMap.get(context);
			if (requestList == null) {
				requestList = new LinkedList();
				this.mRequestMap.put(context, requestList);
			}

			if ((responseHandler instanceof RangeFileAsyncHttpResponseHandler)) {
				((RangeFileAsyncHttpResponseHandler) responseHandler)
						.updateRequestHeaders(uriRequest);
			}
			requestList.add(requestHandle);

			Iterator iterator = requestList.iterator();
			while (iterator.hasNext()) {
				if (((RequestHandle) iterator.next())
						.shouldBeGarbageCollected()) {
					iterator.remove();
				}
			}
		}

		return requestHandle;
	}

	public void setURLEncodingEnabled(boolean enabled) {
		this.isUrlEncodingEnabled = enabled;
	}

	public static String getUrlWithQueryString(boolean shouldEncodeUrl,
			String url, RequestParams params) {
		if (shouldEncodeUrl) {
			url = url.replace(" ", "%20");
		}
		if (params != null) {
			String paramString = params.getParamString().trim();

			if ((!paramString.equals("")) && (!paramString.equals("?"))) {
				url = url + (url.contains("?") ? "&" : "?");
				url = url + paramString;
			}
		}

		return url;
	}

	public static void silentCloseInputStream(InputStream is) {
		try {
			if (is != null)
				is.close();
		} catch (IOException e) {
			TLog.e(TAG, "Cannot close input stream " + e.getMessage());
		}
	}

	public static void silentCloseOutputStream(OutputStream os) {
		try {
			if (os != null)
				os.close();
		} catch (IOException e) {
			TLog.w(TAG, "Cannot close output stream " + e.getMessage());
		}
	}

	private HttpEntity paramsToEntity(RequestParams params,
			ResponseHandlerInterface responseHandler) {
		HttpEntity entity = null;
		try {
			if (params != null)
				entity = params.getEntity(responseHandler);
		} catch (Throwable t) {
			if (responseHandler != null)
				responseHandler.sendFailureMessage(0, null, null, t);
			else {
				t.printStackTrace();
			}
		}
		return entity;
	}

	public boolean isUrlEncodingEnabled() {
		return this.isUrlEncodingEnabled;
	}

	private HttpEntityEnclosingRequestBase addEntityToRequestBase(
			HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
		if (entity != null) {
			requestBase.setEntity(entity);
		}

		return requestBase;
	}

	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super();
		}

		public InputStream getContent() throws IOException {
			return new GZIPInputStream(this.wrappedEntity.getContent());
		}

		public long getContentLength() {
			return -1L;
		}
	}
}