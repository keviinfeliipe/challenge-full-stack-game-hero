package co.com.challenge.model.juego;

import co.com.sofka.domain.generic.DomainEvent;

public class EmpateTecnicoCreado extends DomainEvent {
    public EmpateTecnicoCreado() {
        super("juego.EmpateTecnicoCreado");
    }
}
