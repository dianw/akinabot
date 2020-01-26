package org.codenergic.akinabot.core;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.codenergic.akinatorj.Session;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.source.Source;
import com.pengrad.telegrambot.model.Update;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class QueueConfig {
    private final IQueue<Event> lineEventQueue;
    private final IQueue<Update> telegramUpdateQueue;
    private final IMap<Source, Session> lineSessions;
    private final IMap<Long, Session> telegramSessions;
    private MeterRegistry meterRegistry;

    public QueueConfig(HazelcastInstance hz, MeterRegistry meterRegistry) {
        this.lineEventQueue = hz.getQueue("lineEventQueue");
        this.telegramUpdateQueue = hz.getQueue("telegramUpdateQueue");
        this.lineSessions = hz.getMap("lineSessions");
        this.telegramSessions = hz.getMap("telegramSessions");
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        initSessionsGauge(telegramSessions, "telegram");
        initSessionsGauge(lineSessions, "line");
        initMessagesMetric(telegramUpdateQueue, "telegram");
        initMessagesMetric(lineEventQueue, "line");
    }

    private <I> void initSessionsGauge(IMap<I, Session> sessions, String app) {
        final AtomicInteger sessionsGauge = meterRegistry.gauge("bot.users", Tags.of("app", app),
                new AtomicInteger(sessions.size()));
        sessions.addEntryListener((EntryAddedListener<I, Session>) event -> sessionsGauge.incrementAndGet(), false);
        sessions.addEntryListener((EntryRemovedListener<I, Session>) event -> sessionsGauge.decrementAndGet(), false);
    }

    private <T> void initMessagesMetric(IQueue<T> messageQueue, String app) {
        final AtomicInteger messagesGauge = meterRegistry.gauge("bot.messages.queue", Tags.of("app", app),
                new AtomicInteger(messageQueue.size()));
        final Counter messageCounter = meterRegistry.counter("bot.messages.inbound", Tags.of("app", app));
        messageQueue.addItemListener(new ItemListener<T>() {
            @Override
            public void itemAdded(ItemEvent<T> item) {
                messagesGauge.incrementAndGet();
            }

            @Override
            public void itemRemoved(ItemEvent<T> item) {
                messagesGauge.decrementAndGet();
                messageCounter.increment();
            }
        }, false);
    }

    public BlockingQueue<Event> lineEventQueue() {
        return lineEventQueue;
    }

    public Map<Source, Session> lineSessions() {
        return lineSessions;
    }

    public Map<Long, Session> telegramSessions() {
        return telegramSessions;
    }

    public BlockingQueue<Update> telegramUpdateQueue() {
        return telegramUpdateQueue;
    }
}
