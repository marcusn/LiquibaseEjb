package com.esn.idea.liquibaseejb.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-sep-23
 * Time: 12:56:59
 */
public class EsnPsiUtils
{
	static public void addOrModifyAnnotationAttribute(@NotNull Project project, @NotNull PsiAnnotation annotation, @NotNull String field, @NotNull Object value)
	{
        PsiElementFactory elementFactory = getElementFactory(project);
		PsiAnnotationParameterList parameterList = annotation.getParameterList();

		try
		{
			String stringValue = formatValue(value);

			String annotationText = "@" + annotation.getQualifiedName() + "(" + field + " = " + stringValue + ")";
			PsiAnnotation newAnnotation = elementFactory.createAnnotationFromText(annotationText, annotation.getContext());
			PsiAnnotationParameterList newParameterList = newAnnotation.getParameterList();

			if (parameterList.getAttributes().length == 0)
			{
				annotation.getParameterList().replace(newParameterList);
			}
			else
			{
				PsiAnnotationMemberValue memberValue = annotation.findDeclaredAttributeValue(field);
				if (memberValue != null)
				{
                    PsiAnnotationMemberValue declaredValue = newAnnotation.findDeclaredAttributeValue(field);
                    if (declaredValue != null)
                    {
                        memberValue.replace(declaredValue);
                    }
                }
				else
				{
					PsiElement firstNameValuePair = findFirstNameValuePair(parameterList);
					PsiElement newNameValuePair = findFirstNameValuePair(newParameterList);

                    if (firstNameValuePair != null && newNameValuePair != null)
                    {
                        parameterList.addAfter(newNameValuePair, firstNameValuePair);
                    }
                }
			}

		}
		catch (IncorrectOperationException e)
		{
			e.printStackTrace();
		}
	}

    @Nullable
    private static PsiElement findFirstNameValuePair(@NotNull PsiAnnotationParameterList parameterList)
    {
        PsiElement firstChild = parameterList.getFirstChild();

        return firstChild == null ? null : firstChild.getNextSibling();
    }

    public static void addToAnnotationValueArray(@NotNull Project project, @NotNull PsiAnnotation annotation, @NotNull String attribute, @NotNull Object value) throws IncorrectOperationException
	{
		PsiAnnotationMemberValue psiAnnotationMemberValue = annotation.findDeclaredAttributeValue(attribute);
		if (psiAnnotationMemberValue != null)
		{
			PsiElementFactory elementFactory = getElementFactory(project);

			String allStringValue = formatValue(value);
			for (PsiAnnotationMemberValue e : getInitializerValues(psiAnnotationMemberValue))
			{
				allStringValue += " , " + e.getText();
			}

			PsiAnnotation templateAnnotation = elementFactory.createAnnotationFromText("@" + annotation.getQualifiedName() + " (" + attribute  + " = {" + allStringValue + "})", annotation);
			PsiAnnotationMemberValue templateMemberValue = templateAnnotation.findDeclaredAttributeValue(attribute);
            if (templateMemberValue != null)
            {
                psiAnnotationMemberValue.replace(templateMemberValue);
            }
        }
		else
		{
			addOrModifyAnnotationAttribute(project, annotation, attribute, value);
		}
	}

    @NotNull
    public static String formatValue(@NotNull Object value)
	{
		Class valueClass = value.getClass();
		String stringValue;
		if (valueClass.isEnum())
		{
			stringValue = valueClass.getName() + "." + value.toString();
		}
		else
		{
			stringValue = value.toString();
		}
		return stringValue;
	}

    @NotNull
	public static <T> List<T> getInitializerValues(@Nullable PsiAnnotationMemberValue value, @NotNull Class<T> valueClass)
	{
        if (value == null) return Collections.emptyList();

        List<T> res = new ArrayList<T>();
        Project project = value.getProject();
        PsiConstantEvaluationHelper evaluationHelper = getConstantEvaluationHelper(project);
        for (PsiAnnotationMemberValue psiAnnotationMemberValue : getInitializerValues(value))
		{
			if (psiAnnotationMemberValue instanceof PsiExpression)
			{
				Object v = evaluationHelper.computeConstantExpression((PsiExpression) psiAnnotationMemberValue);
				if (v != null)
				{
					if (valueClass.isAssignableFrom(v.getClass()))
					{
						//noinspection unchecked
						res.add((T)v);
					}
				}
			}

		}
		return res;
	}

