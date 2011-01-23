package com.esn.idea.liquibaseejb.intentions;

import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
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
public class AddIndexAction implements IntentionAction
{
	private static final String INDEX_ANNOTATION_NAME = "org.hibernate.annotations.Index";
	private static final String TABLE_ANNOTATION_NAME = "org.hibernate.annotations.Table";

	@NotNull
	public String getText()
	{
		return "Add database index (Template)";
	}

	@NotNull
	public String getFamilyName()
	{
		return "AddIndex";
	}

	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile)
	{
		PsiField field = getElementAtEditor(psiFile, editor, PsiField.class);

		if (field != null)
		{
			if (EsnPsiUtils.hasAnnotation(field.getContainingClass(), Entity.class.getName()))
			{
				if (!EsnPsiUtils.hasAnnotation(field, INDEX_ANNOTATION_NAME))
				{
				  return true;
				}
			}
		}

		PsiClass psiClass = getElementAtEditor(psiFile, editor, PsiClass.class);

		if (psiClass != null)
		{
			if (EsnPsiUtils.hasAnnotation(psiClass, Entity.class.getName()))
			{
				if (!EsnPsiUtils.hasAnnotation(psiClass, TABLE_ANNOTATION_NAME))
				{
					return true;
				}
			}
		}

		return false;
	}

	public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException
	{
		PsiField field = getElementAtEditor(psiFile, editor, PsiField.class);

		if (field != null)
		{
			String indexName = "ix_" + (System.currentTimeMillis() / 1000);
			EsnPsiUtils.addAnnotationFromText(field, "@" + INDEX_ANNOTATION_NAME + "(name=\"" + indexName + "\")");
		}

		PsiClass psiClass = getElementAtEditor(psiFile, editor, PsiClass.class);

		if (psiClass != null)
		{
			String indexName = "ix_" + (System.currentTimeMillis() / 1000);
			String indexAnnotationText = "@" + INDEX_ANNOTATION_NAME + "(name=\"" + indexName + "\", columnNames = {\"\", \"\"})";
			EsnPsiUtils.addAnnotationFromText(psiClass, "@" + TABLE_ANNOTATION_NAME + "(indexes = " + indexAnnotationText + ", appliesTo=\"" + psiClass.getName() + "\")");
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
