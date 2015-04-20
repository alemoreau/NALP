package com.enseirb.alemoreau.nalp;

/**
 * Created by Alexandre on 12/04/2015.
 */

public class Halfedge
{
    Halfedge ELleft, ELright;
    Edge ELedge;
    boolean deleted;
    int ELpm;
    Site vertex;
    double ystar;
    Halfedge PQnext;

    public Halfedge()
    {
        PQnext = null;
    }
}
