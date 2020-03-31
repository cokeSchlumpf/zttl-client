package com.wellnr.zttl.common.events;

public interface SubscribableEvent<T> {

   void addListener(EventListener<T> listener);

   void removeListener(EventListener<T> listener);

}
