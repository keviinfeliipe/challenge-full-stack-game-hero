package co.com.challenge.usecase;

import co.com.challenge.model.carta.CartaMaestra;
import co.com.challenge.model.carta.gateway.CartaMaestraFactory;
import co.com.challenge.model.carta.gateway.CartaRepository;
import co.com.challenge.model.juego.JugadorFactory;
import co.com.challenge.model.juego.command.IniciarJuegoCommand;
import co.com.challenge.model.juego.event.JuegoCreado;
import co.com.challenge.model.juego.event.JugadorCreado;
import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.domain.generic.DomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepartirCartasUseCaseTest {

    @Mock
    DomainEventRepository eventRepository;

    @Mock
    CartaRepository cartaRepository;

    @InjectMocks
    RepartirCartasUseCase useCase;

    @Test
    public void repartirCartasALosJugadores(){
        //Arrange
        var juegoId = "juego prueba";
        var command = new IniciarJuegoCommand(juegoId);
        var xp = new Random();
        var cartaMaestraFactory = new CartaMaestraFactory();
        for(int i = 1; i <109 ; i++){
            cartaMaestraFactory.add(new CartaMaestra("ID carta #"+i,
                    "Nombre carta #"+i,
                    "Descripción carta #"+i,
                    xp.nextInt(99)+1,
                    "Descripción carta #"+i,
                    "Imagen carta #"+i));
        }

        var cartasMaestras = Flux.fromIterable(cartaMaestraFactory.cartas());

        when(eventRepository.getEventsBy(juegoId)).thenReturn(historico());
        when(cartaRepository.findAll()).thenReturn(cartasMaestras);
        useCase.addRepository(eventRepository);
        //act
        var events = UseCaseHandler
                .getInstance()
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();
        //Assert
        Assertions.assertEquals(7, events.size());
    }

    private List<DomainEvent> historico() {
        var juadorFactory = new JugadorFactory()
                .add("Jugador 1","Juagdor 1")
                .add("Jugador 2","Juagdor 2")
                .add("Jugador 3","Juagdor 3");
        var jugadorId = "jugador id prueba";
        var alias = "juagdor";
        return List.of(new JuegoCreado(jugadorId,alias), new JugadorCreado(juadorFactory));
    }
}