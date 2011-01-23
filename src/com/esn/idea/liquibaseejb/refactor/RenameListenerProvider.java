package com.esn.idea.liquibaseejb.refactor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-17
 * Time: 12:43:23
 */
public class RenameListenerProvider implements RefactoringElementListenerProvider
{
	@Nullable
	public RefactoringElementListener getListener(PsiElement psiElement)
	{
		if (psiElement instanceof PsiField) return new FieldRenameListener((PsiField)psiElement);
		if (psiElement instanceof PsiClass) return new ClassRenameListener((PsiClass)psiElement);

		return null;
	}

}
