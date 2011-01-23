package com.esn.idea.liquibaseejb.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 09:47:59
 */
public class FileUtils
{
	public static void copyFileFromResource(String inputFile, VirtualFile virtualFile)
					throws IOException
	{
		InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(inputFile);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		while (is.available() > 0)
		{
			int nRead = is.read(buf);

			os.write(buf, 0, nRead);
		}

		virtualFile.setBinaryContent(os.toByteArray());
	}

    public static void copyFileFromResource(VirtualFile virtualDirectory, String inputFile, String filename)
    {
        try
        {

            VirtualFile virtualFile = virtualDirectory.createChildData(virtualDirectory, filename);

            if (virtualFile == null) return;

            copyFileFromResource(inputFile, virtualFile);
        }
        catch (IOException e1)
        {
            Messages.showErrorDialog("Could not create " + filename, "CouldNotCreate");
        }
    }
}
