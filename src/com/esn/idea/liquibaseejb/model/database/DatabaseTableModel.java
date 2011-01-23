package com.esn.idea.liquibaseejb.model.database;

import com.esn.idea.liquibaseejb.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract model for a database table
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:57:17
 */
public class DatabaseTableModel
{
	private List<String> columns = new ArrayList<String>(); // Used to keep order
	private Map<String, DatabaseColumnModel> columnMap = new HashMap<String, DatabaseColumnModel>();

    private Map<String, DatabaseConstraint> constraintMap = new HashMap<String, DatabaseConstraint>();

	private Map<String, DatabaseIndex> indexMap = new HashMap<String, DatabaseIndex>();
    private String name;

    public DatabaseTableModel(String name)
    {
        this.name = name;
    }

    public void addColumn(String columnName, DatabaseColumnModel columnModel)
	{
		columns.add(columnName);
		columnMap.put(columnName, columnModel);
	}

	public void dropColumn(String columnName)
	{
		columnMap.remove(columnName);
		columns.remove(columnName);

		for (DatabaseConstraint databaseConstraint : constraintMap.values())
		{
			databaseConstraint.dropColumn(columnName);
		}

		for (DatabaseIndex databaseIndex : indexMap.values())
		{
			databaseIndex.dropColumn(columnName);
		}

	}

	public void renameColumn(String oldColumnName, String newColumnName)
	{
        if (!columnMap.containsKey(oldColumnName)) return;

        if (!columnMap.containsKey(newColumnName))
		{
			columns.set(columns.indexOf(oldColumnName), newColumnName);
			columnMap.put(newColumnName, columnMap.remove(oldColumnName));

		}

		for (DatabaseConstraint databaseConstraint : constraintMap.values())
		{
			databaseConstraint.renameColumn(oldColumnName, newColumnName);
		}

		for (DatabaseIndex databaseIndex : indexMap.values())
		{
			databaseIndex.renameColumn(oldColumnName, newColumnName);
		}
	}

    @Nullable
	public DatabaseColumnModel getColumnModel(String columnName)
	{
		return columnMap.get(columnName);
	}

	public void addForeignConstraint(String constraintName, DatabaseForeignConstraint c)
	{
        c.setName(constraintName);
        constraintMap.put(constraintName, c);
	}

	public void addConstraint(String constraintName, List<String> columnNames, DatabaseConstraintType type)
	{
		for (String columnName : columnNames)
		{
			DatabaseColumnModel databaseColumnModel = getColumnModel(columnName);
			if (databaseColumnModel != null)
			{
				switch (type)
				{
					case PRIMARYKEY:
						databaseColumnModel.setPrimaryKey(true);
                        databaseColumnModel.setSinglePrimaryKey(columnNames.size() == 1);
						break;
					case UNIQUE:
						if (columnNames.size() == 1)
						{
							databaseColumnModel.setUnique(true);
						}
						break;
				}
			}
		}

		constraintMap.put(constraintName, new DatabaseConstraint(constraintName, columnNames, type));
	}

	public void dropConstraint(String constraintName)
	{
		DatabaseConstraint databaseConstraint = constraintMap.remove(constraintName);
		if (databaseConstraint != null)
		{
			List<String> columnNames = databaseConstraint.getColumnNames();

			for (String columnName : columnNames)
			{
				DatabaseColumnModel columnModel = getColumnModel(columnName);

				switch (databaseConstraint.getType())
				{
					case PRIMARYKEY:
						columnModel.setPrimaryKey(false);
                        columnModel.setSinglePrimaryKey(false);
                        break;
					case UNIQUE:
						if (columnNames.size() == 1)
						{
							columnModel.setUnique(false);
						}
						break;
				}
			}
		}
		constraintMap.remove(constraintName);
	}

	public DatabaseConstraint getPrimaryKey()
	{
		for (DatabaseConstraint databaseConstraint : constraintMap.values())
		{
			if (databaseConstraint.getType() == DatabaseConstraintType.PRIMARYKEY)
			{
				return databaseConstraint;
			}
		}

		return null;
	}

	public DatabaseForeignConstraint getForeignConstraint(List<String> columnNames)
	{
		for (DatabaseConstraint databaseConstraint : constraintMap.values())
		{
			if (databaseConstraint instanceof DatabaseForeignConstraint)
			{
				DatabaseForeignConstraint foreignConstraint = (DatabaseForeignConstraint) databaseConstraint;
				if (foreignConstraint.getColumnNames().equals(columnNames)) return foreignConstraint;
			}
		}

		return null;
	}

	public void addIndex(String indexName, List<String> columnNames)
	{
		if (columnNames.size() == 1)
		{
			for (String columnName : columnNames)
			{
                DatabaseColumnModel columnModel = getColumnModel(columnName);
                if (columnModel != null)
                {
                    columnModel.setIndex(true);
                }
            }
		}

		indexMap.put(indexName, new DatabaseIndex(columnNames));
	}

	public void dropIndex(String indexName)
	{
		DatabaseIndex databaseIndex = indexMap.remove(indexName);


		List<String> columnNames = databaseIndex.getColumnNames();
		if (columnNames.size() == 1)
		{
			for (String columnName : columnNames)
			{
                DatabaseColumnModel columnModel = getColumnModel(columnName);
                if (columnModel != null)
                {
                    columnModel.setIndex(false);
                }
            }
		}
	}

	public void describe(PrintWriter w)
	{
		for (String column : columns)
		{
			DatabaseColumnModel columnModel = columnMap.get(column);

			w.printf("  %-16s\t", column);
			columnModel.describe(w);
			w.print("\n");
		}

        for (Map.Entry<String, DatabaseConstraint> entry : constraintMap.entrySet())
        {
            DatabaseConstraint databaseConstraint = entry.getValue();
            w.printf("  ADD %s CONSTRAINT %s (%s)\n",
                    databaseConstraint.getType().toString(),
                    entry.getKey(),
                    StringUtils.join(databaseConstraint.getColumnNames(), ","));
        }

        for (Map.Entry<String, DatabaseIndex> entry : indexMap.entrySet())
        {
            w.printf("  ADD INDEX %s (%s)\n", entry.getKey(), StringUtils.join(entry.getValue().getColumnNames(), ","));
        }
    }

	public DatabaseIndex getIndex(String indexName)
	{
		return indexMap.get(indexName);

	}

	public String getSingletonIndexName(String columnName)
	{
		for (Map.Entry<String, DatabaseIndex> entry : indexMap.entrySet())
		{
			if (entry.getValue().isSingletonWithColumnName(columnName))
			{
				return entry.getKey();
			}
		}

		return null;
	}

	public boolean existsTableColumns(List<String> columnNames)
	{
		for (String columnName : columnNames)
		{
			if (!columns.contains(columnName)) return false;
		}

		return true;
	}

	public DatabaseConstraint getUniqueConstraint(List<String> columnNames)
	{
		for (DatabaseConstraint databaseConstraint : constraintMap.values())
		{
			if (databaseConstraint.getType() == DatabaseConstraintType.UNIQUE)
			{
				if (databaseConstraint.getColumnNames().equals(columnNames)) return databaseConstraint;
			}
		}

		return null;
	}

    public List<String> getColumnNames()
    {
        return new ArrayList<String>(columns);
    }

    public Map<String, DatabaseConstraint> getConstraints()
    {
        return new HashMap<String, DatabaseConstraint>(constraintMap);
    }

    public HashMap<String, DatabaseIndex> getIndexes()
    {
        return new HashMap<String, DatabaseIndex>(indexMap);
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
