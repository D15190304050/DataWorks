package dataworks.mathematics;

import dataworks.collections.IReadOnlyCollection;
import dataworks.collections.LinkedList;
import dataworks.mathematics.geometry.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Vector implements Serializable
{
    private double[] vector;

    @Contract(pure = true)
    public int count()
    {
        return vector.length;
    }

    @Contract(pure = true)
    public Vector(int count)
    {
        if (count <= 0)
            throw new IllegalArgumentException("The length of a Vector must be a positive integer.");

        vector = new double[count];
    }

    @Contract("null -> fail")
    public Vector(double... vector)
    {
        validateVector(vector);

        this.vector = new double[vector.length];
        for (int i = 0; i < vector.length; i++)
            this.set(i, vector[i]);
    }

    @Contract("null, _, _ -> fail")
    public Vector(double[] vector, int startIndex, int endIndex)
    {
        validateVector(vector);

        if ((startIndex < 0) || (startIndex >= vector.length))
            throw new NullPointerException("startIndex is out of range.");
        if ((endIndex < 0) || (startIndex >= vector.length))
            throw new NullPointerException("endIndex is out of range.");

        if (endIndex <= startIndex)
            throw new NullPointerException("startIndex must be less than endIndex.");

        this.vector = new double[endIndex - startIndex + 1];
        for (int i = 0; i < this.vector.length; i++)
            this.vector[i] = vector[i + startIndex];
    }

    @Contract("null -> fail")
    public Vector(Vector vector)
    {
        validateVector(vector);

        this.vector = new double[vector.count()];
        for (int i = 0; i < vector.count(); i++)
            this.set(i, vector.get(i));
    }

    public double getLength()
    {
        double sum = 0;
        for (double v : vector)
            sum += v * v;

        return Math.sqrt(sum);
    }

    public double get(int index)
    {
        validateIndex(index);
        return vector[index];
    }

    public void set(int index, double value)
    {
        validateIndex(index);
        vector[index] = value;
    }

    public Vector getSubVector(int startIndex, int endIndex)
    {
        return new Vector(this.vector, startIndex, endIndex);
    }

    public Vector add(double scalar)
    {
        Vector result = new Vector(this.count());
        for (int i = 0; i < this.count(); i++)
            result.set(i, this.get(i) + scalar);

        return result;
    }

    public Vector add(Vector vector)
    {
        validateVector(vector);
        validateCount(vector);

        Vector result = new Vector(this.count());
        for (int i = 0; i < this.count(); i++)
            result.set(i, this.get(i) + vector.get(i));

        return result;
    }

    public Vector subtract(double scalar)
    {
        Vector result = new Vector(this.count());
        for (int i = 0; i < this.count(); i++)
            result.set(i, this.get(i) - scalar);

        return result;
    }

    public Vector subtract(Vector vector)
    {
        validateVector(vector);
        validateCount(vector);

        Vector result = new Vector(this.count());
        for (int i = 0; i < this.count(); i++)
            result.set(i, this.get(i) - vector.get(i));

        return result;
    }

    @NotNull
    public static Vector subtract(double scalar, Vector vector)
    {
        validateVector(vector);
        Vector result = new Vector(vector.count());
        for (int i = 0; i < result.count(); i++)
            result.set(i, scalar - vector.get(i));

        return result;
    }

    public Vector multiply(double scalar)
    {
        Vector result = new Vector(this.count());
        for (int i = 0; i < result.count(); i++)
            result.set(i, this.get(i) * scalar);

        return result;
    }

    public Vector elementWiseMultiply(Vector vector)
    {
        validateVector(vector);
        validateCount(vector);

        Vector result = new Vector(this.count());
        for (int i = 0; i < result.count(); i++)
            result.set(i, this.get(i) * vector.get(i));

        return result;
    }

    public double dot(Vector vector)
    {
        validateVector(vector);
        validateCount(vector);

        double sum = 0;
        for (int i = 0; i < this.count(); i++)
            sum += this.get(i) * vector.get(i);

        return sum;
    }

    /**
     * Returns the cross product of "ThisVector x TheGivenVector".
     *
     * @param vector
     * @return
     */
    public Vector cross(Vector vector)
    {
        // TODO: Implement cross product for high-dimensional vectors.

        if (this.vector.length != 3)
            throw new IllegalArgumentException("This operation only support 3-D vectors.");

        validateVector(vector);
        validateCount(vector);

        //  (x[0], x[1], x[2]) x (y[0], y[1], y[2])
        // =(x[1]y[2] - x[2]y[1], x[0]y[2] - x[2]y[0], x[0]y[1] - x[1]y[0])
        Vector result = new Vector(3);
        result.set(0, this.get(1) * vector.get(2) - this.get(2) * vector.get(1));
        result.set(1, this.get(2) * vector.get(0) - this.get(0) * vector.get(2));
        result.set(2, this.get(0) * vector.get(1) - this.get(1) * vector.get(0));

        return result;
    }

    public Vector divide(double scalar)
    {
        if (scalar == 0)
            throw new ArithmeticException("Dividend \"scalar\" cannot be 0.");

        Vector result = new Vector(this.count());
        for (int i = 0; i < result.count(); i++)
            result.set(i, this.get(i) / scalar);

        return result;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Vector))
            return false;

        return equals(((Vector) obj));
    }

    public boolean equals(Vector vector)
    {
        return equals(vector, Mathematics.getEpsilon());
    }

    public boolean equals(Vector vector, double epsilon)
    {
        if (vector == null)
            return false;
        if (this.count() != vector.count())
            return false;
        Mathematics.validateEpsilon(epsilon);

        for (int i = 0; i < this.count(); i++)
        {
            if (Math.abs(this.get(i) - vector.get(i)) > epsilon)
                return false;
        }

        return true;
    }

    private void validateIndex(int index)
    {
        if ((index < 0) || (index >= vector.length))
            throw new IndexOutOfBoundsException("Error index for vector operation: " + index);
    }

    public void validateCount(@NotNull Vector vector)
    {
        if (this.count() != vector.count())
            throw new IllegalArgumentException("Argument \"vector\" has different count with the object call this method.");
    }

    @NotNull
    @Contract("null -> fail")
    public static Vector[] pointsToVectors(Point[] points)
    {
        if (points == null)
            throw new NullPointerException("Argument \"points\" cannot be null.");

        Vector[] vectors = new Vector[points.length];
        for (int i = 0; i < vectors.length; i++)
            vectors[i] = new Vector(points[i].getX(), points[i].getY());

        return vectors;
    }

    @NotNull
    @Contract("null -> fail")
    public static Vector[] pointsToVectors(IReadOnlyCollection<Point> points)
    {
        if (points == null)
            throw new NullPointerException("Argument \"points\" cannot be null.");

        Vector[] vectors = new Vector[points.count()];
        int i = 0;
        for (Point point : points)
            vectors[i++] = new Vector(point.getX(), point.getY());

        return vectors;
    }

    @NotNull
    @Contract("null -> fail")
    public static Vector[] pointsToVectors(Iterable<Point> points)
    {
        if (points == null)
            throw new NullPointerException("Argument \"points\" cannot be null.");

        int count = 0;
        for (Point ignored : points)
            count++;

        Vector[] vectors = new Vector[count];
        int i = 0;
        for (Point point : points)
            vectors[i++] = new Vector(point.getX(), point.getY());

        return vectors;
    }

    public Vector normalize()
    {
        Vector normalizedVector = new Vector(this);
        double length = normalizedVector.getLength();
        for (int i = 0; i < normalizedVector.count(); i++)
            normalizedVector.vector[i] /= length;

        return normalizedVector;
    }

    @Override
    public String toString()
    {
        StringBuilder vectorString = new StringBuilder("[");

        for (int i = 0; i < vector.length - 1; i++)
            vectorString.append(vector[i]).append(", ");

        vectorString.append(vector[vector.length - 1]);
        vectorString.append("]");

        return vectorString.toString();
    }

    @Contract("null -> fail")
    public static void validateVector(Vector vector)
    {
        if (vector == null)
            throw new NullPointerException("Argument \"vector\" cannot be null.");
    }

    @Contract("null -> fail")
    public static void validateVector(double... vector)
    {
        if (vector == null)
            throw new NullPointerException("Argument \"vector\" cannot be null.");
    }
}
