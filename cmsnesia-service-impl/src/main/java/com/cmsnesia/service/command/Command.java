package com.cmsnesia.service.command;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.api.Result;
import org.reactivestreams.Publisher;

import java.io.Serializable;

public interface Command<R, T extends Serializable> {

  Publisher<Result<T>> execute(Session session, R request);
}
