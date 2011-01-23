package com.esn.idea.liquibaseejb.facet;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.module.Module;
import com.intellij.util.descriptors.ConfigFileInfoSet;
import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 13:43:43
 */
public class ConfigDescriptorsEditor extends JPanel
{
    public ConfigDescriptorsEditor(final Module module, ConfigFileInfoSet descriptors)
    {
        super();
        final ConfigFilesTableModel configTableModel = new ConfigFilesTableModel(module, descriptors);
        configTableModel.addTableModelListener(new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
               LiquibaseModuleComponent.getInstance(module).refreshLiquibaseModel();
            }
        });
        final JTable descriptorsTable = new JTable(configTableModel);
        descriptorsTable.setShowGrid(true);

        add(new JScrollPane(descriptorsTable));

        JPanel buttonPanel = new JPanel(new VerticalFlowLayout());
        add(buttonPanel);

        JButton addButton = new JButton("Add...");
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                configTableModel.add();
            }
        });

        buttonPanel.add(addButton);

        JButton createButton = new JButton("Create...");
        createButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                configTableModel.create();
            }
        });

        buttonPanel.add(createButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int selectedRow = descriptorsTable.getSelectedRow();

                if (selectedRow != -1)
                {
                    configTableModel.remove(selectedRow);
                }
            }
        });

        buttonPanel.add(removeButton);

    }
}
