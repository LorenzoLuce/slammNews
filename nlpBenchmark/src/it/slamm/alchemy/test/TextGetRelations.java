package it.slamm.alchemy.test;


import com.alchemyapi.api.*;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class TextGetRelations {
    public static void main(String[] args)
        throws IOException, SAXException,
               ParserConfigurationException, XPathExpressionException
    {
        // Create an AlchemyAPI object.
        AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");
		
		AlchemyAPI_RelationParams relationParams = new AlchemyAPI_RelationParams();
/*		relationParams.setSentiment(true);
		relationParams.setEntities(true);
		relationParams.setDisambiguate(true);
		relationParams.setSentimentExcludeEntities(true);
		relationParams.setRequireEntities(true);
		relationParams.setSentimentExcludeEntities(true);*/
		
		byte[] encoded = Files.readAllBytes(Paths.get("news/1.txt"));
		
		String news = new String(encoded);
		
		Document doc = alchemyObj.TextGetRelations(news, relationParams);

		System.out.println(news);
		
        System.out.println(getStringFromDocument(doc));
    }

    // utility function
    private static String getFileContents(String filename)
        throws IOException, FileNotFoundException
    {
        File file = new File(filename);
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new FileReader(file));

        try {
            String line = null;

            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            input.close();
        }

        return contents.toString();
    }

    // utility method
    private static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
