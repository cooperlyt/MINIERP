package com.dgsoft.common.persistence;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/16/13
 * Time: 12:47 PM
 */
public class HibImportEncodeListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String fileName = servletContextEvent.getServletContext().getInitParameter("ddl");
        servletContextEvent.getServletContext().log("Convert " + fileName + "to import.sql");
        String srcEnc = fileName.substring(fileName.lastIndexOf(".") + 1);
        File importsqlfile = null;
        try{
            importsqlfile= new File(new URI(Thread.currentThread().getContextClassLoader().getResource("") + "import.sql"));
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

        Reader reader = null;
        Writer writer = null;
        try{
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName),srcEnc);

            writer = new OutputStreamWriter(new FileOutputStream(importsqlfile),System.getProperty("file.encoding"));

            int c = -1;
            StringBuffer tempSB = new StringBuffer();
            while ((c = reader.read()) != -1){
                writer.write(c);
                tempSB.append((char)c);
            }

            servletContextEvent.getServletContext().log(tempSB.toString());
            servletContextEvent.getServletContext().log("Generate import.sql(" + System.getProperty("file.encoding") + ") success!");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                reader.close();
                writer.flush();
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
