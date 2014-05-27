package com.treecore.http2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import android.util.Log;

import com.treecore.utils.encryption.TMD5;

public class TBsonHttpClient {
	private static final String TAG = TBsonHttpClient.class.getCanonicalName();
	private BasicCredentialsProvider bcp;
	private ArrayList<NameValuePair> headers;
	private BasicBSONObject data;
	private int responseCode;
	private BasicBSONObject response = null;
	private String message;
	private byte[] byte_data = null;
	private URL url;
	private DefaultHttpClient client;
	private String username = "";
	private String password = "";
	private String realm = "";
	private int nonce = 1;

	public TBsonHttpClient(String url) {
		this.client = new DefaultHttpClient();
		this.headers = new ArrayList();
		this.data = new BasicBSONObject();
		try {
			this.url = new URL(url);
		} catch (MalformedURLException localMalformedURLException) {
		}
		HttpParams httpparams = this.client.getParams();

		ConnManagerParams.setTimeout(httpparams, 10000L);

		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(400);
		ConnManagerParams.setMaxConnectionsPerRoute(httpparams, connPerRoute);

		HttpConnectionParams.setConnectionTimeout(httpparams, 10000);

		HttpConnectionParams.setSoTimeout(httpparams, 10000);

		HttpConnectionParams.setLinger(httpparams, 6);
	}

	public URL getURL() {
		return this.url;
	}

	public void close() {
		if (this.client != null)
			this.client.getConnectionManager().shutdown();
		this.client = null;
		this.byte_data = null;
		if (this.headers != null)
			this.headers.clear();
		this.headers = null;
		this.data = null;
		this.bcp = null;
		this.response = null;
		this.url = null;
	}

	public static byte[] getFileBinary(String filename) {
		try {
			FileInputStream fileInputStream = new FileInputStream(filename);
			byte[] binary = new byte[fileInputStream.available()];
			fileInputStream.read(binary);
			return binary;
		} catch (IOException e) {
		}
		return new byte[0];
	}

	public byte[] getByteFiled(String key) {
		if (this.response == null) {
			return null;
		}
		return BSON.encode((BSONObject) this.response.get(key));
	}

	public boolean hasField(String key) {
		return this.data.containsField(key);
	}

	public void addField(String key, String value) {
		this.data.put(key, value);
	}

	public void addField(String key, int value) {
		this.data.put(key, Integer.valueOf(value));
	}

	public void addField(String key, double value) {
		this.data.put(key, Double.valueOf(value));
	}

	public void addField(String key, boolean value) {
		this.data.put(key, Boolean.valueOf(value));
	}

	public void addFileField(String key, String filename) {
		this.data.put(key, getFileBinary(filename));
	}

	public void removeField(String key) {
		this.data.removeField(key);
	}

	public void setBasicBSONObject(BasicBSONObject basicBSONObject) {
		this.data = basicBSONObject;
	}

