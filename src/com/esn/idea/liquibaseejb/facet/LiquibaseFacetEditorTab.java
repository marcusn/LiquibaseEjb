package com.esn.idea.liquibaseejb.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 12:57:28
 */
public class LiquibaseFacetEditorTab extends FacetEditorTab
{
    @SuppressWarnings ({"FieldCanBeLocal"})
    private LiquibaseFacetConfiguration configuration;

    @SuppressWarnings ({"FieldCanBeLocal"})
    private FacetEditorContext context;

    @SuppressWarnings ({"FieldCanBeLocal"})
    private FacetValidatorsManager manager;

    private JPanel mainPanel;

    public LiquibaseFacetEditorTab(LiquibaseFacetConfiguration configuration, FacetEditorContext context, FacetValidatorsManager manager)
    {
        this.configuration = configuration;
        this.context = context;
        this.manager = manager;
        
        mainPanel = new JPanel(new VerticalFlowLayout());

        mainPanel.add(new JLabel("Liquibase ChangeLog descriptors"));
        mainPanel.add(new ConfigDescriptorsEditor(context.getModule(), configuration.getLiquibaseDescriptors()));
    }

    @Nls
    public String getDisplayName()
    {
        return "Liquibase EJB Integration Settings";
    }

    public JComponent createComponent()
    {
        return mainPanel;
    }

    public boolean isModified()
    {
        return false;
    }

    public void apply() throws ConfigurationException
    {

    }

    public void reset()
    {

    }

    public void disposeUIResources()
    {

    }
}
