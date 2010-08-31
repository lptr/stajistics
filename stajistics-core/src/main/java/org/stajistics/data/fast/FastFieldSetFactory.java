package org.stajistics.data.fast;

import java.util.Collection;

import org.stajistics.data.Field;
import org.stajistics.data.FieldSet;
import org.stajistics.data.FieldSetFactory;

public final class FastFieldSetFactory implements FieldSetFactory {
    
    private static final FieldSetFactory INSTANCE = new FastFieldSetFactory();

    @Override
    public FieldSet newFieldSet(Field... fields) {
        return new FastFieldSet(fields);
    }

    @Override
    public FieldSet newFieldSet(Collection<? extends Field> fields) {
        return new FastFieldSet(fields);
    }
    
    public static FieldSetFactory getInstance() {
        return INSTANCE;
    }
}
