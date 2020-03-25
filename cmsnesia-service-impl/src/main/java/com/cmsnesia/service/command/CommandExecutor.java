package com.cmsnesia.service.command;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.api.Result;
import org.reactivestreams.Publisher;

import java.io.Serializable;

public interface CommandExecutor {

  <R, T extends Serializable> Publisher<Result<T>> execute(
      Class<? extends Command<R, T>> commandClass, Session session, R request);
}
