package org.gserve.api.groovy;
/*
 *  Copyright (C) 2020 Dustin K. Redmond
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

import groovy.lang.GroovyClassLoader;
import org.gserve.model.GroovyScript;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Dustin K. Redmond
 * @since 03/02/2020 14:47
 */
public class GroovyLibrary {
    /**
     * Attempts to load an instance of a GroovyScript that exists in the database. This allows multiple
     * scripts to use the same code as an existing <i>library</i> script.
     * @param className Name of a GroovyScript that exists.
     * @return An instance of the script's class if it exists and has a public constructor.
     */
    @SuppressWarnings("unchecked")
    public static Object get(String className) {
        GroovyScript gs = GroovyScript.getByClassName(className);
        if (gs != null) {
            try {
                return new GroovyClassLoader().parseClass(gs.getCode()).getDeclaredConstructor().newInstance();
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new UnsupportedOperationException(
                        String.format("Class %s must declare a default public constructor.", gs.getClass()));
            }
        }
        return null;
    }
}
