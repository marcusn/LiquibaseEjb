package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.DropUniqueConstraintAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-21
 * Time: 18:16:51
 */
public class DropUniqueConstraintFix extends AbstractAddChangesetFix
{
	private DatabaseField field;
	private String constraintName;

	public DropUniqueConstraintFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field, String constraintName)
	{
		super(liquibaseModuleComponent, "Drop unique constraint " + constraintName + " on field " + field.getColumnName() + " in table " + field.getTableName());
		this.field = field;
		this.constraintName = constraintName;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = newChangeSet();
		DropUniqueConstraintAction action = changeSet.addDropUniqueConstraint();
		action.getConstraintName().setStringValue(constraintName);
		action.getTableName().setStringValue(field.getTableName());
	}
}