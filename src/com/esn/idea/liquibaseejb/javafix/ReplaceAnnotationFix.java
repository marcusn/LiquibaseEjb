package com.esn.idea.liquibaseejb.javafix;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
* Date: 2008-okt-16
* Time: 16:16:37
*/
public class ReplaceAnnotationFix extends AbstractAnnotationFix
{
	private PsiAnnotation annotation;
	private String newAnnotationName;

	public ReplaceAnnotationFix(@NotNull PsiAnnotation annotation, String newAnnotationName)
	{
        super("Replace " + annotation.getQualifiedName() + " with " + newAnnotationName);
        this.annotation = annotation;
		this.newAnnotationName = newAnnotationName;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		PsiManager manager = PsiManager.getInstance(project);
		PsiElementFactory elementFactory = EsnPsiUtils.getElementFactory(project);
		try
		{
			PsiAnnotation newAnnotation = elementFactory.createAnnotationFromText("@" + newAnnotationName, annotation);

			annotation.replace(newAnnotation);
		}
		catch (IncorrectOperationException e)
		{
			e.printStackTrace();
		}
	}
}
