package com.mkoffoine.transltion;

import com.mkoffoine.transltion.example.ReadXMLFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Main {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        Map<String, String>  map = StringXmlParser.readFile("string.xml");
        map.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String name, String val) {
                System.out.println("------ "+name + " - " +val);
            }
        });

        Map<String, String>  mapUpd = StringXmlParser.readFile("stringUpd.xml");
        mapUpd.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String name, String val) {
                System.out.println("------2 "+name + " - " +val);
            }
        });

        Files.copy(Path.of("stringUpd.xml"), Path.of("stringRes.xml"), StandardCopyOption.REPLACE_EXISTING);
        String fileText= Files.readString( Path.of("stringRes.xml"));
        Set<String> keys = map.keySet();
        Set<String> keysUpd = map.keySet();
        for(String k : keys) {
            String s = mapUpd.get(k);
            if (s!=null && s.length() >1) {
                if (!map.get(k).equals(s)) {
                    fileText = fileText.replace(mapUpd.get(k), map.get(k));
                    System.out.println("replace "+mapUpd.get(k)+" "+map.get(k));
                }
            }
        }
        Files.writeString( Path.of("stringRes2.xml"), fileText);
    }
}
