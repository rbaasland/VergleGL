package com.dbz.framework.math;

import android.util.FloatMath;

public class Circle {
    public final Vector2 center = new Vector2();
    public float radius;

    public Circle(float x, float y, float radius) {
        this.center.set(x,y);
        this.radius = radius;
    }
    
    /**
    genVerticesInCircle
    Generates a set of vertices on the circumference
    of the circle given by the radius.

    @param circleRadius - radius of circle for points to be assigned
    @param numVertices - number of vertices present on the circle
    @return set of vertexes on circle's circumference
    @pre numVertices >= 1
    @post set of vertexes returned as float array */
    public static Vector2[] genVerticesInCircle(Vector2 center, float circleRadius, int numVertices){

        Vector2[] vertexArray = new Vector2[numVertices];
        float angleDifference = 360.f/(float)(numVertices);
        float currAngle = angleDifference;

        for (int i = 0; i < numVertices; i++) {
            vertexArray[i] = new Vector2(
            		circleRadius * FloatMath.cos(currAngle*Vector2.TO_RADIANS) + center.x,
            		circleRadius * FloatMath.sin(currAngle*Vector2.TO_RADIANS) + center.y);

            currAngle += angleDifference;
        }

        return vertexArray;
    }
}
