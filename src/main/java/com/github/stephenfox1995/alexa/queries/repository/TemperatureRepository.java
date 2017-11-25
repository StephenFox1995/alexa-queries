package com.github.stephenfox1995.alexa.queries.repository;

import com.github.stephenfox1995.alexa.queries.objs.Temperature;
import io.vertx.core.Future;

import java.util.List;


public interface TemperatureRepository {

    Future<Void> store(List<Temperature> temperature);

    Future<Temperature> getLatest();

}
