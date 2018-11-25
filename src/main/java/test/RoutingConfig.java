package test;

import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.function.Consumer;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.netty.handler.codec.http.HttpContentDecompressor;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Configuration
public class RoutingConfig {

    @Bean
    public RouterFunction<ServerResponse> myRoutes() {
        return nest(method(HttpMethod.POST), route(path("/test"), this::test));
    }

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();

        factory.addServerCustomizers(builder -> builder
                .tcpConfiguration(tcpServer -> tcpServer.doOnConnection((Consumer<Connection>) connection -> {
                    connection.channel().pipeline().addAfter("reactor.left.httpCodec", "decompressor", new HttpContentDecompressor());
                })));

        return factory;
    }

    public Mono<ServerResponse> test(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(String.class)
                .doOnEach(System.err::println)
                .then(ok().body(BodyInserters.empty()));
    }
}
