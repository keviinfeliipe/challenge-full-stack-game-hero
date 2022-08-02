package co.com.challenge.controller.router;

import co.com.challenge.controller.router.handles.JuegoHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
@Configuration
public class JuegoRouterController {
    @Bean
    public RouterFunction<ServerResponse> routerFunctionJuego(JuegoHandle handler){
        return route(POST("api/v2/juego/crearjuego"), handler::crearJuego)
                .andRoute(POST("api/v2/juego/agregarjugador"), handler::agregarJugador)
                .andRoute(POST("api/v2/juego/jugarcarta"), handler::jugarCarta)
                .andRoute(GET("api/v2/juego/iniciarjuego/{id}"), handler::iniciarJuego)
                .andRoute(GET("api/v2/juego"), handler::juegosActivos)
                .andRoute(POST("api/v2/juego/jugador"), handler::jugadorCartas)
                .andRoute(GET("api/v2/juego/{id}"), handler::obtenerInformacionDeJuego)
                .andRoute(POST("api/v2/juego/retirarse"), handler::retirarJugador);

    }
}
