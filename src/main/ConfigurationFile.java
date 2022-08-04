package main;

import exceptions.SystemError;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationFile {
   private String location;
   private FileReader confFile;
   private boolean opened;
   private   Properties settingsProperties;

    public ConfigurationFile(String location) {
        this.location = location;
        opened=false;
        confFile=null;
    }
    public void openSettingsFile()throws SystemError
    {
        try {
            confFile =new FileReader(location);
            settingsProperties=new Properties();
            settingsProperties.load(confFile);
            opened=true;

        } catch (Exception e) {
            throw new SystemError(SystemError.ErrorType.configFileError);
        }
    }
    public void closeSettingsFile()throws SystemError
    {
        try {
            confFile.close();
            settingsProperties=null;
            opened=false;
        } catch (IOException e) {
            throw new SystemError(SystemError.ErrorType.configFileError);
        }
    }
    public String getValue(String name)throws SystemError
    {
        if(!opened)
            openSettingsFile();
        return settingsProperties.getProperty(name);


    }
    public  void addValue(String name,String value)throws SystemError
    {
        if(!opened)
            openSettingsFile();
        settingsProperties.put(name,value);


    }
    public void saveData() throws SystemError
    {
        try {
            FileOutputStream file=new FileOutputStream(location);
                settingsProperties.store(file,"");

        } catch (Exception e) {
            throw new SystemError(SystemError.ErrorType.configFileError);
        }

    }

}
