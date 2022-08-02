package co.com.challenge.model.juego.event;

import co.com.challenge.model.juego.value.JugadorId;
import co.com.sofka.domain.generic.DomainEvent;

import java.util.Set;

public class RondaCreada extends DomainEvent {
    private final Set<JugadorId> jugadorIds;

    public RondaCreada(Set<JugadorId> jugadorIds) {
        super("juego.RondaCreada");
        this.jugadorIds = jugadorIds;
    }

    public Set<JugadorId> getJugadorIds() {
        return jugadorIds;
    }
}
