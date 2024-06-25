package info.jab.fp.util.raise;

import info.jab.fp.util.Either;
import java.util.concurrent.CancellationException;
import java.util.function.Function;

/**
 * Port from arrow-kt Raise
 * <a href="https://apidocs.arrow-kt.io/arrow-core/arrow.core.raise/index.html">Raise</a>
 *
 * @param <E> the type of the error
 *
 * @author Raul Raja
 */
public interface Raise<E> {
    <A> A raise(E error);

    default <A> A bind(Either<? extends E, ? extends A> either) {
        if (either instanceof Either.Left) {
            return raise(((Either.Left<? extends E, ? extends A>) either).value());
        } else {
            return ((Either.Right<? extends E, ? extends A>) either).value();
        }
    }

    static <E, A, B> B foldOrThrow(
        Function<Raise<? super E>, ? extends A> block,
        Function<? super E, ? extends B> recoverBlock,
        Function<A, B> transformBlock
    ) {
        return fold(
            block,
            e -> {
                throw new RuntimeException(e);
            },
            recoverBlock,
            transformBlock
        );
    }

    static <E, A, B> B fold(
        Function<Raise<? super E>, ? extends A> block,
        Function<Throwable, B> catchBlock,
        Function<? super E, ? extends B> recoverBlock,
        Function<A, B> transformBlock
    ) {
        DefaultRaise<E> raise = new DefaultRaise<>(false);
        try {
            A res = block.apply(raise);
            raise.complete();
            return transformBlock.apply(res);
        } catch (RaiseCancellationException e) {
            raise.complete();
            return recoverBlock.apply(raisedOrRethrow(e, raise));
        } catch (Throwable e) {
            raise.complete();
            return catchBlock.apply(nonFatalOrThrow(e));
        }
    }

    @SuppressWarnings("unchecked")
    private static <R> R raisedOrRethrow(CancellationException e, DefaultRaise<?> raise) {
        if (e instanceof RaiseCancellationException && ((RaiseCancellationException) e).getRaise() == raise) {
            return (R) ((RaiseCancellationException) e).getRaised();
        } else {
            throw e;
        }
    }

    private static Throwable nonFatalOrThrow(Throwable e) {
        if (nonFatal(e)) {
            return e;
        } else {
            throw new RuntimeException(e);
        }
    }

    // @formatter:off

    private static boolean nonFatal(Throwable t) {
        return (
            !(t instanceof VirtualMachineError) 
            && !(t instanceof InterruptedException) 
            && !(t instanceof LinkageError) 
            && !(t instanceof CancellationException)
        );
    }
    // @formatter:on
}