	public void addBasicAuthentication(String username, String password) {
		this.bcp = new BasicCredentialsProvider();
		this.bcp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
				username, password));
		this.client.setCredentialsProvider(this.bcp);
	}

	public void addDigestAuthentication(String _username, String _password,
			String _realm) {
		this.username = _username;
		this.password = _password;
		this.realm = _realm;

		this.bcp = new BasicCredentialsProvider();
		this.bcp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
				_username, _password));
		this.client.setCredentialsProvider(this.bcp);
	}

	public void addHeader(String name, String value) {
		this.headers.add(new BasicNameValuePair(name, value));
	}

	public boolean execute(HttpClientMethod method) throws Exception {
		HttpRequestBase request;
		switch (method) {
		case GET:
			request = new HttpGetWithBody(this.url.toString());
			request = (HttpGetWithBody) addHeaderParams(request);
			request = (HttpGetWithBody) addBodyParams(request);
			break;
		case POST:
			request = new HttpPost(this.url.toString());
			request = (HttpPost) addHeaderParams(request);
			request = (HttpPost) addBodyParams(request);
			break;
		case PUT:
			request = new HttpPut(this.url.toString());
			request = (HttpPut) addHeaderParams(request);
			request = (HttpPut) addBodyParams(request);
			break;
		case DELETE:
			request = new HttpDeleteWithBody(
					this.url.toString());
			request = (HttpDeleteWithBody) addHeaderParams(request);
			request = (HttpDeleteWithBody) addBodyParams(request);
			break;
		default:
			return false;
		}
		request.setHeader("Content-Type", "bson");
		request.setHeader("Accept", "bson");

		DecimalFormat df = new DecimalFormat("0");
		String nonce = df.format(Math.floor(new Date().getTime() / 1000L));
		DigestScheme digestAuth = new DigestScheme();
		digestAuth.overrideParamter("algorithm", "MD5");
		digestAuth.overrideParamter("realm", this.realm);
		digestAuth.overrideParamter("nonce", nonce);
		digestAuth.overrideParamter("qop", "auth");
		digestAuth.overrideParamter("nc", "0");
		digestAuth.overrideParamter("cnonce", DigestScheme.createCnonce());
		digestAuth.overrideParamter("opaque", TMD5.getMD5(this.realm + nonce));
		Header auth = digestAuth.authenticate(new UsernamePasswordCredentials(
				this.username, this.password), request);
		request.addHeader(auth);

		return executeRequest(request);
	}

	private HttpUriRequest addHeaderParams(HttpUriRequest request)
			throws Exception {
		for (NameValuePair h : this.headers) {
			request.addHeader(h.getName(), h.getValue());
		}
		return request;
	}

	private HttpUriRequest addBodyParams(HttpUriRequest request)
			throws Exception {
		if (!this.data.isEmpty()) {
			AbstractHttpEntity entity = new ByteArrayEntity(
					BSON.encode(this.data));
			if ((request instanceof HttpPost))
				((HttpPost) request).setEntity(entity);
			else if ((request instanceof HttpPut))
				((HttpPut) request).setEntity(entity);
			else if ((request instanceof HttpGetWithBody))
				((HttpGetWithBody) request).setEntity(entity);
			else if ((request instanceof HttpDeleteWithBody)) {
				((HttpDeleteWithBody) request).setEntity(entity);
			}
		}
		return request;
	}

	public String getErrorMessage() {
		return this.message;
	}

	public BasicBSONObject getResponse() {
		if (this.response == null) {
			return null;
		}
		return this.response;
	}

	public boolean hasResponseField(String key) {
		if (this.response == null) {
			return false;
		}
		return this.response.containsField(key);
	}

	public Object getFiled(String key) {
		if (this.response == null) {
			return null;
		}
		return this.response.get(key);
	}

	public String getStringFiled(String key) {
		if (this.response == null) {
			return "";
		}
		return this.response.getString(key);
	}

	public int getIntFiled(String key) {
		if (this.response == null) {
			return -1;
		}
		return this.response.getInt(key);
	}

	public double getDounbleFiled(String key) {
		if (this.response == null) {
			return -1.0D;
		}
		return this.response.getDouble(key);
	}

	public Boolean getBooleanFiled(String key) {
		if (this.response == null) {
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(this.response.getBoolean(key));
	}

	public Date getDateFiled(String key) {
		if (this.response == null) {
			return null;
		}
		return this.response.getDate(key);
	}

	public byte[] getByteResponse() {
		return this.byte_data;
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	private boolean executeRequest(HttpRequestBase request) throws Exception {
		try {
			HttpResponse httpResponse = this.client.execute(request);
			this.responseCode = httpResponse.getStatusLine().getStatusCode();
			this.message = httpResponse.getStatusLine().getReasonPhrase();
			Log.i("CoreRestClient executeRequest()",
					"executeRequest responseCode:" + this.responseCode
							+ ",message:" + this.message);
			HttpEntity entity = httpResponse.getEntity();
			Log.i("CoreRestClient executeRequest()", "executeRequest "
					+ request.toString());

			if (entity != null) {
				Header[] headers = httpResponse.getHeaders("Content-Type");
				String contentType = "";
				if (headers.length > 0) {
					contentType = headers[0].getValue().toString();
				}

				InputStream rStreamContent = entity.getContent();
				this.byte_data = toBArray(rStreamContent);
				rStreamContent.close();

				if (contentType.contains("bson")) {
					this.response = ((BasicBSONObject) BSON
							.decode(this.byte_data));
				}
			}

			return true;
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
			throw new Exception("客户请求异常：" + ex.getMessage());
		}
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\n");
		} catch (IOException e) {
			e.printStackTrace();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public byte[] toBArray(InputStream is) {
		ByteArrayOutputStream outp = new ByteArrayOutputStream();
		int ch = 0;
		try {
			while ((ch = is.read()) != -1)
				outp.write(ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] b = outp.toByteArray();
		return b;
	}
}