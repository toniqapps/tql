package com.toniq.tql.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import helper.logger.Logger;
import helper.utils.AppUtilObject;

public class FileUtil extends AppUtilObject {


    public static File getFile(File parent, String find)
    {
        File child = null;
        if (parent != null)
        {
            for (File f : parent.listFiles())
            {
                if (f.getName().equalsIgnoreCase(find))
                {
                    child = f;
                    break;
                }
            }
        }
        return child;
    }

    public static List<String> getUSBStoragePath(String directoryName)
    {
        List<String> path = new ArrayList<>();
        try
        {
            File file = new File("/storage");
            File[] storageList = file.listFiles();

            if (storageList != null && storageList.length > 0)
            {
                for (File storage : storageList)
                {
                    File[] directoryList = storage.listFiles();
                    if (directoryList != null && directoryList.length > 0)
                    {
                        for (File directory : directoryList)
                        {
                            if (directory.getName().equalsIgnoreCase(directoryName))
                            {
                                path.add(storage.getAbsolutePath());
                                path.add(directory.getName());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return path;
    }

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws Exception
    {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try
        {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally
        {
            try
            {
                if (fromChannel != null)
                {
                    fromChannel.close();
                }
                if (toChannel != null)
                {
                    toChannel.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static File getUsbRootDirectory(String otgPath, String directoryName)
    {
        File usbRootDirectory = null;
        File sdCard = new File(otgPath);
        File[] directoryArray = sdCard.listFiles();
        if (directoryArray != null)
        {
            for (File directory : directoryArray)
            {
                if (directory.getName().equalsIgnoreCase(directoryName))
                {
                    usbRootDirectory = directory;
                    break;
                }
            }
        }
        return usbRootDirectory;
    }

    public static File stringToFile(byte[] btDataArray)
    {
        File file = null;
        FileOutputStream oFileOutputStream = null;
        try
        {
            boolean valid = false;
            file = new File(Environment.getExternalStorageDirectory() + "/dummyFile.txt");
            if (!file.exists())
            {
                if (file.createNewFile())
                {
                    valid = true;
                }
            }
            else
            {
                valid = true;
            }

            if (valid)
            {
                oFileOutputStream = new FileOutputStream(file);
                oFileOutputStream.write(btDataArray);
                oFileOutputStream.flush();
            }
        } catch (Exception e)
        {
            Logger.logException("helper.utils.javaUtils.fileToDrive", e);
        } finally
        {
            try
            {
                if (oFileOutputStream != null)
                {
                    oFileOutputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return file;
    }


    public static void fileToDrive(File usbRootDirectory, String strFileName, byte[] btDataArray)
    {
        File file = new File(usbRootDirectory.getAbsolutePath() + "/" + strFileName);
        FileOutputStream oFileOutputStream = null;
        try
        {
            boolean valid = false;
            if (!file.exists())
            {
                if (file.createNewFile())
                {
                    valid = true;
                }
            }
            else
            {
                valid = true;
            }

            if (valid)
            {
                oFileOutputStream = new FileOutputStream(file);
                oFileOutputStream.write(btDataArray);
                oFileOutputStream.flush();
            }
        } catch (Exception e)
        {
            Logger.logException("helper.utils.javaUtils.fileToDrive", e);
        } finally
        {
            try
            {
                if (oFileOutputStream != null)
                {
                    oFileOutputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public static DocumentFile createDirectory(DocumentFile rootFile, String directoryName)
    {
        DocumentFile directoryFile = rootFile.findFile(directoryName);
        if (directoryFile == null)
        {
            directoryFile = rootFile.createDirectory(directoryName);
        }
        return directoryFile;
    }

    public static DocumentFile createFile(DocumentFile rootFile, String fileName, boolean isRecreate)
    {
        DocumentFile directoryFile = rootFile.findFile(fileName);

        if (isRecreate && directoryFile != null)
        {
            directoryFile.delete();
            directoryFile = null;
        }
        if (directoryFile == null)
        {
            directoryFile = rootFile.createFile("json", fileName);
        }
        return directoryFile;
    }


    public static DocumentFile isFileValid(DocumentFile rootFile, String fileName)
    {
        return rootFile.findFile(fileName);
    }

    public static void writeToFile(Context context, DocumentFile documentFile, byte[] data) throws Exception
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = context.getContentResolver().openOutputStream(documentFile.getUri());
            if (outputStream != null)
            {
                outputStream.write(data);
            }
        } finally
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static byte[] readFromFile(Context context, DocumentFile documentFile) throws Exception
    {
        byte[] dataBytes = null;
        InputStream inputStream = null;
        try
        {
            inputStream = context.getContentResolver().openInputStream(documentFile.getUri());
            if (inputStream != null)
            {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1)
                {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                dataBytes = buffer.toByteArray();
            }
        } finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return dataBytes;
    }

    public static byte[] readFromFile(File file) throws IOException
    {

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try
        {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1)
            {
                ous.write(buffer, 0, read);
            }
        } finally
        {
            try
            {
                if (ous != null)
                {
                    ous.close();
                }
            } catch (IOException e)
            {
            }

            try
            {
                if (ios != null)
                {
                    ios.close();
                }
            } catch (IOException e)
            {
            }
        }
        return ous.toByteArray();
    }


}