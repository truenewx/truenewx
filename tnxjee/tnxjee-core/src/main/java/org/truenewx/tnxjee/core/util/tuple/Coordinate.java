package org.truenewx.tnxjee.core.util.tuple;

/**
 * 坐标
 *
 * @author jianglei
 * 
 */
public class Coordinate {
    /**
     * x轴坐标
     */
    private double x;

    /**
     * y轴坐标
     */
    private double y;

    public Coordinate() {
    }

    /**
     * @param x x轴坐标
     * @param y y轴坐标
     */
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x轴坐标
     * @author jianglei
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return y轴坐标
     * @author jianglei
     */
    public double getY() {
        return this.y;
    }

    /**
     * @param x x轴坐标
     * @author jianglei
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @param y y轴坐标
     * @author jianglei
     */
    public void setY(double y) {
        this.y = y;
    }

}
