package nhl.containing.simulator.game;

import java.util.Date;
import nhl.containing.networking.protobuf.InstructionProto;

/**
 * TODO: add all id stuff
 * @author sietse
 */
public class RFID {
    public final int id;
    public final String length;
    public final String width;
    public final String height;
    public final int weightEmpty;
    public final int weightFull;
    public final String content;
    public final String contentDanger;
    public final String contentType;
    public final String iso;
    public final Date departmentDate;
    public final String departmentTransport;
    public final String departmentCompany;
    public final Date arrivalDate;
    public final String arrivalTransport;
    public final String arrivalCompany;
    
    /**
     * Creates an RFID tag from container details
     * @param container container proto data
     */
    public RFID(InstructionProto.Container container) {
        id = container.getContainerNumber();
        length = container.getLength();
        width = container.getWidth();
        height = container.getHeight();
        weightEmpty = container.getWeightEmpty();
        weightFull = container.getWeightLoaded();
        content = container.getContent();
        contentDanger = container.getConentDanger();
        contentType = container.getContentType();
        iso = container.getIso();
        departmentDate = new Date(container.getDepartmentDate());
        departmentCompany = container.getDepartmentCompany();
        departmentTransport = container.getDepartmentTransport();
        arrivalDate = new Date(container.getArrivalDate());
        arrivalTransport = container.getArrivalTransport();
        arrivalCompany = container.getArrivalCompany();
    }
    
    /**
     * For testing compatibility
     */
    public RFID(){
        this(InstructionProto.Container.getDefaultInstance());
    }
    public String getData()
    {
        return String.format("%-25s", "Weight: ") + String.valueOf(weightFull) + '\n' + String.format("%-25s", "Content: ") + content + '\n' + String.format("%-25s", "Danger: ") + contentDanger + '\n' + String.format("%-25s", "Content Type: ") + contentType + '\n' + String.format("%-25s", "ISO: ") + iso + '\n' + String.format("%-25s", "Department date: ") + String.valueOf(departmentDate) + '\n' + String.format("%-25s", "Department company: ") + departmentCompany + '\n' + String.format("%-25s", "Transport: ") + departmentTransport;
    }
}
