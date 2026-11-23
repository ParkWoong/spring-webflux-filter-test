package io.github.parkwoong.gateway.filter;

import static io.github.parkwoong.gateway.properties.FilterProperties.ID;
import static io.github.parkwoong.gateway.util.GlobIdGenerator.generate;

import io.github.parkwoong.gateway.properties.FilterProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import java.util.List;

@Order(1)
@Component
@RequiredArgsConstructor
public class RequestDecorator implements WebFilter {

    private final FilterProperties filterProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        final List<String> requestIdList = exchange.getRequest().getHeaders().get(ID);

        final String globId =
                (requestIdList == null || requestIdList.isEmpty())
                        ? generate(filterProperties.getDefaultGlobIdGenerationInfix())
                        : requestIdList.get(0);

        exchange.getAttributes().put(ID, globId);

        return chain.filter(exchange);
    }
}
