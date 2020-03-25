package com.cmsnesia.service.command;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.model.api.Result;
import lombok.Setter;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.Set;

@Component
public class DefaultCommandExecutor
    implements CommandExecutor, ApplicationContextAware, InitializingBean {

  @Setter private Validator validator;

  @Setter private ApplicationContext applicationContext;

  @Override
  public void afterPropertiesSet() {}

  @SneakyThrows
  @Override
  public <R, T extends Serializable> Publisher<Result<T>> execute(
      Class<? extends Command<R, T>> commandClass, Session session, R request) {
    Command<R, T> command = applicationContext.getBean(commandClass);
    return command.execute(session, request);
  }
}
