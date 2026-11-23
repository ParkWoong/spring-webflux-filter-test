package io.github.parkwoong.gateway.filter;

import static io.github.parkwoong.gateway.properties.FilterProperties.ID;

import io.github.parkwoong.gateway.history.History;
import io.github.parkwoong.gateway.history.HistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.reactivestreams.Publisher;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class HistoryLogger implements WebFilter {

    private static final String RESPONSE = "response";
    private static final byte[] EMPTY_BYTES = {};

    private final HistoryRepository historyReposiroty;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        final String globId = exchange.getAttribute(ID);

        final History history =
                History.builder()
                        .globId(globId)
                        .uri(exchange.getRequest().getURI().toString())
                        .build();
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(
                        databuffer -> {
                            final byte[] bytes = new byte[databuffer.readableByteCount()];

                            DataBufferUtils.release(databuffer.read(bytes));

                            return bytes;
                        })
                .defaultIfEmpty(EMPTY_BYTES)
                .doOnNext(
                        dataBytes -> {
                            history.setRequestHeaders("[redacted]");
                            history.setRequestType(exchange.getRequest().getMethod().toString());
                            history.setRequestBody("[redacted]");

                            log.info("=======[Request] globId : {}=======", history.getGlobId());
                        })
                .flatMap(
                        bytes -> {
                            return chain.filter(
                                    exchange.mutate()
                                            .request(new RequestBodyDecorator(bytes, exchange))
                                            .response(new ResponseBodyDecorator(exchange))
                                            .build());
                        })
                .doFinally(
                        onFinally ->
                                Mono.fromRunnable(
                                                () -> {
                                                    final byte[] bytes =
                                                            (byte[])
                                                                    exchange.getAttributes()
                                                                            .remove(RESPONSE);

                                                    history.setResponseHeaders("[redacted]");
                                                    history.setResponseBody("[redacted]");
                                                    history.setResponseStatus(
                                                            String.valueOf(
                                                                    exchange.getResponse()
                                                                            .getRawStatusCode()));

                                                    historyReposiroty.save(history);

                                                    log.info(
                                                            "=======[Response] status : {}=======",
                                                            history.getResponseStatus());
                                                })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .subscribe());
    }

    public static class RequestBodyDecorator extends ServerHttpRequestDecorator {

        private final Flux<DataBuffer> flux;

        public RequestBodyDecorator(byte[] bytes, ServerWebExchange serverWebExchange) {
            super(serverWebExchange.getRequest());
            flux =
                    (bytes == null || bytes.length == 0)
                            ? Flux.empty()
                            : Flux.just(
                                    serverWebExchange.getResponse().bufferFactory().wrap(bytes));
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return flux;
        }
    }

    public static class ResponseBodyDecorator extends ServerHttpResponseDecorator {

        private final ServerWebExchange exchange;

        public ResponseBodyDecorator(ServerWebExchange exchange) {
            super(exchange.getResponse());
            this.exchange = exchange;
        }

        @Override
        public Mono<Void> writeWith(
                Publisher<? extends DataBuffer>
                        body) { // ServerHttpResponseDecorator가 writeWith을 읽어오기 전에 데이터 핸들링을 해준다.
            if (body instanceof Mono) {
                final Mono<? extends DataBuffer> mono =
                        body == null ? null : (Mono<? extends DataBuffer>) body;

                return super.writeWith(
                                mono.map(
                                        databuffer -> {
                                            final byte[] bytes =
                                                    new byte[databuffer.readableByteCount()];

                                            DataBufferUtils.release(databuffer.read(bytes));
                                            exchange.getAttributes().put(RESPONSE, bytes);

                                            return exchange.getResponse()
                                                    .bufferFactory()
                                                    .wrap(bytes);
                                        }))
                        .onErrorResume(
                                e -> {
                                    e.printStackTrace();
                                    return Mono.empty();
                                });
            } else if (body instanceof Flux) {
                final Flux<? extends DataBuffer> flux = (Flux<? extends DataBuffer>) body;

                return super.writeWith(
                                flux.map(
                                        databuffer -> {
                                            final byte[] bytes =
                                                    new byte[databuffer.readableByteCount()];

                                            DataBufferUtils.release(databuffer.read(bytes));
                                            exchange.getAttributes().put(RESPONSE, bytes);

                                            return exchange.getResponse()
                                                    .bufferFactory()
                                                    .wrap(bytes);
                                        }))
                        .onErrorResume(
                                e -> {
                                    e.printStackTrace();
                                    return Mono.empty();
                                });
            }
            return super.writeWith(body);
        }
    }
}
