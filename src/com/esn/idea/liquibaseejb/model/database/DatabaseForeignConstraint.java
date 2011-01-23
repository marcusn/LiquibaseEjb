package com.esn.idea.liquibaseejb.model.database;

import java.util.List;

/**
 * Abstract model for a foreign contraint
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 15:02:37
 */
public class DatabaseForeignConstraint extends DatabaseConstraint
{
	private DatabaseTableModel targetTable;
	private List<String> targetColumnNames;

	public DatabaseForeignConstraint(String name, List<String> columnNames, DatabaseTableModel targetTable, List<String> targetColumnNames)
	{
		super(name, columnNames, DatabaseConstraintType.FOREIGNKEY);
		this.targetTable = targetTable;
		this.targetColumnNames = targetColumnNames;
	}

	public DatabaseTableModel getTargetTable()
	{
		return targetTable;
	}

    public String getTargetTableName()
    {
        return targetTable.getName();
    }

    public List<String> getTargetColumnNames()
	{
		return targetColumnNames;
	}

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DatabaseForeignConstraint that = (DatabaseForeignConstraint) o;

        if (!targetColumnNames.equals(that.targetColumnNames)) return false;
        if (!targetTable.equals(that.targetTable)) return false;

        return true;
    }

    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + targetTable.hashCode();
        result = 31 * result + targetColumnNames.hashCode();
        return result;
    }
}
