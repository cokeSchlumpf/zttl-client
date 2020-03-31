package com.wellnr.zttl.common.events;

import java.util.HashSet;
import java.util.Set;

public final class ActionHandlerProperty implements SubscribableAction {

   private final Set<Runnable> handlers;

   public ActionHandlerProperty() {
      this.handlers = new HashSet<>();
   }

   public void addHandler(Runnable handler) {
      this.handlers.add(handler);
   }

   public void emit() {
      this.handlers.forEach(Runnable::run);
   }

   public void removeHandler(Runnable handler) {
      this.handlers.remove(handler);
   }

}
