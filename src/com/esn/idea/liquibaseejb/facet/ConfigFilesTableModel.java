package com.esn.idea.liquibaseejb.facet;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.util.descriptors.ConfigFileInfo;
import com.intellij.util.descriptors.ConfigFileInfoSet;
import com.intellij.util.descriptors.ConfigFileMetaData;
import com.esn.idea.liquibaseejb.util.FileUtils;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.io.IOException;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 13:22:02
 */
public class ConfigFilesTableModel implements TableModel
{
    private Module module;
    private ConfigFileInfoSet configFiles;
    private Collection<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();

    public ConfigFilesTableModel(Module module, ConfigFileInfoSet configFiles)
    {
        this.module = module;
        this.configFiles = configFiles;
    }

    public int getRowCount()
    {
        return configFiles.getConfigFileInfos().length;
    }

    public int getColumnCount()
    {
        return 2;
    }

    public String getColumnName(int columnIndex)
    {
        switch(columnIndex)
        {
            case 0: return "Liquibase Changelog file";
            case 1: return "Version";
        }

        return null;
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        ConfigFileInfo fileInfo = configFiles.getConfigFileInfos()[rowIndex];
        ConfigFileMetaData metaData = fileInfo.getMetaData();
        switch(columnIndex)
        {
            case 0: return fileInfo.getUrl();
            case 1:
                return metaData.getVersions()[0].getName();
        }

        return "";
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        ConfigFileInfo fileInfo = configFiles.getConfigFileInfos()[rowIndex];

        configFiles.replaceConfigFile(fileInfo.getMetaData(), (String) aValue);
    }

    public void addTableModelListener(TableModelListener l)
    {
        tableModelListeners.add(l);
    }

    public void removeTableModelListener(TableModelListener l)
    {
        tableModelListeners.remove(l);
    }

    public void create()
    {
        if (!checkEmpty()) return;

        FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        chooserDescriptor.setTitle("Select directory for Liquibase ChangeLog");
        VirtualFile[] files = chooseFromDescriptor(chooserDescriptor);

        for (final VirtualFile file : files)
        {
            if (file.isDirectory())
            {
                ApplicationManager.getApplication().invokeLater(
                        new Runnable() {
                            public void run() {
                                try {
                                    VirtualFile liquibaseXml = file.createChildData(this, "liquibase.xml");
                                    FileUtils.copyFileFromResource("com/esn/idea/liquibaseejb/resources/liquibase.xml", liquibaseXml);

                                    addFromUrl(liquibaseXml.getUrl());


                                } catch (IOException e) {
                                    Messages.showErrorDialog(module.getProject(), "Could not create liquibase changelog", "Liquibase file");
                                }
                            }
                        }
                );
                break; // TODO: Support multiple files
            }
        }

    }

    private VirtualFile[] chooseFromDescriptor(FileChooserDescriptor chooserDescriptor)
    {
        FileChooserFactory fileChooserFactory = FileChooserFactory.getInstance();
        FileChooserDialog fileChooserDialog = fileChooserFactory.createFileChooser(chooserDescriptor, module.getProject());

        VirtualFile moduleFile = module.getModuleFile();
        VirtualFile chooseRoot = moduleFile;

        if (moduleFile != null)
        {
            VirtualFile moduleRoot = moduleFile.getParent();
            if (moduleRoot != null)
            {
                chooseRoot = moduleRoot.findChild("META-INF");
            }
        }

        return fileChooserDialog.choose(chooseRoot, module.getProject());
    }

    private boolean checkEmpty()
    {
        if (configFiles.getConfigFileInfos().length > 0)
        {
            Messages.showErrorDialog("Only one liquibase file per module is currently supported", "LiquibaseEJB");

            return false;
        }

        return true;
    }

    public void add()
    {
        if (!checkEmpty()) return;

        FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.getFileChooserDescriptor("LiquiBase File");
        chooserDescriptor.setTitle("Select Liquibase ChangeLog");
        VirtualFile[] files = chooseFromDescriptor(chooserDescriptor);

        for (VirtualFile file : files)
        {
            addFromUrl(file.getUrl());
        }
    }

    private void addFromUrl(String fileName)
    {
        configFiles.addConfigFile(LiquibaseFacetType.LIQUIBASE_CONFIGURATION_META_DATA, fileName);

        emit(new TableModelEvent(this));
    }

    private void emit(TableModelEvent event)
    {
        for (TableModelListener tableModelListener : tableModelListeners)
        {
            tableModelListener.tableChanged(event);
        }
    }

    public void remove(int selectedRow)
    {        
        configFiles.removeConfigFile(configFiles.getConfigFileInfos()[selectedRow]);
        emit(new TableModelEvent(this));
    }
}
