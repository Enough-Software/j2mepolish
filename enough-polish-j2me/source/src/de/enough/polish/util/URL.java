package de.enough.polish.util;

/**
 * <p>Helps to deal with HTTP GET URLs.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 */
public class URL {
	
	String host;
	
	String path;
	
	HashMap parameters;
	
	String result;
	
	boolean needsBuild = true;
	
	public URL(String url) {
		this(url,null);
	}
	
	public URL(String host, String path) {
		this.host = host;
		this.path = path;
		this.parameters = new HashMap();
	}
	
	public String build() {
		if(this.needsBuild) {
			StringBuffer buffer = new StringBuffer();
			StringBuffer paramsBuffer = new StringBuffer();
			
			buffer.append(this.host);
			
			if(this.path != null) {
				buffer.append(this.path);
			}
			
			if(this.parameters.size() > 0) {
				
				paramsBuffer.append('?');
				
				Object[] keys = this.parameters.keys();
				for (int i = 0; i < keys.length; i++) {
					Object key = keys[i];
					Object value = this.parameters.get(key);
					
					paramsBuffer.append(key);
					paramsBuffer.append('=');
					paramsBuffer.append(TextUtil.encodeUrl(value.toString()));
					
					if(i < keys.length - 1) {
						paramsBuffer.append('&');
					}
				}
				
				String params =  paramsBuffer.toString();
				
				buffer.append(params);
				
				this.result = buffer.toString();
				
			} 
			
			this.result = buffer.toString();
			 
			this.needsBuild = false;
		}
		
		return this.result; 
	}
	
	public URL addParameters(HashMap params) {
		Object[] keys = params.keys();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object value = params.get(key);
			
			addParameter(key, value);
		}
		
		return this;
	}
	
	public HashMap getParameters() {
		return this.parameters;
	}
	
	public URL addParameter(String key, String value) {
		addParameter((Object)key, (Object)value);
		return this;
	}
	
	public URL addParameter(String key, int value) {
		addParameter(key, new Integer(value));
		return this;
	}
	
	public URL addParameter(String key, boolean value) {
		addParameter(key, new Boolean(value));
		return this;
	}
	
	//#if polish.hasFloatingPoint
	public URL addParameter(String key, double value) {
		addParameter(key, new Double(value));
		return this;
	}
	//#endif

	//#if polish.hasFloatingPoint
	public URL addParameter(String key, float value) {
		addParameter(key, new Float(value));
		return this;
	}
	//#endif

	void addParameter(Object key, Object value) {
		this.parameters.put(key, value);
		this.needsBuild = true;
	}
	
	public URL clearParameters() {
		this.parameters.clear();
		this.needsBuild = true;
		return this;
	}
	
	public String toString() {
		return build();
	}
}
