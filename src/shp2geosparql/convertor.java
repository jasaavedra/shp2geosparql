/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shp2geosparql;

import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Alex
 */
public class convertor {
    Model model;
    IDBConnection conn;
        
        String nsgeo = "http://www.opengis.net/ont/geosparql#"; 
        String sf = "http://www.opengis.net/ont/sf#";   
        
   
      public static void main(String[] args) throws Exception {

        
        Properties myproperties = new properties().getproperties("configuration.properties");
        CodeSource codeSource = properties.class.getProtectionDomain().getCodeSource();
        String ontologyUri = myproperties.getProperty("ontologyUri");
        String nsOntology = myproperties.getProperty("nsOntology");
        String resourceUri = myproperties.getProperty("resourceUri");
        String lang = myproperties.getProperty("lang");
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        File jarDir = jarFile.getParentFile();
        File folder = new File(jarDir+"/modelo"); 
        File rdfFolder = new File(jarDir+"/rdf");
        rdfFolder.mkdir();
        String modelDirectory = folder.getPath();
        convertor gr1 = new convertor(modelDirectory, ontologyUri, resourceUri, nsOntology);
        String fileString = myproperties.getProperty("shpDir")+"/"+ myproperties.getProperty("shpName")+".shp";
        String featureString = myproperties.getProperty("shpName");
        String featureClass = myproperties.getProperty("ontologyClass");
        String attribute =  myproperties.getProperty("idField");
        String label = myproperties.getProperty("labelField");
        //String linkUriField = myproperties.getProperty("linkUriField");
        String outputFile = myproperties.getProperty("outputFileDir");
        //String shpLinkString = myproperties.getProperty("shpLink")+"/"+ myproperties.getProperty("shpName")+".shp";
        String includeLabel = myproperties.getProperty("includeLabel");
        String includePoints = myproperties.getProperty("includePoints");
        String includeRelations = myproperties.getProperty("includeRelations");
        String ignore = "";
        gr1.SHPtoRDF(null, null, fileString, featureString, featureClass, outputFile, attribute, label, ignore, resourceUri, ontologyUri, lang, includeLabel, includeRelations, includePoints);
        
    }
    
    public convertor(String modelDirectory, String ontologyUri, String resourceUri, String nsOntology) throws ClassNotFoundException {

        File f = new File(modelDirectory);

        if (f.exists()){
            String[] ficheros = f.list();
            if (ficheros.length>0){
                for (int i=0;i<ficheros.length;i++){
                    File auxFile = new File(modelDirectory+"/"+ficheros[i]);
                    boolean delete = auxFile.delete();
                }
            }
            if (f.delete())
                System.out.println("El directorio " + modelDirectory + " ha sido borrado correctamente");
            else
                System.out.println("El directorio " + modelDirectory + " no se ha podido borrar");
        }

        boolean dirBool = f.mkdir();
    
        model = TDBFactory.createModel(modelDirectory);
        model.removeAll();
        model.setNsPrefix(nsOntology, ontologyUri); 
        model.setNsPrefix("resource", resourceUri);
        model.setNsPrefix("geo", nsgeo);
        model.setNsPrefix("sf", sf);
              

    }
 

