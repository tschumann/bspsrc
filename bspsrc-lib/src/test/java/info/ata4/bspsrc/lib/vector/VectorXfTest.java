package info.ata4.bspsrc.lib.vector;

import info.ata4.io.DataReader;
import info.ata4.io.DataReaders;
import info.ata4.io.DataWriter;
import info.ata4.io.DataWriters;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

abstract class VectorXfTest<T extends VectorXf<T>> {

    protected final Random random = new Random();

    private float[] randomFloats(int size) {
        float[] floats = new float[size];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = random.nextFloat();
        }
        return floats;
    }
    private float[] randomFloats() {
        return randomFloats(size());
    }

    protected abstract int size();
    protected abstract T instantiate(float... elements);
    protected abstract float[] getElements(T vec);

    private T instantiateRandom() {
        return instantiate(randomFloats());
    }

    protected void verifySize(float[] elements) {
        if (elements.length != size()) {
            throw new IllegalArgumentException("elements array length must be equal to size");
        }
    }

    abstract class AbstractConstructors {

        private abstract class AbstractConstructor {

            protected abstract T create(float[] elements);
            protected abstract float[] getNewElements();

            @DisplayName("Test constructor instantiation")
            @Test
            void testConstructorInstantiation() {
                float[] elements = getNewElements();
                T vec = create(elements);

                float[] actualElements = getElements(vec);
                for (int i = 0; i < actualElements.length; i++) {
                    assertEquals(elements[i], actualElements[i],
                            "Actual value for component " + i + " doesn't match expected");
                }
                assertEquals(size(), vec.size(),
                        "Actual size of the instantiated vector doesn't match expected");
            }
        }

        abstract class AbstractConstructorNormal extends AbstractConstructors.AbstractConstructor {

            protected abstract T constructor(float[] elements);

            @Override
            protected T create(float[] elements) {
                return constructor(elements);
            }

            @Override
            protected float[] getNewElements() {
                return randomFloats();
            }
        }

        abstract class AbstractConstructorArray extends AbstractConstructors.AbstractConstructor {

            protected abstract T constructor(float[] elements);

            @Override
            protected T create(float[] elements) {
                return constructor(elements);
            }

            @Override
            protected float[] getNewElements() {
                return randomFloats();
            }

            @DisplayName("Fail on to few elements")
            @Test
            void testFailToFewElements() {
                assertThrows(IllegalArgumentException.class, () -> constructor(randomFloats(size() - 1)));
            }

            @DisplayName("Fail on null")
            @Test
            void testFailOnNull() {
                assertThrows(NullPointerException.class, () -> constructor(null));
            }
        }
    }

    abstract class AbstractProperties {

        @Nested
        class Getter {

            @DisplayName("Test gettings values")
            @Test
            void testValues() {
                float[] actualElements = randomFloats();
                T vec = instantiate(actualElements);

                float[] elements = new float[vec.size()];
                for (int i = 0; i < vec.size(); i++) {
                    elements[i] = vec.get(i);
                }

                assertArrayEquals(actualElements, elements);
            }

            @DisplayName("Fail on out of range index")
            @Test
            void testFailOnIllegalIndex() {
                assertThrows(IllegalArgumentException.class, () -> instantiateRandom().get(-1));
                assertThrows(IllegalArgumentException.class, () -> instantiateRandom().get(size()));
            }
        }

        @Nested
        class Setter {

            @DisplayName("Test setting values")
            @Test
            void testValues() {
                float[] elements = randomFloats();
                T vec = instantiate(elements);

                for (int i = 0; i < vec.size(); i++) {
                    elements[i] = random.nextFloat();
                    vec = vec.with(i, elements[i]);
                    assertArrayEquals(elements, getElements(vec));
                }
            }

            @DisplayName("Test immutability")
            @Test
            void testImmutability() {
                T vec = instantiateRandom();

                for (int i = 0; i < vec.size(); i++) {
                    T newVec = vec.with(i, random.nextFloat());
                    assertNotSame(newVec, vec);
                    vec = newVec;
                }
            }

            @DisplayName("Fail on out of range index")
            @Test
            void testFailOnIllegalIndex() {
                assertThrows(IllegalArgumentException.class, () -> instantiateRandom().with(-1, 0));
                assertThrows(IllegalArgumentException.class, () -> instantiateRandom().with(size(), 0));
            }
        }

        @Nested
        class ElementIterator {

            @DisplayName("Test values matching")
            @Test
            void testValuesMatch() {
                float[] elements = randomFloats();
                T vec = instantiate(elements);

                Iterator<Float> iterator = vec.iterator();

                for (int i = 0; iterator.hasNext(); i++) {
                    assertEquals(elements[i], iterator.next(), "Element " + i + " doesn't match");
                }
            }

            @DisplayName("Fail on iterator.remove()")
            @Test
            void testFailOnRemove() {
                assertThrows(UnsupportedOperationException.class, () -> instantiateRandom().iterator().remove());
            }

            @DisplayName("Fail on iterator.next() when iterator.hasNext() == false")
            @Test
            void testFailOnToManyNext() {
                Iterator<Float> iterator = instantiateRandom().iterator();
                while (iterator.hasNext()) {
                    iterator.next();
                }

                assertThrows(NoSuchElementException.class, iterator::next);
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractLength {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec, float expectedLength, float epsilon) {
                    args = new Object[]{vec, expectedLength, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSamplesProvider();

            @DisplayName("Test length")
            @ParameterizedTest(name = "[{index}] {0} has length of {1} with epsilon {2}")
            @MethodSource("testSamplesProvider")
            void testLength(T vec, float expectedLength, float epsilon) {
                assertEquals(expectedLength, vec.length(), epsilon);
            }
        }

        @DisplayName("Test isNan")
        @Test
        void testIsNan() {
            T vec = instantiateRandom();
            assertFalse(vec.isNaN(), "isNan() should return false on " + vec);

            for (int i = 0; i < vec.size(); i++) {
                float[] elements = randomFloats(vec.size());
                elements[i] = Float.NaN;
                vec = instantiate(elements);
                assertTrue(vec.isNaN(), "isNan() should return true on " + vec);
            }
        }

        @DisplayName("Test isInfinite")
        @Test
        void testIsInfinite() {
            T vec = instantiateRandom();
            assertFalse(vec.isInfinite(), "isInfinite() should return false on " + vec);

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < vec.size(); j++) {
                    float[] elements = randomFloats(vec.size());
                    elements[j] = i == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
                    vec = instantiate(elements);
                    assertTrue(vec.isInfinite(), "isInfinite() should return true on " + vec);
                }
            }
        }

        @DisplayName("Test isValid")
        @Test
        void testIsValid() {
            T vec = instantiateRandom();
            assertTrue(vec.isValid(), "isValid() should return false on " + vec);

            float[] values = new float[]{Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < vec.size(); j++) {
                    float[] elements = randomFloats(vec.size());
                    elements[j] = values[i];
                    vec = instantiate(elements);
                    assertFalse(vec.isValid(), "isValid() should return false on " + vec);
                }
            }
        }

        @DisplayName("Equals/Hashcode")
        @Nested
        class EqualsHashcode {
            
            @DisplayName("Test equals")
            @TestFactory
            Stream<DynamicNode> testEquals() {
                float[] elements = randomFloats();
                T vec = instantiate(elements);

                return Stream.of(
                        testForEqualsData(true, vec, vec),
                        testForEqualsData(false, vec, null),
                        testForEqualsData(false, vec, new Object()),
                        testForEqualsData(true, vec, instantiate(elements)),
                        testForEqualsData(false, vec, instantiate(randomFloats()))
                );
            }

            private DynamicTest testForEqualsData(boolean equals, VectorXf<?> expected, Object actual) {
                return DynamicTest.dynamicTest(expected + (equals ? " == " : " != ") + actual, () -> {
                    if (equals) {
                        assertEquals(expected, actual, expected + " should be equal to " + actual);
                    } else {
                        assertNotEquals(expected, actual, expected + " should not be equal to " + actual);
                    }
                });
            }


            @DisplayName("Test equals to hashcode consistency")
            @TestFactory
            Stream<DynamicNode> testEqualsHashcodeConsistency() {
                float[] elements = randomFloats();
                T vec = instantiate(elements);

                return Stream.of(
                        testForEqualsHashConsistencyData(true, vec, vec),
                        testForEqualsHashConsistencyData(true, vec, instantiate(elements)),
                        testForEqualsHashConsistencyData(false, vec, instantiate(randomFloats(vec.size())))
                );
            }

            private DynamicTest testForEqualsHashConsistencyData(boolean equals, VectorXf<?> expected, Object actual) {
                return DynamicTest.dynamicTest(expected + (equals ? " == " : " != ") + actual, () -> {
                    if (equals) {
                        assertEquals(expected.hashCode(), actual.hashCode(),
                                expected + " hashcode should be equal to " + actual + " hashcode");
                    } else {
                        assertNotEquals(expected.hashCode(), actual.hashCode(),
                                expected + " hashcode should not be equal to " + actual + " hashcode");
                    }
                });
            }
        }
    }

    abstract class AbstractOperations {

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractDotProduct {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec0, T vec1, float expectedDot, float epsilon) {
                    args = new Object[]{vec0, vec1, expectedDot, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSampleProvider();

            @DisplayName("Test value")
            @ParameterizedTest(name = "[{index}] {0} * {1} == {2} with epsilon {3}")
            @MethodSource("testSampleProvider")
            void test(T vec0, T vec1, float expectedDot, float epsilon) {
                assertEquals(expectedDot, vec0.dot(vec1), epsilon);
            }

            @DisplayName("Fail on null argument")
            @Test
            void testFailOnNull() {
                assertThrows(NullPointerException.class, () -> instantiateRandom().dot(null));
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractNormalize {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec, T expectedNormalized, float epsilon) {
                    args = new Object[]{vec, expectedNormalized, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSampleProvider();

            @DisplayName("Test values")
            @ParameterizedTest(name = "[{index}] {0} normalized is {1} with epsilon {2}")
            @MethodSource("testSampleProvider")
            void testNormalizeValues(T vec, T expectedNormalized, float epsilon) {
                float[] elements = getElements(vec.normalize());
                float[] expectedElements = getElements(expectedNormalized);

                assertArrayEquals(expectedElements, elements, epsilon);
            }

            @DisplayName("Test immutability")
            @Test
            void testImmutability() {
                T vec = instantiateRandom();
                assertNotSame(vec.normalize(), vec);
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractAdd {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec, T otherVec, T expectedSum, float epsilon) {
                    args = new Object[]{vec, otherVec, expectedSum, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSampleProvider();

            @DisplayName("Test values")
            @ParameterizedTest(name = "[{index}] {0} add {1} is {2} with epsilon {3}")
            @MethodSource("testSampleProvider")
            void testValues(T vec, T otherVec, T expectedSum, float epsilon) {
                float[] elements = getElements(vec.add(otherVec));
                float[] expectedElements = getElements(expectedSum);

                assertArrayEquals(expectedElements, elements, epsilon);
            }

            @DisplayName("Test immutability")
            @Test
            void testImmutability() {
                T vec = instantiateRandom();
                assertNotSame(vec.add(vec), vec);
            }

            @DisplayName("Fail on null argument")
            @Test
            void testFailOnNull() {
                assertThrows(NullPointerException.class, () -> instantiateRandom().add(null));
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractSubtract {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec, T otherVec, T expectedDifference, float epsilon) {
                    args = new Object[]{vec, otherVec, expectedDifference, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSampleProvider();

            @DisplayName("Test values")
            @ParameterizedTest(name = "[{index}] {0} subtract {1} is {2} with epsilon {3}")
            @MethodSource("testSampleProvider")
            void testValues(T vec, T otherVec, T expectedDifference, float epsilon) {
                float[] elements = getElements(vec.sub(otherVec));
                float[] expectedElements = getElements(expectedDifference);

                assertArrayEquals(expectedElements, elements, epsilon);
            }

            @DisplayName("Test immutability")
            @Test
            void testImmutability() {
                T vec = instantiateRandom();
                assertNotSame(vec.sub(vec), vec);
            }

            @DisplayName("Fail on null argument")
            @Test
            void testFailOnNull() {
                assertThrows(NullPointerException.class, () -> instantiateRandom().sub(null));
            }
        }

        abstract class AbstractScalar {

            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            abstract class AbstractFloat {

                protected class TestSample implements Arguments {

                    private final Object[] args;

                    public TestSample(T vec, float scalar, T expectedVec, float epsilon) {
                        args = new Object[]{vec, scalar, expectedVec, epsilon};
                    }

                    @Override
                    public Object[] get() {
                        return args;
                    }
                }

                protected abstract Stream<TestSample> testSampleProvider();

                @DisplayName("Test values")
                @ParameterizedTest(name = "[{index}] {0} scalar {1} is {2} with epsilon {3}")
                @MethodSource("testSampleProvider")
                void testValues(T vec, float scalar, T expectedVec, float epsilon) {
                    float[] elements = getElements(vec.scalar(scalar));
                    float[] expectedElements = getElements(expectedVec);

                    assertArrayEquals(expectedElements, elements, epsilon);
                }

                @DisplayName("Test immutability")
                @Test
                void testImmutability() {
                    T vec = instantiateRandom();
                    assertNotSame(vec.scalar(1), vec);
                }
            }

            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            abstract class AbstractVector {

                protected class TestSample implements Arguments {

                    private final Object[] args;

                    public TestSample(T vec, T scalar, T expectedVec, float epsilon) {
                        args = new Object[]{vec, scalar, expectedVec, epsilon};
                    }

                    @Override
                    public Object[] get() {
                        return args;
                    }
                }

                protected abstract Stream<TestSample> testSampleProvider();

                @DisplayName("Test values")
                @ParameterizedTest(name = "[{index}] {0} scalar {1} is {2} with epsilon {3}")
                @MethodSource("testSampleProvider")
                void testValues(T vec, T scalar, T expectedVec, float epsilon) {
                    float[] elements = getElements(vec.scalar(scalar));
                    float[] expectedElements = getElements(expectedVec);

                    assertArrayEquals(expectedElements, elements, epsilon);
                }

                @DisplayName("Test immutability")
                @Test
                void testImmutability() {
                    T vec = instantiateRandom();
                    assertNotSame(vec.scalar(vec), vec);
                }

                @DisplayName("Fail on null argument")
                @Test
                void testFailOnNull() {
                    assertThrows(NullPointerException.class, () -> instantiateRandom().scalar(null));
                }
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractMin {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec, T otherVec, T expectedVec, float epsilon) {
                    args = new Object[]{vec, otherVec, expectedVec, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSampleProvider();

            @DisplayName("Test values")
            @ParameterizedTest(name = "[{index}] {0} scalar {1} is {2} with epsilon {3}")
            @MethodSource("testSampleProvider")
            void testValues(T vec, T otherVec, T expectedVec, float epsilon) {
                float[] elements = getElements(vec.min(otherVec));
                float[] expectedElements = getElements(expectedVec);

                assertArrayEquals(expectedElements, elements, epsilon);
            }

            @DisplayName("Test immutability")
            @Test
            void testImmutability() {
                T vec = instantiateRandom();
                assertNotSame(vec.min(instantiateRandom()), vec);
            }

            @DisplayName("Fail on null argument")
            @Test
            void testFailOnNull() {
                assertThrows(NullPointerException.class, () -> instantiateRandom().min(null));
            }
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        abstract class AbstractMax {

            protected class TestSample implements Arguments {

                private final Object[] args;

                public TestSample(T vec, T otherVec, T expectedVec, float epsilon) {
                    args = new Object[]{vec, otherVec, expectedVec, epsilon};
                }

                @Override
                public Object[] get() {
                    return args;
                }
            }

            protected abstract Stream<TestSample> testSampleProvider();

            @DisplayName("Test values")
            @ParameterizedTest(name = "[{index}] {0} scalar {1} is {2} with epsilon {3}")
            @MethodSource("testSampleProvider")
            void testValues(T vec, T otherVec, T expectedVec, float epsilon) {
                float[] elements = getElements(vec.max(otherVec));
                float[] expectedElements = getElements(expectedVec);

                assertArrayEquals(expectedElements, elements, epsilon);
            }

            @DisplayName("Test immutability")
            @Test
            void testImmutability() {
                T vec = instantiateRandom();
                assertNotSame(vec.max(instantiateRandom()), vec);
            }

            @DisplayName("Fail on null argument")
            @Test
            void testFailOnNull() {
                assertThrows(NullPointerException.class, () -> instantiateRandom().max(null));
            }
        }
    }

    abstract class AbstractIO {

        protected abstract T read(DataReader dataReader) throws IOException;
        protected abstract void write(DataWriter dataWriter, T vec) throws IOException;

        @DisplayName("Test read vector with DataReader")
        @Test
        void testRead() throws IOException {
            float[] expectedElements = randomFloats();

            ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES * size());
            buffer.asFloatBuffer().put(expectedElements);

            try (DataReader dataReader = DataReaders.forByteBuffer(buffer)) {
                T vec = read(dataReader);
                float[] elements = getElements(vec);
                assertArrayEquals(expectedElements, elements);
            }
        }

        @DisplayName("Test write vector with DataWriter")
        @Test
        void testWrite() throws IOException {
            float[] expectedElements = randomFloats();

            ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES * size());

            try (DataWriter dataWriter = DataWriters.forByteBuffer(buffer)) {
                write(dataWriter, instantiate(expectedElements));

                float[] elements = new float[size()];
                buffer.flip();
                buffer.asFloatBuffer().get(elements);
                assertArrayEquals(expectedElements, elements);
            }
        }
    }
}
