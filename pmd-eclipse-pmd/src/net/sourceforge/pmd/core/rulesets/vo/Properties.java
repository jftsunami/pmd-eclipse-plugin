/*
 * Created on 17 juin 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.core.rulesets.vo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is a value objet that composes the structure of a rulesets object.
 * It holds a collection of Property objects.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/21 23:06:41  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 * Revision 1.2  2006/06/20 21:26:42  phherlin
 * Fix/review PMD violations
 *
 * Revision 1.1  2006/06/18 22:33:02  phherlin
 * Begin to implement a new model for the plugin to handle rules and rulesets.
 *
 * 
 */

public class Properties {
    private Set propertiesSet = new HashSet();

    /**
     * Getter for the propertiesSet.
     * 
     * @return Returns the propertiesSet.
     */
    public Set getProperties() {
        return this.propertiesSet;
    }

    /**
     * Setter for the propertiesSet
     * 
     * @param propertiesSet The propertiesSet to set.
     */
    public void setProperties(Set properties) {
        if (properties == null) {
            throw new IllegalArgumentException("propertiesSet cannot be null");
        }

        this.propertiesSet = properties;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        boolean equal = false;
        
        if (arg0 instanceof Properties) {
            final Properties p = (Properties) arg0;
            equal = this.propertiesSet.equals(p.propertiesSet);
        }
        
        return equal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.propertiesSet.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer("Properties");
        for (final Iterator i = this.propertiesSet.iterator(); i.hasNext();) {
            final Property p = (Property) i.next();
            buffer.append(' ');
            buffer.append(p);
        }
        
        return buffer.toString();
    }

}
