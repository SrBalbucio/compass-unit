package balbucio.compass.api.http;

import org.jsoup.Connection;

public interface ResponseHandler {

    boolean onResponse(Connection.Request request, Connection.Response response);
}
