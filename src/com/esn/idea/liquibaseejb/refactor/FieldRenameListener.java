package com.esn.idea.liquibaseejb.refactor;

import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.RenameColumnAction;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.LiquibaseProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-17
 * Time: 12:50:45
 */
public class FieldRenameListener implements RefactoringElementListener
{
    private DatabaseModel oldDatabaseModel;
    private DatabaseModel oldLiquibaseDatabaseModel;

    public FieldRenameListener(@NotNull PsiField field)
	{
        MemberModel memberModel = findFieldModel(field);

        if (memberModel != null)
        {
            oldDatabaseModel = memberModel.databaseForAllContexts();
            LiquibaseModuleComponent liquibaseComponent = findLiquibaseComponent(field);
            if (liquibaseComponent != null)
            {
                oldLiquibaseDatabaseModel = liquibaseComponent.findDatabaseModel();
            }
        }

    }

    private MemberModel findFieldModel(PsiField field)
    {
        LiquibaseModuleComponent liquibaseModuleComponent = findLiquibaseComponent(field);

        if (liquibaseModuleComponent == null) return null;

        ModuleModel moduleModel = ModuleModel.getInstance(liquibaseModuleComponent);

        if (moduleModel == null) return null;

        return moduleModel.getModelForMember(field);
    }

    public void elementMoved(PsiElement psiElement)
	{

	}

	public void elementRenamed(PsiElement psiElement)
	{
        LiquibaseModuleComponent liquibaseModuleComponent = findLiquibaseComponent(psiElement);

        if (psiElement instanceof PsiField && liquibaseModuleComponent != null && oldLiquibaseDatabaseModel != null)
		{
            PsiField field = (PsiField) psiElement;

            MemberModel memberModel = findFieldModel(field);

            if (memberModel == null) return;

            DatabaseModel newDatabaseModel = memberModel.databaseForAllContexts();

            List<String> tablesToRename = new ArrayList<String>();
            String renameDescription = "";

            for (String tableName : newDatabaseModel.getTableNames())
            {
                List<String> newColumnNames = newDatabaseModel.getColumnNames(tableName);
                List<String> oldColumnNames = oldDatabaseModel.getColumnNames(tableName);

                if (newColumnNames.size() == oldColumnNames.size() &&
                        !newColumnNames.equals(oldColumnNames) &&
                        oldLiquibaseDatabaseModel.existsTableColumns(tableName, oldColumnNames))
                {
                    tablesToRename.add(tableName);

                    boolean multi = newColumnNames.size() > 1;

                    renameDescription += "Column" + (multi ? "s" : "") + " " + StringUtil.join(oldColumnNames, ",") +
                            " in table " + tableName + " " + (multi ? "were" : "was") + " renamed to " + StringUtil.join(newColumnNames, ",") + "\n";
                }
            }

			if (!tablesToRename.isEmpty())
			{
				Project project = field.getProject();
				Icon icon = project.getComponent(LiquibaseProjectComponent.class).getIcon();
				String message =
                        "Renamed field caused renaming of columns in database:\n\n" +
                        renameDescription +
                        "\nDo you want to add a changeset to Liquibase renaming these columns?";

				int response = Messages.showYesNoDialog(project, message, "Rename database columns with Liquibase?", icon);

				if (response == 0)
				{
					ChangeSet changeSet = liquibaseModuleComponent.newChangeSet();
					for (String tableName : tablesToRename)
					{
                        List<String> newColumnNames = newDatabaseModel.getColumnNames(tableName);
                        List<String> oldColumnNames = oldDatabaseModel.getColumnNames(tableName);

                        for (int i = 0;i < newColumnNames.size();++i)
                        {
                            RenameColumnAction action = changeSet.addRenameColumn();

                            String newColumnName = newColumnNames.get(i);
                            String oldColumnName = oldColumnNames.get(i);

                            action.getOldColumnName().setStringValue(oldColumnName);
                            action.getNewColumnName().setStringValue(newColumnName);
                            DatabaseColumnModel oldColumnModel = oldDatabaseModel.getColumnModel(tableName, oldColumnName);
                            if (oldColumnModel != null)
                            {
                                action.getColumnDataType().setStringValue(oldColumnModel.getType());
                            }

                            action.getTableName().setStringValue(tableName);
                        }
					}
				}
			}
		}
	}

    private LiquibaseModuleComponent findLiquibaseComponent(PsiElement psiElement)
    {
        return LiquibaseModuleComponent.getInstance(psiElement.getContainingFile());
    }
}
