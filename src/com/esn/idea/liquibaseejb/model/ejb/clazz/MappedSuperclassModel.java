package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.ejb.clazz.ClassModel;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.EjbModel;
import com.intellij.javaee.model.common.persistence.mapping.PersistentObject;
import com.intellij.javaee.model.common.persistence.mapping.MappedSuperclass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.module.Module;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-09
 * Time: 17:53:26
 */
public class MappedSuperclassModel extends ClassModel<MappedSuperclass>
{
    public MappedSuperclassModel(ModuleModel moduleModel, MappedSuperclass persistentObject)
    {
        super(moduleModel, persistentObject);
    }

    public Collection<EjbModelContext> findAllContexts()
    {
        Collection<EjbModelContext> res = new ArrayList<EjbModelContext>();

        PsiClass psiClass = persistentObject.getClazz().getValue();

        Module module = moduleModel.getModule();
        SearchScope scope = GlobalSearchScope.moduleScope(module);
        com.intellij.util.Query<PsiClass> query = ClassInheritorsSearch.search(psiClass, scope, true);

        for (PsiClass subClass : query)
        {
            ClassModel subClassModel = moduleModel.getModelForClass(subClass);

            if (subClassModel instanceof EntityModel)
            {
                for (EjbModelContext parentContext : ((EjbModel) subClassModel).findAllContexts())
                {
                    res.add(subClassModel.createSuperclassContext(parentContext));
                }
            }
        }

        return res;
    }

    public EjbModelContext createMemberContext(EjbModelContext parent)
    {
        return parent;
    }
}
