/*
 * Copyright 2011-2015 PrimeFaces Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.primefaces.extensions.showcase.util;

import javax.faces.context.FacesContext;
import java.io.InputStream;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * ShowcaseUtil
 *
 * @author Pavol Slany / last modified by $Author$
 * @version $Revision$
 */
@ManagedBean
@ViewScoped
public class ShowcaseUtil {

    public String getFileContent(final String fullPathToFile) {
        try {
            // Finding in WEB ...
            FacesContext fc = FacesContext.getCurrentInstance();
            InputStream is = fc.getExternalContext().getResourceAsStream(fullPathToFile);
            if (is != null) {
                return FileContentMarkerUtil.readFileContent(fullPathToFile, is);
            }

            // Finding in ClassPath ...
            is = ShowcaseUtil.class.getResourceAsStream(fullPathToFile);
            if (is != null) {
                return FileContentMarkerUtil.readFileContent(fullPathToFile, is);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Internal error: file " + fullPathToFile + " could not be read", e);
        }

        return "";
    }
}
