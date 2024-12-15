package adeo.leroymerlin.cdp;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public void delete(Long id) { eventRepository.deleteById(id); }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(event -> event.getBands().stream()
                        .anyMatch(band -> band.getMembers().stream()
                                .anyMatch(member -> member.getName().toLowerCase().contains(query.toLowerCase()))))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<String> updateEvent(Long id, Event event) {
        Optional<Event> existingEventOptional = eventRepository.findById(id);

        if (existingEventOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event existingEvent = existingEventOptional.get();
        existingEvent.setComment(event.getComment());
        existingEvent.setNbStars(event.getNbStars());
        eventRepository.save(existingEvent);

        return ResponseEntity.noContent().build();
    }
}
