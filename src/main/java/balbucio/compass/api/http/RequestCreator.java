package balbucio.compass.api.http;

import balbucio.compass.api.utilities.Utilities;
import org.jsoup.Connection;

public interface RequestCreator {

    Connection createRequest(Connection connection, Utilities utils);
}
