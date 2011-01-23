package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseForeignConstraint;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.AddForeignKeyConstraintAction;
import com.esn.idea.liquibaseejb.util.StringUtils;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 15:35:22
 */
public class AddForeignKeyConstraintFix extends AbstractAddChangesetFix
{
	private String tableName;
	private DatabaseForeignConstraint constraint;

	public AddForeignKeyConstraintFix(LiquibaseModuleComponent liquibaseModuleComponent, String tableName, DatabaseForeignConstraint constraint)
	{
		super(liquibaseModuleComponent, "Add foreign key on columns " + StringUtils.join(constraint.getColumnNames(), ",") + " in table " + tableName + " to " + StringUtils.join(constraint.getTargetColumnNames(), ",") + " in table " + constraint.getTargetTableName());
		this.tableName = tableName;
		this.constraint = constraint;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = newChangeSet();
		AddForeignKeyConstraintAction action = changeSet.addAddForeignKeyConstraint();

		action.getBaseColumnNames().setStringValue(StringUtils.join(constraint.getColumnNames(), ","));
		action.getBaseTableName().setStringValue(tableName);
		action.getConstraintName().setStringValue("fk_" + constraint.getTargetTableName() + "_" + (System.currentTimeMillis()) / 1000);
		action.getReferencedTableName().setStringValue(constraint.getTargetTableName());
		action.getReferencedColumnNames().setStringValue(StringUtils.join(constraint.getTargetColumnNames(), ","));
	}
}
