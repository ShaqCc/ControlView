package com.example.aaa.myapplication.demo;

/****************************************
 * 功能说明:  
 *
 * Author: Created by bayin on 2017/8/24.
 ****************************************/

public class Cubic {

    float a,b,c,d;         /* a + b*u + c*u^2 +d*u^3 */

    public Cubic(float a, float b, float c, float d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }


    /** evaluate cubic */
    public float eval(float u) {
        return (((d*u) + c)*u + b)*u + a;
    }
}