    private void SHPtoRDF(String source, String target, String fileString, String featureString, String featureClass, String outputFile, String attribute, String label, String ignore, String resourceUri, String ontologyUri,  String lang, String includeLabel, String includeRelations, String includePoints) throws MalformedURLException, IOException, NoSuchAuthorityCodeException, FactoryException, TransformException{

        File file = new File(fileString);
        Map map = new HashMap();
        map.put("url", file.toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(featureString);
        SimpleFeatureCollection collection = featureSource.getFeatures();
        Iterator iterator = collection.iterator();          
        int  collectionSize = collection.size();
        
        int num = 0;

        while (iterator.hasNext()) {
            
            
            
            SimpleFeatureImpl feature = (SimpleFeatureImpl) iterator.next();            
            Geometry o = (Geometry) feature.getDefaultGeometry();
            
            
                                   
            String namn1 = feature.getAttribute(attribute).toString();
            namn1 = namn1.toLowerCase();
            String namn2 = feature.getAttribute(label).toString();
            System.out.println("###################El valor de FEATURENAME2 is -->"+ namn1);

            // Si el elemento tiene de nombre N_P, no sabemos como actuar con el
            if (!namn1.equals(ignore)) //&& (namn1.startsWith("Río") || namn1.startsWith("Riu") || namn1.startsWith("Rio"))
            {
                String tipo = featureClass;
                String resource = namn1;
                String defaultLang = lang;
                if (source != null && target != null) {
                    //Quiere decir que hay que transformar
                    CoordinateReferenceSystem sourceCRS = CRS.decode(source);
                    CoordinateReferenceSystem targetCRS = CRS.decode(target);
                    MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
                    Geometry targetGeometry = JTS.transform(o, transform);
                    o = targetGeometry;
                }
                String hash = HashGeometry.getHash(o.toText());
                //URLEncoder.encode("Río"+name, "utf-8");
                String encTipo = URLEncoder.encode(tipo,"utf-8").replace("+", "");
                String encResource = URLEncoder.encode(resource,"utf-8").replace("+", "");
                 //encTipo + "/" +:
                String aux = encResource;
                insertarResourceTypeResource(resourceUri + aux, ontologyUri + URLEncoder.encode(tipo, "utf-8").replace("+", "%20"));
                defaultLang=detectLang(resource);
                if (includeLabel.equals("yes")){ 
                insertarLabelResource(resourceUri + aux, namn2, defaultLang);
                }
                
                System.out.println("object converted-->"+namn1);
                              
                
                
                
                if (o.getGeometryType().equals("LineString"))
                    insertarLineString(resourceUri, aux, hash, o, collectionSize, collection, includeRelations);
                else if (o.getGeometryType().equals("Point"))
                    insertarPoint(resourceUri, aux, hash, o, collectionSize, collection, includeRelations);
                else if (o.getGeometryType().equals("Polygon")){
                    insertarPolygon(resourceUri, aux, hash, o,  collectionSize, collection, includeRelations);
                    if (includePoints.equals("yes")){
                        Point centroide = o.getCentroid();     
                        String hashPoint = HashGeometry.getHash(centroide.toText());
                        insertarCentroide(resourceUri, resource, centroide, hashPoint);
                    }
                }
                else if (o.getGeometryType().equals("MultiPolygon")){
                    insertarMultiPolygon(resourceUri, aux, hash, o, collectionSize, collection, includeRelations);     
                    if (includePoints.equals("yes")){
                        Point centroide = o.getCentroid();     
                        String hashPoint = HashGeometry.getHash(centroide.toText());                   
                        insertarCentroide(resourceUri, resource, centroide, hashPoint);
                     }
                }
                else if (o.getGeometryType().equals("MultiLineString")){
                    insertarMultiLineString(resourceUri, aux, hash, o, collectionSize, collection, includeRelations);
                }
                 else if (o.getGeometryType().equals("MultiPoint")){
                    insertarMultiPoint(resourceUri, aux, hash, o, collectionSize, collection, includeRelations);
                 }
                
            }
            else {
                System.out.println("No transformamos el elemento número-->"+num);
            }
            /*if (shpLinkString != null ){
            insertarLinks(resourceUri, namn1, o, collection, shpLinkString, linkUriField );
            }
            num++;
            if (num>2)
                break;*/
        }
        FileOutputStream out = new FileOutputStream(outputFile);
        model.write(out);

    }

    /*private void insertarLinks(String resourceUri, String resource, Geometry geo, FeatureCollection collection, String shpLinkString, String linkUriField)throws MalformedURLException, IOException, NoSuchAuthorityCodeException, FactoryException, TransformException
    {
        File file = new File(shpLinkString);
        Map map = new HashMap();
        map.put("url", file.toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(shpLinkString);
        SimpleFeatureCollection collectionLinks = featureSource.getFeatures();        
        Iterator iterator = collectionLinks.iterator();          
     
        while (iterator.hasNext()) {
            SimpleFeatureImpl featurelink = (SimpleFeatureImpl) iterator.next();            
            Geometry linkGeo = (Geometry) featurelink.getDefaultGeometry();
           
            if (contains(geo,linkGeo))
            {
                
               String linkUri = featurelink.getAttribute(linkUriField).toString();
               insertarTripletaResource(resourceUri + resource, nsgeo + "sfContains", resourceUri + linkUri);
            }                
           
        }
        
    }*/
    
    private void insertarLineString(String resourceUri, String resource, String hash, Geometry geo, int collectionSize, FeatureCollection collection, String includeRelations )throws UnsupportedEncodingException{
        
        insertarResourceTypeResource(resourceUri+ "Geometry/"+ hash, nsgeo + URLEncoder.encode("LineString", "utf-8").replace("+", "%20"));
        insertarTripletaResource(resourceUri + resource,nsgeo + "defaultGeometry", resourceUri + "Geometry/" + hash);
        insertarTripletaLiteral(resourceUri +"Geometry/"+ hash, nsgeo + "asWKT" , geo.toText(), nsgeo + "wktLiteral");
        if (includeRelations.equals("yes")){  
        Iterator iterator2 = collection.iterator();
        for (int i=0; i< collectionSize; i++) {
            
            
            SimpleFeatureImpl featureP = (SimpleFeatureImpl) iterator2.next();                   
            Geometry geo2 = (Geometry) featureP.getDefaultGeometry();
            if  (equals(geo,geo2)== false){
            relacionesTopologicas(resourceUri, geo, geo2, hash);
            }   
            
         }  
        }
    }

    private void insertarPolygon(String resourceUri, String resource, String hash, Geometry geo, int collectionSize, FeatureCollection collection, String includeRelations) throws UnsupportedEncodingException{
        
       insertarResourceTypeResource(resourceUri+ "Geometry/"+ hash, sf + URLEncoder.encode("Polygon", "utf-8").replace("+", "%20"));
       insertarTripletaResource(resourceUri + resource,nsgeo + "defaultGeometry", resourceUri + "Geometry/" + hash);
       insertarTripletaLiteral(resourceUri +"Geometry/"+ hash, nsgeo + "asWKT" , geo.toText(), sf + "wktLiteral");
       if (includeRelations.equals("yes")){ 
       Iterator iterator2 = collection.iterator();
        for (int i=0; i< collectionSize; i++) {
            
            SimpleFeatureImpl featureP = (SimpleFeatureImpl) iterator2.next();                   
            Geometry geo2 = (Geometry) featureP.getDefaultGeometry();
            if  (equals(geo,geo2)== false){
            relacionesTopologicas(resourceUri, geo, geo2, hash);
            }   
         }
       }
    }
    
    private void insertarCentroide(String resourceUri, String resource, Point centroide, String hashPoint) throws UnsupportedEncodingException{
        
       insertarResourceTypeResource(resourceUri+ "Geometry/"+ hashPoint, sf + URLEncoder.encode("Point", "utf-8").replace("+", "%20"));
       insertarTripletaResource(resourceUri + resource,nsgeo + "hasGeometry", resourceUri + "Geometry/" + hashPoint);
       insertarTripletaLiteral(resourceUri +"Geometry/"+ hashPoint, nsgeo + "asWKT" , centroide.toText(), sf + "wktLiteral");
    
    }
    
    private void insertarPoint(String resourceUri, String resource, String hash, Geometry geo, int collectionSize, FeatureCollection collection, String includeRelations) throws UnsupportedEncodingException{
     insertarResourceTypeResource(resourceUri+ "Geometry/"+ hash, sf + URLEncoder.encode("Point", "utf-8").replace("+", "%20"));
     insertarTripletaResource(resourceUri + resource,nsgeo + "defaultGeometry", resourceUri + "Geometry/" + hash);
     insertarTripletaLiteral(resourceUri +"Geometry/"+ hash, nsgeo + "asWKT" , geo.toText(), sf + "wktLiteral");
     if (includeRelations.equals("yes")){ 
     Iterator iterator2 = collection.iterator();
        for (int i=0; i< collectionSize; i++) {
            
            SimpleFeatureImpl featureP = (SimpleFeatureImpl) iterator2.next();                   
            Geometry geo2 = (Geometry) featureP.getDefaultGeometry();
            if  (equals(geo,geo2)== false){
            relacionesTopologicas(resourceUri, geo, geo2, hash);
            }              
         }
     }
    }
       
    
    private void insertarMultiPolygon(String resourceUri, String resource, String hash, Geometry geo,int collectionSize, FeatureCollection collection, String includeRelations) throws UnsupportedEncodingException{
        
        insertarResourceTypeResource(resourceUri + "Geometry/" + hash, sf + URLEncoder.encode("MultiPolygon", "utf-8").replace("+", "%20"));
        insertarTripletaResource(resourceUri + resource,nsgeo + "defaultGeometry", resourceUri  + "Geometry/" + hash);
        insertarTripletaLiteral(resourceUri + "Geometry/" + hash, nsgeo + "asWKT" , geo.toText(), sf + "wktLiteral");
        if (includeRelations.equals("yes")){ 
        Iterator iterator2 = collection.iterator();
        
        for (int i=0; i< collectionSize; i++) {
            
            SimpleFeatureImpl featureP = (SimpleFeatureImpl) iterator2.next();                   
            Geometry geo2 = (Geometry) featureP.getDefaultGeometry();
            if  (equals(geo,geo2)== false){
            relacionesTopologicas(resourceUri, geo, geo2, hash);
            }   
         }
        }
    }

    private void insertarMultiPoint(String resourceUri, String resource, String hash, Geometry geo, int collectionSize, FeatureCollection collection, String includeRelations) throws UnsupportedEncodingException{
        
       insertarResourceTypeResource(resourceUri+ "Geometry/"+ hash, sf + URLEncoder.encode("MultiPoint", "utf-8").replace("+", "%20"));
        insertarTripletaResource(resourceUri + resource,nsgeo + "defaultGeometry", resourceUri + "Geometry/" + hash);
        insertarTripletaLiteral(resourceUri +"Geometry/"+ hash, nsgeo + "asWKT" , geo.toText(), sf + "wktLiteral");
       if (includeRelations.equals("yes")){ 
        Iterator iterator2 = collection.iterator();
        for (int i=0; i< collectionSize; i++) {
            
            SimpleFeatureImpl featureP = (SimpleFeatureImpl) iterator2.next();                   
            Geometry geo2 = (Geometry) featureP.getDefaultGeometry();
            if  (equals(geo,geo2)== false){
            relacionesTopologicas(resourceUri, geo, geo2, hash);
            }             
         }
       }

    }
    
     private void insertarMultiLineString(String resourceUri, String resource, String hash, Geometry geo, int collectionSize, FeatureCollection collection, String includeRelations) throws UnsupportedEncodingException{
        
        insertarResourceTypeResource(resourceUri+ "Geometry/"+ hash, nsgeo + URLEncoder.encode("MultiLineString", "utf-8").replace("+", "%20"));
        insertarTripletaResource(resourceUri + resource,nsgeo + "defaultGeometry", resourceUri + "Geometry/" + hash);
        insertarTripletaLiteral(resourceUri +"Geometry/"+ hash, nsgeo + "asWKT" , geo.toText(), nsgeo + "wktLiteral");
        if (includeRelations.equals("yes")){ 
        Iterator iterator2 = collection.iterator();
        for (int i=0; i< collectionSize; i++) {
            
            SimpleFeatureImpl featureP = (SimpleFeatureImpl) iterator2.next();                   
            Geometry geo2 = (Geometry) featureP.getDefaultGeometry();
            if  (equals(geo,geo2)== false){
            relacionesTopologicas(resourceUri, geo, geo2, hash);
            }         
         }
        }
    }
     
     private void relacionesTopologicas (String resourceUri, Geometry geo, Geometry geo2, String hash){
        if (geo != geo2){
            
            if (overlaps(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfOverlaps", resourceUri + "Geometry/" + hashGeo2); 
            } 
            if (contains(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfContains", resourceUri + "Geometry/" + hashGeo2); 
               
            }
            if (crosses(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfCrosses", resourceUri + "Geometry/" + hashGeo2); 
            }
            
            if (equals(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfEquals", resourceUri + "Geometry/" + hashGeo2); 
            }
            /*if (intersects(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfIntersects", resourceUri + "Geometry/" + hashGeo2); 
            }*/
            if (touches(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfTouches", resourceUri + "Geometry/" + hashGeo2); 
            }
            if (within(geo,geo2)){
               String hashGeo2 = HashGeometry.getHash(geo2.toText());
               insertarTripletaResource(resourceUri +"Geometry/"+ hash ,nsgeo + "sfWithin", resourceUri + "Geometry/" + hashGeo2); 
            }
        }
}
    private boolean overlaps(Geometry geo1, Geometry geo2)  {

        return geo1.overlaps( geo2 );        
        
    }
    private boolean contains(Geometry geo1, Geometry geo2)  {
        
    if  (geo1 != geo2){
        return geo1.contains( geo2 );
    }
    else 
        return false;        
    }
     private boolean crosses(Geometry geo1, Geometry geo2)  {

        return geo1.crosses( geo2 );        
        
    }
     
     private boolean disjoint(Geometry geo1, Geometry geo2)  {

        return geo1.disjoint( geo2 );        
        
    }
     private boolean equals(Geometry geo1, Geometry geo2)  {

        return geo1.equals( geo2 );        
        
    }
     private boolean intersects(Geometry geo1, Geometry geo2)  {

        return geo1.intersects( geo2 );        
        
    }
     
     private boolean touches(Geometry geo1, Geometry geo2)  {

        return geo1.touches( geo2 );        
        
    }
     private boolean within(Geometry geo1, Geometry geo2)  {

        return geo1.within(geo2) ;        
        
    }
    
  

    private void insertarResourceTypeResource(String r1, String r2) {

        Resource resource1 = model.createResource(r1);
        Resource resource2 = model.createResource(r2);
        model.add(resource1, RDF.type, resource2);
    }
    
         
    private void insertarTripletaLiteral(String s, String p, String o,  String x) {
        //Permite ingresar una tripleta en el rdf
        if (x != null) {
        
            Literal l = model.createTypedLiteral(o, x);
            Resource rGeometry = model.createResource(s);
            Property P = model.createProperty(p);
            rGeometry.addLiteral(P, l);
        } else {
            Resource rGeometry = model.createResource(s);
            Property P = model.createProperty(p);
            rGeometry.addProperty(P, o);
        }
        //   model.write(System.out);
    }

    private void insertarTripletaResource(String s, String p, String o) {
        //Permite ingresar una tripleta en el rdf
        Resource rGeometry = model.createResource(s);
        Property P = model.createProperty(p);
        Resource r2 = model.createResource(o);
        rGeometry.addProperty(P, r2);
        // model.write(System.out);
    }

    private void insertarLabelResource(String r1, String label, String lang) {

        Resource resource1 = model.createResource(r1);
        model.add(resource1, RDFS.label, model.createLiteral(label, lang));
    }

    private String detectLang(String r1){
        String defaultLang = "es";
        //('Barranco%')('Barranquillo%')('Barranc%')('Barrancu%')('Barranquet%')('Barranqueira%')
        if (r1.startsWith("Riu "))
            defaultLang="ca";
        return defaultLang;
    }


}
