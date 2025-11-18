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

/// Base class for immutable float vectors of a specific size (N-dimensional).
/// This class provides common vector arithmetic operations. All modification
/// methods (like [#add(T)], [#scalar(T)], [#with(int,float)]) return a
/// **new** vector instance, preserving the immutability of the original.
///
/// @param <T> The concrete vector type that extends this base class.
public sealed abstract class VectorXf<T extends VectorXf<T>>
        implements Iterable<Float>
        permits Vector2f, Vector3f, Vector4f {
    /// The internal array holding the vector's components.
    protected final float[] storage;

    /// Constructs a new vector, initializing its internal storage with the
    /// provided array. The array is not copied; it is used directly as the
    /// vector's storage.
    ///
    /// @param storage The float array containing the vector components. Must not be null.
    protected VectorXf(float[] storage) {
        this.storage = requireNonNull(storage);
    }

    /// Creates a new, deep copy of this vector's concrete type.
    /// This is used by modification methods to ensure immutability.
    ///
    /// @return A new instance of the concrete vector type.
    protected abstract T copy();
    protected T copy(IntToFloatFunction function) {
        var copy = copy();
        for (int i = 0; i < storage.length; i++) {
            copy.storage[i] = function.applyAsFloat(i);
        }
        return copy;
    }


    /// Returns the component value at the specified index.
    ///
    /// @param index The index of the component to retrieve (0 to [#size()] - 1).
    /// @return The float value of the component.
    /// @throws ArrayIndexOutOfBoundsException if the index is out of range.
    public float get(int index) {
        return storage[index];
    }

    /// Returns a new vector with the component at the specified index set to the given value.
    /// This vector remains unchanged (immutability).
    ///
    /// @param index The index of the component to change.
    /// @param value The new float value for that component.
    /// @return A new vector instance with the modified component.
    /// @throws ArrayIndexOutOfBoundsException if the index is out of range.
    public T with(int index, float value) {
        var copy = copy();
        copy.storage[index] = value;
        return copy;
    }

    /// @return The dimension (number of components) of the vector.
    public int size() {
        return storage.length;
    }

    /// @param value The scalar float value to add.
    /// @return A new vector instance representing the sum.
    public T add(float value) {
        return copy(i -> this.storage[i] + value);
    }
    
    /// @param other The vector to add.
    /// @return A new vector instance representing the sum.
    public T add(T other) {
        return copy(i -> this.storage[i] + other.storage[i]);
    }

    /// @param value The scalar float value to subtract.
    /// @return A new vector instance representing the difference.
    public T sub(float value) {
        return copy(i -> this.storage[i] - value);
    }

    /// @param other The vector to subtract.
    /// @return A new vector instance representing the difference.
    public T sub(T other) {
        return copy(i -> this.storage[i] - other.storage[i]);
    }

    /// @param other The vector to multiply with.
    /// @return The dot product as a float.
    public float dot(T other) {
        var sum = 0f;
        for (int i = 0; i < storage.length; i++) {
            sum += this.storage[i] * other.storage[i];
        }
        return sum;
    }

    /// @param value The scalar float value to multiply by.
    /// @return A new vector instance representing the scaled vector.
    public T scalar(float value) {
        return copy(i -> this.storage[i] * value);
    }

    /// @param other The vector to multiply with.
    /// @return A new vector instance representing the component-wise product vector.
    public T scalar(T other) {
        return copy(i -> this.storage[i] * other.storage[i]);
    }

    /// @param value The scalar float value to compare against.
    /// @return A new vector instance with component-wise minimum values.
    public T min(float value) {
        return copy(i -> Math.min(this.storage[i], value));
    }

    /// @param other The vector to compare against.
    /// @return A new vector instance with component-wise minimum values.
    public T min(T other) {
        return copy(i -> Math.min(this.storage[i], other.storage[i]));
    }

    /// @param value The scalar float value to compare against.
    /// @return A new vector instance with component-wise maximum values.
    public T max(float value) {
        return copy(i -> Math.max(this.storage[i], value));
    }

    /// @param other The vector to compare against.
    /// @return A new vector instance with component-wise maximum values.
    public T max(T other) {
        return copy(i -> Math.max(this.storage[i], other.storage[i]));
    }

    /// Returns a new vector that has the same direction as this vector but a length of 1.
    /// If the current length is 0, the resulting vector is undefined (contains NaN or Inf).
    ///
    /// @return A new normalized vector instance.
    public T normalize() {
        var length = length();
        return copy(i -> this.storage[i] / length);
    }

    /// @return The  magnitude (Euclidean length) of the vector as a float.
    public float length() {
        var sum = 0.0;
        for (float v : storage) {
            sum += v * v;
        }
        return (float) Math.sqrt(sum);
    }

    /// @return true if one or more components are NaN.
    public boolean isNaN() {
        return stream().anyMatch(v -> v.isInfinite());
    }

    /// @return true if one or more components are infinite.
    public boolean isInfinite() {
        return stream().anyMatch(v -> v.isInfinite());
    }

    /// @return true if all components are finite and not NaN.
    public boolean isValid() {
        return !isNaN() && !isInfinite();
    }

    /// Compares this vector with another object for equality. Two vectors are considered
    /// equal if the other object is a `VectorXf` and their internal storage arrays
    /// (the component values) are equal.
    ///
    /// @param o The object to compare with.
    /// @return true if the vectors are equal, false otherwise.
    @Override
    public final boolean equals(Object o) {
        return o instanceof VectorXf<?> vectorXf
                && Arrays.equals(storage, vectorXf.storage);
    }

    /// @return A hash code for this vector, based on the hash code of its internal float array.
    @Override
    public int hashCode() {
        return Arrays.hashCode(storage);
    }

    /// @return A string in the format `(c1, c2, ..., cn)`.
    @Override
    public String toString() {
        return stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    /// @return An iterator that yields the components in order.
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

    /// @return A sequential [Stream] with the components of this vector as elements.
    public Stream<Float> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /// Verifies that the length of the given array matches the expected size
    /// of the vector.
    ///
    /// @param elements The array of elements to check.
    /// @param size The expected size.
    /// @throws IllegalArgumentException if the array length does not match the size.
    protected static void verifySize(float[] elements, int size) {
        if (elements.length != size) {
            throw new IllegalArgumentException(
                    String.format("Elements array size %d, doesn't match vector size %d", elements.length, size));
        }
    }
}
