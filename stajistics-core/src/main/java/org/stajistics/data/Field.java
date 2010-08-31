package org.stajistics.data;

public interface Field {
	
	public enum Type {
		LONG(Long.class), DOUBLE(Double.class);
		
		private Class<?> valueType;

		Type(Class<?> valueType) {
			this.valueType = valueType;
		}
		
		public Class<?> getValueType() {
			return valueType;
		}
	}

	String name();

	Type type();
	
	Object defaultValue();
}
