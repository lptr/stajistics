package org.stajistics.integration.servlet;

import static org.stajistics.Util.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.io.StatsFilterReader;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsHttpServletRequest extends HttpServletRequestWrapper {

    private final StatsFactory statsFactory;
    private final StatsKey requestStreamKey;

    private ServletInputStream statsInputStream;
    private BufferedReader statsReader;

    public StatsHttpServletRequest(final HttpServletRequest request,
    							   final StatsFactory statsFactory,
    							   final StatsKey requestStreamKey) {
        super(request);

        assertNotNull(statsFactory, "statsFactory");

        this.statsFactory = statsFactory;
        this.requestStreamKey = requestStreamKey;
    }

    protected ServletInputStream wrapInputStream(final ServletInputStream in) {
    	return new StatsServletInputStream(statsFactory, requestStreamKey, in);
    }

    protected BufferedReader wrapReader(final BufferedReader reader) {
    	return new BufferedReader(new StatsFilterReader(statsFactory, requestStreamKey, reader));
    }

	@Override
	public ServletInputStream getInputStream() throws IOException {
        if (requestStreamKey != null) {
            if (statsInputStream == null) {
                statsInputStream = wrapInputStream(super.getInputStream()); 
            }
            return statsInputStream;
        }
        return super.getInputStream();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (requestStreamKey != null) {
			if (statsReader == null) {
				statsReader = wrapReader(super.getReader());
			}
		}
		return statsReader;
	}
    
}
