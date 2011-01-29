package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.intellij.javaee.model.common.persistence.mapping.Entity;
import com.intellij.javaee.model.common.persistence.mapping.JoinColumnBase;
import com.intellij.persistence.model.PersistentEntity;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-29
 * Time: 17:49:07
 */
public class JoinedModel extends InheritanceModel
{
    public JoinedModel(ModuleModel moduleModel, Entity persistentObject, Entity basePersistentObject)
    {
        super(moduleModel, persistentObject, basePersistentObject);
    }

    protected void executeSuperclass(EjbModelContext context, DatabaseModel databaseModel)
    {
        PsiClass psiClass = persistentObject.getClazz().getValue();

        if (psiClass != null)
        {
            PsiClass superClass = psiClass.getSuperClass();
            ClassModel modelForSuperClass = moduleModel.getModelForClass(superClass);

            if (modelForSuperClass instanceof JoinedModel)
            {
                EjbModelContext superContext = createSuperclassContext(context);
                String fkName = "fk_primary";
                String tableName = superContext.getTableName();
                List<? extends JoinColumnBase> joinColumns = persistentObject.getPrimaryKeyJoinColumns();
                MemberModel.createPrimaryKeyJoinColumns(databaseModel, tableName, superClass, joinColumns, "", moduleModel, fkName, true);
            }
        }
    }

}
