package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.javafix.AddAnnotationElementFix;
import com.esn.idea.liquibaseejb.javafix.DropAnnotationFix;
import com.esn.idea.liquibaseejb.javafix.ReplaceAnnotationFix;
import com.esn.idea.liquibaseejb.fix.*;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraint;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifierList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-03
 * Time: 18:20:11
 */
public class ColumnInspection extends DiffModelInspection
{
    private PsiMember member;
    private PsiModifierList modifierList;

    public ColumnInspection(PsiMember member)
    {
        this.member = member;
        this.modifierList = member.getModifierList();
    }

    public void visitCreateColumn(String tableName, String columnName)
    {
        DatabaseField declaredField = getDeclaredField(tableName, columnName);

        AddColumnFix fix = new AddColumnFix(getLiquibaseModuleComponent(), declaredField, "Add column " + columnName + " in table " + tableName + " with type " + declaredField.getColumnModel().getType());

        String problemDesc = "Database column " + columnName + " in table " + tableName + " is not created by Liquibase";
        registerProblem(problemDesc, fix);
    }

    private DatabaseField getDeclaredField(String tableName, String columnName)
    {
        return new DatabaseField(tableName, columnName, getDeclaredDatabaseModel().getColumnModel(tableName, columnName));
    }

    public void visitColumnNotNull(String tableName, String columnName, boolean oldNotNull, boolean newNotNull)
    {
        String problemPrefix = "Database column " + columnName + " in table " + tableName + " created by Liquibase ";

        DatabaseField declaredField = getDeclaredField(tableName, columnName);

        LocalQuickFix fixDeclaration =
                new AddAnnotationElementFix(
                        "Declare field as " + (oldNotNull ? "not nullable" : "nullable"),
                        member,
                        Column.class.getName(),
                        "nullable",
                        (oldNotNull ? "false" : "true"));

        PsiAnnotation toOneAnnotation = modifierList.findAnnotation(OneToOne.class.getName());
        if (toOneAnnotation == null) toOneAnnotation = modifierList.findAnnotation(ManyToOne.class.getName());
        if (toOneAnnotation != null)
        {
            fixDeclaration =
                    new AddAnnotationElementFix(
                            "Declare field as " + (oldNotNull ? "non-optional" : "optional"),
                            toOneAnnotation,
                            "optional",
                            oldNotNull ? "false" : "true");
        }

        registerProblem(problemPrefix + (oldNotNull ? " is not nullable" : "is nullable"),
                new AddModifyNullableFix(getLiquibaseModuleComponent(), declaredField, newNotNull),
                fixDeclaration);
    }

    public void visitColumnType(String tableName, String columnName, String oldType, String newType)
    {
        registerProblem(
                "Database type for " + columnName + " in table " + tableName + " created by Liquibase is " + oldType + " but declared type is " + newType,
                new AddModifyColumnFix(getLiquibaseModuleComponent(), getDeclaredField(tableName, columnName), "Modify column type of column " + columnName + " in table " + tableName + " to " + newType)
        );
    }

    public void visitColumnUnique(String tableName, String columnName, boolean oldUnique, boolean newUnique)
    {
        String problemPrefix = "Database column " + columnName + " in table " + tableName + " created by Liquibase ";

        LocalQuickFix fixDeclaration =
                new AddAnnotationElementFix(
                        "Declare field as " + (oldUnique ? "unique" : "not unique"),
                        member,
                        Column.class.getName(),
                        "unique",
                        (oldUnique ? "true" : "false"));

        PsiAnnotation oneToOneAnnotation = modifierList.findAnnotation(OneToOne.class.getName());
        if (oneToOneAnnotation != null)
        {
            fixDeclaration = new ReplaceAnnotationFix(oneToOneAnnotation, ManyToOne.class.getName());
        }

        LocalQuickFix fixLiquibase = null;
        DatabaseField declaredField = getDeclaredField(tableName, columnName);

        if (oldUnique)
        {
            DatabaseConstraint uniqueConstraint = getLiquibaseDatabaseModel().getUniqueConstraint(tableName, Arrays.asList(columnName));

            if (uniqueConstraint != null)
            {
                fixLiquibase = new DropUniqueConstraintFix(getLiquibaseModuleComponent(), declaredField, uniqueConstraint.getName());
            }
        }
        else
        {
            fixLiquibase = new AddUniqueConstraintFix(getLiquibaseModuleComponent(), declaredField);
        }

        Collection<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
        fixes.add(fixDeclaration);
        if (fixLiquibase != null) fixes.add(fixLiquibase);

        registerProblem(
            problemPrefix + (oldUnique ? " is unique" : "is not unique"),
                fixes.toArray(new LocalQuickFix[fixes.size()]));
    }

    public void visitColumnIndex(String tableName, String columnName, boolean oldIndex, boolean newIndex)
    {
        String indexAnnotationName = "org.hibernate.annotations.Index";
        String problemPrefix = "Database column " + columnName + " in table " + tableName + " created by Liquibase ";

        PsiAnnotation indexAnnotation = modifierList.findAnnotation(indexAnnotationName);

        LocalQuickFix fixDeclaration;
        if (oldIndex)
        {
            String indexName = getLiquibaseDatabaseModel().getSingletonIndexName(tableName, columnName);

            if (indexName == null)
            {
                indexName = "ix_" + (System.currentTimeMillis() / 1000);
            }

            fixDeclaration = new AddAnnotationElementFix(
                    "Add index declaration to field",
                    member,
                    indexAnnotationName,
                    "name",
                    "\"" + indexName + "\"");
        }
        else
        {
            fixDeclaration = new DropAnnotationFix(
                    "Drop index declaration",
                    member,
                    indexAnnotationName);
        }

        List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
        fixes.add(fixDeclaration);

        DatabaseField declaredField = getDeclaredField(tableName, columnName);

        if (newIndex)
        {
            String declaredIndexName = EsnPsiUtils.evalAttributeValue(indexAnnotation, "name", String.class);

            if (declaredIndexName != null)
            {
                fixes.add(new AddIndexFix(getLiquibaseModuleComponent(), declaredField, declaredIndexName));
            }
        }
        else
        {
            String indexName = getLiquibaseDatabaseModel().getSingletonIndexName(tableName, columnName);

            if (indexName != null)
            {
                fixes.add(new DropIndexFix(getLiquibaseModuleComponent(), declaredField, indexName));
            }
        }

        registerProblem(
                problemPrefix + (oldIndex ? " has index" : "does not have index"),
                fixes.toArray(new LocalQuickFix[fixes.size()])
        );

    }
}
