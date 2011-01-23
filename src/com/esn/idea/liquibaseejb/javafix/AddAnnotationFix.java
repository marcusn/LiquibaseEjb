package com.esn.idea.liquibaseejb.javafix;

import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiModifierList;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
* Date: 2008-sep-23
* Time: 12:49:04
*/
public final class AddAnnotationFix extends AbstractAnnotationFix
{
	private String name;

    @NotNull
    private PsiModifierListOwner parent;
	private String annotationName;

	public AddAnnotationFix(String name, @NotNull PsiModifierListOwner parent, String annotationName)
	{
        super(name);
		this.parent = parent;
		this.annotationName = annotationName;
	}

	public AddAnnotationFix(String name, @NotNull PsiModifierListOwner parent, Class annotationClass)
	{
        super(name);
		this.parent = parent;
		this.annotationName = annotationClass.getName();
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		PsiManager manager = parent.getManager();

		try
		{
            PsiModifierList modifierList = parent.getModifierList();
            if (modifierList != null)
            {
                EsnPsiUtils.addAnnotation(EsnPsiUtils.getElementFactory(project), modifierList, annotationName);
            }
        }
		catch (IncorrectOperationException e)
		{
			e.printStackTrace();
		}
	}
}