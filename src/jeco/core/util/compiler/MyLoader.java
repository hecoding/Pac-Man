package jeco.core.util.compiler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Compiles source and also makes sure that reloading a compiled class does not
 * "caches" the first compiled class.
 *
 * @author José Luis Risco Martín
 * @author J. M. Colmenar
 */
public class MyLoader extends ClassLoader {

    private static final Logger logger = Logger.getLogger(MyLoader.class.getName());
    protected String compilationDir;

    public MyLoader(String compilationDir) {
        this.compilationDir = compilationDir;
    }

    @Override
    public Class<?> loadClass(String className) {
        return findClass(className);
    }

    @Override
    public Class<?> findClass(String className) {
        try {
            byte[] bytes = loadClassData(className);
            Class<?> classObject = super.defineClass(className, bytes, 0, bytes.length);
            return classObject;
        } catch (IOException ex1) {
            try {
                return super.loadClass(className);
            } catch (ClassNotFoundException ex2) {
                logger.info(ex2.getLocalizedMessage());
            }
            logger.severe(ex1.getLocalizedMessage());
            return null;
        }
    }

    private byte[] loadClassData(String className) throws IOException {
        File f = new File(compilationDir + File.separator + className + ".class");
        int size = (int) f.length();
        byte buff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        return buff;
    }
}
