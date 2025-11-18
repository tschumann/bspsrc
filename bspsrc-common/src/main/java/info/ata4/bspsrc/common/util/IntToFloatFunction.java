package info.ata4.bspsrc.common.util;

@FunctionalInterface
public interface IntToFloatFunction {
    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    float applyAsFloat(int value);
}