    private static <T> PsiConstantEvaluationHelper getConstantEvaluationHelper(Project project) {
        PsiConstantEvaluationHelper evaluationHelper = getPsiFacade(project).getConstantEvaluationHelper();
        return evaluationHelper;
    }

    @NotNull
	static public Collection<PsiAnnotationMemberValue> getInitializerValues(@Nullable PsiAnnotationMemberValue value)
    {
        if (value == null) return Collections.emptyList();

        if (value instanceof PsiArrayInitializerMemberValue)
		{
			return Arrays.asList(((PsiArrayInitializerMemberValue)value).getInitializers());
		}

		return Arrays.asList(value);
	}

    @NotNull
	static public Collection<PsiElement> getResolvedInitializerValues(@Nullable PsiAnnotationMemberValue value)
	{
		if (value == null) return Collections.emptyList();

		Collection<PsiElement> initializerValues = new ArrayList<PsiElement>();

		for (PsiAnnotationMemberValue initializer : getInitializerValues(value))
		{
			PsiElement resolvedInitializerValue = getResolvedInitializerValue(initializer);
			if (resolvedInitializerValue != null)
			{
				initializerValues.add(resolvedInitializerValue);
			}
		}
		return initializerValues;
	}

    @Nullable
	static public <T> T getInitializerValue(@Nullable PsiAnnotationMemberValue value, Class<T> valueClass)
	{
        if (value == null) return null;

        PsiConstantEvaluationHelper evaluationHelper = getConstantEvaluationHelper(value.getProject());
		if (value instanceof PsiExpression)
		{
			Object v = evaluationHelper.computeConstantExpression((PsiExpression) value);
			if (v != null)
			{
				if (valueClass.isAssignableFrom(v.getClass()))
				{
					//noinspection unchecked
					return (T)v;
				}
			}
		}

		return null;
	}

    @Nullable
	static public PsiElement getResolvedInitializerValue(@Nullable PsiAnnotationMemberValue initializer)
	{
		if (initializer == null) return null;

		PsiReference psiReference = initializer.getReference();

		if (psiReference == null) return null;

		return psiReference.resolve();
	}


    @Nullable
	public static PsiAnnotation addFieldAnnotation(@NotNull PsiElementFactory elementFactory, @NotNull PsiField field, @NotNull Class annotation) throws IncorrectOperationException
	{
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null)
        {
            return addAnnotation(elementFactory, modifierList, annotation);
        }

