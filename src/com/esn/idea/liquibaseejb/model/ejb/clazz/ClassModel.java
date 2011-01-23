package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.ejb.EjbModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModel;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModelFactory;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.intellij.javaee.model.common.persistence.mapping.PersistentObject;
import com.intellij.psi.PsiClass;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-30
 * Time: 09:22:05
 */
public abstract class ClassModel<T extends PersistentObject> extends EjbModel
{
    protected ModuleModel moduleModel;
    protected T persistentObject;

    public ClassModel(ModuleModel moduleModel, T persistentObject)
    {
        this.moduleModel = moduleModel;
        this.persistentObject = persistentObject;
    }

    public Collection<MemberModel> getFieldModels()
    {
        final Collection<MemberModel> res = new ArrayList<MemberModel>();

        persistentObject.visitAttributes(new MemberModelFactory(moduleModel, res));

        return res;
    }

    public void execute(EjbModelContext context, DatabaseModel databaseModel)
    {
        executeMembers(context, databaseModel);

        executeSuperclass(context, databaseModel);
    }

    protected void executeSuperclass(EjbModelContext context, DatabaseModel databaseModel)
    {
        PsiClass psiClass = persistentObject.getClazz().getValue();

        if (psiClass != null)
        {
            PsiClass superClass = psiClass.getSuperClass();

            ClassModel modelForSuperClass = moduleModel.getModelForClass(superClass);

            if (modelForSuperClass != null)
            {
                EjbModelContext superContext = createSuperclassContext(context);

                modelForSuperClass.execute(superContext, databaseModel);
            }
        }
    }

    protected void executeMembers(EjbModelContext context, DatabaseModel databaseModel)
    {
        EjbModelContext memberContext = createMemberContext(context);

        Collection<MemberModel> memberModels = getFieldModels();
        for (MemberModel memberModel : memberModels)
        {
            if (context.isOnlyId() && !memberModel.willCreateIdField(context))
            {
                continue;
            }
            memberModel.execute(memberContext, databaseModel);

        }
    }

    public abstract EjbModelContext createMemberContext(EjbModelContext parent);

    public abstract Collection<EjbModelContext> findAllContexts();

    public EjbModelContext createSuperclassContext(EjbModelContext context)
    {
        return createMemberContext(context);
    }


    public static String classToTableName(PsiClass psiClass)
    {
        return psiClass.getName();
    }

    public PersistentObject getPersistentObject()
    {
        return persistentObject;
    }

    public Collection<MemberModel> getIdMembers()
    {
        Collection<MemberModel> res = new ArrayList<MemberModel>();
        for (MemberModel memberModel : getFieldModels())
        {
            if (memberModel.getAttribute().getAttributeModelHelper().isIdAttribute())
            {
               res.add(memberModel);
            }
        }

        return res;
    }

}
