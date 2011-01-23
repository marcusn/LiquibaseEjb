package com.esn.idea.liquibaseejb.facet;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.descriptors.ConfigFileInfo;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 12:21:48
 */
public class LiquibaseFacet extends Facet<LiquibaseFacetConfiguration>
{
    public static final FacetTypeId<LiquibaseFacet> ID = new FacetTypeId<LiquibaseFacet>("liquibaseejb");

    boolean disposed = false;

    public LiquibaseFacet(@org.jetbrains.annotations.NotNull FacetType facetType, @org.jetbrains.annotations.NotNull Module module, String s, @org.jetbrains.annotations.NotNull LiquibaseFacetConfiguration liquibaseFacetConfiguration, Facet facet)
    {
        super(facetType, module, s, liquibaseFacetConfiguration, facet);
    }

    public static LiquibaseFacet getInstance(Module module)
    {
        return FacetManager.getInstance(module).getFacetByType(ID);
    }

    @SuppressWarnings ({"LoopStatementThatDoesntLoop"})
    public VirtualFile getLiquibaseFile()
    {
        if (disposed) return null;

        // TODO: Support multiple liquibase files

        LiquibaseFacetConfiguration configuration = getConfiguration();
        ConfigFileInfo[] configFileInfos = configuration.getLiquibaseDescriptors().getConfigFileInfos();
        for (ConfigFileInfo configFileInfo : configFileInfos)
        {
            String liquibaseUrl = configFileInfo.getUrl();

            return VfsUtil.findFileByURL(VfsUtil.convertToURL(liquibaseUrl));
        }
        return null;
    }

    public void disposeFacet()
    {
        disposed = true;

        LiquibaseModuleComponent.getInstance(getModule()).refreshLiquibaseModel();
        
        super.disposeFacet();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
