package com.esn.idea.liquibaseejb.model.ejb;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.context.OverridingEjbModelContext;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;

import javax.persistence.TableGenerator;
import java.util.Collection;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-30
 * Time: 08:45:02
 */
public abstract class EjbModel
{
    public abstract void execute(EjbModelContext context, DatabaseModel databaseModel);

    public abstract Collection<EjbModelContext> findAllContexts();

    public DatabaseModel databaseForAllContexts()
    {
        DatabaseModel databaseModel = new DatabaseModel();
        executeAllContexts(databaseModel);
        return databaseModel;
    }

    public void executeAllContexts(DatabaseModel databaseModel)
    {
        for (EjbModelContext context : findAllContexts())
        {
            context.execute(databaseModel);

            execute(context, databaseModel);
        }
    }

    static protected void executeGenerators(EjbModelContext context, DatabaseModel databaseModel, PsiModifierListOwner psiElement)
    {
        if (context.isOnlyId()) return;

        PsiModifierList modifierList = psiElement.getModifierList();

        if (modifierList != null)
        {
            PsiAnnotation tableGeneratorAnnotation = modifierList.findAnnotation(TableGenerator.class.getName());

            if (tableGeneratorAnnotation != null)
            {
                String tableName = EsnPsiUtils.evalAttributeValue(tableGeneratorAnnotation, "table", String.class);
                String pkColumnName = EsnPsiUtils.evalAttributeValue(tableGeneratorAnnotation, "pkColumnName", String.class);
                String valueColumnName = EsnPsiUtils.evalAttributeValue(tableGeneratorAnnotation, "valueColumnName", String.class);

                if (isNonEmpty(tableName) && isNonEmpty(pkColumnName) && isNonEmpty(valueColumnName))
                {
                    databaseModel.addTable(tableName);
                    databaseModel.addColumn(tableName, pkColumnName, new DatabaseColumnModel("VARCHAR(255)"));
                    databaseModel.addColumn(tableName, valueColumnName, new DatabaseColumnModel("INT"));

                    OverridingEjbModelContext subContext = new OverridingEjbModelContext();
                    subContext.setTableName(tableName);

                    executeAnnotationUniqueConstraints(subContext, databaseModel, tableGeneratorAnnotation);
                }
            }
        }
    }

    private static boolean isNonEmpty(String tableName)
    {
        return tableName != null && !"".equals(tableName);
    }

    protected static void executeAnnotationUniqueConstraints(EjbModelContext context, DatabaseModel databaseModel, PsiAnnotation annotation)
    {
        int i = 0;
        Collection<PsiAnnotationMemberValue> initializerValues = EsnPsiUtils.getInitializerValues(annotation.findAttributeValue("uniqueConstraints"));

        for (PsiAnnotationMemberValue initializerValue : initializerValues)
        {
            if (initializerValue instanceof PsiAnnotation)
            {
                PsiAnnotation uniqueConstraintAnnotation = (PsiAnnotation) initializerValue;

                List<String> columnNames = EsnPsiUtils.getInitializerValues(uniqueConstraintAnnotation.findAttributeValue("columnNames"), String.class);

                databaseModel.addUniqueConstraint(context.getTableName(), "uc_" + i, columnNames);
            }
        }
    }
}
