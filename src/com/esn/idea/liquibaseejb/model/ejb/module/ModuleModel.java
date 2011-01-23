package com.esn.idea.liquibaseejb.model.ejb.module;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.ejb.EjbModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.*;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.context.DefaultEjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.context.OverridingEjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModel;
import com.intellij.facet.FacetManager;
import com.intellij.javaee.model.common.persistence.mapping.*;
import com.intellij.javaee.model.common.persistence.mapping.Entity;
import com.intellij.javaee.model.common.persistence.mapping.MappedSuperclass;
import com.intellij.javaee.model.common.persistence.mapping.EntityMappings;
import com.intellij.javaee.model.common.persistence.mapping.Embeddable;
import com.intellij.javaee.model.xml.persistence.PersistenceUnit;
import com.intellij.jpa.facet.JpaFacet;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.persistence.model.helpers.PersistentEntityModelHelper;
import com.intellij.persistence.model.PersistentEntity;
import com.intellij.persistence.model.PersistenceInheritanceType;

import java.util.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-30
 * Time: 09:16:27
 */
public class ModuleModel extends EjbModel
{
    private JpaFacet jpaFacet;
    private PersistenceUnit unit;

    private Map<PsiClass, ClassModel> classToModel = new HashMap<PsiClass, ClassModel>();
    private Map<PsiMember, MemberModel> memberToModel = new HashMap<PsiMember, MemberModel>();
    private Module module;

    public ModuleModel(Module module, PersistenceUnit unit)
    {
        this(module);
        this.unit = unit;
    }

    public ModuleModel(Module module)
    {
        this.module = module;

        FacetManager facetManager = FacetManager.getInstance(module);
        jpaFacet = facetManager.getFacetByType(JpaFacet.ID);


        // TODO: Support mapping of liquibase file to persistence unit
        List<PersistenceUnit> units = null;
        if (jpaFacet != null)
        {
            units = jpaFacet.getPersistenceUnits();

            if (units.size() > 0)
            {
                this.unit = units.get(0);

                EntityMappings entityMappings = jpaFacet.getEntityMappings(unit);

                Map<PsiClass, Entity> classToInheritanceBaseEntity = new HashMap<PsiClass, Entity>();

                for (Entity entity : entityMappings.getEntities())
                {
                    PersistentEntityModelHelper entityModelHelper = ((PersistentEntity) entity).getObjectModelHelper();

                    PersistenceInheritanceType inheritanceType = entityModelHelper.getInheritanceType(entity);

                    if (inheritanceType != null)
                    {
                        SearchScope scope = GlobalSearchScope.moduleScope(module);
                        PsiClass entityClass = entity.getClazz().getValue();
                        com.intellij.util.Query<PsiClass> query = ClassInheritorsSearch.search(entityClass, scope, true);

                        for (PsiClass subClass : query)
                        {
                            classToInheritanceBaseEntity.put(subClass, entity);
                        }

                        classToInheritanceBaseEntity.put(entityClass, entity);
                    }
                }


                for (Entity entity : entityMappings.getEntities())
                {
                    Entity baseEntity = classToInheritanceBaseEntity.get(entity.getClazz().getValue());

                    EntityModel entityModel = null;
                    if (baseEntity != null)
                    {
                        PersistentEntityModelHelper entityModelHelper = ((PersistentEntity) baseEntity).getObjectModelHelper();

                        PersistenceInheritanceType inheritanceType = entityModelHelper.getInheritanceType(baseEntity);
                        if (inheritanceType != null)
                        {
                            switch (inheritanceType)
                            {
                                case JOINED:
                                    entityModel = new JoinedModel(this, entity, baseEntity);
                                    break;
                                case SINGLE_TABLE:
                                    entityModel = new SingleTableModel(this, entity, baseEntity);
                                    break;
                                case TABLE_PER_CLASS:
                                    entityModel = new TablePerClassModel(this, entity, baseEntity);
                                    break;
                            }
                        }
                    }

                    if (entityModel == null)
                    {
                        entityModel = new EntityModel(this, entity);
                    }

                    classToModel.put(entity.getClazz().getValue(), entityModel);
                }

                for (Embeddable embeddable : entityMappings.getEmbeddables())
                {
                    classToModel.put(embeddable.getClazz().getValue(), new EmbeddableModel(this, embeddable));
                }

                for (MappedSuperclass mappedSuperclass : entityMappings.getMappedSuperclasses())
                {
                    classToModel.put(mappedSuperclass.getClazz().getValue(), new MappedSuperclassModel(this, mappedSuperclass));
                }

                for (ClassModel classModel : classToModel.values())
                {
                    Collection<MemberModel> fieldModels = classModel.getFieldModels();
                    for (MemberModel memberModel : fieldModels)
                    {
                        memberToModel.put(memberModel.getMember(), memberModel);
                    }
                }
            }
        }
    }

    public static ModuleModel getInstance(LiquibaseModuleComponent liquibaseModuleComponent)
    {
        return new ModuleModel(liquibaseModuleComponent.getModule());
    }

    public PersistentObject getPersistentObjectForClass(PsiClass psiClass)
    {
        ClassModel modelForClass = getModelForClass(psiClass);

        return modelForClass != null ? modelForClass.getPersistentObject() : null;
    }


    public ClassModel getModelForClass(PsiClass psiClass)
    {
        return classToModel.get(psiClass);
    }

    public MemberModel getModelForMember(PsiMember psiMember)
    {
        return memberToModel.get(psiMember);
    }

    public void execute(EjbModelContext context, DatabaseModel databaseModel)
    {
        for (ClassModel classModel : classToModel.values())
        {
            if (classModel instanceof EntityModel)
            {
                classModel.execute(context, databaseModel);

            }
        }
    }

    public Collection<EjbModelContext> findAllContexts()
    {
        EjbModelContext ejbModelContext = new DefaultEjbModelContext();
        return Arrays.asList(ejbModelContext);
    }

    public Module getModule()
    {
        return module;
    }

    public List<DatabaseField> getPrimaryKeyFieldsForClass(PsiClass memberClass)
    {
        ClassModel memberClassModel = getModelForClass(memberClass);

        if (memberClassModel != null)
        {
            OverridingEjbModelContext context = new OverridingEjbModelContext();
            context.setOnlyId(true);
            DatabaseModel targetDatabaseModel = new DatabaseModel();
            memberClassModel.execute(context, targetDatabaseModel);

            List<String> tableNames = targetDatabaseModel.getTableNames();

            if (tableNames.size() == 1)
            {
                return targetDatabaseModel.getTableFields(tableNames.get(0));
            }
        }

        return Collections.emptyList();
    }
}
