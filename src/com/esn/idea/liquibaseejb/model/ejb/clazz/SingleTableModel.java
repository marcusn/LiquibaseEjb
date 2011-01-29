package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.context.OverridingEjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.member.AttributeWithColumnModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.javaee.model.common.persistence.mapping.Entity;
import com.intellij.javaee.model.common.persistence.mapping.DiscriminatorColumn;
import com.intellij.javaee.model.xml.persistence.mapping.DiscriminatorType;
import com.intellij.persistence.model.PersistentEntity;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.openapi.module.Module;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-29
 * Time: 17:49:07
 */
public class SingleTableModel extends InheritanceModel
{
    public SingleTableModel(ModuleModel moduleModel, Entity persistentObject, Entity basePersistentObject)
    {
        super(moduleModel, persistentObject, basePersistentObject);
    }

    protected void executeSuperclass(EjbModelContext context, DatabaseModel databaseModel)
    {
        // Do not execute superclasses
        return;
    }

    public void execute(EjbModelContext context, DatabaseModel databaseModel)
    {
        if (context.getTableName() == null && persistentObject != basePersistentObject)
        {
            return;
        }

        super.execute(context, databaseModel);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EjbModelContext createMemberContext(EjbModelContext parent)
    {
        EjbModelContext context = super.createMemberContext(parent);
        if (persistentObject == basePersistentObject)
        {
            OverridingEjbModelContext subContext = new OverridingEjbModelContext(context);

            // 2.1.10.1 All fields must be nullable in single table strateigy
            subContext.setOverrideNullable(true);

            context = subContext;

        }

        return context;
    }

    public void executeMembers(EjbModelContext context, DatabaseModel databaseModel)
    {
        if (persistentObject == basePersistentObject)
        {
            createDiscriminator(createMemberContext(context), databaseModel);
        }

        super.executeMembers(context, databaseModel);

        EjbModelContext memberContext = createMemberContext(context);

        // Execute subclass members as well

        PsiClass psiClass = persistentObject.getClazz().getValue();

        Module module = moduleModel.getModule();
        SearchScope scope = GlobalSearchScope.moduleScope(module);
        com.intellij.util.Query<PsiClass> query = ClassInheritorsSearch.search(psiClass, scope, true);

        for (PsiClass subClass : query)
        {
            ClassModel subClassModel = moduleModel.getModelForClass(subClass);

            if (subClassModel instanceof SingleTableModel)
            {
                subClassModel.execute(memberContext, databaseModel);
            }
        }
    }

    private void createDiscriminator(EjbModelContext context, DatabaseModel databaseModel)
    {
        DiscriminatorColumn discriminatorColumn = persistentObject.getDiscriminatorColumn();
        String name = discriminatorColumn.getName().getValue();

        if (name == null || name.isEmpty()) name="DTYPE";

        DiscriminatorType discriminatorType = discriminatorColumn.getDiscriminatorType().getValue();
        if (discriminatorType == null) discriminatorType = DiscriminatorType.STRING;

        String databaseType = AttributeWithColumnModel.stringTypeFromLength(31);

        switch (discriminatorType)
        {
            case CHAR:
                databaseType = "CHAR(1)";
                break;
            case INTEGER:
                databaseType = "INT";
                break;
            case STRING:
                PsiClass psiClass = persistentObject.getClazz().getValue();

                if (psiClass != null)
                {
                    PsiAnnotation discriminatorAnnotation = psiClass.getModifierList().findAnnotation(javax.persistence.DiscriminatorColumn.class.getName());

                    if (discriminatorAnnotation != null)
                    {
                        Integer length = EsnPsiUtils.evalAttributeValue(discriminatorAnnotation, "length", Integer.class);

                        if (length != null)
                        {
                            databaseType = AttributeWithColumnModel.stringTypeFromLength(length);
                        }

                        String columnDefinition = EsnPsiUtils.evalAttributeValue(discriminatorAnnotation, "columnDefinition", String.class);

                        if (columnDefinition != null && !columnDefinition.isEmpty())
                        {
                            databaseType = columnDefinition;
                        }
                    }
                }
                break;
        }

        DatabaseColumnModel columnModel = new DatabaseColumnModel(databaseType);
        columnModel.setNotNull(true);

        databaseModel.addColumn(context.getTableName(), name, columnModel);
    }

    public Collection<EjbModelContext> findAllContexts()
    {
        if (persistentObject == basePersistentObject)
        {
            // Root of hierarchy, behave as an entity with subclasses as mapped superclasses
            return super.findAllContexts();
        }


        PsiClass psiClass = persistentObject.getClazz().getValue();

        Collection<EjbModelContext> res = new ArrayList<EjbModelContext>();
        if (psiClass != null)
        {
            PsiClass superClass = psiClass.getSuperClass();

            ClassModel modelForSuperClass = moduleModel.getModelForClass(superClass);

            if (modelForSuperClass instanceof SingleTableModel)
            {
                SingleTableModel parentModel = (SingleTableModel) modelForSuperClass;

                for (EjbModelContext parentContext : parentModel.findAllContexts())
                {
                    res.add(parentModel.createMemberContext(parentContext));
                }
            }
        }


        return res;
    }

}