package co.com.challenge.usecase;


import co.com.challenge.model.juego.command.CrearJuegoCommand;
import co.com.challenge.model.juego.event.JuegoCreado;
import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.support.RequestCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CrearJuegoUseCaseTest {

    @Test
    public void crearJuego(){

        //Arrange
        var juegoId = "juego id prueba";
        var jugadorId = "jugador id prueba";
        var alias = "juagdor";
        var command = new CrearJuegoCommand(juegoId, jugadorId, alias);
        var usecase = new CrearJuegoUseCase();

        //Act
        var events = UseCaseHandler
                .getInstance()
                .syncExecutor(usecase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();
        var event = (JuegoCreado)events.get(0);
        //Assert
        Assertions.assertEquals("juego id prueba", event.aggregateRootId());
        Assertions.assertEquals("jugador id prueba",event.getJugadorId());
        Assertions.assertEquals("juagdor",event.getAlias());

    }

}