package com.esn.idea.liquibaseejb.model.database;

import java.util.List;

/**
 * Abstract model for a database index
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-21
 * Time: 17:21:11
 */
public class DatabaseIndex
{
	private List<String> columnNames;

	public DatabaseIndex(List<String> columnNames)
	{
		this.columnNames = columnNames;
	}

	public void renameColumn(String oldColumnName, String newColumnName)
	{
		if (columnNames.contains(oldColumnName))
		{
			columnNames.remove(oldColumnName);
			columnNames.add(newColumnName);
		}
	}

	public void dropColumn(String columnName)
	{
		columnNames.remove(columnName);
	}

	public List<String> getColumnNames()
	{
		return columnNames;
	}

	public boolean isSingletonWithColumnName(String columnName)
	{
		return columnNames.size() == 1 && columnNames.contains(columnName);

	}
}
