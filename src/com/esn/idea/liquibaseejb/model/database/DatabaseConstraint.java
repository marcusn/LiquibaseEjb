package com.esn.idea.liquibaseejb.model.database;

import java.util.List;
import java.util.ArrayList;

/**
 * Abstract model for a database constraint
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 09:33:15
 */
public class DatabaseConstraint
{
    private List<String> columnNames = new ArrayList<String>();
	private DatabaseConstraintType type;
    private String name;

    public DatabaseConstraint(String name, List<String> columnNames, DatabaseConstraintType type)
	{
        this.name = name;
        this.columnNames = new ArrayList<String>(columnNames);
		this.type = type;
	}

	public List<String> getColumnNames()
	{
		return columnNames;
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

	public DatabaseConstraintType getType()
	{
		return type;
	}

	public void setType(DatabaseConstraintType type)
	{
		this.type = type;
	}


    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseConstraint that = (DatabaseConstraint) o;

        if (!columnNames.equals(that.columnNames)) return false;
        if (type != that.type) return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        result = columnNames.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
