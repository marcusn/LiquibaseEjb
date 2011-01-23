package com.esn.idea.liquibaseejb.facet;

import com.esn.idea.liquibaseejb.model.liquibase.xml.DatabaseChangeLog;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.descriptors.*;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 12:27:11
 */
public class LiquibaseFacetType extends FacetType<LiquibaseFacet, LiquibaseFacetConfiguration>
{
    public static final LiquibaseFacetType INSTANCE = new LiquibaseFacetType();
    public static final ConfigFileVersion[] LIQUIBASE_CONFIGURATION_VERSIONS = {
            new ConfigFileVersion("1.6", "liquibase.xml")
    };

    public static final ConfigFileMetaData LIQUIBASE_CONFIGURATION_META_DATA =

      new ConfigFileMetaData("Liquibase Changelog File", "liquibase.xml", "",
                             LIQUIBASE_CONFIGURATION_VERSIONS, null, false, false, false);


    public LiquibaseFacetType()
    {
        super(LiquibaseFacet.ID, "LiquibaseEjb", "Liquibase EJB");
    }

    public LiquibaseFacetConfiguration createDefaultConfiguration()
    {
        final ConfigFileFactory factory = ConfigFileFactory.getInstance();
        final ConfigFileMetaDataRegistry metadataRegistry = factory.createMetaDataRegistry();
        metadataRegistry.registerMetaData(LIQUIBASE_CONFIGURATION_META_DATA);
        ConfigFileInfoSet configFileInfoSet = factory.createConfigFileInfoSet(metadataRegistry);
        return new LiquibaseFacetConfiguration(configFileInfoSet);
    }

    public LiquibaseFacet createFacet(@NotNull Module module, String s, @NotNull LiquibaseFacetConfiguration liquibaseFacetConfiguration, @Nullable Facet facet)
    {
        return new LiquibaseFacet(this, module, s, liquibaseFacetConfiguration, facet);
    }

    public boolean isSuitableModuleType(ModuleType moduleType)
    {
        return true;
    }

    public void registerDetectors(FacetDetectorRegistry<LiquibaseFacetConfiguration> registry)
    {
        FacetDetector<PsiFile, LiquibaseFacetConfiguration> facetDetector = new FacetDetector<PsiFile, LiquibaseFacetConfiguration>()
        {
            public LiquibaseFacetConfiguration detectFacet(PsiFile source, Collection<LiquibaseFacetConfiguration> existingConfigurations)
            {
                if (!existingConfigurations.isEmpty())
                {
                    return existingConfigurations.iterator().next();
                }

                LiquibaseFacetConfiguration configuration = createDefaultConfiguration();
                VirtualFile file = source.getVirtualFile();
                if (file != null)
                {
                    configuration.getLiquibaseDescriptors().addConfigFile(LIQUIBASE_CONFIGURATION_META_DATA, file.getUrl());
                }

                return configuration;
            }
        };

        Condition<PsiFile> fileCondition = new Condition<PsiFile>()
        {
            public boolean value(PsiFile psiFile)
            {
                if (psiFile instanceof XmlFile && isInSourceFolder(psiFile))
                {
                    XmlFile xmlFile = (XmlFile) psiFile;

                    DomManager domManager = DomManager.getDomManager(xmlFile.getProject());
                    DomFileElement<DatabaseChangeLog> databaseChangeLogDomFileElement = domManager.getFileElement(xmlFile, DatabaseChangeLog.class);

                    return databaseChangeLogDomFileElement != null;
                }
                return false;
            }
        };
        registry.registerOnTheFlyDetector(StdFileTypes.XML, VirtualFileFilter.ALL, fileCondition, facetDetector);

    }

    public boolean isInSourceFolder(PsiFile file)
    {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) return false;

        ModuleRootManager rootManager = ModuleRootManager.getInstance(ModuleUtil.findModuleForPsiElement(file));

        for (VirtualFile sourceRoot : rootManager.getSourceRoots())
        {
            if (VfsUtil.isAncestor(sourceRoot, virtualFile, false)) return true;

        }

        return false;
    }
}
