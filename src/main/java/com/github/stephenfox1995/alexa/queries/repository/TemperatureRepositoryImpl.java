package com.github.stephenfox1995.alexa.queries.repository;

import com.github.stephenfox1995.alexa.queries.objs.Temperature;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;


public class TemperatureRepositoryImpl implements TemperatureRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureRepositoryImpl.class);
    private static TemperatureRepositoryImpl repository;
    private final JDBCClient jdbcClient = JDBCClient.create(Vertx.vertx(), AlexaDatasource.getDataSource());
    private static final String STORE_QUERY = "insert into alexa.temperature(temperature, timestamp) values (?, ?);";
    private static final String GET_LATEST_QUERY = "select temperature, timestamp from alexa.temperature order by timestamp desc limit 1;";

    public static TemperatureRepository getInstance() {
        if (repository == null) {
            repository = new TemperatureRepositoryImpl();
        }
        return repository;
    }

    @Override
    public Future<Void> store(List<Temperature> temperatures) {
        final Future<Void> future = Future.future();
        jdbcClient.getConnection(conn -> {
            if (conn.succeeded()) {
                LOGGER.info("Connection to database retrieved");
                final SQLConnection connection = conn.result();
                final List<JsonArray> batch =
                        temperatures.stream()
                                .map(t -> new JsonArray().add(t.getTemperature()).add(t.getTimestamp()))
                                .collect(Collectors.toList());

                connection.batchWithParams(STORE_QUERY, batch, r -> {
                    if (r.succeeded()) {
                        LOGGER.info("Successful batch operation");
                        future.complete();
                    } else {
                        LOGGER.info("Failed batch operation {}", r.cause());
                        future.fail(r.cause());
                    }
                    connection.close();
                });

            }
        });
        return future;
    }

    @Override
    public Future<Temperature> getLatest() {
        Future<Temperature> future = Future.future();
        jdbcClient.getConnection(conn -> {
            if (conn.succeeded()) {
                LOGGER.info("Connection to the database retrieved");
                final SQLConnection connection = conn.result();

                connection.query(GET_LATEST_QUERY, r -> {
                    if (r.succeeded()) {
                        final ResultSet resultSet = r.result();
                        final JsonObject record = resultSet.getRows().get(0);
                        LOGGER.info("Successfully retrieved latest temperature record {}", record);
                        final Temperature temperature = new Temperature(
                                record.getFloat("temperature"),
                                record.getString("timestamp"));
                        future.complete(temperature);
                    } else {
                        LOGGER.info("Could not retrieve latest temperature record {}", r.cause());
                        future.fail(r.cause());
                    }
                    connection.close();
                });
            }
        });
        return future;
    }
}
