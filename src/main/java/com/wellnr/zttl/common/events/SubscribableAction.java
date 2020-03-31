package com.wellnr.zttl.common.events;

public interface SubscribableAction {

   void addHandler(Runnable handler);

   void removeHandler(Runnable handler);

}
