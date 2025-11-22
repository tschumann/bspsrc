package info.ata4.bspsrc.lib.vector;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.util.Objects.requireNonNull;

public sealed abstract class VectorXd<T extends VectorXd<T>> implements Iterable<Double> permits Vector3d {
    protected final double[] storage;
    
    protected VectorXd(double[] storage) {
        this.storage = requireNonNull(storage);
    }
    
    protected abstract T copy();
    protected T copy(IntToDoubleFunction function) {
        var copy = copy();
        for (int i = 0; i < storage.length; i++) {
            copy.storage[i] = function.applyAsDouble(i);
        }
        return copy;
    }
    
    
    public double get(int index) {
        verifyInRange(index);
        return storage[index];
    }
    
    public T with(int index, double value) {
        verifyInRange(index);
        var copy = copy();
        copy.storage[index] = value;
        return copy;
    }

    /// @return The dimension (number of components) of the vector.
    public int size() {
        return storage.length;
    }
    
    public T add(double value) {
        return copy(i -> this.storage[i] + value);
    }
    public T add(T other) {
        return copy(i -> this.storage[i] + other.storage[i]);
    }
    
    public T sub(double value) { 
        return copy(i -> this.storage[i] - value);
    }
    public T sub(T other) {
        return copy(i -> this.storage[i] - other.storage[i]);
    }
    
    public double dot(T other) {
        var sum = 0.0;
        for (int i = 0; i < storage.length; i++) {
            sum += this.storage[i] * other.storage[i];
        }
        return sum;
    }

    public T scalar(double value) {
        return copy(i -> this.storage[i] * value);
    }
    public T scalar(T other) {
        return copy(i -> this.storage[i] * other.storage[i]);
    }
    
    public T min(double value) {
        return copy(i -> Math.min(this.storage[i], value));
    }
    public T min(T other) {
        return copy(i -> Math.min(this.storage[i], other.storage[i]));
    }
    
    public T max(double value) {
        return copy(i -> Math.max(this.storage[i], value));
    }
    public T max(T other) {
        return copy(i -> Math.max(this.storage[i], other.storage[i]));
    }
    
    public T normalize() {
        var length = length();
        return copy(i -> this.storage[i] / length);
    }
    
    public double length() {
        var sum = 0.0;
        for (double v : storage) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    /**
     * Checks if the vector has NaN values.
     *
     * @return true if one value is NaN
     */
    public boolean isNaN() {
        return stream().anyMatch(Double::isNaN);
    }

    /**
     * Checks if the vector has infinite values.
     *
     * @return true if one value is infinite
     */
    public boolean isInfinite() {
        return stream().anyMatch(Double::isInfinite);
    }

    /**
     * Checks if the vector has NaN or infinite values.
     *
     * @return true if one value is NaN or infinite
     */
    public boolean isValid() {
        return !isNaN() && !isInfinite();
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof VectorXd<?> vectorXd
                && Arrays.equals(storage, vectorXd.storage);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(storage);
    }

    /// @return A string in the format `(c1, c2, ..., cn)`.
    @Override
    public String toString() {
        return stream()
                .mapToObj(Double::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    @Override
    public PrimitiveIterator.OfDouble iterator() {
        return new PrimitiveIterator.OfDouble() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < storage.length;
            }

            @Override
            public double nextDouble() {
                if (index < storage.length)
                    return storage[index++];
                else
                    throw new NoSuchElementException();
            }
        };
    }

    @Override
    public Spliterator.OfDouble spliterator() {
        return Arrays.spliterator(storage);
    }
    
    public DoubleStream stream() {
        return Arrays.stream(storage);
    }

    protected void verifyInRange(int index) {
        if (index < 0 || index >= storage.length)
            throw new IllegalArgumentException("Index %d is out of bounds for length %d".formatted(index, storage.length));
    }

    /// Verifies that the length of the given array matches the expected size
    /// of the vector.
    ///
    /// @param elements The array of elements to check.
    /// @param size The expected size.
    /// @throws IllegalArgumentException if the array length does not match the size.
    protected static void verifySize(double[] elements, int size) {
        if (elements.length != size) {
            throw new IllegalArgumentException(
                    String.format("Elements array size %d, doesn't match vector size %d", elements.length, size));
        }
    }

}
