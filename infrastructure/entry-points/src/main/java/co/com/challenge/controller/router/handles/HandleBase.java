package co.com.challenge.controller.router.handles;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.bus.EventBus;
import co.com.sofka.infraestructure.repository.EventStoreRepository;
import co.com.sofka.infraestructure.store.StoredEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class HandleBase{

    private final EventStoreRepository repository;
    private final EventBus eventBus;

    public Mono<Void> emit(Flux<DomainEvent> events){

        return events.map(event -> {
            var store = StoredEvent.wrapEvent(event);
            repository.saveEvent("juego", event.aggregateRootId(), store);
            eventBus.publish(event);
            return store;
        }).then();
    }

}
