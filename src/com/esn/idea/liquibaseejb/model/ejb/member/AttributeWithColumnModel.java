package com.esn.idea.liquibaseejb.model.ejb.member;

import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.util.EsnPsiUtils;
import com.intellij.javaee.model.common.persistence.mapping.AttributeWithColumn;
import com.intellij.javaee.model.common.persistence.mapping.ColumnBase;
import com.intellij.javaee.model.common.persistence.mapping.Id;
import com.intellij.psi.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-09
 * Time: 18:46:48
 */
public class AttributeWithColumnModel extends MemberModel<AttributeWithColumn>
{
    public AttributeWithColumnModel(ModuleModel moduleModel, AttributeWithColumn attribute)
    {
        super(moduleModel, attribute);
    }

    protected void executeMember(EjbModelContext context, DatabaseModel databaseModel, PsiMember psiMember, PsiType psiMemberType)
    {
        String columnName = attribute.getName().getStringValue();
        PsiMember member = attribute.getPsiMember();
        PsiModifierList modifierList = member.getModifierList();

        if (attribute instanceof Id)
        {
            executeGenerators(context, databaseModel, member);
        }

        if (columnName != null && modifierList != null)
        {
            String tableName = context.getTableName();

            AttributeWithColumn attributeWithColumn = (AttributeWithColumn) attribute;
            ColumnBase columnSpecOverride = context.getAttributeOverride(attribute.getName().getStringValue());
            ColumnBase columnSpec = columnSpecOverride != null ? columnSpecOverride : attributeWithColumn.getColumn();

            if (columnSpec != null)
            {
                String overridingName = columnSpec.getName().getStringValue();

                if (overridingName != null && !overridingName.isEmpty())
                {
                    columnName = overridingName;
                }

                String overridingTableName = columnSpec.getTable().getStringValue();

                if (overridingTableName != null && !overridingTableName.isEmpty())
                {
                    tableName = overridingTableName;
                }
            }

            columnName = context.getColumnPrefix() + columnName;

            DatabaseColumnModel columnModel = fieldToColumnModel(columnSpec);

            if (context.isId())
            {
                columnModel.setPrimaryKey(true);
                columnModel.setNotNull(true);
            }

            if (context.isNotId())
            {
                columnModel.setPrimaryKey(false);
                columnModel.setAutoIncrement(false);
            }

            Boolean overrideUnique = context.getOverrideUnique();
            if (overrideUnique != null)
            {
                columnModel.setUnique(overrideUnique);
            }

            Boolean overrideNullable = context.getOverrideNullable();
            if (overrideNullable != null)
            {
                columnModel.setNotNull(!overrideNullable);
            }

            databaseModel.addColumn(
                    tableName,
                    columnName,
                    columnModel);
        }

    }


    private DatabaseColumnModel fieldToColumnModel(ColumnBase columnSpec)
    {
        PsiType memberType = attribute.getPsiType();
        DatabaseColumnModel columnModel = new DatabaseColumnModel(fieldToColumnType(attribute.getPsiMember(), memberType, columnSpec));



        if (attribute instanceof com.intellij.javaee.model.common.persistence.mapping.Basic)
        {
            com.intellij.javaee.model.common.persistence.mapping.Basic basic = (com.intellij.javaee.model.common.persistence.mapping.Basic) attribute;

            if (memberType instanceof PsiPrimitiveType)
            {
                columnModel.setNotNull(true);
            }
            else
            {
                Boolean optionalValue = basic.getOptional().getValue();
                if (optionalValue != null)
                {
                    columnModel.setNotNull(!optionalValue);
                }
            }
        }

        if (attribute instanceof Id)
        {
            columnModel.setPrimaryKey(true);
            columnModel.setNotNull(true);


        }

        applyColumnSpec(columnSpec, columnModel);

        PsiMember psiMember = attribute.getPsiMember();
        if (psiMember != null)
        {
            PsiModifierList modifierList = psiMember.getModifierList();
            if (modifierList != null)
            {

                String indexAnnotationName = "org.hibernate.annotations.Index";

                PsiAnnotation indexAnnotation = modifierList.findAnnotation(indexAnnotationName);

                if (indexAnnotation != null)
                {
                    columnModel.setIndex(true);
                }

                PsiAnnotation generatedValueAnnotation = modifierList.findAnnotation(GeneratedValue.class.getName());
                if (generatedValueAnnotation != null)
                {
                    columnModel.setNotNull(true);

                    GenerationType type = EsnPsiUtils.getAnnotationEnumValue(generatedValueAnnotation, "strategy", GenerationType.class, GenerationType.AUTO);

                    if (type == GenerationType.AUTO)
                    {
                        columnModel.setAutoIncrement(true);
                    }
                }
            }
        }

        return columnModel;
    }

