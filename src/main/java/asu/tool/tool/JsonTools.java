/*
 * Copyright (c) 2018 Suk Honzeon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package asu.tool.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.regex.Pattern;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;


public class JsonTools {

	/**
	 * 集合深度,如果是3则为ArrayList<ArrayList<ArrayList<>>>
	 */
	private static int deepLevel = 0;
	/**
	 * 集合类型数据,用于保存递归获取到的集合信息
	 */
	private static ArrayType arrayType = new ArrayType();

	public static void main(String[] args) {
		/// 读取json字符串
		String json = "{\"a\": 1, \"b\": \"string\", \"c\": {\"obj\": \"object\"}, \"arr1\":[1,2,3], \"arr2\":[\"a\",\"b\"]}";
		String s = getJavaFromJson(json, "TestJopo", "test.json.jopo");
		System.out.println(s);
	}

	/**
	 * 将json字符串转换为对应的javabean
	 * <p>
	 * <p>
	 * 用法:<br>
	 * 将json字符串拷贝至本项目中/Json/JsonString.txt 文件中去,然后调用该方法,<br>
	 * 就会在本项目中/Json/JsonBean.java中生成一个对应的JavaBean类<br><br>
	 * 注意:<br>
	 * 如果json字符串中有null或者空集合[]这种无法判断类型的,会统一使用Object类型
	 */
	public static void parseJson2Java(String jsonStr, String className, String packageName, String outputFile) {
		// 利用获取到的json结构集合,创建对应的javabean文件内容
		String content = getJavaFromJson(jsonStr, className, packageName);
		Files.write(outputFile, content);
	}

	/**
	 * 将json字符串转换为对应的javabean
	 *
	 * @return 生成的javabean代码
	 */
	public static String getJavaFromJson(String jsonStr, String className, String packageName) {
		// 解析获取整个json结构集合
		List<Json2JavaElement> jsonBeanTree = getJsonBeanTree(jsonStr);

		// 利用获取到的json结构集合,创建对应的javabean文件内容
		String content = createJavaBean(jsonBeanTree);
		StringBuilder builder = new StringBuilder();
		if (Strings.isNotBlank(packageName)) {
			builder.append("package ").append(packageName).append(";\n");
		}
		// append import
		builder.append("import java.util.List;\n\n");
		// append content
		builder.append("public class ").append(className).append(" {\n");
		builder.append(content).append("\n}\n");
		return builder.toString();
	}

	/**
	 * 根据解析好的数据创建生成对应的javabean类字符串
	 *
	 * @param jsonBeanTree 解析好的数据集合
	 * @return 生成的javabean类字符串
	 */
	public static String createJavaBean(List<Json2JavaElement> jsonBeanTree) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbGetterAndSetter = new StringBuilder();
		sb.append("\n");

		// 是否包含自定义子类
		boolean hasCustomeClass = false;
		List<String> customClassNames = new ArrayList<String>();

		// 由于在循环的时候有移除操作,所以使用迭代器遍历
		Iterator<Json2JavaElement> iterator = jsonBeanTree.iterator();
		while (iterator.hasNext()) {
			Json2JavaElement j2j = iterator.next();

			// 保存自定义类名称至集合中,注意已经包含的不再添加
			if (j2j.getCustomClassName() != null && !customClassNames.contains(j2j.getCustomClassName())) {
				customClassNames.add(j2j.getCustomClassName());
			}

			if (j2j.getParentJb() != null) {
				// 如果有parent,则为自定义子类,设置标识符不做其他操作
				hasCustomeClass = true;
			} else {
				// 如果不是自定义子类,则根据类型名和控件对象名生成变量申明语句
				genField(sb, sbGetterAndSetter, j2j, 0);

				// 已经使用的数据会移除,则集合中只会剩下自定义子类相关的元素数据,将在后续的循环中处理
				iterator.remove();
			}
		}

		// 设置所有自定义类
		if (hasCustomeClass) {
			for (String customClassName : customClassNames) {
				// 根据名称申明子类

				// public class CustomClass {
				sb.append("\n");
				sb.append(StringUtils.formatSingleLine(1, "public static class " + customClassName + " {"));

				StringBuilder sbSubGetterAndSetter = new StringBuilder();
				// 循环余下的集合
				Iterator<Json2JavaElement> customIterator = jsonBeanTree.iterator();
				while (customIterator.hasNext()) {
					Json2JavaElement j2j = customIterator.next();

					// 根据当前数据的parent名称,首字母转为大写生成parent的类名
					String parentClassName = StringUtils.firstToUpperCase(j2j.getParentJb().getName());

					// 如果当前数据属于本次外层循环需要处理的子类
					if (parentClassName.equals(customClassName)) {
						// 根据类型名和控件对象名生成变量申明语句
						genField(sb, sbSubGetterAndSetter, j2j, 1);

						// 已经使用的数据会移除,减少下一次外层循环的遍历次数
						customIterator.remove();
					}
				}

				sb.append(sbSubGetterAndSetter.toString());
				sb.append(StringUtils.formatSingleLine(1, "}"));
			}
		}

		sb.append(sbGetterAndSetter.toString());
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * 生成变量相关代码
	 *
	 * @param sb                添加申明变量部分
	 * @param sbGetterAndSetter 添加getter和setter方法部分
	 * @param j2j               变量信息
	 * @param extraTabNum       额外缩进量\t
	 */
	private static void genField(StringBuilder sb, StringBuilder sbGetterAndSetter,
			Json2JavaElement j2j, int extraTabNum) {
		// 先判断是否有注释,有的话添加之
		// /**
		//  * 姓名
		//  */
		String des = j2j.getDes();
		if (des != null && des.length() > 0) {
			sb.append(StringUtils.formatSingleLine(1 + extraTabNum, "/**"));
			sb.append(StringUtils.formatSingleLine(1 + extraTabNum, " * " + des));
			sb.append(StringUtils.formatSingleLine(1 + extraTabNum, " */"));
		}

		// 申明变量
		// private String name;
		sb.append(StringUtils.formatSingleLine(1 + extraTabNum,
				"private " + getTypeName(j2j) + " " + j2j.getName() + ";"));

		// 生成变量对应的getter和setter方法
		// public String getName() {
		//     return name;
		// }
		sbGetterAndSetter.append("\n");
		sbGetterAndSetter.append(StringUtils.formatSingleLine(1 + extraTabNum,
				"public " + getTypeName(j2j) + " get" + StringUtils.firstToUpperCase(j2j.getName()) + "() {"));
		sbGetterAndSetter.append(StringUtils.formatSingleLine(2 + extraTabNum, "return " + j2j.getName() + ";"));
		sbGetterAndSetter.append(StringUtils.formatSingleLine(1 + extraTabNum, "}"));

		// public void setName(String name) {
		//     this.name = name;
		// }
		sbGetterAndSetter.append("\n");
		sbGetterAndSetter.append(StringUtils.formatSingleLine(1 + extraTabNum,
				"public void set" + StringUtils.firstToUpperCase(j2j.getName()) +
						"(" + getTypeName(j2j) + " " + j2j.getName() + ") {"));
		sbGetterAndSetter.append(StringUtils.formatSingleLine(2 + extraTabNum,
				"this." + j2j.getName() + " = " + j2j.getName() + ";"));
		sbGetterAndSetter.append(StringUtils.formatSingleLine(1 + extraTabNum, "}"));
	}

	/**
	 * 递归遍历整个json数据结构,保存至jsonBeans集合中
	 *
	 * @param jsonStr json字符串
	 * @return 解析好的数据集合
	 */
	public static List<Json2JavaElement> getJsonBeanTree(String jsonStr) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(jsonStr);

		// 根element可能是对象也可能是数组
		JsonObject rootJo = null;
		if (element.isJsonObject()) {
			rootJo = element.getAsJsonObject();
		} else if (element.isJsonArray()) {
			// 集合中如果有数据,则取第一个解析
			JsonArray jsonArray = element.getAsJsonArray();
			if (jsonArray.size() > 0) {
				rootJo = jsonArray.get(0).getAsJsonObject();
			}
		}

		jsonBeans = new ArrayList<Json2JavaElement>();
		recursionJson(rootJo, null);
		return jsonBeans;
	}

	/**
	 * 保存递归获取到数据的集合
	 */
	private static List<Json2JavaElement> jsonBeans = new ArrayList<Json2JavaElement>();

	/**
	 * 递归获取json数据
	 *
	 * @param jo     当前递归解析的json对象
	 * @param parent 已经解析好的上一级数据,无上一级时传入null
	 */
	private static void recursionJson(JsonObject jo, Json2JavaElement parent) {
		if (jo == null) {
			return;
		}

		// 循环整个json对象的键值对
		for (Entry<String, JsonElement> entry : jo.entrySet()) {
			// json对象的键值对建构为 {"key":value}
			// 其中,值可能是基础类型,也可能是集合或者对象,先解析为json元素
			String name = entry.getKey();
			JsonElement je = entry.getValue();

			Json2JavaElement j2j = new Json2JavaElement();
			j2j.setName(name);
			if (parent != null) {
				j2j.setParentJb(parent);
			}

			// 获取json元素的类型,可能为多种情况,如下
			Class<?> type = getJsonType(je);
			if (type == null) {
				// 自定义类型

				// json键值的首字母转为大写,作为自定义类名
				j2j.setCustomClassName(StringUtils.firstToUpperCase(name));
				// ?
				j2j.setSouceJo(je.getAsJsonObject());
				jsonBeans.add(j2j);

				// 自定义类需要继续递归,解析自定义类中的json结构
				recursionJson(je.getAsJsonObject(), j2j);
			} else if (type.equals(JsonArray.class)) {
				// 集合类型

				// 重置集合数据,并获取当前json元素的集合类型信息
				deepLevel = 0;
				arrayType = new ArrayType();
				getJsonArrayType(je.getAsJsonArray());

				j2j.setArray(true);
				j2j.setArrayDeep(deepLevel);

				if (arrayType.getJo() != null) {
					j2j.setCustomClassName(StringUtils.firstToUpperCase(name));
					// 集合内的末点元素类型为自定义类, 递归
					recursionJson(arrayType.getJo(), j2j);
				} else {
					j2j.setType(arrayType.getType());
				}
				jsonBeans.add(j2j);
			} else {
				// 其他情况,一般都是String,int等基础数据类型

				j2j.setType(type);
				jsonBeans.add(j2j);
			}
		}
	}

	/**
	 * 递归获取集合的深度和类型等信息
	 *
	 * @param jsonArray json集合数据
	 */
	private static void getJsonArrayType(JsonArray jsonArray) {
		// 每次递归,集合深度+1
		deepLevel++;

		if (jsonArray.size() == 0) {
			// 如果集合为空,则集合内元素类型无法判断,直接设为Object
			arrayType.setArrayDeep(deepLevel);
			arrayType.setType(Object.class);
		} else {
			// 如果集合非空则取出第一个元素进行判断
			JsonElement childJe = jsonArray.get(0);

			// 获取json元素的类型
			Class<?> type = getJsonType(childJe);
			if (type == null) {
				// 自定义类型

				// 设置整个json对象,用于后续进行进一步解析处理
				arrayType.setJo(childJe.getAsJsonObject());
				arrayType.setArrayDeep(deepLevel);
			} else if (type.equals(JsonArray.class)) {
				// 集合类型

				// 如果集合里面还是集合,则递归本方法
				getJsonArrayType(childJe.getAsJsonArray());
			} else {
				// 其他情况,一般都是String,int等基础数据类型

				arrayType.setArrayDeep(deepLevel);
				if (type.isPrimitive()) {
					Class newType = type;
					switch (type.getTypeName()) {
						case "int":
							newType = Integer.class;
							break;
						case "long":
							newType = Long.class;
							break;
						case "float":
							newType = Float.class;
							break;
						case "double":
							newType = Double.class;
							break;
						default:
							// pass
					}
					arrayType.setType(newType);
				} else {
					arrayType.setType(type);
				}
			}
		}
	}

	/**
	 * 获取json元素的类型
	 *
	 * @param je json元素
	 * @return 类型
	 */
	private static Class<?> getJsonType(JsonElement je) {
		Class<?> clazz = null;

		if (je.isJsonNull()) {
			// 数据为null时,无法获取类型,则视为object类型
			clazz = Object.class;
		} else if (je.isJsonPrimitive()) {
			// primitive类型为基础数据类型,如String,int等
			clazz = getJsonPrimitiveType(je);
		} else if (je.isJsonObject()) {
			// 自定义类型参数则返回null,让json的解析递归进行进一步处理
			clazz = null;
		} else if (je.isJsonArray()) {
			// json集合类型
			clazz = JsonArray.class;
		}
		return clazz;
	}

	/**
	 * 将json元素中的json基础类型,转换为String.class,int.class等具体的类型
	 *
	 * @param je json元素
	 * @return 具体数据类型, 无法预估的类型统一视为Object.class类型
	 */
	private static Class<?> getJsonPrimitiveType(JsonElement je) {
		Class<?> clazz = Object.class;
		JsonPrimitive jp = je.getAsJsonPrimitive();
		// json中的类型会将数字集合成一个总的number类型,需要分别判断
		if (jp.isNumber()) {
			String num = jp.getAsString();
			if (num.contains(".")) {
				// 如果包含"."则为小数,先尝试解析成float,如果失败则视为double
				try {
					Float.parseFloat(num);
					clazz = float.class;
				} catch (NumberFormatException e) {
					clazz = double.class;
				}
			} else {
				// 如果不包含"."则为整数,先尝试解析成int,如果失败则视为long
				try {
					Integer.parseInt(num);
					clazz = int.class;
				} catch (NumberFormatException e) {
					clazz = long.class;
				}
			}
		} else if (jp.isBoolean()) {
			clazz = boolean.class;
		} else if (jp.isString()) {
			clazz = String.class;
		}
		// json中没有其他具体类型如byte等
		return clazz;
	}

	/**
	 * 获取类型名称字符串
	 *
	 * @param j2j 转换数据元素
	 * @return 类型名称, 无法获取时, 默认Object
	 */
	private static String getTypeName(Json2JavaElement j2j) {
		String name = "Object";

		Class<?> type = j2j.getType();
		if (j2j.getCustomClassName() != null && j2j.getCustomClassName().length() > 0) {
			// 自定义类,直接用自定义的名称customClassName
			name = j2j.getCustomClassName();
		} else {
			// 非自定义类即可以获取类型,解析类型class的名称,如String.class就对应String
			name = type.getName();
			int lastIndexOf = name.lastIndexOf(".");
			if (lastIndexOf != -1) {
				name = name.substring(lastIndexOf + 1);
			}
		}

		// 如果集合深度大于0,则为集合数据,根据深度进行List嵌套
		// 深度为3就是List<List<List<type>>>
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < j2j.getArrayDeep(); i++) {
			sb.append("List<");
		}
		sb.append(name);
		for (int i = 0; i < j2j.getArrayDeep(); i++) {
			sb.append(">");
		}
		return sb.toString();
	}

	/**
	 * 转换数据元素
	 */
	public static class Json2JavaElement {

		/**
		 * 是否为集合类型
		 *
		 * <p>
		 * 如果是集合的话,集合内数据类型为customClassName对应的自定义类,或者type
		 */
		private boolean isArray;

		/**
		 * 集合数据
		 */
		private JsonElement arrayItemJe;

		/**
		 * 集合深度,如果是3则为ArrayList<ArrayList<ArrayList<>>>
		 */
		private int arrayDeep;

		/**
		 * 自定义类名
		 *
		 * <p>
		 * 非空时代表是自定义类,此时不使用type参数(customClassName和type只能二选一,互斥关系)
		 */
		private String customClassName;
		private JsonObject souceJo;
		private Json2JavaElement parentJb;

		private String name;
		private Class<?> type;

		/**
		 * 注释,null时不添加注释
		 */
		private String des;

		public boolean isArray() {
			return isArray;
		}

		public void setArray(boolean isArray) {
			this.isArray = isArray;
		}

		public JsonElement getArrayItemJe() {
			return arrayItemJe;
		}

		public void setArrayItemJe(JsonElement arrayItemJe) {
			this.arrayItemJe = arrayItemJe;
		}

		public int getArrayDeep() {
			return arrayDeep;
		}

		public void setArrayDeep(int arrayDeep) {
			this.arrayDeep = arrayDeep;
		}

		public String getCustomClassName() {
			return customClassName;
		}

		public void setCustomClassName(String customClassName) {
			this.customClassName = customClassName;
		}

		public JsonObject getSouceJo() {
			return souceJo;
		}

		public void setSouceJo(JsonObject souceJo) {
			this.souceJo = souceJo;
		}

		public Json2JavaElement getParentJb() {
			return parentJb;
		}

		public void setParentJb(Json2JavaElement parentJb) {
			this.parentJb = parentJb;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		public String getDes() {
			return des;
		}

		public void setDes(String des) {
			this.des = des;
		}

		@Override
		public String toString() {
			return "\n"
					+ "Json2JavaElement [isArray=" + isArray
					+ ", arrayDeep=" + arrayDeep + ", name=" + name + ", type="
					+ type + "]";
		}

	}

	/**
	 * 集合类型数据
	 */
	public static class ArrayType {
		/**
		 * 集合中泛型的类型
		 */
		private Class<?> type;
		/**
		 * 如果集合泛型为自定义类型,用此参数保存数据
		 */
		private JsonObject jo;
		/**
		 * 集合深度,如果是3则为ArrayList<ArrayList<ArrayList<>>>
		 */
		private int arrayDeep;

		public Class<?> getType() {
			return type;
		}

		public void setType(Class<?> type) {
			this.type = type;
		}

		public JsonObject getJo() {
			return jo;
		}

		public void setJo(JsonObject jo) {
			this.jo = jo;
		}

		public int getArrayDeep() {
			return arrayDeep;
		}

		public void setArrayDeep(int arrayDeep) {
			this.arrayDeep = arrayDeep;
		}

	}

	/**
	 * @author suk
	 */
	public static class StringUtils {

		/**
		 * 将string按需要格式化,前面加缩进符,后面加换行符
		 * @param tabNum 缩进量
		 * @param srcString
		 * @return
		 */
		public static String formatSingleLine(int tabNum, String srcString) {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<tabNum; i++) {
				sb.append("\t");
			}
			sb.append(srcString);
			sb.append("\n");
			return sb.toString();
		}

		public static String firstToUpperCase(String key) {
			return key.substring(0, 1).toUpperCase(Locale.CHINA) + key.substring(1);
		}

		public static String gapToCamel(String src) {
			StringBuilder sb = new StringBuilder();
			for(String s : src.trim().split(" ")) {
				sb.append(firstToUpperCase(s));
			}
			return sb.toString();
		}

		/**
		 * 驼峰转下划线命名
		 */
		public static String camelTo_(String src) {
			StringBuilder sb = new StringBuilder();
			StringBuilder sbWord = new StringBuilder();
			char[] chars = src.trim().toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if(c >= 'A' && c <= 'Z') {
					// 一旦遇到大写单词，保存之前已有字符组成的单词
					if(sbWord.length() > 0) {
						if(sb.length() > 0) {
							sb.append("_");
						}
						sb.append(sbWord.toString());
					}
					sbWord = new StringBuilder();
				}
				sbWord.append(c);
			}

			if(sbWord.length() > 0) {
				if(sb.length() > 0) {
					sb.append("_");
				}
				sb.append(sbWord.toString());
			}

			return sb.toString();
		}

		public static boolean hasChinese(String s) {
			String regexChinese = "[\u4e00-\u9fa5]+";
			Pattern patternChinese = Pattern.compile(regexChinese);
			return patternChinese.matcher(s).find();
		}

		public static boolean isEmpty(String s) {
			return s == null || s.length() == 0;
		}
	}
}
