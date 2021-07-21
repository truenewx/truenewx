package org.truenewx.tnxjee.core.spec;

import java.util.Objects;

/**
 * 平面尺寸
 */
public class FlatSize {

    private int width;
    private int height;

    public FlatSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlatSize other = (FlatSize) o;
        return this.width == other.width && this.height == other.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }

    @Override
    public String toString() {
        return "(w=" + this.width + ", h=" + this.height + ")";
    }
}
