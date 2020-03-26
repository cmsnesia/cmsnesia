package com.cmsnesia.service.command;

import com.cmsnesia.accounts.model.Session;
import org.reactivestreams.Publisher;

public interface CommandExecutor {

  <R, T> Publisher<T> execute(
      Class<? extends Command<R, T>> commandClass, Session session, R request);
}
