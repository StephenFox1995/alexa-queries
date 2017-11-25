package com.github.stephenfox1995.alexa.queries.service.router;

import com.github.stephenfox1995.alexa.queries.objs.Temperature;
import com.github.stephenfox1995.alexa.queries.repository.TemperatureRepository;
import com.github.stephenfox1995.alexa.queries.repository.TemperatureRepositoryImpl;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

public class TemperatureRouter implements Handler<RoutingContext> {

    private TemperatureRepository repository = TemperatureRepositoryImpl.getInstance();

    @Override
    public void handle(RoutingContext routingContext) {
        switch (routingContext.request().method()) {
            case POST:
                handlePost(routingContext);
                break;
            case GET:
                handleGet(routingContext);
                break;
            default:
                routingContext.response().setStatusCode(404).end();
        }
    }

    private void handleGet(RoutingContext routingContext) {
        Future<Temperature> future = repository.getLatest();
        future.setHandler(r -> {
            if (r.succeeded()) {
                final Temperature temperature = r.result();
                final JsonObject body =
                        new JsonObject()
                                .put("temperature", temperature.getTemperature())
                                .put("timestamp", temperature.getTimestamp());
                routingContext.response().setStatusCode(200).end(body.encode());
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void handlePost(RoutingContext routingContext) {
        final JsonObject body = routingContext.getBodyAsJson();
        final JsonArray data = body.getJsonArray("data");
        List<Temperature> temperatureList = data.stream().map(e ->
                new Temperature(((JsonObject) e).getFloat("temperature"), ((JsonObject) e).getString("timestamp"))
        ).collect(Collectors.toList());
        Future<Void> future = repository.store(temperatureList);
        future.setHandler(result -> {
            if (result.succeeded()) {
                routingContext.response().setStatusCode(200).end();
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });

    }
}
