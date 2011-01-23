package com.esn.idea.liquibaseejb.util;

import java.util.Collection;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 15:40:53
 */
public class StringUtils
{
	public static String join(Collection<String> strings, final String sep)
	{
		StringBuffer res = new StringBuffer();

		String prepend = "";
		for (String string : strings)
		{
			res.append(prepend);
			res.append(string);
			prepend = sep;
		}

		return res.toString();
	}
}
