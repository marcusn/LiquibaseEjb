package com.esn.idea.liquibaseejb.javafix;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
* Date: 2008-sep-25
* Time: 13:04:11
*/
public class AddStatementFix extends AbstractFix
{
	private PsiElement newStatement;

	public AddStatementFix(String name, @NotNull PsiElement newStatement)
	{
		super(name, "AddStatement");
		this.newStatement = newStatement;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		PsiExpressionStatement statement = PsiTreeUtil.getParentOfType(problemDescriptor.getPsiElement(), PsiExpressionStatement.class);

        if (statement != null)
		{
			try
			{
				statement.getParent().addAfter(newStatement, statement);
			}
			catch (IncorrectOperationException e)
			{
				e.printStackTrace();
			}
		}
	}
}
