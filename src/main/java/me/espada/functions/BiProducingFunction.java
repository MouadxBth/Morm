package me.espada.functions;

@FunctionalInterface
public interface BiProducingFunction<T, U, Return> {
    Return apply(T t, U u);
}
