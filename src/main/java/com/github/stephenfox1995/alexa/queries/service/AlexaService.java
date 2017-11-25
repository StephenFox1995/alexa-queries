package com.github.stephenfox1995.alexa.queries.service;

import com.github.stephenfox1995.alexa.queries.repository.AlexaDatasource;
import com.github.stephenfox1995.alexa.queries.service.router.TemperatureRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.flywaydb.core.Flyway;

import java.io.IOException;


public class AlexaService extends AbstractVerticle {

    private static final int HTTP_PORT = 8080;
    private static final String TEMPERATURE_END_POINT = "/api/alexa/temperature";
    private final TemperatureRouter temperatureRouter = new TemperatureRouter();

    public void start(Future<Void> future) throws Exception {
        doDatabaseMigrations();
        final Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());
        router.route(TEMPERATURE_END_POINT).handler(temperatureRouter);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(HTTP_PORT, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }

    private final void doDatabaseMigrations() throws IOException {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(AlexaDatasource.getDataSource());
        flyway.migrate();
    }
}
