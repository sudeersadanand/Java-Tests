package com.sadanand.httpConnection.httpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sadanand.httpConnection.Pooling.PooledHttpConnectionManagerTestApplication;

@Component
@Scope("prototype")
public class MultiHttpClientConnThread extends Thread {
	private static final Logger log = LogManager.getLogger(PooledHttpConnectionManagerTestApplication.class);

    private CloseableHttpClient client;
    private HttpGet get;
    private static int runningThreads = 0;
    @Autowired
    private HttpClientHandler handler;
    
    public MultiHttpClientConnThread() {
	}
    
    public MultiHttpClientConnThread(CloseableHttpClient client, HttpGet get) {
		this.client = client;
		this.get = get;
	}
    
    // standard constructors
    public void run(){
    	boolean shouldRetry = true;
    	int retryCount = 0;
    	while (shouldRetry) {
    		try {
    			runningThreads ++;
    			this.client = this.handler.getClient();
    			this.get = new HttpGet("http://localhost:8080/hello/20000");

    			HttpResponse response = client.execute(get);

    			HttpEntity entity = response.getEntity(); 
    			InputStream is = entity.getContent(); // Create an InputStream with the response
    			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
    			StringBuilder sb = new StringBuilder();
    			String line = null;
    			while ((line = reader.readLine()) != null) // Read line by line
    				sb.append(line + "\n");

    			String resString = sb.toString(); // Result is here
    			log.info(resString);

    			is.close(); // Close the stream
    			//EntityUtils.consume(response.getEntity());
    			//this.client.close();
    			shouldRetry = false;
    		} catch (ConnectException connectEx) {
    			shouldRetry = ++retryCount < 5;
    			if (!shouldRetry)
    				log.error(String.format("%s -> Failed to get Connection %s\r\n", Thread.currentThread().getName() ,connectEx.getMessage()), connectEx);
    		} catch (ClientProtocolException ex) {
    			log.error(String.format("%s -> Protocol Exception %s\r\n", Thread.currentThread().getName() ,ex.getMessage()), ex);
    			shouldRetry = false;
    		} catch (IOException ex) {
    			log.error(String.format("%s -> IO Exception %s\r\n", Thread.currentThread().getName() ,ex.getMessage()), ex);
    			shouldRetry = false;
    		}
    		finally {
    			runningThreads --;
    		}

    	}
    }
    
    public static int getThreadCount() {
    	//System.out.println("Running Threads" + runningThreads);
    	return runningThreads;
    }
}