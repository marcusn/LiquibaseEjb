package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 17:48:25
 */
public abstract class CreateTableAction extends AbstractAddColumnsAction implements ChangeSetAction
{
	public void execute(DatabaseModel model)
	{
		model.addTable(getTableName().getValue());

		executeAddColumns(model);
	}
}