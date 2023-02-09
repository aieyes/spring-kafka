package org.apache.kafka.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.common.annotation.InterfaceStability.Evolving;
import org.apache.kafka.common.internals.KafkaFutureImpl;

@Evolving
public abstract class KafkaFuture<T> implements Future<T> {
    public static interface BaseFunction<A, B> {
        B apply(A param1A);
    }

    public static abstract class Function<A, B> implements BaseFunction<A, B> {}

    public static interface BiConsumer<A, B> {
        void accept(A param1A, B param1B);
    }

    private static class AllOfAdapter<R> implements BiConsumer<R, Throwable> {
        private int remainingResponses;

        private KafkaFuture<?> future;

        public AllOfAdapter(int remainingResponses, KafkaFuture<?> future) {
            this.remainingResponses = remainingResponses;
            this.future = future;
            maybeComplete();
        }

        public synchronized void accept(R newValue, Throwable exception) {
            if (this.remainingResponses <= 0)
                return;
            if (exception != null) {
                this.remainingResponses = 0;
                this.future.completeExceptionally(exception);
            } else {
                this.remainingResponses--;
                maybeComplete();
            }
        }

        private void maybeComplete() {
            if (this.remainingResponses <= 0)
                this.future.complete(null);
        }
    }

    public static <U> KafkaFuture<U> completedFuture(U value) {
        KafkaFutureImpl<U> kafkaFutureImpl = new KafkaFutureImpl<>();
        kafkaFutureImpl.complete(value);
        return kafkaFutureImpl;
    }

    public static KafkaFuture<Void> allOf(KafkaFuture<?>... futures) {
        KafkaFutureImpl<Void> kafkaFutureImpl = new KafkaFutureImpl<>();
        AllOfAdapter<Object> allOfWaiter = new AllOfAdapter<>(futures.length, kafkaFutureImpl);
        for (KafkaFuture<?> future : futures)
            future.addWaiter(allOfWaiter);
        return kafkaFutureImpl;
    }

    public abstract <R> KafkaFuture<R> thenApply(BaseFunction<T, R> paramBaseFunction);

    public abstract <R> KafkaFuture<R> thenApply(Function<T, R> paramFunction);

    public abstract KafkaFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> paramBiConsumer);

    protected abstract void addWaiter(BiConsumer<? super T, ? super Throwable> paramBiConsumer);

    protected abstract boolean complete(T paramT);

    protected abstract boolean completeExceptionally(Throwable paramThrowable);

    public abstract boolean cancel(boolean paramBoolean);

    public abstract T get() throws InterruptedException, ExecutionException;

    public abstract T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract T getNow(T paramT) throws InterruptedException, ExecutionException;

    public abstract boolean isCancelled();

    public abstract boolean isCompletedExceptionally();

    public abstract boolean isDone();
}
