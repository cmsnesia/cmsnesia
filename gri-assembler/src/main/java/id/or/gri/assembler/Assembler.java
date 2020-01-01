package id.or.gri.assembler;

import java.util.Collection;
import javax.annotation.Nonnull;

public interface Assembler<A, B> {

    @Nonnull
    A fromDto(@Nonnull B dto);

    @Nonnull
    Collection<A> fromDto(@Nonnull Collection<B> dtos);

    @Nonnull
    B fromEntity(@Nonnull A entity);

    @Nonnull
    Collection<B> fromEntity(@Nonnull Collection<A> entities);

}