package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.ModifyColumnAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:57:21
 */
public class AddModifyColumnFix extends AbstractAddChangesetFix
{
	private DatabaseField field;

	public AddModifyColumnFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field, String changeSetDesc)
	{
		super(liquibaseModuleComponent, changeSetDesc);
		this.field = field;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = newChangeSet();
		ModifyColumnAction action = changeSet.addModifyColumn();
		action.getTableName().setStringValue(field.getTableName());
		addColumnTag(field, changeSet, action, false);
	}
}
