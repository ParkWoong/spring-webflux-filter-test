package io.github.parkwoong.gateway;

import static org.junit.jupiter.api.Assertions.*;

import io.github.parkwoong.gateway.filter.RequestDecorator;
import io.github.parkwoong.gateway.properties.FilterProperties;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;

class FilterTest {
    @Test
    void createsRequestIdWhenMissing() {
        FilterProperties props = new FilterProperties();
        props.setDefaultGlobIdGenerationInfix("REQ");
        MockServerWebExchange exchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/sample"));
        new RequestDecorator(props).filter(exchange, e -> Mono.empty()).block();
        assertNotNull(exchange.getAttribute(FilterProperties.ID));
    }
}
