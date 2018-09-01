package com.ypq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Event {
	private Object object;
	private String methodName;
	private Object[] params;
	private Class<?>[] paramTypes;
	
	public Event() {
		
	}
	
	public Event(Object object, String methodName, Object...args) {
		this.object = object;
		this.methodName = methodName;
		this.params = args;
		contractParamTypes(this.params);
	}
	
	private void contractParamTypes(Object[] params) {
		this.paramTypes = new Class[params.length];
		for(int i = 0; i < params.length; i++) {
			this.paramTypes[i] = params[i].getClass();
		}
	}
	
	public void invoke() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = object.getClass().getMethod(this.getMethodName(), this.getParamTypes());
		if(method == null) {
			return;
		}
		method.invoke(this.getObject(), this.getParams());
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public String getMethodName() {
		return this.methodName;
	}
	
	public Object[] getParams() {
		return this.params;
	}
	
	public Class<?>[] getParamTypes() {
		return this.paramTypes;
	}
	
	
}
