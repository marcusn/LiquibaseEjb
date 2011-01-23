package com.esn.idea.liquibaseejb.model.ejb.member;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.ClassModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.context.OverridingEjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.intellij.javaee.model.common.persistence.mapping.AttributeOverride;
import com.intellij.javaee.model.common.persistence.mapping.Embedded;
import com.intellij.javaee.model.common.persistence.mapping.EmbeddedId;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiType;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-09
 * Time: 18:55:58
 */
public class EmbeddedModel extends MemberModel<Embedded>
{
    public EmbeddedModel(ModuleModel moduleModel, Embedded attribute)
    {
        super(moduleModel, attribute);
    }

    protected void executeMember(EjbModelContext context, DatabaseModel databaseModel, PsiMember psiMember, PsiType memberType)
    {
        if (memberType instanceof PsiClassType)
        {
            PsiClassType embeddedClassType = (PsiClassType) memberType;

            PsiClass embeddedClass = embeddedClassType.resolve();

            executeClass(context, databaseModel, embeddedClass);
        }
    }

    private void executeClass(EjbModelContext context, DatabaseModel databaseModel, PsiClass psiClass)
    {
        ClassModel idClassModel = moduleModel.getModelForClass(psiClass);

        if (idClassModel != null)
        {
            EjbModelContext subContext = createMemberContext(context);
            idClassModel.execute(subContext, databaseModel);
        }
    }


    public EjbModelContext createMemberContext(EjbModelContext context)
    {
        OverridingEjbModelContext subContext = new OverridingEjbModelContext(context);

        for (AttributeOverride attributeOverride : attribute.getAttributeOverrides())
        {
            subContext.setAttributeOverride(attributeOverride.getName().getStringValue(), attributeOverride.getColumn());
        }

        if (attribute instanceof EmbeddedId)
        {
            subContext.setId(true);
        }

        return subContext;
    }
}
