// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// 3 May 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to mark the
// project as needing saving if a property is set.


package org.argouml.uml.ui;

import java.lang.reflect.*;

import org.apache.log4j.Logger;
import org.argouml.kernel.*;

/**
 * @deprecated as of ArgoUml 0.13.5 (10-may-2003),
 * TODO: What is this replaced by?,
 * this class is part of the 'old'(pre 0.13.*) implementation of proppanels
 * that used reflection a lot.
 */
public class UMLReflectionBooleanProperty extends UMLBooleanProperty {
    /**
     * @deprecated by Linus Tolke as of 0.15.4. Use your own logger in your
     * class. This will be removed.
     */
    protected static Logger cat =
	Logger.getLogger(UMLReflectionBooleanProperty.class);

    private Method _getMethod;
    private Method _setMethod;
    private Class _objectClass;
    private static final Object[] _noArg = {};
    private static final Object[] _trueArg = {
	new Boolean(true) 
    };
    private static final Object[] _falseArg = {
	new Boolean(false) 
    };
    
    /**
     * Creates new BooleanChangeListener.<p>
     */
    public UMLReflectionBooleanProperty(String propertyName,
					Class elementClass,
					String getMethod, String setMethod) {
        super(propertyName);

	_objectClass = elementClass;

        Class[] noClass = {};
        try {
            _getMethod = elementClass.getMethod(getMethod, noClass);
        }
        catch (Exception e) {
            cat.error(e.toString()
		      + " in UMLReflectionBooleanProperty(): "
		      + getMethod,
		      e);
            cat.error("Going to rethrow as RuntimeException");
	    // need to throw exception again for unit testing!
	    throw new RuntimeException(e.toString());
        }
        Class[] boolClass = {
	    boolean.class 
	};
        try {
            _setMethod = elementClass.getMethod(setMethod, boolClass);
        }
        catch (Exception e) {
            cat.error(e.toString()
		      + " in UMLReflectionBooleanProperty(): "
		      + setMethod,
		      e);
	    cat.error("Going to rethrow as RuntimeException");
	    // need to throw exception again for unit testing!
	    throw new RuntimeException(e.toString());
        }
    }
    
    
    public void setProperty(Object element, boolean newState) {
        if (_setMethod != null && element != null) {
            try {
                //
                //   this allows enumerations to work properly
                //      if newState is false, it won't override
                //      a different enumeration value
                boolean oldState = getProperty(element);
                if (newState != oldState) {
                    if (newState) {
                        _setMethod.invoke(element, _trueArg);
                    }
                    else {
                        _setMethod.invoke(element, _falseArg);
                    }
                    
                    // Having set a property, mark as needing saving

                    Project p = ProjectManager.getManager().getCurrentProject();
                    p.setNeedsSave(true);
                }
            }
            catch (Exception e) {
                cat.error(e.toString()
			  + " in UMLReflectionBooleanProperty.setMethod()",
			  e);
            }
        }
    }
    
    public boolean getProperty(Object element) {
        boolean state = false;
        if (_getMethod != null && element != null
	    && _objectClass.isAssignableFrom(element.getClass())) {
            try {
                Object retval = _getMethod.invoke(element, _noArg);
                if (retval != null && retval instanceof Boolean) {
                    state = ((Boolean) retval).booleanValue();
                }
            }
            catch (Exception e) {
                cat.error(e.toString()
			  + " in UMLReflectionBooleanProperty.getMethod()",
			  e);
            }
        }
        return state;
    }
    
}


