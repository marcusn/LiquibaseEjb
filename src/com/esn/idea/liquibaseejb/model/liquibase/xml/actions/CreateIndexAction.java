package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseColumn;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ColumnsHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IndexNameHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 17:48:25
 */
public abstract class CreateIndexAction implements TableHolderAction, ColumnsHolderAction, ChangeSetAction, IndexNameHolderAction
{
	public void execute(DatabaseModel model)
	{
		List<String> columnNames = new ArrayList<String>();

		for (LiquibaseColumn column : getColumns())
		{
			String stringValue = column.getName().getStringValue();

			if (stringValue != null) columnNames.add(stringValue);
		}

        String indexName = getIndexName().getStringValue();
        if (indexName == null)
        {
            indexName = "ix_" + System.currentTimeMillis();
        }
        
        model.addIndex(getTableName().getStringValue(), indexName, columnNames);
	}
}