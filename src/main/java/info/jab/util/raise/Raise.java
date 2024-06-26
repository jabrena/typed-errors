package info.jab.util.raise;

import info.jab.util.either.Either;
import info.jab.util.either.Left;
import info.jab.util.either.Right;
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
    /**
     * raise method
     *
     * @param <A> a description
     * @param error error description
     * @return a a
     */
    <A> A raise(E error);

    /**
     * bind
     *
     * @param <A> a description
     * @param either either description
     * @return a description
     */
    default <A> A bind(Either<? extends E, ? extends A> either) {
        if (either instanceof Left) {
            return raise(((Left<? extends E, ? extends A>) either).value());
        } else {
            return ((Right<? extends E, ? extends A>) either).value();
        }
    }

    /**
     * foldOrThrow
     *
     * @param <E> e
     * @param <A> a
     * @param <B> b
     * @param block block
     * @param recoverBlock recoverBlock
     * @param transformBlock transformBlock
     * @return b
     */
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

    /**
     * fold
     *
     * @param <E> e
     * @param <A> a
     * @param <B> b
     * @param block block
     * @param catchBlock catchBlock
     * @param recoverBlock recoverBlock
     * @param transformBlock transformBlock
     * @return b
     */
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
