package com.sadanand.httpConnection.httpClient;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.sadanand.httpConnection.Pooling.PooledHttpConnectionManagerTestApplication;

public class LogManagerStatusThread extends Thread {
	private static final Logger log = LogManager.getLogger(LogManagerStatusThread.class);
	final static Marker MARKER_WHITESPACE = MarkerManager.getMarker("ANA_WHITESPACE");
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
				log.info(MARKER_WHITESPACE, "Thread sleep failed");
			}
		    PoolStats stats = connMgr.getTotalStats();
		    log.info(MARKER_WHITESPACE, String.format(
		    		"\r\nTotal Available:%d, Leased:%d, Max:%d, Pending:%d", 
		    		stats.getAvailable(), 
		    		stats.getLeased(),
		    		stats.getMax(),
		    		stats.getPending()));
		    connMgr.getRoutes().forEach(r -> {
		        PoolStats hostStats = connMgr.getStats(r);
		        log.info(MARKER_WHITESPACE, String.format(
		        		"\r\nHost Host:%s -> Available:%d, Leased:%d, Max:%d, Pending:%d",
		        		r.getTargetHost().getHostName(),
		        		hostStats.getAvailable(), 
		        		hostStats.getLeased(),
		        		hostStats.getMax(),
		        		hostStats.getPending()));
		        log.info(MARKER_WHITESPACE, String.format("Number of Threads:%d", Thread.activeCount()));
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