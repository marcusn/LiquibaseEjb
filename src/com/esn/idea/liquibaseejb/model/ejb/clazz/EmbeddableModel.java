package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModel;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.javaee.model.common.persistence.mapping.PersistentObject;
import com.intellij.javaee.model.common.persistence.mapping.Embeddable;
import com.intellij.psi.*;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedMembersSearch;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-09
 * Time: 17:58:06
 */
public class EmbeddableModel extends ClassModel<Embeddable>
{
    public EmbeddableModel(ModuleModel moduleModel, Embeddable persistentObject)
    {
        super(moduleModel, persistentObject);
    }

    public Collection<EjbModelContext> findAllContexts()
    {
        List<EjbModelContext> res = new ArrayList<EjbModelContext>();

        PsiClass psiClass = persistentObject.getClazz().getValue();

        if (psiClass != null)
        {
            PsiFile containingFile = psiClass.getContainingFile();
            if (containingFile != null)
            {
                VirtualFile containingVirtualFile = containingFile.getVirtualFile();
                if (containingVirtualFile != null)
                {
                    Module module = ModuleUtil.findModuleForFile(containingVirtualFile, psiClass.getProject());
                    if (module != null)
                    {
                        SearchScope scope = GlobalSearchScope.moduleScope(module);

                        addMembersOfClassTypeWithAnnotation(res, psiClass, scope, Embedded.class.getName());
                        addMembersOfClassTypeWithAnnotation(res, psiClass, scope, EmbeddedId.class.getName());
                    }
                }
            }
        }

        return res;
    }

    private void addMembersOfClassTypeWithAnnotation(List<EjbModelContext> res, PsiClass psiClass, SearchScope scope, String annotationName)
    {
        PsiClass embeddedClass = EsnPsiUtils.getPsiFacade(psiClass).getResolveHelper().resolveReferencedClass(annotationName, psiClass);
        if (embeddedClass != null)
        {
            addMembersOfClassType(res, psiClass, AnnotatedMembersSearch.search(embeddedClass, scope));
        }
    }

    private void addMembersOfClassType(List<EjbModelContext> res, PsiClass psiClass, com.intellij.util.Query<PsiMember> query)
    {
        for (PsiMember member : query)
        {
            if (member instanceof PsiField)
            {
                PsiType fieldType = ((PsiField) member).getType();

                if (fieldType instanceof PsiClassType)
                {
                    PsiClass fieldClass = ((PsiClassType) fieldType).resolve();

                    if (fieldClass == psiClass)
                    {
                        MemberModel memberModel = moduleModel.getModelForMember(member);
                        if (memberModel != null)
                        {
                            Collection<EjbModelContext> parentContexts = memberModel.findAllContexts();
                            for (EjbModelContext parentContext : parentContexts)
                            {
                                res.add(memberModel.createMemberContext(parentContext));
                            }
                        }
                    }
                }
            }
        }
    }


    public EjbModelContext createMemberContext(EjbModelContext parent)
    {
        return parent;
    }
}
