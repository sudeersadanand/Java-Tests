package com.sadanand.httpConnection.httpClient;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

public class LogManagerStatusThread extends Thread {
    private final PoolingHttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public LogManagerStatusThread(
      PoolingHttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
        
    }
    @Override
    public void run() {
        while (!shutdown) {
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    PoolStats stats = connMgr.getTotalStats();
		    System.out.printf(
		    		"\r\nTotal Available:%s, Leased:%s, Max:%s, Pending:%s", 
		    		stats.getAvailable(), 
		    		stats.getLeased(),
		    		stats.getMax(),
		    		stats.getPending());
		    connMgr.getRoutes().forEach(r -> {
		        PoolStats hostStats = connMgr.getStats(r);
		        System.out.printf(
		        		"\r\nHost Host:%s -> Available:%s, Leased:%s, Max:%s, Pending:%s",
		        		r.getTargetHost().getHostName(),
		        		hostStats.getAvailable(), 
		        		hostStats.getLeased(),
		        		hostStats.getMax(),
		        		hostStats.getPending());
		        System.out.println("\r\nNumber of Threads " + Thread.activeCount());
		    });
		}
    }
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}