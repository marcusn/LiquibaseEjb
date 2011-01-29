package com.esn.idea.liquibaseejb.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.descriptors.ConfigFileInfoSet;
import org.jdom.Element;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 12:20:41
 */
public class LiquibaseFacetConfiguration implements FacetConfiguration
{
    private final ConfigFileInfoSet liquibaseDescriptors;

    public LiquibaseFacetConfiguration(ConfigFileInfoSet liquibaseDescriptors)
    {
        this.liquibaseDescriptors = liquibaseDescriptors;
    }

    public FacetEditorTab[] createEditorTabs(FacetEditorContext facetEditorContext, FacetValidatorsManager facetValidatorsManager)
    {
        return new FacetEditorTab[]{
          new LiquibaseFacetEditorTab(this, facetEditorContext, facetValidatorsManager)  
        };
    }

    public void readExternal(Element element) throws InvalidDataException
    {
        liquibaseDescriptors.readExternal(element);

    }

    public void writeExternal(Element element) throws WriteExternalException
    {
       liquibaseDescriptors.writeExternal(element);
    }

    public ConfigFileInfoSet getLiquibaseDescriptors()
    {
        return liquibaseDescriptors;
    }
}
