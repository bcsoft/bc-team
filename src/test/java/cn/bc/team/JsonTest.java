package cn.bc.team;

import org.junit.Assert;
import org.junit.Test;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by dragon on 2014/11/7.
 */
public class JsonTest {
	String jsonString = "{\"str\":\"a\",\"num\":1,\"bool\":false,\"k\":null}";
	String jsonArrayString = "[{\"id\":1,\"str\":\"a\"},{\"id\":2,\"str\":\"b\"},null]";

	// 创建json对象
	@Test
	public void createJsonObject() {
		JsonObject json = Json.createObjectBuilder()
			.add("str", "a")
			.add("num", 1)
			.add("bool", false)
			.addNull("k")
			.build();
		//System.out.println(json.getClass());
		Assert.assertEquals(jsonString, json.toString());
	}

	// 创建json数组
	@Test
	public void createJsonArray() {
		JsonArray jsonArray = Json.createArrayBuilder()
			.add(Json.createObjectBuilder()
				.add("id", 1)
				.add("str", "a"))
			.add(Json.createObjectBuilder()
				.add("id", 2)
				.add("str", "b"))
			.addNull()
			.build();
		//System.out.println(jsonArray.getClass());
		Assert.assertEquals(jsonArrayString, jsonArray.toString());
	}

	// 将json对象字符串转换为json对象
	@Test
	public void createJsonObjectByString() {
		JsonStructure j = getJsonStructure(jsonString);
		Assert.assertEquals(JsonValue.ValueType.OBJECT, j.getValueType());
		Assert.assertTrue(j instanceof JsonObject);
		Assert.assertEquals(jsonString, j.toString());

		j = getJsonStructure(jsonArrayString);
		Assert.assertEquals(JsonValue.ValueType.ARRAY, j.getValueType());
		Assert.assertTrue(j instanceof JsonArray);
		Assert.assertEquals(jsonArrayString, j.toString());
	}

	// 将json数组字符串转换为json数组对象
	@Test
	public void createJsonArrayByString() {
		JsonStructure j = getJsonStructure(jsonArrayString);
		Assert.assertEquals(JsonValue.ValueType.ARRAY, j.getValueType());
		Assert.assertTrue(j instanceof JsonArray);
		Assert.assertEquals(jsonArrayString, j.toString());
	}

	// 将json字符串转换为json结构（json对象或json数组）
	private JsonStructure getJsonStructure(String jsonValueString) {
		JsonReader reader = Json.createReader(new StringReader(jsonValueString));
		JsonStructure j = reader.read();
		return j;
	}

	// 像Map一样使用JsonObject
	@Test(expected = java.lang.UnsupportedOperationException.class)
	public void useJsonObjectAsMap(){
		JsonObject json = Json.createObjectBuilder()
				.add("int", 1)
				.add("float", 1.23)
				.add("n", "name")
				.build();
		Assert.assertEquals(1, json.getInt("int"));
		Assert.assertEquals(JsonValue.ValueType.NUMBER, json.get("float").getValueType());
		Assert.assertEquals("1.23", json.getJsonNumber("float").toString());

		// 调用Map的get操作正常返回JsonValue对象
		Assert.assertEquals("1", json.get("int").toString());

		// 调用Map的put操作将抛出异常
		json.put("n", JsonValue.TRUE);
	}

	@Test
	public void json2Writer() {
		JsonStructure j = getJsonStructure(jsonString);

		StringWriter stWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.write(j);
		jsonWriter.close();

		Assert.assertEquals(jsonString, stWriter.toString());
	}

	@Test
	public void useGenerator() {
		// FileWriter writer = new FileWriter("test.txt"); // write json to file
		StringWriter writer = new StringWriter();// write json to string
		JsonGenerator gen = Json.createGenerator(writer);
		gen.writeStartObject()
			.write("str", "a")
			.write("num", 1)
			.write("bool", false)
			.writeNull("k")

			.writeStartArray("array")
			.writeStartObject()
			.write("id", 1)
			.write("n", "a")
			.writeEnd()
			.writeStartObject()
			.write("id", 2)
			.write("n", "b")
			.writeEnd()
			.writeEnd()

			.writeEnd();
		gen.close();

		String origin = "{\"str\":\"a\",\"num\":1,\"bool\":false,\"k\":null,\"array\":[{\"id\":1,\"n\":\"a\"},{\"id\":2,\"n\":\"b\"}]}";
		Assert.assertEquals(origin, writer.toString());
	}
}