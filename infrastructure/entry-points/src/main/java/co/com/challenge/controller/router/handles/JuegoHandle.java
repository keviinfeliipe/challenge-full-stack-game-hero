package co.com.challenge.controller.router.handles;

import co.com.challenge.model.juego.command.*;
import co.com.challenge.usecase.*;
import co.com.challenge.usecase.model.JuegoActivo;
import co.com.challenge.usecase.model.JugadorActual;
import co.com.challenge.usecase.service.JuegoActivoService;
import co.com.challenge.usecase.service.JuegoInformacionService;
import co.com.challenge.usecase.service.JugadorCartasService;
import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.asyn.SubscriberEvent;
import co.com.sofka.infraestructure.bus.EventBus;
import co.com.sofka.infraestructure.repository.EventStoreRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class JuegoHandle extends HandleBase{

    private final SubscriberEvent subscriberEvent;
    private final CrearJuegoUseCase crearJuegoUseCase;
    private final CrearJugadorUseCase crearJugadorUseCase;
    private final JugarCartaUseCase jugarCartaUseCase;
    private final RepartirCartasUseCase repartirCartasUseCase;
    private final JuegoActivoService juegoActivoService;
    private final JugadorCartasService jugadorCartasService;
    private final JuegoInformacionService juegoInformacionService;
    private final RetirarJugadorUseCase retirarJugadorUseCase;
    private final EventStoreRepository eventStoreRepository;

    public JuegoHandle(EventStoreRepository repository,
                       EventBus eventBus,
                       SubscriberEvent subscriberEvent,
                       CrearJuegoUseCase crearJuegoUseCase,
                       CrearJugadorUseCase crearJugadorUseCase,
                       JugarCartaUseCase jugarCartaUseCase,
                       RepartirCartasUseCase repartirCartasUseCase,
                       JuegoActivoService juegoActivoService,
                       JugadorCartasService jugadorCartasService,
                       JuegoInformacionService juegoInformacionService,
                       RetirarJugadorUseCase retirarJugadorUseCase,
                       EventStoreRepository repository1) {
        super(repository, eventBus);
        this.subscriberEvent = subscriberEvent;
        this.crearJuegoUseCase = crearJuegoUseCase;
        this.crearJugadorUseCase = crearJugadorUseCase;
        this.jugarCartaUseCase = jugarCartaUseCase;
        this.repartirCartasUseCase = repartirCartasUseCase;
        this.juegoActivoService = juegoActivoService;
        this.jugadorCartasService = jugadorCartasService;
        this.juegoInformacionService = juegoInformacionService;
        this.retirarJugadorUseCase = retirarJugadorUseCase;
        this.eventStoreRepository = repository1;
    }

    public Mono<ServerResponse> crearJuego(ServerRequest request){
        var command = request.bodyToMono(CrearJuegoCommand.class);
        return command.map(command1 -> {
            crearJuegoUseCase.addRepository(domainEventRepository());
            UseCaseHandler.getInstance()
                    .asyncExecutor(crearJuegoUseCase, new RequestCommand<>(command1))
                    .subscribe(subscriberEvent);
            return command1;
        }).flatMap(command1 -> ServerResponse
                .created(URI.create("/api/v1/juego/".concat(command1.getJuegoId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(command1.getJuegoId())));
    }

    public Mono<ServerResponse> agregarJugador(ServerRequest request){
        var command = request.bodyToMono(CrearJugadorCommand.class);
        return command.map(command1 -> {
            System.out.println(command);
            crearJugadorUseCase.addRepository(domainEventRepository());
            UseCaseHandler.getInstance()
                    .asyncExecutor(crearJugadorUseCase, new RequestCommand<>(command1))
                    .subscribe(subscriberEvent);
            return command1;
        }).flatMap(command1 -> ServerResponse
                .created(URI.create("/api/v1/juego/".concat(command1.getJuegoId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(command1.getJuegoId())));
    }

    public Mono<ServerResponse> jugarCarta(ServerRequest request){
        var command = request.bodyToMono(JugarCartaCommand.class);
        return command.map(command1 -> {
            jugarCartaUseCase.addRepository(domainEventRepository());
            UseCaseHandler.getInstance()
                    .asyncExecutor(jugarCartaUseCase, new RequestCommand<>(command1))
                    .subscribe(subscriberEvent);
            return command1;
        }).flatMap(command1 -> ServerResponse
                .created(URI.create("/api/v1/juego/".concat(command1.getJuegoId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(command1.getJuegoId())));
    }

    public Mono<ServerResponse> iniciarJuego(ServerRequest request){
        var id = request.pathVariable("id");
        repartirCartasUseCase.addRepository(domainEventRepository());
        UseCaseHandler.getInstance()
                .asyncExecutor(repartirCartasUseCase, new RequestCommand<>(new IniciarJuegoCommand(id)))
                .subscribe(subscriberEvent);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(id));
    }

    public Mono<ServerResponse> juegosActivos(ServerRequest request){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(juegoActivoService.obtenerJuegosActivos(), JuegoActivo.class);
    }

    public Mono<ServerResponse> jugadorCartas(ServerRequest request){
        var command = request.bodyToMono(JuegoJugadorCommand.class);
        return command.flatMap(juegoJugadorCommand -> {
            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jugadorCartasService.obtenerCartasDeJugador(juegoJugadorCommand.getJuegoId(), juegoJugadorCommand.getJugadorId()), JugadorActual.class);
        });
    }

    public Mono<ServerResponse> obtenerInformacionDeJuego(ServerRequest request){
        var id = request.pathVariable("id");
        return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(juegoInformacionService.obtenerInformacionDelJuego(id), JugadorActual.class);
    }

    public Mono<ServerResponse> retirarJugador(ServerRequest request){
        retirarJugadorUseCase.addRepository(domainEventRepository());
        var command = request.bodyToMono(RetirarJugadorCommand.class);
        return command.flatMap(jugadorCommand -> {
            UseCaseHandler.getInstance()
                    .asyncExecutor(retirarJugadorUseCase, new RequestCommand<>(new RetirarJugadorCommand(jugadorCommand.getJuegoId(), jugadorCommand.getJugadorId())))
                    .subscribe(subscriberEvent);
            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue(jugadorCommand.getJugadorId()));
        });
    }

    private DomainEventRepository domainEventRepository() {
        return new DomainEventRepository() {
            @Override
            public List<DomainEvent> getEventsBy(String aggregateId) {
                return eventStoreRepository.getEventsBy("juego", aggregateId);
            }

            @Override
            public List<DomainEvent> getEventsBy(String aggregateName, String aggregateRootId) {
                return eventStoreRepository.getEventsBy(aggregateName, aggregateRootId);
            }
        };
    }
}
