package com.esn.idea.liquibaseejb.model.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Abstract model for database. Both Liquibase and EJB are translated into this common model.
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:56:53
 */
public class DatabaseModel
{
	/** Maps table name to table model  */
    private Map<String, DatabaseTableModel> tableMap = new HashMap<String, DatabaseTableModel>();

	/**
     * Add column to table
     *
     * @param tableName name of table to add column to
     * @param columnName name of column to add
     * @param columnModel Column model of column to add
     */
    public void addColumn(String tableName, String columnName, DatabaseColumnModel columnModel)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.addColumn(columnName, columnModel);
		}
	}

	/**
     * Drop column from table
     *
     * @param tableName name of table to drop column from
     * @param columnName name of column to drop
     */
    public void dropColumn(String tableName, String columnName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.dropColumn(columnName);
		}
	}

	/**
     * Rename column in table
     *
     * @param tableName name of table to rename column in
     * @param oldColumnName name of column to rename
     * @param newColumnName new name of column
     */
    public void renameColumn(String tableName, String oldColumnName, String newColumnName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.renameColumn(oldColumnName, newColumnName);
		}
	}

	/**
     * Add table to model. The created table will have no columns.
     *
     * @param tableName Name of table to add
     */
    public void addTable(String tableName)
	{
		if (!tableMap.containsKey(tableName))
		{
			tableMap.put(tableName, new DatabaseTableModel(tableName));
		}
	}

	/**
     * Drop table from model
     *
     * @param tableName name of table to drop
     */
    public void dropTable(String tableName)
	{
		tableMap.remove(tableName);
	}

	/**
     * Rename table in model
     *
     * @param oldTableName name of table to rename
     * @param newTableName new name for table
     */
    public void renameTable(String oldTableName, String newTableName)
	{
		if (!tableMap.containsKey(newTableName))
		{
            DatabaseTableModel tableModel = tableMap.remove(oldTableName);
            tableModel.setName(newTableName);
            tableMap.put(newTableName, tableModel);
		}
	}

	/**
     * Get column model of a column
     *
     * @param tableName name of table where the column is
     * @param columnName name of column to get model of
     * @return Column model of the specified column, or NULL if table or column does not exist
     */
    public DatabaseColumnModel getColumnModel(String tableName, String columnName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			return tableModel.getColumnModel(columnName);
		}

		return null;
	}

	/**
     * Check if a table exists
     *
     * @param tableName name of table to check for existance
     * @return Whether the specified table exists
     */
    public boolean existsTable(String tableName)
	{
		return tableMap.containsKey(tableName);
	}

	/**
     * Add a foreign constraint to model
     *
     * @param tableName name of table to add constraint to
     * @param constraintName name of new constraint
     * @param columnNames columns in the new constraint
     * @param targetTableName Target table of foreign constraint
     * @param targetColumnNames Columns in target table, must be same size as columnNames
     */
    public void addForeignKeyConstraint(String tableName, String constraintName, List<String> columnNames, String targetTableName, List<String> targetColumnNames)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		DatabaseTableModel targetTableModel = tableMap.get(targetTableName);
		if (tableModel != null && targetTableModel != null)
		{
			tableModel.addForeignConstraint(constraintName, new DatabaseForeignConstraint(constraintName, columnNames, targetTableModel, targetColumnNames));
		}
	}

	/**
     * Drop constraint
     *
     * @param tableName name of table to drop constraint from

     * @param constraintName name of constraint to drop
     */
    public void dropConstraint(String tableName, String constraintName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.dropConstraint(constraintName);
		}
	}
	/**
     * Add unique constraint
     *
     * @param tableName name of table to add constraint to
     * @param constraintName name of new constraint
     * @param columnNames columns in the new constraint
     */
    public void addUniqueConstraint(String tableName, String constraintName, List<String> columnNames)
	{
        if (constraintName == null)
        {
            constraintName = "uc_" + (System.currentTimeMillis());
        }
        DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.addConstraint(constraintName, columnNames, DatabaseConstraintType.UNIQUE);
		}
	}

    /**
     * Add index to table
     *
     * @param tableName name of table to add index to
     * @param indexName name of index
     * @param columnNames columns in the new index
     */
    public void addIndex(String tableName, String indexName, List<String> columnNames)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.addIndex(indexName, columnNames);
		}
	}

	/**
     * Drop index from table
     *
     * @param tableName name of table to drop index from
     * @param indexName name of index to drop
     */
    public void dropIndex(String tableName, String indexName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.dropIndex(indexName);
		}
	}
	/**
     * Add primary key constraint
     *
     * @param tableName name of table to add constraint to
     * @param constraintName name of new constraint
     * @param columnNames columns of primary key
     */
    public void addPrimaryKeyConstraint(String tableName, String constraintName, List<String> columnNames)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			tableModel.addConstraint(constraintName, columnNames, DatabaseConstraintType.PRIMARYKEY);
		}
	}

    /**
     * Method getPrimaryKey ...
     *
     * @param tableName of type String
     * @return List<String>
     */
    public List<String> getPrimaryKey(String tableName)
	{
		DatabaseConstraint constraint = getPrimaryKeyConstraint(tableName);
		if (constraint == null) return Collections.emptyList();
		return constraint.getColumnNames();
	}

	/**
     * Get primary key constraint of table
     *
     * @param tableName name of table to get primary key constraint for
     * @return Primary key constraint for the specified table, or NULL if table has no primary key
     */
    public DatabaseConstraint getPrimaryKeyConstraint(String tableName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			return tableModel.getPrimaryKey();
		}

		return null;
	}

	/**
     * Get foreign key constraint for columns
     *
     * @param tableName name of table to get constraint of
     * @param columnNames columns in constraint
     * @return The foreign key constraint for the specified columns, or NULL if the specified columns has no
     * foreign key constraint
     */
    public DatabaseForeignConstraint getForeignKeyConstraint(String tableName, List<String> columnNames)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			return tableModel.getForeignConstraint(columnNames);
		}

		return null;
	}

	/**
     * Get unique key constraint for columns
     *
     * @param tableName name of table to get constraint of
     * @param columnNames columns in constraint
     * @return The unique constraint for the specified columns, or NULL if the specified columns has no
     * unique constraint
     */
    public DatabaseConstraint getUniqueConstraint(String tableName, List<String> columnNames)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			return tableModel.getUniqueConstraint(columnNames);
		}

		return null;
	}

    /**
     * Get all constraint of a table
     *
     * @param tableName name of table to get constraints of
     * @return A mapping (name, c) with constraint c named name
     */
    public Map<String, DatabaseConstraint> getConstraints(String tableName)
    {
        DatabaseTableModel tableModel = tableMap.get(tableName);
        if (tableModel != null)
        {
            return tableModel.getConstraints();
        }

        return Collections.emptyMap();
    }

    /**
     * Get indexes of table
     *
     * @param tableName name of table to get indexes of
     * @return A mapping (name, i) with index i named name
     */
    public Map<String, DatabaseIndex> getIndexes(String tableName)
    {
        DatabaseTableModel tableModel = tableMap.get(tableName);
        if (tableModel != null)
        {
            return tableModel.getIndexes();
        }

        return Collections.emptyMap();
    }

    /**
     * Describe a table, used for formatting the table to a readable string
     *
     * @param w writer to write description to
     */
    public void describe(PrintWriter w)
	{
		for (Map.Entry<String, DatabaseTableModel> entry : tableMap.entrySet())
		{
			w.println("CREATE TABLE " + entry.getKey());
			entry.getValue().describe(w);
			w.append("\n");
		}
	}

	/**
     * Get a description of table
     * @return A description of table
     */
    public String toText()
	{
		StringWriter writer = new StringWriter();
		describe(new PrintWriter(writer));
		return writer.toString();
	}

	/**
     * Get index of table
     *
     * @param tableName name of table to get index of
     * @param indexName name of index
     * @return The specified index, or NULL if it does not exist
     */
    public DatabaseIndex getIndex(String tableName, String indexName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			return tableModel.getIndex(indexName);
		}

		return null;
	}

	/**
     * Get singleton index for a specific column
     *
     * @param tableName name of table to get index of
     * @param columnName name of column to get index of
     * @return Name of an index containing only the specified column, or NULL if no such index exist
     */
    public String getSingletonIndexName(String tableName, String columnName)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
		if (tableModel != null)
		{
			return tableModel.getSingletonIndexName(columnName);
		}

		return null;
	}

	/**
     * Check if some columns exist
     *
     * @param tableName name of table to check existance of columns in
     * @param columnNames names of columns to check
     * @return Whether all specified columns exist
     */
    public boolean existsTableColumns(String tableName, List<String> columnNames)
	{
		DatabaseTableModel tableModel = tableMap.get(tableName);
        return tableModel != null && tableModel.existsTableColumns(columnNames);

		}

    /**
     * Get names of all tables
     *
     * @return Names of all tables that exists in this model
     */
    public List<String> getTableNames()
    {
        return new ArrayList<String>(tableMap.keySet());
    }

    /**
     * Get names of all columns in a specified table
     *
     * @param tableName name of table to get columns of
     * @return List of column names in the specified table
     */
    public List<String> getColumnNames(String tableName)
    {
        DatabaseTableModel tableModel = tableMap.get(tableName);
        if (tableModel != null)
        {
            return tableModel.getColumnNames();
        }

        return Collections.emptyList();

    }

    /**
     * Get fields of table. This is like getColumnNames, but gives more information about the columns.
     *
     * @param tableName name of table to get fields of
     * @return Fields of the specified table, containing name and additional information about column
     */
    public List<DatabaseField> getTableFields(String tableName)
    {
        List<DatabaseField> res = new ArrayList<DatabaseField>();

        for (String columnName : getColumnNames(tableName))
        {
           res.add(new DatabaseField(tableName, columnName, getColumnModel(tableName, columnName)));
        }

        return res;
    }
}
