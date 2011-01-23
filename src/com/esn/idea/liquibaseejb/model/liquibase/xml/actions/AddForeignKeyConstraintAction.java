package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IAddForeignConstraintAction;

import java.util.Arrays;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class AddForeignKeyConstraintAction implements ChangeSetAction, IAddForeignConstraintAction
{
	public void execute(DatabaseModel model)
	{
		String baseColumnNamesString = getBaseColumnNames().getStringValue();
		if (baseColumnNamesString == null) return;

		String referencedColumnNamesString = getReferencedColumnNames().getStringValue();
		if (referencedColumnNamesString == null) return;

		List<String> baseColumnNames = Arrays.asList(baseColumnNamesString.split("\\,"));
		List<String> referencedColumnNames = Arrays.asList(referencedColumnNamesString.split("\\,"));
		model.addForeignKeyConstraint(
						getBaseTableName().getStringValue(),
						getConstraintName().getStringValue(),
						baseColumnNames,
						getReferencedTableName().getStringValue(),
						referencedColumnNames);
	}
}