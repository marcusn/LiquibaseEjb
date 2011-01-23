package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraint;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.AddUniqueConstraintAction;
import com.esn.idea.liquibaseejb.util.StringUtils;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 16:08:50
 */
public class AddUniqueConstraintFix extends AbstractAddChangesetFix
{
	private String tableName;
	private List<String> columnNames;

	public AddUniqueConstraintFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field)
	{
		super(liquibaseModuleComponent, "Add unique constraint to " + field.getColumnName() + " in table " + field.getTableName());
		this.tableName = field.getTableName();
		this.columnNames = Arrays.asList(field.getColumnName());
	}

	public AddUniqueConstraintFix(LiquibaseModuleComponent liquibaseModuleComponent, String tableName, DatabaseConstraint constraint)
	{
		super(liquibaseModuleComponent, "Add unique constraint on " + StringUtils.join(constraint.getColumnNames(), ",") + " in table " + tableName);
		this.tableName = tableName;
		this.columnNames = constraint.getColumnNames();
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = newChangeSet();
		AddUniqueConstraintAction action = changeSet.addAddUniqueConstraint();
		action.getTableName().setStringValue(tableName);
		action.getConstraintName().setStringValue("uq_" + generateId());
		action.getColumnNames().setStringValue(StringUtils.join(columnNames, ","));
	}
}
