package com.cmsnesia.assembler;

import java.util.Collection;
import javax.annotation.Nonnull;

public abstract class Assembler<A, B> {

  @Nonnull
  public abstract A fromDto(@Nonnull B dto);

  @Nonnull
  public abstract Collection<A> fromDto(@Nonnull Collection<B> dtos);

  @Nonnull
  public abstract B fromEntity(@Nonnull A entity);

  @Nonnull
  public abstract Collection<B> fromEntity(@Nonnull Collection<A> entities);
}
