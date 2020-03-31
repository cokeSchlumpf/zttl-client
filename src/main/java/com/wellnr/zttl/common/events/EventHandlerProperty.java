package com.wellnr.zttl.common.events;

import java.util.HashSet;
import java.util.Set;

public final class EventHandlerProperty<T> implements SubscribableEvent<T> {

   private final Set<EventListener<T>> listeners;

   public EventHandlerProperty() {
      this.listeners = new HashSet<>();
   }

   public void addListener(EventListener<T> listener) {
      this.listeners.add(listener);
   }

   public void emit(T event) {
      this.listeners.forEach(l -> l.handle(event));
   }

   public void removeListener(EventListener<T> listener) {
      this.listeners.remove(listener);
   }

}
