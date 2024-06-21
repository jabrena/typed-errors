package info.jab.sc;

import info.jab.fp.util.Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class EitherResultScope<T> extends StructuredTaskScope<T> {

    // @formatter:off

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        var state = subtask.state();
        switch (state) {
            case UNAVAILABLE -> {
                //Ignore
            }
            case SUCCESS -> {
                //T result = Result.success(subtask.get());
                T result = subtask.get();
            }
            case FAILED -> {
                //Ignore
                //T result = Result.failure(subtask.exception());
            }
            default -> { 
                //Ignore
            }
        }
    }
    // @formatter:on

}
