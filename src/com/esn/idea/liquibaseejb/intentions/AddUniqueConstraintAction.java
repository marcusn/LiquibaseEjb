package com.esn.idea.liquibaseejb.intentions;

import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-22
 * Time: 14:12:41
 */
public class AddUniqueConstraintAction implements IntentionAction
{
	@NotNull
	public String getText()
	{
		return "Add database unique constraint (Template)";
	}

	@NotNull
	public String getFamilyName()
	{
		return "AddUniqueConstraint";
	}

	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile)
	{
		PsiClass psiClass = getElementAtEditor(psiFile, editor, PsiClass.class);

		if (psiClass != null)
		{
			if (EsnPsiUtils.hasAnnotation(psiClass, Entity.class.getName()))
			{
				if (!EsnPsiUtils.hasAnnotation(psiClass, Table.class.getName()))
				{
					return true;
				}
			}
		}

		return false;
	}

	public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException
	{
		PsiClass psiClass = getElementAtEditor(psiFile, editor, PsiClass.class);

				if (psiClass != null)
		{
			String indexAnnotationText = "@" + UniqueConstraint.class.getName() + "(columnNames = {\"\", \"\"})";
			EsnPsiUtils.addAnnotationFromText(psiClass, "@" + Table.class.getName() + "(uniqueConstraints = " + indexAnnotationText + ")");
		}
	}

	private <T extends PsiElement> T getElementAtEditor(PsiFile psiFile, Editor editor, Class<T> elementClass)
	{
		PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());

		return PsiTreeUtil.getParentOfType(psiElement, elementClass);
	}

	public boolean startInWriteAction()
	{
		return true;
	}
}