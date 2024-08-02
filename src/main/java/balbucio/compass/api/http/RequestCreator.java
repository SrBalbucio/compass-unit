package balbucio.compass.api.http;

import balbucio.compass.api.utilities.Utilities;
import com.github.nidorx.http.HttpRequest;

public interface RequestCreator {

    HttpRequest createRequest(HttpRequest request, Utilities utils);
}
