package com.sadanand.httpConnection.httpClient;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Component;

@Component
public class HttpClientHandler {
	private static PoolingHttpClientConnectionManager manager = null;
	private static IdleConnectionMonitorThread cleanup = null;
	private static LogManagerStatusThread monitor = null;
	static {
		String[] sslProtocols = new String[] {"TLSv1.2", "TLSv1.3"};
				
		manager = new PoolingHttpClientConnectionManager(
				RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https",
						new SSLConnectionSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault(), 
						sslProtocols, 
						null, 
						SSLConnectionSocketFactory.getDefaultHostnameVerifier()) {
					@Override
					public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
						Socket s = super.createLayeredSocket(socket, target, port, context);
						return s;
					}
				}).build());

//		ConnectionConfig conConfig = ConnectionConfig.custom()
//				.setCharset(Charsets.UTF_8)
//				.build();
//		manager.setDefaultConnectionConfig(conConfig);
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setSoLinger(1)
                .setSoReuseAddress(true)
                .setSoTimeout(35000)
                .setTcpNoDelay(true)
                .build();
        
        manager.setDefaultSocketConfig(socketConfig);
        
        int connectionLimit = 1000;
        manager.setMaxTotal(connectionLimit);
		manager.setDefaultMaxPerRoute(connectionLimit);
		HttpHost host = new HttpHost("http://localhost/", 80);
		//manager.setMaxPerRoute(new HttpRoute(host), connectionLimit);
		
		cleanup = new IdleConnectionMonitorThread(manager);
		monitor = new LogManagerStatusThread(manager);
		cleanup.start();
		monitor.start();
	}
	public CloseableHttpClient getClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(35000)
                .setConnectionRequestTimeout(35000)
                .setSocketTimeout(35000).build();

		CloseableHttpClient client 
		  = HttpClients.custom().setConnectionManager(manager).setDefaultRequestConfig(config).disableAutomaticRetries().build();
		return client;
	}
	
	
	
}
