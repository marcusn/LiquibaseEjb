package com.esn.idea.liquibaseejb.model.ejb.member;

import com.esn.idea.liquibaseejb.model.database.*;
import com.esn.idea.liquibaseejb.model.ejb.member.AttributeWithColumnModel;
import com.esn.idea.liquibaseejb.model.ejb.clazz.ClassModel;
import com.esn.idea.liquibaseejb.model.ejb.EjbModel;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.persistence.model.PersistentAttribute;
import com.intellij.persistence.model.PersistentObject;
import com.intellij.psi.*;
import com.intellij.javaee.model.common.persistence.mapping.ColumnBase;
import com.intellij.javaee.model.common.persistence.mapping.JoinColumnBase;
import com.intellij.util.xml.GenericValue;
import org.jetbrains.annotations.Nullable;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import java.util.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-30
 * Time: 10:11:27
 */
public abstract class MemberModel<T extends PersistentAttribute> extends EjbModel
{
    protected ModuleModel moduleModel;
    protected T attribute;

    public MemberModel(ModuleModel moduleModel, T attribute)
    {
        this.moduleModel = moduleModel;
        this.attribute = attribute;
    }

    public boolean willCreateIdField(EjbModelContext context)
    {
        return getAttribute().getAttributeModelHelper().isIdAttribute() ||
                context.isId();
    }

    public PsiMember getMember()
    {
        PsiMember psiMember = attribute.getPsiMember();

        return psiMember instanceof PsiField ? (PsiField) psiMember : null;
    }


    protected abstract void executeMember(EjbModelContext context, DatabaseModel databaseModel, PsiMember psiMember, PsiType psiMemberType);

    public void execute(EjbModelContext context, DatabaseModel databaseModel)
    {
        if (context.getTableName() == null) return;

        PsiMember psiMember = attribute.getPsiMember();
        PsiType memberType = attribute.getPsiType();

        if (psiMember == null || memberType == null) return;

        executeMember(context, databaseModel, psiMember, memberType);
    }

    public Collection<EjbModelContext> findAllContexts()
    {
        Collection<EjbModelContext> res = new ArrayList<EjbModelContext>();

        PersistentObject persistentObject = attribute.getPersistentObject();
        if (persistentObject != null)
        {
            ClassModel modelForClass = moduleModel.getModelForClass(persistentObject.getClazz().getValue());

            if (modelForClass != null)
            {
                for (EjbModelContext classParentContext : ((EjbModel)modelForClass).findAllContexts())
                {
                    res.add(modelForClass.createMemberContext(classParentContext));
                }
            }
        }

        return res;
    }

    @Nullable
    protected static PsiField findEntityIdField(PsiClass fieldClass)
    {
        if (fieldClass == null) return null;

        PsiField[] fields = fieldClass.getFields();
        for (PsiField field : fields)
        {
            if (AttributeWithColumnModel.hasAnnotation(field.getModifierList(), Id.class) || AttributeWithColumnModel.hasAnnotation(field.getModifierList(), EmbeddedId.class))
            {
                return field;
            }

        }
        return null;
    }

    public EjbModelContext createMemberContext(EjbModelContext context)
    {
        return context;
    }


    public PersistentAttribute getAttribute()
    {
        return attribute;
    }

    protected static void applyColumnSpec(ColumnBase columnSpec, DatabaseColumnModel columnModel)
    {
        if (columnSpec == null) return;
        
        PsiElement columnElement = columnSpec.getIdentifyingPsiElement();

        if (columnElement instanceof PsiAnnotation)
        {
            PsiAnnotation columnAnnotation = (PsiAnnotation)columnElement;

            columnModel.setNotNull(Boolean.FALSE.equals(EsnPsiUtils.evalAttributeValue(columnAnnotation, "nullable", Boolean.class)));
            columnModel.setUnique(Boolean.TRUE.equals(EsnPsiUtils.evalAttributeValue(columnAnnotation, "unique", Boolean.class)));

            String columnDefinition = EsnPsiUtils.evalAttributeValue(columnAnnotation, "columnDefinition", String.class);

            if (columnDefinition != null && !columnDefinition.isEmpty())
            {
                columnModel.setType(columnDefinition);
            }
        }
    }

