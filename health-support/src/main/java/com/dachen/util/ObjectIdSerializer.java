package com.dachen.util;

import java.io.IOException;
import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

public class ObjectIdSerializer implements ObjectSerializer {

//	@Override
	public void write(JSONSerializer serializer, Object object,
			Object fieldName, Type fieldType) throws IOException {
		SerializeWriter out = serializer.getWriter();
		if (object == null) {
			serializer.getWriter().writeNull();
			return;
		}
		out.write("\"" + ((ObjectId) object).toString() + "\"");
	}

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName,
			Type fieldType, int features) throws IOException {
		write(serializer,object,fieldName,fieldType);
		
	}
}
