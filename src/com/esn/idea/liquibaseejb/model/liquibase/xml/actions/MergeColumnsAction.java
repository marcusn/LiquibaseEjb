package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IMergeColumnsAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:35:49
 */
public abstract class MergeColumnsAction implements TableHolderAction, ChangeSetAction, IMergeColumnsAction
{
	public void execute(DatabaseModel model)
	{
		String tableName = getTableName().getStringValue();
		model.dropColumn(tableName, getColumn1Name().getStringValue());
		model.dropColumn(tableName, getColumn2Name().getStringValue());
		model.addColumn(
						tableName, getFinalColumnName().getStringValue(),
						new DatabaseColumnModel(getFinalColumnType().getStringValue())
		);
	}
}