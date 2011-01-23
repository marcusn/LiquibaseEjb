package com.esn.idea.liquibaseejb.refactor;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.RenameTableAction;
import com.esn.idea.liquibaseejb.model.ejb.clazz.ClassModel;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.EntityModel;
import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.LiquibaseProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.text.MessageFormat;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-22
 * Time: 10:13:37
 */
public class ClassRenameListener implements RefactoringElementListener
{
	private String oldTableName;

	public ClassRenameListener(@NotNull PsiClass psiClass)
	{
        ClassModel classModel = findClassModel(psiClass);

        if (classModel instanceof EntityModel)
        {
            oldTableName = ClassModel.classToTableName(psiClass);
        }
	}

    private LiquibaseModuleComponent findLiquibaseComponent(PsiElement psiElement)
    {
        return LiquibaseModuleComponent.getInstance(psiElement.getContainingFile());
    }


    private ClassModel findClassModel(PsiClass psiClass)
    {
        LiquibaseModuleComponent liquibaseModuleComponent = findLiquibaseComponent(psiClass);

        if (liquibaseModuleComponent == null) return null;

        ModuleModel moduleModel = ModuleModel.getInstance(liquibaseModuleComponent);

        if (moduleModel == null) return null;

        return moduleModel.getModelForClass(psiClass);
    }


    public void elementMoved(PsiElement psiElement)
	{

	}

	public void elementRenamed(PsiElement psiElement)
	{
		LiquibaseModuleComponent liquibaseModuleComponent = LiquibaseModuleComponent.getInstance(psiElement.getContainingFile());

		if (psiElement instanceof PsiClass && liquibaseModuleComponent != null)
		{
			PsiClass psiClass = (PsiClass) psiElement;

            ClassModel classModel = findClassModel(psiClass);

            if (classModel == null) return;

            String newTableName = ClassModel.classToTableName(psiClass);

            if (newTableName != null && oldTableName != null && !newTableName.equals(oldTableName))
			{

				DatabaseModel databaseModel = liquibaseModuleComponent.findDatabaseModel();
				if (databaseModel.existsTable(oldTableName))
				{
					Project project = psiClass.getProject();
					Icon icon = project.getComponent(LiquibaseProjectComponent.class).getIcon();
					String message = MessageFormat.format("A class corresponding to table {0} was renamed\n\nDo you want to add a changeset to Liquibase renaming table {0} to {1}?",
									oldTableName,
									newTableName);

					int response = Messages.showYesNoDialog(project, message, "Rename database table with Liquibase?", icon);

					if (response == 0)
					{
						ChangeSet changeSet = liquibaseModuleComponent.newChangeSet();
						RenameTableAction action = changeSet.addRenameTable();
						action.getOldTableName().setStringValue(oldTableName);
						action.getNewTableName().setStringValue(newTableName);
					}
				}
			}
		}
	}
}
