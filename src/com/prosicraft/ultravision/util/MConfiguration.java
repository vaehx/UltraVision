/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author prosicraft
 */
public class MConfiguration {
    public enum DataType {
        DATATYPE_STRING,
        DATATYPE_INTEGER,
        DATATYPE_LIST_STRING,
        DATATYPE_LIST_INTEGER,
        DATATYPE_BOOLEAN        
    }
    
    private FileConfiguration       fc1 = null;
    private File                    f1  = null;
    private Map<String,DataType>    dt  = null;
    
    public MConfiguration (FileConfiguration fc, File f) {
        this.fc1 = fc;
        this.f1 = f;
    }
    
    public MConfiguration (FileConfiguration fc, File f, Map<String,DataType> dataTypeTable)
    {
        this.fc1 = fc;
        this.f1 = f;
        this.dt = dataTypeTable;
    }
    
    public void setDataTypeTable ( Map<String,DataType> dataTypeTable )
    {
        this.dt = dataTypeTable;
    }          
    
    public Map<String,DataType> getDataTypeTable ()
    {
        return this.dt;
    }
    
    public DataType getDataType ( String node )            
    {                
        if( dt.containsKey( node ) )
            return dt.get( node );
        return DataType.DATATYPE_STRING;
    }

    public File getFile () {
        return f1;
    }        
    
    public FileConfiguration getConfig () {
        return fc1;
    }
    
    public void setFile (File f) {
        f1 = f;
    }
    
    public void setConfig (FileConfiguration fc) {
        fc1 = fc;
    }
    
    public void setDefault (String path, Object def) {
        fc1.set(path, fc1.get(path, def));
    }
    
    public void setProperty (String path, Object value) {
        fc1.set(path, value);
    }   
    
    public void set (String path, Object value) {
        fc1.set(path, value);
    }
    
    public boolean getBoolean (String path, boolean def) {
        return fc1.getBoolean(path, def);
    }
    
    public String getString (String path, String ref) {
        return fc1.getString(path, ref);
    }
    
    public String getString (String path) {
        return getString(path, "");
    }       
    
    public String getValueAsString (String path) {
        if( dt.containsKey( path ) )
        {
            if( dt.get(path) == DataType.DATATYPE_BOOLEAN )
                return String.valueOf(fc1.getBoolean(path));
            else if( dt.get(path) == DataType.DATATYPE_INTEGER )
                return String.valueOf(fc1.getInt(path));
            else if( dt.get(path) == DataType.DATATYPE_LIST_STRING )
                return String.valueOf(fc1.getStringList(path));
            else if( dt.get(path) == DataType.DATATYPE_LIST_INTEGER )
                return String.valueOf(fc1.getIntegerList(path));
            else
                return "Unknown Data type '" + String.valueOf(dt.get(path)) + "'";
        }
        return "DataType for path '" + path + "' not set!";
    }
    
    public Set<String> getKeys (String path, boolean deep) {
        try {
            return fc1.getConfigurationSection(path).getKeys(deep);
        } catch (NullPointerException nex) {
            return new HashSet<String>();
        }
    }
    
    public Set<String> getKeys (String path) {
        return getKeys (path, false);
    }      
    
    public void save () {
        try {
            fc1.options().header("Plugin configuration file");
            fc1.save(f1);            
        } catch (IOException iex) {            
            MLog.e("Can't save configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file!"));
        }
    }
    
    /**
     * Creates file if it doesn't exist
     */
    public void init () {
        try {                       
            if ( f1 != null && !f1.exists() ) {
                f1.createNewFile();
            }
        } catch (IOException ex) {
            MLog.e("Can't create not existing configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file!"));
        }
    }
    
    public void load () {
        try {
            fc1.load(f1);
        } catch (IOException iex) {
            MLog.e("Can't load configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file! (forgot init?)"));
        } catch (InvalidConfigurationException icex) {
            MLog.e("Loaded invalid configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file! (forgot init?)"));
        }
    }
    
    public void clear () {        
        for ( String s1 : fc1.getKeys(false) )
            fc1.set(s1, null);        
    }
    
    public int getInt (String path, int def) {
        return fc1.getInt(path, def);
    }
    
    public long getLong (String path, long def) {
        return fc1.getLong(path, def);
    }
    
    public double getDouble (String path, double def) {
        return fc1.getDouble(path, def);
    }
    
    public List<String> getStringList (String path, List<String> def) {
        List res = fc1.getStringList(path);
        return ((res == null) ? ((def == null) ? new ArrayList() : def) : res);
    }
    
    public void removeProperty (String path) {
        fc1.set(path, null);
    }
    
    public void remove (String path) { removeProperty(path); }
    
    public static String normalizePath (String path) {
        return ((path.equals("")) ? "not given file" : path);
    }
    
    public static String normalizePath (File file) {
        if ( file == null ) return "[ERR:Got No File!]";
        return ((file.getAbsolutePath().equals("")) ? "not given file" : file.getAbsolutePath());
    }
} 
