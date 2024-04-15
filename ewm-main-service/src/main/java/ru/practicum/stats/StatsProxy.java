package ru.practicum.stats;

import ru.practicum.event.entity.Event;

public interface StatsProxy {

    long getViews(Event event);

    void addHit(String uri, String ip);
}
