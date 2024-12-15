## Identified Issues and Fixes

### 1. Adding Reviews Does Not Work
**Analysis:**
The issue was caused by the absence of the `updateEvent` method, which was required to handle the logic for updating an event.

**Solution:**
```java
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
```

### 2. Delete Button Issue
**Analysis:**
The delete operation appeared to work initially but failed to persist changes because the repository is annotated with `@Transactional(readOnly = true)`. This prevented the operation from committing changes to the database.

**Solution:**
1. Add a standard `@Transactional` annotation to enable write operations.
```java
@Transactional
public void delete(Long id) {
    eventRepository.deleteById(id);
}
```

## New Features 
### 1. Search Endpoint /search/{query}
This method allows users to search for events based on the names of band members
```java
@Transactional
public List<Event> getFilteredEvents(String query) {
    List<Event> events = eventRepository.findAll();

    return events.stream()
            .filter(event -> event.getBands().stream()
                    .anyMatch(band -> band.getMembers().stream()
                            .anyMatch(member -> member.getName().toLowerCase().contains(query.toLowerCase()))))
            .collect(Collectors.toList());
}
```

### 2. Bonus Add a [count] at each event and band to display the number of child items.
This method modifies the way the "name" field is serialized into JSON.
Instead of returning just the name, it appends the number of members in brackets.
```java
   @JsonProperty("name")
    public String getNameWithMembersCount() {
        return name + " [" + (members != null ? members.size() : 0) + "]";
    }
```

This method modifies the way the "title" field is serialized into JSON.
Instead of returning just the title, it appends the number of bands in brackets.
```java
  @JsonProperty("title")
    public String getTitleWithBandsCount() {
        return title + " [" + (bands != null ? bands.size() : 0) + "]";
    }
```

### ⚠️ I’m a fullstack TypeScript developer, and I have experience with Java SE, but this is my first time working with Spring Boot. Due to time constraints I wasn’t able to add tests.