        return null;
    }

    @NotNull
	public static PsiAnnotation addAnnotation(@NotNull PsiElementFactory elementFactory, @NotNull PsiElement element, @NotNull Class annotationType) throws IncorrectOperationException
	{
		return addAnnotation(elementFactory, element, annotationType.getName());
	}
	
    @NotNull
	public static PsiAnnotation addAnnotation(@NotNull PsiElementFactory elementFactory, @NotNull PsiElement element, @NotNull String annotationTypeName)
					throws IncorrectOperationException
	{
		String annotationText = "@" + annotationTypeName;
		return addAnnotationFromText(element, annotationText);
	}

	public static boolean isAlreadyImported(@NotNull PsiElement element, @NotNull String qualifiedName)
	{
		String packageName =  qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
		String className = qualifiedName.substring(packageName.length() + 1);

		PsiFile containingFile = element.getContainingFile();
		if (containingFile instanceof PsiJavaFile)
		{
			if (getPsiFacade(element).getResolveHelper().resolveReferencedClass(className, containingFile) != null)
			{
				return true;
			}

			PsiJavaFile javaFile = (PsiJavaFile) containingFile;

			PsiImportList importList = javaFile.getImportList();

            if (importList != null)
            {
                for (PsiImportStatementBase importStatementBase : importList.getAllImportStatements())
                {
                    PsiJavaCodeReferenceElement importReference = importStatementBase.getImportReference();

                    if (importReference != null)
                    {
                        String importName = importReference.getQualifiedName();

                        if (importStatementBase.isOnDemand())
                        {
                            if (packageName.equals(importName)) return true;
                        }
                        else
                        {
                            if (importName.equals(qualifiedName)) return true;
                        }
                    }
                }
            }
        }

		return false;
	}

    public static JavaPsiFacade getPsiFacade(PsiElement element) {
        return getPsiFacade(element.getProject());
    }

    public static PsiElementFactory getElementFactory(PsiElement element)
    {
        Project project = element.getProject();
        return getElementFactory(project);
    }

    public static PsiElementFactory getElementFactory(Project project) {
        return getPsiFacade(project).getElementFactory();
    }


    public static JavaPsiFacade getPsiFacade(Project project) {
        return JavaPsiFacade.getInstance(project);
    }

    @NotNull
	public static PsiAnnotation addAnnotationFromText(@NotNull PsiElement element, @NotNull String annotationText)
					throws IncorrectOperationException
	{
        PsiElementFactory elementFactory = getElementFactory(element);
        PsiAnnotation annotation = elementFactory.createAnnotationFromText(annotationText, element);

		String qualifiedName = annotation.getQualifiedName();

		if (qualifiedName != null && !isAlreadyImported(element, qualifiedName))
		{
			PsiClass psiClass = getContainingClass(element);
			if (psiClass != null)
			{
				EsnPsiUtils.addImportAboveClass(element.getProject(), psiClass, annotation.getQualifiedName());
			}
		}

		PsiElement anchor = element.getFirstChild();
		while (anchor instanceof PsiComment)
		{
			anchor = anchor.getNextSibling();
		}
		element.addBefore(annotation, anchor);
		return annotation;
	}

    @Nullable
    private static PsiClass getContainingClass(@NotNull PsiElement element)
	{
		if (element instanceof PsiClass) return (PsiClass) element;
		
		return PsiTreeUtil.getParentOfType(element, PsiClass.class);
	}

    @NotNull
	public static String capitalize(@NotNull String s)
	{
		if (s.equals("")) return "";
		
		return (s.substring(0, 1).toUpperCase() + s.substring(1));
	}

    @NotNull
	public static String setterNameFor(@NotNull String s)
	{
		return "set" + capitalize(s);
	}

	public static void addImportAboveClass(@NotNull Project project, @NotNull PsiClass psiClass, @Nullable String classToImport) throws IncorrectOperationException
	{
        if (classToImport == null) return;

        PsiElement parent = PsiTreeUtil.getChildOfType(psiClass.getParent(), PsiImportList.class);
		PsiManager manager = PsiManager.getInstance(project);
		PsiClass importClass = getPsiFacade(project).findClass(classToImport, GlobalSearchScope.allScope(project));

		if (importClass != null && parent != null)
		{
			PsiImportStatement importStatement = getElementFactory(project).createImportStatement(importClass);
			parent.add(importStatement);
		}
	}

    @NotNull
    public static PsiAnnotation addAnnotationWithImport(@NotNull Project project, @NotNull PsiClass psiClass, @NotNull String annotationName)
					throws IncorrectOperationException
	{
		PsiElementFactory elementFactory = getElementFactory(project);
		addImportAboveClass(project, psiClass, annotationName);
		return addAnnotation(elementFactory, psiClass, annotationName);
	}

	public static boolean hasFieldWithAnnotation(@Nullable PsiClass psiClass, @NotNull String annotationName)
	{
		if (psiClass == null) return false;

		for (PsiField f : psiClass.getFields())
		{
			if (hasAnnotation(f, annotationName))
			{
				return true;
			}
		}

		return hasFieldWithAnnotation(psiClass.getSuperClass(), annotationName);
	}

	public static boolean hasAnnotation(@NotNull PsiModifierListOwner psiElement, @NotNull String annotationName)
	{
		PsiModifierList modifierList = psiElement.getModifierList();

        return modifierList != null && modifierList.findAnnotation(annotationName) != null;

		}

    @NotNull
    public static Collection<String> getAnnotationEnumStringValues(@NotNull PsiAnnotation enumerationAnnotation, @NotNull String attributeValue)
	{
		PsiAnnotationMemberValue psiAnnotationMemberValue = enumerationAnnotation.findAttributeValue(attributeValue);
		Collection<PsiElement> resolvedEnumTypes = getResolvedInitializerValues(psiAnnotationMemberValue);

		Collection<String> res = new ArrayList<String>();
		for (PsiElement resolvedEnumType : resolvedEnumTypes)
		{
			res.add(getEnumStringValueFromResolved(resolvedEnumType));
		}

		return res;
	}

    @NotNull
    public static String getAnnotationEnumStringValue(@NotNull PsiAnnotation enumerationAnnotation, @NotNull String attributeName)
	{
		PsiAnnotationMemberValue psiAnnotationMemberValue = enumerationAnnotation.findAttributeValue(attributeName);
		PsiElement resolvedEnumType = getResolvedInitializerValue(psiAnnotationMemberValue);
		if (resolvedEnumType == null) return "";
		return getEnumStringValueFromResolved(resolvedEnumType);
	}

    @NotNull
    private static String getEnumStringValueFromResolved(@NotNull PsiElement resolvedEnumType)
	{
		PsiIdentifier enumIdentifier = PsiTreeUtil.getChildOfType(resolvedEnumType, PsiIdentifier.class);
		if (enumIdentifier == null) return "";
		return enumIdentifier.getText();
	}

	public static <T extends Enum<T>> T getAnnotationEnumValue(@Nullable PsiAnnotation enumerationAnnotation, @NotNull String attributeName, @NotNull Class<T> enumClass, @Nullable T defaultValue)
	{
		if (enumerationAnnotation == null) return defaultValue;
		
		String enumTextValue = getAnnotationEnumStringValue(enumerationAnnotation, attributeName);

		if (enumTextValue == null) return defaultValue;

		try
		{
				return Enum.valueOf(enumClass, enumTextValue);
		}
		catch(IllegalArgumentException e)
		{
			return defaultValue;
		}
	}

    @NotNull
    public static <T extends Enum<T>> Collection<T> getAnnotationEnumValues(@Nullable PsiAnnotation enumerationAnnotation, @NotNull String attributeValue, @NotNull Class<T> enumClass)
	{
		if (enumerationAnnotation == null) return Collections.emptyList();

		Collection<String> enumStringValues = getAnnotationEnumStringValues(enumerationAnnotation, attributeValue);

		Collection<T> res = new ArrayList<T>();
		for (String enumStringValue : enumStringValues)
		{
			try
			{
				res.add(Enum.valueOf(enumClass, enumStringValue));
			}
			catch(IllegalArgumentException e)
			{
				// ignore
			}
		}
		return res;
	}

    @Nullable
    public static Object evalAttributeValue(@Nullable PsiAnnotation annotation, @NotNull String attributeName)
	{
		if (annotation == null) return null;
		
		PsiAnnotationMemberValue memberValue = annotation.findAttributeValue(attributeName);

		if (memberValue instanceof PsiExpression)
		{
			PsiConstantEvaluationHelper evaluationHelper = getConstantEvaluationHelper(annotation.getProject());
			return evaluationHelper.computeConstantExpression((PsiExpression) memberValue);
		}

		return null;
	}

    @Nullable
	public static <T> T evalAttributeValue(@NotNull PsiAnnotation annotation, @NotNull String attributeName, @NotNull Class<T> valueClass)
	{
		Object value = evalAttributeValue(annotation, attributeName);

		if (value == null) return null;

		if (valueClass.isAssignableFrom(value.getClass()))
		{
			//noinspection unchecked
			return (T)value;
		}

		return null;
	}
}
