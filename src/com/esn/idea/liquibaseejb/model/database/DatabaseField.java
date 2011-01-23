package com.esn.idea.liquibaseejb.model.database;

/**
 * A field in a table. This is used as a helper value class to return all tablename, columnname, and column model.
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:26:58
 */
public class DatabaseField
{
	private String tableName;
	private String columnName;
	private DatabaseColumnModel columnModel;

	public DatabaseField(String tableName, String columnName, DatabaseColumnModel columnModel)
	{
		this.tableName = tableName;
		this.columnName = columnName;
		this.columnModel = columnModel;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public DatabaseColumnModel getColumnModel()
	{
		return columnModel;
	}

	public void setColumnModel(DatabaseColumnModel columnModel)
	{
		this.columnModel = columnModel;
	}
}