    public static List<String> createJoinColumns(DatabaseModel databaseModel, List<JoinColumn> joinColumns, List<DatabaseField> targetFields, Boolean isOptional, boolean createSourceColumns, String fkName)
    {
        Map<String, DatabaseField> fieldByColumn = new HashMap<String, DatabaseField>();
        for (DatabaseField targetField : targetFields)
        {
            fieldByColumn.put(targetField.getColumnName(), targetField);
        }

        List<String> sourceColumnNames = new ArrayList<String>();
        List<String> targetColumnNames = new ArrayList<String>();

        if (!targetFields.isEmpty() && joinColumns.size() == targetFields.size())
        {
            String targetTableName = targetFields.get(0).getTableName();
            String sourceTableName = joinColumns.get(0).getSourceTable();
            for (JoinColumn joinColumn : joinColumns)
            {
                String sourceColumnName = joinColumn.getSourceName();
                String targetColumnName = joinColumn.getTargetName();
                DatabaseField targetField = fieldByColumn.get(targetColumnName);

                if (targetField == null) continue; // Target field not found

                DatabaseColumnModel targetColumnModel = targetField.getColumnModel();

                DatabaseColumnModel sourceColumnModel = new DatabaseColumnModel(targetColumnModel.getType());
                applyColumnSpec(joinColumn.getJoinColumnSpec(), sourceColumnModel);
                if (isOptional != null) sourceColumnModel.setNotNull(!isOptional);

                if (createSourceColumns)
                {
                    databaseModel.addColumn(joinColumn.getSourceTable(), sourceColumnName, sourceColumnModel);
                }

                sourceColumnNames.add(sourceColumnName);
                targetColumnNames.add(targetColumnName);

            }
            databaseModel.addTable(targetTableName);

            databaseModel.addForeignKeyConstraint(
                    sourceTableName,
                    fkName,
                    sourceColumnNames,
                    targetTableName,
                    targetColumnNames);

        }

        return sourceColumnNames;
    }

    static public List<String> createJoinColumns(DatabaseModel databaseModel, String tableName, PsiClass targetClass, List<? extends JoinColumnBase> joinColumnBases, String columnPrefix, Boolean optional, boolean createSourceColumns, ModuleModel moduleModel, String fkName)
    {
        List<DatabaseField> targetIdFields = moduleModel.getPrimaryKeyFieldsForClass(targetClass);
        List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();

        if (joinColumnBases.isEmpty())
        {
            // Default join columns are all primary key fields in target table
            for (DatabaseField targetIdField : targetIdFields)
            {
                joinColumns.add(new JoinColumn(null, tableName, columnPrefix + targetIdField.getColumnName(), targetIdField.getColumnName()));
            }
        }
        else if (joinColumnBases.size() == 1 && targetIdFields.size() == 1)
        {
            JoinColumnBase joinColumnBase = joinColumnBases.get(0);
            DatabaseField targetIdField = targetIdFields.get(0);
            joinColumns.add(new JoinColumn(joinColumnBase, tableName, columnPrefix + targetIdField.getColumnName(), targetIdField.getColumnName()));
        }
        else
        {
            for (JoinColumnBase joinColumnBase : joinColumnBases)
            {
                String sourceName = joinColumnBase.getName().getStringValue();
                String targetName = joinColumnBase.getReferencedColumnName().getStringValue();

                // EJB Spec 9.1.6, defaults only apply if one join column is used
                if (sourceName != null && targetName != null)
                {
                    joinColumns.add(new JoinColumn(joinColumnBase, tableName, sourceName, targetName));
                }
            }
        }

        return createJoinColumns(databaseModel, joinColumns, targetIdFields, optional, createSourceColumns, fkName);
    }

    public static void createPrimaryKeyJoinColumns(DatabaseModel databaseModel, String tableName, PsiClass targetClass, List<? extends JoinColumnBase> joinColumns, String columnPrefix, ModuleModel moduleModel, String fkName, boolean createSourceColumns)
    {
        List<String> joinColumnNames = createJoinColumns(
                databaseModel,
                tableName,
                targetClass,
                joinColumns,
                columnPrefix,
                false,
                createSourceColumns,
                moduleModel,
                fkName
        );

        if (createSourceColumns)
        {
            databaseModel.addPrimaryKeyConstraint(tableName, "pk", joinColumnNames);
        }
    }

    protected static class JoinColumn
    {
        private JoinColumnBase joinColumnSpec;

        private String sourceTable;

        private String sourceName;

        private String targetName;

        public JoinColumn(JoinColumnBase joinColumnSpec, String sourceTable, String sourceName, String targetName)
        {
            this.joinColumnSpec = joinColumnSpec;
            this.sourceName = sourceName;
            this.targetName = targetName;
            this.sourceTable = sourceTable;
        }

        private String ifEmpty(GenericValue<String> value, String valueIfEmpty)
        {
            return ifEmpty(value.getStringValue(), valueIfEmpty);
        }

        private String ifEmpty(String value, String valueIfEmpty)
        {
            return value != null && !value.isEmpty() ? value : valueIfEmpty;
        }

        public String getSourceTable()
        {
            if (joinColumnSpec == null) return sourceTable;
            return ifEmpty(joinColumnSpec.getTable(), sourceTable);
        }

        public String getSourceName()
        {
            if (joinColumnSpec == null) return sourceName;
            return ifEmpty(joinColumnSpec.getName(), sourceName);
        }

        public String getTargetName()
        {
            if (joinColumnSpec == null) return targetName;
            return ifEmpty(joinColumnSpec.getReferencedColumnName(), targetName);
        }

        public JoinColumnBase getJoinColumnSpec()
        {
            return joinColumnSpec;
        }
    }
}
