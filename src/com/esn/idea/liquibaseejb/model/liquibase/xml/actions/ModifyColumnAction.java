package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseColumn;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ColumnsHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 17:48:25
 */
public abstract class ModifyColumnAction implements ColumnsHolderAction, TableHolderAction, ChangeSetAction
{
	public void execute(DatabaseModel model)
	{
		String tableName = getTableName().getStringValue();
		for (LiquibaseColumn column : this.getColumns())
		{
			String columnName = column.getName().getStringValue();
			DatabaseColumnModel columnModel = model.getColumnModel(tableName, columnName);
			columnModel.setType(column.getType().getStringValue());
			columnModel.setAutoIncrement(false); // Find out if this is how it works (no drop auto increment exists in liquibase)
		}
	}
}