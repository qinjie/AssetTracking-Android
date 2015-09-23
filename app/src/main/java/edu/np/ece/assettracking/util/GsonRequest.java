package edu.np.ece.assettracking.util;

/**
 * Created by zqi2 on 21/9/2015.
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class GsonRequest<T> extends Request<T> {
    protected final Gson gson = new GsonBuilder().
            excludeFieldsWithoutExposeAnnotation().create();
    protected final Class<T> responseClass;
    protected final Listener<T> listener;
    protected Map<String, String> headers;

    public GsonRequest(int method,
                       String url,
                       Class<T> responseClass,
                       Listener<T> listener,
                       ErrorListener errorListener
    ) {
        super(method, url, errorListener);
        this.listener = listener;
        this.responseClass = responseClass;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        if (listener != null)
            listener.onResponse(response);
        else
            Log.w("exampleapp", "listener was null delivering response.");
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            T result = gson.fromJson(json, this.responseClass);
            return Response.success(
                    result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}