package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.AddNotNullConstraintAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.DropNotNullConstraintAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:01:18
 */
public class AddModifyNullableFix extends AbstractAddChangesetFix
{
	private DatabaseField field;
	private boolean notNull;

	public AddModifyNullableFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field, boolean notNull)
	{
		super(liquibaseModuleComponent, "Set " + field.getColumnName() + " in table " + field.getTableName() + " to " + (notNull ? "not nullable" : "nullable"));
		this.field = field;
		this.notNull = notNull;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = liquibaseModuleComponent.newChangeSet();

		if (notNull)
		{
			AddNotNullConstraintAction action = changeSet.addAddNotNullConstraint();
			action.getColumnName().setStringValue(field.getColumnName());
			action.getTableName().setStringValue(field.getTableName());
			String type = field.getColumnModel().getType();
			action.getColumnDataType().setStringValue(type);
			if (type.equals("INT") || type.equals("BIGINT") || type.equals("SMALLINT"))
			{
				action.getDefaultNullValue().setStringValue("0");
			}
			else
			{
				action.getDefaultNullValue().setStringValue("");
			}
		}
		else
		{
			DropNotNullConstraintAction action = changeSet.addDropNotNullConstraint();
			action.getColumnName().setStringValue(field.getColumnName());
			action.getTableName().setStringValue(field.getTableName());
		}
	}
}
