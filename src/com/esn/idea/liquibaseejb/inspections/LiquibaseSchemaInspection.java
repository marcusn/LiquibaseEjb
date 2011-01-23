package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.diff.DiffModel;
import com.esn.idea.liquibaseejb.model.ejb.EjbModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.ClassModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.member.MemberModel;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-13
 * Time: 18:16:16
 */
public class LiquibaseSchemaInspection extends BaseJavaLocalInspectionTool
{
    @Nls
    @NotNull
    public String getGroupDisplayName()
    {
        return "Liquibase EJB Issues";
    }

    @Nls
    @NotNull
    public String getDisplayName()
    {
        return "Liquibase EJB Schema Issues";
    }

    @NonNls
    @NotNull
    public String getShortName()
    {
        return "LiquibaseEJBSchema";
    }

    @Override
    @NotNull
    public HighlightDisplayLevel getDefaultLevel()
    {
        return HighlightDisplayLevel.ERROR;
    }

    @Override
    public boolean isEnabledByDefault()
    {
        return true;
    }

    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile psiFile, @NotNull InspectionManager inspectionManager, boolean b)
    {
        if (psiFile instanceof XmlFile)
        {
            LiquibaseModuleComponent liquibaseModuleComponent = LiquibaseModuleComponent.getInstance(psiFile);
            if (liquibaseModuleComponent != null)
            {
                liquibaseModuleComponent.findDatabaseModel(); // Force reload on
            }
        }

        return new ProblemDescriptor[0];
    }

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass psiClass, @NotNull InspectionManager inspectionManager, boolean b)
    {
        ProblemsHolder problemsHolder = new ProblemsHolder(inspectionManager, psiClass.getContainingFile());
        LiquibaseModuleComponent liquibaseModuleComponent = LiquibaseModuleComponent.getInstance(psiClass);
        if (liquibaseModuleComponent != null && liquibaseModuleComponent.isLiquibaseEnabled())
        {
            liquibaseModuleComponent.updateEjbModel();
            DatabaseModel liquibaseDatabaseModel = liquibaseModuleComponent.findDatabaseModel();
            ModuleModel moduleModel = new ModuleModel(liquibaseModuleComponent.getModule());
            ClassModel classModel = moduleModel.getModelForClass(psiClass);

            if (classModel != null)
            {
                //noinspection unchecked
                Collection<MemberModel> fieldModels = classModel.getFieldModels();
                for (MemberModel memberModel : fieldModels)
                {
                    DatabaseModel declaredDatabaseModel = memberModel.databaseForAllContexts();

                    DiffModel diffModel = new DiffModel(liquibaseDatabaseModel, declaredDatabaseModel);

                    // We do not want warning for tables created by class on field
                    List<String> ignoredTableNames = findTablesForAllContexts(memberModel);

                    PsiMember psiMember = memberModel.getMember();

                    if (psiMember == null) continue;

                    DiffModelInspection[] diffModelInspections = {
                            new MissingTableInspection(ignoredTableNames),
                            new ColumnInspection(psiMember),
                            new ForeignConstraintInspection(),
                            new UniqueConstraintInspection()
                    };

                    PsiElement errorElement = psiMember instanceof PsiField ? (((PsiField) psiMember).getNameIdentifier()) : psiMember;

                    for (DiffModelInspection diffModelInspection : diffModelInspections)
                    {
                        diffModelInspection.check(diffModel, problemsHolder, errorElement, liquibaseModuleComponent, declaredDatabaseModel);
                    }
                }
                final DatabaseModel declaredDatabaseModel = classModel.databaseForAllContexts();

                final PsiIdentifier classIdentifier = psiClass.getNameIdentifier();


                DiffModel diffModel = new DiffModel(liquibaseDatabaseModel, declaredDatabaseModel);

                // TODO: Add primary key fix

                DiffModelInspection inspections[] = new DiffModelInspection[] {
                        new MissingTableInspection(),
                        new MissingColumnsInspection(),
                        new IndexInspection(),
                        new ForeignConstraintInspection(),
                        new UniqueConstraintInspection()
                };

                for (DiffModelInspection inspection : inspections)
                {
                    inspection.check(diffModel, problemsHolder, classIdentifier, liquibaseModuleComponent, declaredDatabaseModel);
                }
            }
        }

        List<ProblemDescriptor> problemHolderResults = problemsHolder.getResults();

        if (problemHolderResults != null)
        {
            return problemHolderResults.toArray(new ProblemDescriptor[problemHolderResults.size()]);
        }
        else
        {
            return new ProblemDescriptor[0];
        }
    }

    private List<String> findTablesForAllContexts(MemberModel memberModel)
    {
        List<String> ignoredTableNames = new ArrayList<String>();

        for (EjbModelContext context : ((EjbModel) memberModel).findAllContexts())
        {
            ignoredTableNames.addAll(context.getAllTableNames());
        }
        return ignoredTableNames;
    }

}
