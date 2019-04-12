package com.dataos.task.util.match;

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
public class VarSelect2 {
	public static void main(String args[]) {
		String content = "${yyyymmdd hh24:mi:ss -1 days} asdf ${yyyymmdd hh24:mi:ss}";
		
		String pstr = "\\s*\\$\\{(yyyy)?(yyyymm)?(yyyy-mm)?(yyyymmdd)?(yyyy-mm-dd)?\\s*(hh24)?(hh24:mi)?(hh24:mi:ss)?\\s*[-+]?[0-9]*\\s*(years)?(months)?(weeks)?(days)?(hours)?(minutes)?\\s*\\}\\s*";
		// String content = "yyyy";

		// String pattern = "y{0,4}";

		boolean isMatch = Pattern.matches(pstr, content);

		System.out.println("是否匹配" + isMatch);

		Pattern pattern = Pattern.compile(pstr);
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			//System.out.println(matcher.group(0));
			varToDateExpre(matcher.group());
			
		}

	}
	private static String varToDateExpre(String varExpre) {
		String datePttern = "\\s*\\$\\{(yyyy)*(yyyymm)*(yyyy-mm)*(yyyymmdd)*(yyyy-mm-dd)*\\s*";
		String tailPttern = "\\s*(hh24)?(hh24:mi)?(hh24:mi:ss)?\\s*[-+]?[0-9]*\\s*(years)?(months)?(weeks)?(days)?(hours)?(minutes)?\\s*\\}\\s*";
		
		Pattern patternD = Pattern.compile(datePttern);
		Matcher matcherD = patternD.matcher(varExpre);
		while (matcherD.find()) {
			System.out.println(matcherD.group());
			//System.out.println(matcher.group(1));
			//System.out.println(matcher.group(2));

			
		}
		
		return null ;
	}
}
