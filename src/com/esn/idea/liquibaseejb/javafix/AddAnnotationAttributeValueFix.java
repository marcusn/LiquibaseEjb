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
* Date: 2008-sep-25
* Time: 08:12:36
*/
public class AddAnnotationAttributeValueFix extends AbstractAnnotationFix
{
	private Object attributeValue;
	private PsiModifierListOwner parent;
	private String annotationName;
	private String attributeName;
	private PsiAnnotation annotation;

	public AddAnnotationAttributeValueFix(String name, @NotNull PsiModifierListOwner parent, String annotationName, String attributeName, Object attributeValue)
	{
		super(name);
		this.annotationName = annotationName;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.parent = parent;
		PsiModifierList list = parent.getModifierList();
		if (list != null)
		{
			annotation = list.findAnnotation(annotationName);
		}

	}

	public AddAnnotationAttributeValueFix(String name, @NotNull PsiModifierListOwner parent, @NotNull PsiAnnotation annotation, String attributeName, Object attributeValue)
	{
		super(name);
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.annotation = annotation;
		this.parent = parent;
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
				EsnPsiUtils.addToAnnotationValueArray(project, annotation, attributeName, attributeValue);
			}
		}
		catch (IncorrectOperationException e)
		{
			e.printStackTrace();
		}

	}
}
