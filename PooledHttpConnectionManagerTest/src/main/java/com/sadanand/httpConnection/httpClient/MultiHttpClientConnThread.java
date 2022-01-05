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
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MultiHttpClientConnThread extends Thread {
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
        try {
        	runningThreads ++;
    		this.client = this.handler.getClient();
    		this.get = new HttpGet("http://localhost/");

        	HttpResponse response = client.execute(get);
            //System.out.println("Thread " + Thread.currentThread().getName() + " Response:" + response.getStatusLine());
        	HttpEntity entity = response.getEntity(); 
        	InputStream is = entity.getContent(); // Create an InputStream with the response
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
        	StringBuilder sb = new StringBuilder();
        	String line = null;
        	while ((line = reader.readLine()) != null) // Read line by line
        	  sb.append(line + "\n");

        	String resString = sb.toString(); // Result is here

        	is.close(); // Close the stream
            //EntityUtils.consume(response.getEntity());
			//this.client.close();
        } catch (ConnectException connectEx) {
			System.out.printf("%s -> Failed to get Connection %s", Thread.currentThread().getName() ,connectEx.getMessage());
		} catch (ClientProtocolException ex) {
			System.out.printf("%s -> Protocol Exception %s", Thread.currentThread().getName() ,ex.getMessage());
        } catch (IOException ex) {
        	System.out.printf("%s -> IO Exception %s", Thread.currentThread().getName() ,ex.getMessage());
        }
        finally {
        	runningThreads --;
        }
    }
    
    public static int getThreadCount() {
    	//System.out.println("Running Threads" + runningThreads);
    	return runningThreads;
    }
}