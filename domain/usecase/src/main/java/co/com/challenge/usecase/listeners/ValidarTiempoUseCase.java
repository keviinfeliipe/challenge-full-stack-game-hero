package co.com.challenge.usecase.listeners;

import co.com.challenge.model.juego.Juego;
import co.com.challenge.model.juego.event.TiempoDescontado;
import co.com.challenge.model.juego.value.JuegoId;
import co.com.sofka.business.generic.UseCase;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.business.support.TriggeredEvent;

public class ValidarTiempoUseCase extends UseCase<TriggeredEvent<TiempoDescontado>, ResponseEvents> {

    @Override
    public void executeUseCase(TriggeredEvent<TiempoDescontado> triggeredEvent) {
        var event = triggeredEvent.getDomainEvent();
        var juegoId = JuegoId.of(event.aggregateRootId());
        var events = repository().getEventsBy("juego", juegoId.value());
        var juego = Juego.from(juegoId, events);

        if (juego.tablero().tiempo().value()!=0){
            juego.descontarTiempo();
        }else{
            juego.deshabilitarTablero();
            juego.terminoElTimepo();
        }

        emit().onResponse(new ResponseEvents(juego.getUncommittedChanges()));
    }
}
