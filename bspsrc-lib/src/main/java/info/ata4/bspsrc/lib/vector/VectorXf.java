/*
 ** 2012 August 29
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.bspsrc.lib.vector;

import info.ata4.bspsrc.common.util.IntToFloatFunction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * Base class for immutable float vectors of a specific size.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public sealed abstract class VectorXf<T extends VectorXf<T>>
        implements Iterable<Float>
        permits Vector2f, Vector3f, Vector4f {
    protected final float[] storage;

    protected VectorXf(float[] storage) {
        this.storage = requireNonNull(storage);
    }

    protected abstract T copy();
    protected T copy(IntToFloatFunction function) {
        var copy = copy();
        for (int i = 0; i < storage.length; i++) {
            copy.storage[i] = function.applyAsFloat(i);
        }
        return copy;
    }


    public float get(int index) {
        return storage[index];
    }

    public T with(int index, float value) {
        var copy = copy();
        copy.storage[index] = value;
        return copy;
    }
    
    public int size() {
        return storage.length;
    }

    public T add(float value) {
        return copy(i -> this.storage[i] + value);
    }
    public T add(T other) {
        return copy(i -> this.storage[i] + other.storage[i]);
    }

    public T sub(float value) {
        return copy(i -> this.storage[i] - value);
    }
    public T sub(T other) {
        return copy(i -> this.storage[i] - other.storage[i]);
    }

    public float dot(T other) {
        var sum = 0f;
        for (int i = 0; i < storage.length; i++) {
            sum += this.storage[i] * other.storage[i];
        }
        return sum;
    }

    public T scalar(float value) {
        return copy(i -> this.storage[i] * value);
    }
    public T scalar(T other) {
        return copy(i -> this.storage[i] * other.storage[i]);
    }

    public T min(float value) {
        return copy(i -> Math.min(this.storage[i], value));
    }
    public T min(T other) {
        return copy(i -> Math.min(this.storage[i], other.storage[i]));
    }

    public T max(float value) {
        return copy(i -> Math.max(this.storage[i], value));
    }
    public T max(T other) {
        return copy(i -> Math.max(this.storage[i], other.storage[i]));
    }

    public T normalize() {
        var length = length();
        return copy(i -> this.storage[i] / length);
    }

    public float length() {
        var sum = 0.0;
        for (float v : storage) {
            sum += v * v;
        }
        return (float) Math.sqrt(sum);
    }

    /**
     * Checks if the vector has NaN values.
     *
     * @return true if one value is NaN
     */
    public boolean isNaN() {
        return stream().anyMatch(v -> v.isInfinite());
    }

    /**
     * Checks if the vector has infinite values.
     *
     * @return true if one value is infinite
     */
    public boolean isInfinite() {
        return stream().anyMatch(v -> v.isInfinite());
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
        return o instanceof VectorXf<?> vectorXf
                && Arrays.equals(storage, vectorXf.storage);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(storage);
    }

    @Override
    public String toString() {
        return stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < storage.length;
            }

            @Override
            public Float next() {
                return storage[index++];
            }
        };
    }

    public Stream<Float> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    protected static void verifySize(float[] elements, int size) {
        if (elements.length != size) {
            throw new IllegalArgumentException(
                    String.format("Elements array size %d, doesn't match vector size %d", elements.length, size));
        }
    }
}
