package co.com.challenge.bus;

import co.com.challenge.controller.rest.SocketController;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.bus.serialize.SuccessNotificationSerializer;
import co.com.sofka.infraestructure.event.EventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class RabbitMQConsumer {


    private final EventListenerSubscriber eventListenerSubscriber;
    private final SocketController socketController;

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);

    public RabbitMQConsumer(EventListenerSubscriber eventListenerSubscriber, SocketController socketController) {
        this.eventListenerSubscriber = eventListenerSubscriber;
        this.socketController = socketController;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "juego.handles", durable = "true"),
            exchange = @Exchange(value = "juego-heroes", type = "topic"),
            key = "juego.#"
    ))
    public void recievedMessageJuego(Message<String> message) {
        messageShared(message);
    }

    private void messageShared(Message<String> message) {
        var successNotification = SuccessNotificationSerializer.instance().deserialize(message.getPayload());
        var event = successNotification.deserializeEvent();
        var messages = EventSerializer.instance().serialize(event);
        log.info("Event: {}", mostrarEvento(event));
        try {
            this.eventListenerSubscriber.onNext(event);
            Optional.ofNullable(event.aggregateParentId())
                    .ifPresentOrElse(id -> socketController.send(id, event), () -> socketController.send(event.aggregateRootId(), event));
            Thread.sleep(1000);
        } catch (Exception e) {
            this.eventListenerSubscriber.onError(e);
        }
    }

    private String mostrarEvento(DomainEvent event){
        var sb = new StringBuilder("DomainEvent{");
        sb.append("when=").append(event.when);
        sb.append(", uuid=").append(event.uuid);
        sb.append(", type='").append(event.type).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
