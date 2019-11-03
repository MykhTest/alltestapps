package com.mkoffoine.transltion;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class StringXmlParser {
    private static Boolean DEBUG = false;
    public static Map<String, String> readFile(String fileName) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> map = new HashMap<>();
        String name = "";
        String value = "";
        File file = new File(fileName);
        if (DEBUG) System.out.println(file.getAbsolutePath());

        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document doc = null;
        try {
             doc = dBuilder.parse(file);
        }catch (SAXParseException saxEx) {
            File tmpFile = new File(fileName+"tmp.xml");
            Files.copy(file.toPath(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            fixFile(tmpFile, saxEx);
            doc = dBuilder.parse(tmpFile);
        }



        if (DEBUG) System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        if (doc.hasChildNodes()) {
            NodeList nodeList = doc.getChildNodes();
            nodeList = nodeList.item(0).getChildNodes();
            for (int count = 0; count < nodeList.getLength(); count++) {

                Node tempNode = nodeList.item(count);

                // make sure it's element node.
                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                    // get node name and value
                    if (DEBUG)System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                    if (DEBUG)System.out.println("Node Value =" + tempNode.getTextContent());
                    value = tempNode.getTextContent();

                    if (tempNode.hasAttributes()) {

                        // get attributes names and values
                        NamedNodeMap nodeMap = tempNode.getAttributes();

                        for (int i = 0; i < nodeMap.getLength(); i++) {

                            Node node = nodeMap.item(i);
                            if (DEBUG)System.out.println("attr name : " + node.getNodeName());
                            if (DEBUG)System.out.println("attr value : " + node.getNodeValue());
                            name = node.getNodeValue();

                        }
                    }
                    if (DEBUG)System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

                }
                map.put(name, value);

            }

        }

        return map;
    }
    static void fixFile(File tmpFile, SAXParseException saxEx) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("/////////"+saxEx.toString()+"----");
        int lineNumberIndex = saxEx.toString().indexOf("lineNumber");
        int colomn = saxEx.toString().indexOf(";",lineNumberIndex);
        int lineNumber = Integer.parseInt(saxEx.toString().substring(lineNumberIndex+12, colomn));
        System.out.println("---"+lineNumber);
        Scanner scanner = new Scanner(tmpFile);
        int i = 0;
        String badLine = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            i++;
            if (i == lineNumber) {
                badLine = line;
                String name = line.substring(line.indexOf("\""), line.indexOf("\"",line.indexOf("\"")+1));
                System.out.println(" bad ---- name -- "+name);
                break;
            }

        }
        scanner.close();
        String fixedContent = Files.readString(tmpFile.toPath()).replace(badLine, " <!-- nononono bad line -->");
        Files.writeString(tmpFile.toPath(),fixedContent);


        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = null;
        try {
            doc = dBuilder.parse(tmpFile);
        }catch (SAXParseException sxEx) {
            fixFile(tmpFile, sxEx);
        }

    }

}
