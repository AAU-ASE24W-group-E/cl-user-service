package at.aau.ase.cl.service;

import at.aau.ase.cl.event.UserEvent;
import at.aau.ase.cl.model.UserEntity;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class EventService {
    public static final String USER_EVENT_CHANNEL = "user";

    @Channel(USER_EVENT_CHANNEL)
    Emitter<UserEvent> userEventEmitter;

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void sendUserEvent(UserEntity user) {
        UserEvent userEvent = new UserEvent(user.id, user.username, user.address.latitude, user.address.longitude);
        Log.debugf("Sending user event: %s", userEvent);
        userEventEmitter.send(KafkaRecord.of(userEvent.id(), userEvent)
                .withAck(() -> {
                    Log.infof("User event sent: %s", userEvent);
                    return CompletableFuture.completedFuture(null);
                }).withNack(e -> {
                    Log.errorf(e, "Failed to send user event: %s", userEvent);
                    return CompletableFuture.completedFuture(null);
                }));
    }
}
