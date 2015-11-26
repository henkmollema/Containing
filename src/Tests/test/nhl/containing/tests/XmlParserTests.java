package nhl.containing.tests;

import static org.junit.Assert.*;
import java.net.*;
import java.nio.file.*;
import nhl.containing.controller.RecordSet;
import nhl.containing.controller.XmlParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class XmlParserTests
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parsesSingleRecordInlineXml() throws Exception
    {
        String xml = "<recordset><record id=\"id0\"><aankomst><datum><d>13</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>0.10</tot></tijd><soort_vervoer>vrachtauto</soort_vervoer><bedrijf>DijckLogisticsBV</bedrijf><positie><x>1</x><y>0</y><z>0</z></positie></aankomst><eigenaar><naam>FlowersNL</naam><containernr>19965</containernr></eigenaar><vertrek><datum><d>22</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>12.00</tot></tijd><soort_vervoer>zeeschip</soort_vervoer><bedrijf>ChinaShippingAgency</bedrijf></vertrek><afmetingen><l>40'</l><b>8'</b><h>8'6''</h></afmetingen><gewicht><leeg>3</leeg><inhoud>78</inhoud></gewicht><inhoud><naam>nitrotolueen</naam><soort>gas</soort><gevaar>brandbaar</gevaar></inhoud><ISO>1496-1</ISO></record></recordset>";
        RecordSet recordSet = XmlParser.parse(xml);
        assertEquals(recordSet.records.size(), 1);
    }

    @Test
    public void parsesMultipleRecordsInlineXml() throws Exception
    {
        String xml = "<recordset><record id=\"id0\"><aankomst><datum><d>13</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>0.10</tot></tijd><soort_vervoer>vrachtauto</soort_vervoer><bedrijf>DijckLogisticsBV</bedrijf><positie><x>1</x><y>0</y><z>0</z></positie></aankomst><eigenaar><naam>FlowersNL</naam><containernr>19965</containernr></eigenaar><vertrek><datum><d>22</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>12.00</tot></tijd><soort_vervoer>zeeschip</soort_vervoer><bedrijf>ChinaShippingAgency</bedrijf></vertrek><afmetingen><l>40'</l><b>8'</b><h>8'6''</h></afmetingen><gewicht><leeg>3</leeg><inhoud>78</inhoud></gewicht><inhoud><naam>nitrotolueen</naam><soort>gas</soort><gevaar>brandbaar</gevaar></inhoud><ISO>1496-1</ISO></record><record id=\"id1\"><aankomst><datum><d>2</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>0.10</tot></tijd><soort_vervoer>vrachtauto</soort_vervoer><bedrijf>LindenOITTransportBV</bedrijf><positie><x>1</x><y>0</y><z>0</z></positie></aankomst><eigenaar><naam>FederalExpress</naam><containernr>89603</containernr></eigenaar><vertrek><datum><d>4</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>12.00</tot></tijd><soort_vervoer>zeeschip</soort_vervoer><bedrijf>BeaverShipsAgents</bedrijf></vertrek><afmetingen><l>20'</l><b>8'</b><h>8'6''</h></afmetingen><gewicht><leeg>2</leeg><inhoud>25</inhoud></gewicht><inhoud><naam>onderdelen</naam><soort>los</soort><gevaar>geen</gevaar></inhoud><ISO>1496-1</ISO></record><record id=\"id2\"><aankomst><datum><d>14</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>0.10</tot></tijd><soort_vervoer>vrachtauto</soort_vervoer><bedrijf>DHL</bedrijf><positie><x>1</x><y>0</y><z>0</z></positie></aankomst><eigenaar><naam>IntTrans</naam><containernr>4219</containernr></eigenaar><vertrek><datum><d>22</d><m>12</m><j>04</j></datum><tijd><van>0.00</van><tot>12.00</tot></tijd><soort_vervoer>zeeschip</soort_vervoer><bedrijf>DutchGeneralCargo</bedrijf></vertrek><afmetingen><l>40'</l><b>8'</b><h>8'6''</h></afmetingen><gewicht><leeg>3</leeg><inhoud>68</inhoud></gewicht><inhoud><naam>pianos</naam><soort>los</soort><gevaar>brandbaar</gevaar></inhoud><ISO>1496-1</ISO></record></recordset>";
        RecordSet recordSet = XmlParser.parse(xml);
        assertEquals(recordSet.records.size(), 3);
    }

    @Test
    public void throwsWhenInvalidXml() throws Exception
    {
        String xml = "<fail></fail>";

        thrown.expect(Exception.class);
        thrown.expectMessage("Error when parsing the XML file. It's probably invalid.");
        XmlParser.parse(xml);
    }

    @Test
    public void parsesXmlFile1() throws Exception
    {
        String xml = readXml("xml1.xml");
        assertNotNull("The XML is null", xml);

        RecordSet recordSet = XmlParser.parse(xml);
        assertEquals(recordSet.records.size(), 10);
    }

    private static String readXml(String xmlFileName)
    {
        try
        {
            URI url = XmlParserTests.class.getResource("xml/" + xmlFileName).toURI();
            return new String(Files.readAllBytes(Paths.get(url)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
