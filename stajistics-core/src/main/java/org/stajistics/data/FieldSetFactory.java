package org.stajistics.data;

import java.io.Serializable;
import java.util.Collection;

public interface FieldSetFactory extends Serializable {

    FieldSet newFieldSet(Field... fields);
    
    FieldSet newFieldSet(Collection<? extends Field> fields);
	
}
