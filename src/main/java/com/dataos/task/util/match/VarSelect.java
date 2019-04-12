package com.dataos.task.util.match;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright © 砥特信息科技有限公司. All rights reserved.
 *
 * @Title: VarSelect.java
 * @Prject: dataos
 * @Package: com.dt.dataos.param.match
 * @Description: TODO
 * @author: yangguowen
 * @date: Mar 7, 2019 7:56:26 PM
 * @version: V1.0
 */
public class VarSelect {
	public static void main(String args[]) {
		String content4 = "a=${yyyy-mm-dd hh24:mi:ss -1 days} asdf ";
		String content3y = "a=${yyyy-mm-dd -1 days}";
		String content3h = "a=${hh24:mi:ss -1 days} asdf ";
		String content2 = "a=${yyyy-mm-dd hh24:mi:ss} asdf ";
		String content1y = "a=${yyyy-mm-dd} asdf ";
		String content1h = "a=${yyyy-mm-dd} asdf ";

		String bizdateString = "bizdate=${yyyy-mm-dd -1 days},db_type=\"oracle\",curdate=${yyyy-mm-dd hh24:mi:ss}";
		String pstr = "\\s*\\$\\{((yyyy)?(yyyymm)?(yyyy-mm)?(yyyymmdd)?(yyyy-mm-dd)?\\s*(hh24)?(hh24:mi)?(hh24:mi:ss)?)\\s*([-+]?[0-9]*\\s*(years)?(months)?(weeks)?(days)?(hours)?(minutes)?)\\s*\\}\\s*";
		// String content = "yyyy";

		// String pattern = "y{0,4}";

		boolean isMatch = Pattern.matches(pstr, bizdateString);

		//Map<String, String> map = getLocalExpre(bizdateString);

		System.out.println("是否匹配" + isMatch);

	}

	/**
	 * 用户组装shell用的参数及默认参数 分为恒量参数：不参与传递 时间参数：参与传递，只统计这种参数个数，也就是d_开头的
	 * 
	 * @param params
	 * @return
	 */
	public static void getShellParam(String params,Map<String, Object>shellMap) {
		Map<String, Map> map = getLocalExpre(params);
		Map<String, String> mapConstant=map.get("cons");
		Map<String, String> mapDate=map.get("date");
		int paramCN=mapDate.size();//时间参数个数
		
		StringBuffer defaultDate=new StringBuffer("");//默认时间参数
		StringBuffer transDate=new StringBuffer("");//传递时间参数
		StringBuffer jobDate=new StringBuffer("");//job传递参数
		
		StringBuffer constant=new StringBuffer("");//job传递参数
		StringBuffer sqlScript=new StringBuffer("");//sql 脚本传递参数
		
		
		
		int cn=1;
		for (Map.Entry<String, String>entry :mapDate.entrySet()) {//时间参数处理
			defaultDate.append(entry.getKey());//默认时间参数
			defaultDate.append("=");
			defaultDate.append(entry.getValue());
			defaultDate.append("\n");
			
			transDate.append(entry.getKey());//传递时间参数
			transDate.append("=");
			transDate.append("$");
			transDate.append(cn);
			transDate.append("\n");
			
			jobDate.append("${");//job时间参数
			jobDate.append(entry.getKey());
			jobDate.append("} ");
			
			sqlScript.append(" --define ");//sql --define run_day=${run_day}"
			sqlScript.append(entry.getKey());
			sqlScript.append("=");
			sqlScript.append("${");
			sqlScript.append(entry.getKey());
			sqlScript.append("} ");
			
			cn++;

		}
		for (Map.Entry<String, String>entry :mapDate.entrySet()) {//常量参数处理
			constant.append(entry.getKey());
			constant.append("=");
			constant.append(entry.getValue());
			constant.append("\n");
			
			sqlScript.append(" --define ");//sql --define run_day=${run_day}"
			sqlScript.append(entry.getKey());
			sqlScript.append("=");
			sqlScript.append("${");
			sqlScript.append(entry.getKey());
			sqlScript.append("} ");
			
		}
		
		shellMap.put("paramCN", paramCN);
		shellMap.put("defaultDate", defaultDate.toString());
		shellMap.put("transDate", transDate.toString());
		shellMap.put("jobDate", jobDate.toString());
		shellMap.put("constant", constant.toString());
		shellMap.put("sqlScript", sqlScript.toString());
		
	}

