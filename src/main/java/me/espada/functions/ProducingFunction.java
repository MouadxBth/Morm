package me.espada.functions;

@FunctionalInterface
public interface ProducingFunction<Type> {
    Type apply(Type type);
}
