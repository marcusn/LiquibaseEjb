package com.esn.idea.liquibaseejb.javafix;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-21
 * Time: 18:13:39
 */
public class DropAnnotationFix extends AbstractAnnotationFix
{
	private PsiModifierListOwner owner;
	private String annotationName;

	public DropAnnotationFix(String name, @NotNull PsiModifierListOwner owner, String annotationName)
	{
        super(name);
        this.name = name;
		this.owner = owner;
		this.annotationName = annotationName;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		PsiModifierList modifierList = owner.getModifierList();
		if (modifierList != null)
		{
			PsiAnnotation annotation = modifierList.findAnnotation(annotationName);

			if (annotation != null)
			{
				try
				{
					annotation.delete();
				}
				catch (IncorrectOperationException e)
				{
					e.printStackTrace();
				}
			}
		}

	}
}
