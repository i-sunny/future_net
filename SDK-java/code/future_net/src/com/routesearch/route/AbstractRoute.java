package com.routesearch.route;

import com.routesearch.model.Graph;
import com.routesearch.model.Path;

/**
 * Created by sunny on 4/4/16.
 */
public abstract class AbstractRoute {
    protected Graph graph;

    public abstract Path searchRoute();
}
