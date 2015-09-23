package edu.np.ece.assettracking.util;

/**
 * Created by zqi2 on 21/9/2015.
 */

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class CachedStringGETRequest extends Request<String> {

    protected static final int defaultClientCacheExpiry =
            1000 * 60 * 15; // milliseconds; = 15 min

    protected final Listener<String> listener;

    public CachedStringGETRequest(
            String url,
            Listener<String> listener,
            ErrorListener errorListener
    ) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
        setRetryPolicy(new DefaultRetryPolicy(
                20000, // 20 seconds, slow VPN and slow service
                3, // 3 retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<String> parseNetworkResponse(
            NetworkResponse response
    ) {
        String data;
        String charset =
                HttpHeaderParser.parseCharset(response.headers);

        try {
            data = new String(response.data, charset);
            return Response.success(
                    data,
                    enforceClientCaching(
                            HttpHeaderParser.parseCacheHeaders(response),
                            response)
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }

    }

    protected Cache.Entry enforceClientCaching(
            Cache.Entry entry,
            NetworkResponse response
    ) {
        if (getClientCacheExpiry() == null) return entry;

        long now = System.currentTimeMillis();

        if (entry == null) {
            entry = new Cache.Entry();
            entry.data = response.data;
            entry.etag = response.headers.get("ETag");
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
            entry.serverDate = now;
            entry.responseHeaders = response.headers;
        } else if (entry.isExpired()) {
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
        }

        return entry;
    }

    protected Integer getClientCacheExpiry() {
        return defaultClientCacheExpiry;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

}