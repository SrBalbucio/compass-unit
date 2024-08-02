package balbucio.compass.api.http;

import com.github.nidorx.http.HttpRequest;
import com.github.nidorx.http.HttpResponse;
public interface ResponseHandler {

    boolean onResponse(HttpRequest request, HttpResponse response);
}
