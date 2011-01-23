package com.esn.idea.liquibaseejb.javafix;

import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
* Date: 2008-sep-23
* Time: 12:49:04
*/
public final class AddAnnotationElementFix extends AbstractAnnotationFix
{
	private PsiModifierListOwner parent;
	private String annotationName;
	private String attributeName;
	private Object attributeValue;
	private PsiAnnotation annotation;

	public AddAnnotationElementFix(String name, @NotNull PsiModifierListOwner parent, String annotationName, String attributeName, Object attributeValue)
	{
        super(name);
		this.parent = parent;
		this.annotationName = annotationName;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;

		PsiModifierList list = parent.getModifierList();
		if (list != null)
		{
			annotation = list.findAnnotation(annotationName);
		}

	}

	public AddAnnotationElementFix(String name, @NotNull PsiAnnotation annotation, String attributeName, Object attributeValue)
	{
        super(name);
        this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.annotation = annotation;
		parent = (PsiModifierListOwner) annotation.getParent().getParent();
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		PsiManager manager = parent.getManager();
		PsiModifierList list = parent.getModifierList();
		if (list == null) return;

		try
		{
			if (annotation == null)
			{
				annotation = EsnPsiUtils.getElementFactory(project).createAnnotationFromText("@" + annotationName + "(" + attributeName + " = " + EsnPsiUtils.formatValue(attributeValue) + ")", annotation);
				list.addBefore(annotation, list.getFirstChild());
			}
			else
			{
				EsnPsiUtils.addOrModifyAnnotationAttribute(project, annotation, attributeName, attributeValue);
			}
		}
		catch (IncorrectOperationException e)
		{
			e.printStackTrace();
		}
	}
}
