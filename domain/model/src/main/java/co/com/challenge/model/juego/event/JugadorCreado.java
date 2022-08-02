package co.com.challenge.model.juego.event;

import co.com.challenge.model.juego.JugadorFactory;
import co.com.sofka.domain.generic.DomainEvent;

public class JugadorCreado extends DomainEvent {


    private final JugadorFactory jugadorFactory;

    public JugadorCreado(JugadorFactory jugadorFactory) {
        super("juego.JugadorCreado");

        this.jugadorFactory = jugadorFactory;
    }

    public JugadorFactory getJugadorFactory() {
        return jugadorFactory;
    }
}
