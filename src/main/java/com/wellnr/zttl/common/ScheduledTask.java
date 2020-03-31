package com.wellnr.zttl.common;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduledTask {

   private final Runnable runnable;

   private final AtomicBoolean cancelled;

   private ScheduledTask(Runnable runnable) {
      this.runnable = runnable;
      this.cancelled = new AtomicBoolean(false);
   }

   public static ScheduledTask schedule(Runnable action, Duration timeout) {
      ScheduledTask task = new ScheduledTask(action);

      CompletableFuture
         .delayedExecutor(timeout.toMillis(), TimeUnit.MILLISECONDS)
         .execute(task::run);

      return task;
   }

   public void cancel() {
      this.cancelled.set(true);
   }

   private void run() {
      if (!cancelled.get()) {
         runnable.run();
      }
   }

}
