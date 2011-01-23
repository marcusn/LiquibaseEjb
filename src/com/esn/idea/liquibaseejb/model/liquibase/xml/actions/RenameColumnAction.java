package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IRenameColumnAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:35:49
 */
public abstract class RenameColumnAction implements TableHolderAction, ChangeSetAction, IRenameColumnAction
{
	public void execute(DatabaseModel model)
	{
        String tableName = getTableName().getStringValue();
        String newColumnName = getNewColumnName().getStringValue();
        String oldColumnName = getOldColumnName().getStringValue();

        model.renameColumn(
                tableName,
                oldColumnName,
                newColumnName
		);

        String columnDataType = getColumnDataType().getStringValue();

        if (columnDataType != null && !columnDataType.isEmpty())
        {
            DatabaseColumnModel columnModel = model.getColumnModel(tableName, newColumnName);

            if (columnModel != null)
            {
                columnModel.setType(columnDataType);
            }
        }
    }
}
