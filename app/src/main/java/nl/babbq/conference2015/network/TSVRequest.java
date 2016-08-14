package nl.babbq.conference2015.network;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.babbq.conference2015.objects.Session;

/**
 * Created by nono on 11/1/15.
 */
public class TSVRequest  extends Request<List<Session>> {
    private final Response.Listener<List<Session>> mListener;
    private Context mContext;

    public TSVRequest(Context context,
                      int method,
                      String url,
                      Response.Listener<List<Session>> listener,
                      Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mContext = context;
        setShouldCache(false);
    }

    @Override
    protected Response<List<Session>> parseNetworkResponse(NetworkResponse response) {
        InputStream inputStream = new ByteArrayInputStream(response.data);
        List<Session> sessions;
        try {
            sessions = Session.parseInputStream(mContext, new InputStreamReader(inputStream));
        } catch (Exception e) {
            mContext = null;
            return Response.error(new ParseError(e));
        }
        return Response.success(sessions, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(List<Session> response) {
        mContext = null;
        mListener.onResponse(response);
    }
}
