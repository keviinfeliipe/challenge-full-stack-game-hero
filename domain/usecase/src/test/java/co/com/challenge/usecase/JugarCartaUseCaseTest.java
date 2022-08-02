package co.com.challenge.usecase;

import co.com.challenge.model.juego.command.JugarCartaCommand;
import co.com.challenge.model.juego.event.JuegoCreado;
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

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JugarCartaUseCaseTest {

    @Mock
    DomainEventRepository repository;
    @InjectMocks
    JugarCartaUseCase useCase;
    @Test
    public void apostarUnaCartaEnElJuego(){
        //Arrange
        var juegoId = "juego prueba";
        var jugadorId = "jugador id prueba";
        var cartaId = "cartaid";
        var command = new JugarCartaCommand(juegoId,jugadorId, cartaId);
        when(repository.getEventsBy(juegoId)).thenReturn(historico());
        useCase.addRepository(repository);
        //act
        var events = UseCaseHandler
                .getInstance()
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();
        //Assert
        Assertions.assertEquals(2, events.size());
    }


    private List<DomainEvent> historico() {
        var jugadorId = "jugador id prueba";
        var alias = "juagdor";
        return List.of(new JuegoCreado(jugadorId,alias));
    }

}