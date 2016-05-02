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
import java.util.List;

public class DataHandler
{
    private List<String> exceptions;

    public DataHandler(List exceptions)
    {
        this.exceptions = exceptions;
    }

    public long updateData()
    {
        long z = System.currentTimeMillis();
        try
        {
            boolean a = updateAirports();
            if (a) return -3;
        } catch (Exception ex)
        {
            if (ex instanceof FileNotFoundException)
                return -2;
            else
                return -1;
        }

        try
        {
            boolean a = updateWaypoints();
            if (a) return -3;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            if (ex instanceof FileNotFoundException)
                return -2;
            else
                return -1;
        }

        return System.currentTimeMillis() - z;
    }

    private boolean isRemovable(String id, String type)
    {
        if (exceptions == null || exceptions.isEmpty()) return false;
        for (String s : exceptions)
        {
            String[] q = s.split("\\|");
            if ((q.length == 1 || type == null || q[1].equals(type)) && q[0].contains("*") && id.substring(0, q[0].indexOf("*")).equals(q[0].substring(0, q[0].indexOf("*"))))
                return true;
            else if ((q.length == 1 || type == null || q[1].equals(type)) && q[0].equals(id)) return true;
        }
        return false;
    }

    private boolean updateAirports() throws Exception
    {
        File vSTARS = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vSTARS\\NavData");
        File vERAM = new File(System.getProperty("user.home") + "\\AppData\\Local\\vERAM");
        if (!vSTARS.exists() && !vERAM.exists()) return true;

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
                eElement.setAttribute("MagVar", "0");
                if (isRemovable(eElement.getAttribute("ID"), null)) eElement.getParentNode().removeChild(eElement);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        if (vSTARS.exists())
            transformer.transform(source, new StreamResult(vSTARS + "\\Airports.xml"));
        if (vERAM.exists())
            transformer.transform(source, new StreamResult(vERAM + "\\Airports.xml"));
        return false;
    }

    private boolean updateWaypoints() throws Exception
    {
        File vSTARS = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vSTARS\\NavData");
        File vERAM = new File(System.getProperty("user.home") + "\\AppData\\Local\\vERAM");
        if (!vSTARS.exists() && !vERAM.exists()) return true;

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
            element.setAttribute("Type", "Intersection");
            Element child = doc.createElement("Location");
            child.setAttribute("Lat", q[1].replaceAll("[^0-9A-Z\\.\\-]", ""));
            child.setAttribute("Lon", q[2].replaceAll("[^0-9A-Z\\.\\-]", ""));
            element.appendChild(child);
            doc.getDocumentElement().appendChild(element);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        if (vSTARS.exists())
            transformer.transform(source, new StreamResult(vSTARS + "\\Waypoints.xml"));
        if (vERAM.exists())
            transformer.transform(source, new StreamResult(vERAM + "\\Waypoints.xml"));
        return false;
    }

    private List<String> getAdditions()
    {
        if (exceptions == null || exceptions.isEmpty()) return new ArrayList<String>();
        List<String> additions = new ArrayList<String>();
        for (String s : exceptions)
            if (s.split("\\|").length == 3)
                additions.add(s);
        return additions;
    }
}
