package test.mavenexitcode.mail;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class BasicAuthRequestFactory
		extends HttpComponentsClientHttpRequestFactory {

	private HttpHost host;

	public BasicAuthRequestFactory(HttpHost host) {
		this.host = host;
	}

	@Override
	protected BasicHttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
		return createHttpContext();
	}

	private BasicHttpContext createHttpContext() {
		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
		return localcontext;
	}
}