    /**
     * Return column type of field if it is supposed to be in database
     *
     * @param field Field to get column type of
     * @param columnSpec
     * @return The database column type of field, null if field is not persisted
     */
    public static String fieldToColumnType(PsiMember field, PsiType fieldType, ColumnBase columnSpec)
    {
        if (field == null) return null;
        PsiModifierList fieldModifierList = field.getModifierList();
        if (fieldModifierList.findAnnotation(Transient.class.getName()) != null) return null;
        if (fieldModifierList.hasModifierProperty("static")) return null;

        if (fieldType instanceof PsiArrayType)
        {
            PsiArrayType psiArrayType = (PsiArrayType) fieldType;

            PsiType type = psiArrayType.getComponentType();

            if (type == PsiArrayType.BYTE || isJavaClass(type, Byte.class)) return "BLOB";
            if (type == PsiArrayType.CHAR || isJavaClass(type, Character.class)) return "CLOB";
        }

        if (hasAnnotation(fieldModifierList, Lob.class))
        {
            return "BLOB";
        }

        if (fieldType instanceof PsiPrimitiveType)
        {
            if (fieldType == PsiType.BOOLEAN) return "BOOLEAN";
            if (fieldType == PsiType.BYTE) return "SMALLINT";
            if (fieldType == PsiType.CHAR) return "CHAR(1)";
            if (fieldType == PsiType.DOUBLE) return "DOUBLE PRECISION";
            if (fieldType == PsiType.FLOAT) return "FLOAT";
            if (fieldType == PsiType.INT) return "INT";
            if (fieldType == PsiType.LONG) return "BIGINT";
            if (fieldType == PsiType.SHORT) return "SMALLINT";
        }
        else
        {
            if (isJavaClass(fieldType, String.class))
            {
                return stringTypeFromAnnotations(columnSpec, 255);

            }
            if (isJavaClass(fieldType, BigInteger.class)) return "BIGINT";
            if (isJavaClass(fieldType, Date.class) ||
                    isJavaClass(fieldType, java.sql.Date.class) ||
                    isJavaClass(fieldType, java.sql.Time.class) ||
                    isJavaClass(fieldType, java.sql.Timestamp.class) ||
                    isJavaClass(fieldType, Calendar.class)
                    )
            {
                PsiAnnotation temporalAnnotation = fieldModifierList.findAnnotation(Temporal.class.getName());

                TemporalType temporalType = EsnPsiUtils.getAnnotationEnumValue(temporalAnnotation, "value", TemporalType.class, TemporalType.TIMESTAMP);

                switch (temporalType)
                {

                    case DATE:
                        return "DATE";
                    case TIME:
                        return "TIME";
                    case TIMESTAMP:
                        return "DATETIME";
                }
            }


            if (isJavaClass(fieldType, Boolean.class)) return "BOOLEAN";
            if (isJavaClass(fieldType, Byte.class)) return "SMALLINT";
            if (isJavaClass(fieldType, Character.class)) return "CHAR(1)";
            if (isJavaClass(fieldType, Double.class)) return "DOUBLE PRECISION";
            if (isJavaClass(fieldType, Float.class)) return "FLOAT";
            if (isJavaClass(fieldType, Integer.class)) return "INT";
            if (isJavaClass(fieldType, Long.class)) return "BIGINT";
            if (isJavaClass(fieldType, Short.class)) return "SMALLINT";

            if (fieldType instanceof PsiClassType)
            {
                PsiClassType fieldClassType = (PsiClassType) fieldType;
                PsiClass fieldClass = fieldClassType.resolve();

                if (fieldClass != null)
                {
                    if (fieldClass.isEnum())
                    {
                        EnumType enumType = EnumType.ORDINAL;
                        PsiAnnotation enumerationAnnotation = fieldModifierList.findAnnotation(Enumerated.class.getName());

                        if (enumerationAnnotation != null)
                        {
                            enumType = EsnPsiUtils.getAnnotationEnumValue(enumerationAnnotation, "value", EnumType.class, EnumType.ORDINAL);
                        }
                        switch (enumType)
                        {
                            case ORDINAL:
                                return "SMALLINT";
                            case STRING:
                                return stringTypeFromAnnotations(columnSpec, 255);
                        }
                    }


                }
            }
        }
        return null;
    }

    public static String stringTypeFromAnnotations(ColumnBase columnSpec, int defaultLength)
    {
        int length = defaultLength;


        PsiElement columnElement = columnSpec.getIdentifyingPsiElement();

        if (columnElement instanceof PsiAnnotation)
        {
            PsiAnnotation columnAnnotation = (PsiAnnotation)columnElement;
            try
            {
                length = Integer.valueOf(columnAnnotation.findAttributeValue("length").getText());
            }
            catch (NumberFormatException e)
            {
                // Ignore
            }
        }

        return stringTypeFromLength(length);
    }

    public static String stringTypeFromLength(int length)
    {
        if (length > 255) return "CLOB";
        return "VARCHAR(" + length + ")";
    }

    public static boolean hasAnnotation(PsiModifierList fieldModifierList, Class annotation)
    {
        return fieldModifierList.findAnnotation(annotation.getName()) != null;
    }

    public static boolean isJavaClass(PsiType fieldType, Class javaClass)
    {
        return fieldType.equalsToText(javaClass.getName());
    }
}
