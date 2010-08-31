package org.stajistics.data;

import java.util.Collection;

public interface FieldSetFactory {

    FieldSet newFieldSet(Field... fields);
    
    FieldSet newFieldSet(Collection<? extends Field> fields);
	
}
