package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.EjbModel;
import com.intellij.javaee.model.common.persistence.mapping.Entity;
import com.intellij.persistence.model.PersistentEntity;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.openapi.module.Module;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-29
 * Time: 17:49:07
 */
public class TablePerClassModel extends InheritanceModel
{
    public TablePerClassModel(ModuleModel moduleModel, Entity persistentObject, Entity basePersistentObject)
    {
        super(moduleModel, persistentObject, basePersistentObject);
    }


    public Collection<EjbModelContext> findAllContexts()
    {
        Collection<EjbModelContext> res = new ArrayList<EjbModelContext>();

        /**
         * A table per class entity behaves as a mapped superclass and an entity
         */

        // As mapped superclass..
        PsiClass psiClass = persistentObject.getClazz().getValue();

        Module module = moduleModel.getModule();
        SearchScope scope = GlobalSearchScope.moduleScope(module);
        com.intellij.util.Query<PsiClass> query = ClassInheritorsSearch.search(psiClass, scope, true);

        for (PsiClass subClass : query)
        {
            ClassModel subClassModel = moduleModel.getModelForClass(subClass);

            if (subClassModel instanceof TablePerClassModel)
            {
                for (EjbModelContext parentContext : ((EjbModel) subClassModel).findAllContexts())
                {
                    res.add(subClassModel.createSuperclassContext(parentContext));
                }
            }
        }

        // As entity
        res.addAll(super.findAllContexts());

        return res;
    }

}