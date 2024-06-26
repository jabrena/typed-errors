package info.jab.sc;

import info.jab.fp.util.Result;
import info.jab.fp.util.either.Either;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CustomScopePolicies {
    class ResultScope<T> extends StructuredTaskScope<T> {

        private static final Object POISON = new Object();
        private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

        @Override
        protected void handleComplete(Subtask<? extends T> future) {
            try {
                queue.put(
                    switch (future.state()) {
                        case SUCCESS -> new Result.Success<>(future.get());
                        case FAILED -> new Result.Failure<>(future.exception());
                        case UNAVAILABLE -> throw new AssertionError();
                    }
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public ResultScope<T> join() throws InterruptedException {
            try {
                super.join();
                return this;
            } finally {
                queue.put(POISON);
                shutdown();
            }
        }

        @Override
        public ResultScope<T> joinUntil(Instant deadline) throws InterruptedException, TimeoutException {
            try {
                super.joinUntil(deadline);
                return this;
            } finally {
                queue.put(POISON);
                shutdown();
            }
        }

        @SuppressWarnings("unchecked")
        public Stream<Result<T>> stream() {
            return Stream.of((Object) null).mapMulti((__, consumer) -> {
                try {
                    Object result;
                    while ((result = queue.take()) != POISON) {
                        consumer.accept((Result<T>) result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    class EitherScope<T> extends StructuredTaskScope<T> {

        @Override
        protected void handleComplete(Subtask<? extends T> future) {
            switch (future.state()) {
                case SUCCESS -> {
                    // Ignore
                    Either result = (Either) future.get();
                    if (result.isLeft()) {
                        super.shutdown();
                    }
                    System.out.println(result.fold(Function.identity(), Function.identity()));
                }
                case FAILED -> {
                    // Ignore
                }
                case UNAVAILABLE -> {
                    // Ignore
                }
                default -> {
                    // Ignore
                }
            }
        }
    }
}
