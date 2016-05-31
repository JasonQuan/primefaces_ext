package com.primefaces.ext.component.column;

import javax.faces.component.FacesComponent;
import org.primefaces.component.column.Column;

/**
 *
 * @author Jason
 */
@FacesComponent("com.primefaces.ext.component.Column")
public class ExtColumn extends Column {

    protected enum PropertyKeys {
        exportSort,
        exportHeader;
        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public java.lang.Integer getExportSort() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.exportSort, 0);
    }

    public void setExportSort(int _styleClass) {
        getStateHelper().put(PropertyKeys.exportSort, _styleClass);
    }
}
