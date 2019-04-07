package me.espada.functions;

@FunctionalInterface
public interface BiProducingFunction<T, U, R> {
    R apply(T t, U u);
}
