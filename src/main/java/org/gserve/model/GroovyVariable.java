package org.gserve.model;
/*
 *  Copyright (C) 2019 Dustin K. Redmond
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

/**
 * Created: 12/16/2019 15:31
 * Author: Dustin K. Redmond
 */
public class GroovyVariable {
    private int id;
    private String variable;
    private String value;

    public GroovyVariable () {}
    public GroovyVariable(int id, String variable, String value) {
        this.id = id;
        this.variable = variable;
        this.value = value;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getVariable() { return this.variable; }
    public void setVariable(String variable) { this.variable = variable; }
    public String getValue() { return this.value; }
    public void setValue(String value) { this.value = value; }
}
