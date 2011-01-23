package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseColumn;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseConstraints;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ColumnsHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

import java.util.Arrays;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 10:21:17
 */
public abstract class AbstractAddColumnsAction implements ColumnsHolderAction, TableHolderAction
{
	protected void executeAddColumns(DatabaseModel model)
	{
		for (LiquibaseColumn column : this.getColumns())
		{
			DatabaseColumnModel databaseColumnModel = new DatabaseColumnModel(column.getType().getStringValue());
			if (column.getDefaultValue().getStringValue() != null)
			{
				databaseColumnModel.setDefaultValue(column.getDefaultValue().getStringValue());
			}
			String autoIncrement = column.getAutoIncrement().getStringValue();
			if (autoIncrement != null)
			{
				if (autoIncrement.equals("true"))
				{
					databaseColumnModel.setAutoIncrement(true);
				}
			}
			LiquibaseConstraints constraints = column.getConstraints();
			if ("false".equals(constraints.getNullable().getStringValue()))
			{
				databaseColumnModel.setNotNull(true);
			}
			String tableName = getTableName().getStringValue();
			String columnName = column.getName().getStringValue();
			model.addColumn(
							tableName,
							columnName,
							databaseColumnModel);

			if ("true".equals(constraints.getPrimaryKey().getStringValue()))
			{
				String name = constraints.getPrimaryKeyName().getStringValue();
				if (name == null) name = "pk_" + tableName;

				model.addPrimaryKeyConstraint(tableName, name, Arrays.asList(columnName));
			}
			if ("true".equals(constraints.getUnique().getStringValue()))
			{
				String name = constraints.getUniqueConstraintName().getStringValue();
				if (name == null) name = "uq_" + tableName + "_" + (System.currentTimeMillis() / 1000);

				model.addUniqueConstraint(tableName, name, Arrays.asList(columnName));
			}

			// TODO: Handle foreign key constraint
		}
	}
}
