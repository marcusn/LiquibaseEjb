package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.model.diff.DiffModelVisitor;
import com.esn.idea.liquibaseejb.model.diff.DiffModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 13:12:42
 */
public class DiffModelInspection extends DiffModelVisitor
{
    private ProblemsHolder problemsHolder;
    private PsiElement problemElement;
    private DatabaseModel liquibaseDatabaseModel;
    private DatabaseModel declaredDatabaseModel;
    private LiquibaseModuleComponent liquibaseModuleComponent;

    public DiffModelInspection()
    {
    }

    public void check(DiffModel diffModel, ProblemsHolder problemsHolder, PsiElement problemElement, LiquibaseModuleComponent liquibaseModuleComponent, DatabaseModel declaredDatabaseModel)
    {
        this.problemsHolder = problemsHolder;
        this.problemElement = problemElement;
        this.liquibaseDatabaseModel = liquibaseModuleComponent.findDatabaseModel();
        this.declaredDatabaseModel = declaredDatabaseModel;
        this.liquibaseModuleComponent = liquibaseModuleComponent;

        diffModel.accept(this);
    }

    public ProblemsHolder getProblemsHolder()
    {
        return problemsHolder;
    }

    public PsiElement getProblemElement()
    {
        return problemElement;
    }

    public DatabaseModel getLiquibaseDatabaseModel()
    {
        return liquibaseDatabaseModel;
    }

    public DatabaseModel getDeclaredDatabaseModel()
    {
        return declaredDatabaseModel;
    }

    public LiquibaseModuleComponent getLiquibaseModuleComponent()
    {
        return liquibaseModuleComponent;
    }

    protected void registerProblem(String problemDesc, LocalQuickFix... fixes)
    {
        getProblemsHolder().registerProblem(
                getProblemElement(),
                problemDesc,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                fixes);
    }

}
