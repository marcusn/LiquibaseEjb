package com.esn.idea.liquibaseejb.model.ejb.member;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.ClassModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.EntityModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.intellij.javaee.model.common.persistence.mapping.JoinTable;
import com.intellij.javaee.model.common.persistence.mapping.RelationAttributeBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-09
 * Time: 19:03:48
 */
public class AnyToManyModel extends RelationModel<RelationAttributeBase.AnyToManyBase>
{
    public AnyToManyModel(ModuleModel moduleModel, RelationAttributeBase.AnyToManyBase attribute)
    {
        super(moduleModel, attribute);
    }

    protected void executeMember(EjbModelContext context, DatabaseModel databaseModel, PsiMember psiMember, PsiType memberType)
    {
        String mappedBy = attribute.getMappedBy().getStringValue();
        if ("".equals(mappedBy) && memberType instanceof PsiClassType)
        {
            int parameterIndex = 0;
            if (memberType.getCanonicalText().startsWith(Map.class.getName())) parameterIndex = 1;

            PsiType[] parameterTypes = ((PsiClassType) memberType).getParameters();
            if (parameterTypes.length > parameterIndex && parameterTypes[parameterIndex] instanceof PsiClassType)
            {
                PsiClass targetClass = ((PsiClassType)parameterTypes[parameterIndex]).resolve();
                ClassModel targetModel = moduleModel.getModelForClass(targetClass);
                PsiClass thisClass = psiMember.getContainingClass();
                ClassModel thisModel = moduleModel.getModelForClass(thisClass);


                if (targetClass != null && targetModel instanceof EntityModel && thisModel instanceof EntityModel)
                {
                    EntityModel targetEntityModel = (EntityModel) targetModel;
                    EntityModel thisEntityModel = (EntityModel) thisModel;

                    String thisTableName = context.getTableName();

                    String targetTableName = ClassModel.classToTableName(targetClass);

                    String joinTableName = thisTableName + "_" + targetTableName;
                    JoinTable joinTableSpec = attribute.getJoinTable();
                    String overridingTableName = joinTableSpec.getTableName().getStringValue();
                    if (overridingTableName != null && !overridingTableName.isEmpty())
                    {
                        joinTableName = overridingTableName;
                    }

                    /**
                     * Add join table by executing the two id fields with the join table context.
                     */

                    databaseModel.addTable(joinTableName);

                    List<String> joinColumns = createJoinColumns(databaseModel, joinTableName, thisClass, attribute.getJoinTable().getJoinColumns(), thisEntityModel.entityName() + "_", false, true, moduleModel, "fk_" + attribute.getName());
                    List<String> inverseJoinColumns = createJoinColumns(databaseModel, joinTableName, targetClass, joinTableSpec.getInverseJoinColumns(), psiMember.getName() + "_", false, true, moduleModel, "fk_" + attribute.getName());

                    if (attribute instanceof com.intellij.javaee.model.common.persistence.mapping.OneToMany)
                    {
                        databaseModel.addUniqueConstraint(joinTableName, null, inverseJoinColumns);
                    }
                    else
                    {
                        List<String> allJoinColumns = new ArrayList<String>();
                        allJoinColumns.addAll(inverseJoinColumns);
                        allJoinColumns.addAll(joinColumns);

                        databaseModel.addUniqueConstraint(joinTableName, null, allJoinColumns);
                    }
                }
            }
        }
    }
}
