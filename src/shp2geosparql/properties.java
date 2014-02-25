/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shp2geosparql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Alex
 */
public class properties {
    public Properties getproperties(String arc) throws FileNotFoundException{
         Properties prop = null;     
        try{
            CodeSource codeSource = properties.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            File jarDir = jarFile.getParentFile();
            
            if (jarDir != null && jarDir.isDirectory()){
                File propFile = new File(jarDir, arc);
                prop = new Properties();
                prop.load(new BufferedReader(new FileReader(propFile.getAbsoluteFile())));
            }else {
                return null;
            }
        }catch (URISyntaxException ex){
            Logger.getLogger(properties.class.getName()).log(Level.SEVERE, null, ex);
        }catch (FileNotFoundException ex){
            throw new FileNotFoundException("properties file is not found");
        }catch (IOException ex){
           Logger.getLogger(properties.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prop;
    }
}