	/**
	 * 所有参数变量都以键值对的形式出现，var=${yyyymmdd}并以逗号隔开； 变量有两种：恒定变量（常量变量），不用处理，并以map方式存放；
	 * 日期变量，处理成linux日期格式。 以逗号分割，获取键值对，并分别处理。生成map
	 * 
	 * @param params
	 * @return
	 */
	public static Map<String, Map> getLocalExpre(String params) {
		System.out.println("params=" + params);
		// 日期变量 bizdate=${yyyymmdd -1 days} $(date -d "-1 day" "+%Y-%m-%d"）mapDate
		// 存储常量变量 例如db_type="oracle" mapConstant
		Map<String, String> mapConstant = new HashMap<String, String>();
		Map<String, String> mapDate = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String obj1, String obj2) {
				// 降序排序
				return obj2.compareTo(obj1);
			}
		});
		Map<String, Map> mapLocal = new HashMap<String, Map>();
		mapLocal.put("date",mapDate );
		mapLocal.put("cons",mapConstant );
		if (params != null) {
			String[] arrParam = params.split(",");// 使用逗号分割参数，获取键值对
			for (String dosVar : arrParam) {
				String[] kvs = dosVar.split("=");
				String localTime = getLocalTime(kvs[1]);
				// 日期变量 bizdate=${yyyymmdd -1 days} $(date -d "-1 day" "+%Y-%m-%d"）key前d_
				// 存储常量变量 例如db_type="oracle" key前c_
				if (localTime != null) {
					mapDate.put(kvs[0], localTime);
				} else {
					mapConstant.put( kvs[0], kvs[1]);
				}
			}

		}

		return mapLocal;
	}

	/**
	 * 从给定变量和参数中 获取参数的表达式
	 * 
	 * @param params
	 * @return
	 */
	private static String getLocalTime(String params) {
		System.out.println("params=" + params);
		Map<String, String> map = new HashMap<String, String>();
		String pstr = "\\s*\\$\\{((yyyy)?(yyyymm)?(yyyy-mm)?(yyyymmdd)?(yyyy-mm-dd)?\\s*(hh24)?(hh24:mi)?(hh24:mi:ss)?)\\s*([-+]?[0-9]*\\s*(years)?(months)?(weeks)?(days)?(hours)?(minutes)?)\\s*\\}\\s*";
		Pattern pattern = Pattern.compile(pstr);
		Matcher matcher = pattern.matcher(params);
		String tmpString = null;
		while (matcher.find()) {
			System.out.println("matcher.group()=" + matcher.group());
			System.out.println("varToDateExpre(matcher.group())=" + varToDateExpre(matcher.group()));
			// map.put(matcher.group(),varToDateExpre(matcher.group()));
			tmpString = varToDateExpre(matcher.group());
			// System.out.println(matcher.group(1));
			// System.out.println(matcher.group(2));
		}
		return tmpString;
	}

	/**
	 * 根据表达，生成linux识别的日期格式
	 * 
	 * @param varExpre
	 * @return
	 */
	private static String varToDateExpre(String varExpre) {
		String[] varExpreArr = varExpre.split(" ");// 使用空格分割字符，得到日期 时间 加减数量 加减单位
		StringBuffer locTime = new StringBuffer("$(date ");
		if (varExpreArr.length == 1) {// 日期类型
			locTime.append("+\"");
			locTime.append(varExpreArr[0].replace("${", "").replace("yyyy", "%Y").replace("mm", "%m")
					.replace("dd", "%d").replace("}", "\")"));
		} else if (varExpreArr.length == 2) {// 日期时间类型
			locTime.append("+\"");
			locTime.append(
					varExpreArr[0].replace("${", "").replace("yyyy", "%Y").replace("mm", "%m").replace("dd", "%d"));
			// hh24:mi:ss //%H:%M:%S
			locTime.append(" ");
			locTime.append(varExpreArr[1].replace("hh24", "%H")// %H:%M:%S
					.replace(":mi", ":%M").replace(":ss", ":%S").replace("dd", "%d").replace("}", "\")"));

		} else if (varExpreArr.length == 3) {// 日期或时间 前后推演
			locTime.append("-d \"");// -d "-1 month" "+
			locTime.append(varExpreArr[1]); // -1
			locTime.append(" ");
			locTime.append(varExpreArr[2].replace("}", ""));// month

			locTime.append("\" +\"");
			if (varExpre.contains("yyyy"))
				locTime.append(
						varExpreArr[0].replace("${", "").replace("yyyy", "%Y").replace("mm", "%m").replace("dd", "%d"));
			// hh24:mi:ss //%H:%M:%S
			if (varExpre.contains("hh24"))
				locTime.append(varExpreArr[0].replace("${", "").replace("hh24", "%H")// %H:%M:%S
						.replace(":mi", ":%M").replace(":ss", ":%S")

				);
			locTime.append("\") ");
		} else if (varExpreArr.length == 4) {// 日期 时间 前后推演
			locTime.append("-d \"");// -d "-1 month" "+
			locTime.append(varExpreArr[2]); // -1
			locTime.append(" ");
			locTime.append(varExpreArr[3].replace("}", ""));// month

			locTime.append("\" +\"");
			locTime.append(
					varExpreArr[0].replace("${", "").replace("yyyy", "%Y").replace("mm", "%m").replace("dd", "%d"));
			// hh24:mi:ss //%H:%M:%S
			locTime.append(" ");
			locTime.append(varExpreArr[1].replace("hh24", "%H")// %H:%M:%S
					.replace(":mi", ":%M").replace(":ss", ":%S").replace("dd", "%d"));
			locTime.append("\") ");
		}
		System.out.println(locTime.toString());

		return locTime.toString();
	}

	/**
	 * 多个字符转换成一个字符
	 * 
	 * @param dateExpres
	 * @return
	 */
	private static String replaceMBlank(String dateExpres) {
		if (dateExpres != null && !"".equals(dateExpres)) {
			Pattern pattern = Pattern.compile("\\s+"); // 去掉空格符合换行符
			Matcher matcher = pattern.matcher(dateExpres);
			String result = matcher.replaceAll(" ");
			return result;
		}

		return null;
	}
}
