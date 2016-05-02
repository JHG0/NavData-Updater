package io.github.jhg0.NavDataUpdater;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class DataHandler
{
    private ArrayList<String> exceptions;

    public DataHandler(ArrayList exceptions)
    {
        this.exceptions = exceptions;
    }

    public long updateData()
    {
        long z = System.currentTimeMillis();
        try
        {
            updateAirports();
        } catch (Exception ex)
        {
            if(ex instanceof FileNotFoundException)
                return -2;
            else
                return -1;
        }

        try
        {
            updateWaypoints();
        } catch (Exception ex)
        {
            if(ex instanceof FileNotFoundException)
                return -2;
            else
                return -1;
        }

        return System.currentTimeMillis() - z;
    }

    private boolean isRemovable(String id, String type)
    {
        for (String s : exceptions)
        {
            String[] q = s.split("\\|");
            if ((q.length == 1 || type == null || q[1].equals(type)) && q[0].contains("*") && id.substring(0, q[0].indexOf("*")).equals(q[0].substring(0, q[0].indexOf("*"))))
                return true;
            else if ((q.length == 1 || type == null || q[1].equals(type)) && q[0].equals(id)) return true;
        }
        return false;
    }

    private void updateAirports() throws Exception
    {
        URL xmlURL = new URL("http://www.myfsim.com/sectorfilecreation/Airports.xml");
        InputStream xml = xmlURL.openStream();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xml);
        xml.close();

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Airport");

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) nNode;
                if (isRemovable(eElement.getAttribute("ID"), null)) eElement.getParentNode().removeChild(eElement);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("C:\\Users\\Josh\\Downloads\\Airports.xml"));
        transformer.transform(source, result);
    }

    private void updateWaypoints() throws Exception
    {
        URL xmlURL = new URL("http://www.myfsim.com/sectorfilecreation/Waypoints.xml");
        InputStream xml = xmlURL.openStream();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xml);
        xml.close();

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Waypoint");

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) nNode;
                if (isRemovable(eElement.getAttribute("ID"), eElement.getAttribute("Type")))
                    eElement.getParentNode().removeChild(eElement);
            }
        }

        for (String s : getAdditions())
        {
            Element element = doc.createElement("Waypoint");
            String[] q = s.split("\\|");
            element.setAttribute("ID", q[0]);
            element.setAttribute("Type", "Generic");
            Element child = doc.createElement("Location");
            child.setAttribute("Lat", q[1]);
            child.setAttribute("Lon", q[2]);
            element.appendChild(child);
            doc.getDocumentElement().appendChild(element);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("C:\\Users\\Josh\\Downloads\\Waypoints.xml"));
        transformer.transform(source, result);
    }

    private ArrayList<String> getAdditions()
    {
        ArrayList<String> additions = new ArrayList<String>();
        for (String s : exceptions)
            if (s.split("\\|").length == 3)
                additions.add(s);
        return additions;
    }
}
