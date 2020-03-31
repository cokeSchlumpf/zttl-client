package com.wellnr.zttl.common.events;

@FunctionalInterface
public interface EventListener<T> {

   public void handle(T event);

}
