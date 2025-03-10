package balbucio.compass.api.route;

import balbucio.compass.api.http.RequestCreator;
import balbucio.compass.api.http.ResponseHandler;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestRoute {

    @Getter
    private List<RequestCreator> creators = new CopyOnWriteArrayList<>();
    @Getter
    private ResponseHandler responseHandler;

    public TestRoute(){
    }

    public TestRoute addCreator(RequestCreator creator){
        creators.add(creator);
        return this;
    }

    public TestRoute onResponse(ResponseHandler responseHandler){
        this.responseHandler = responseHandler;
        return this;
    }
}
