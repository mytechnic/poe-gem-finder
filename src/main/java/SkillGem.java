import lombok.Data;

@Data
public class SkillGem {
    private String name;
    private Integer level;
    private String quality;
    private boolean isCorrupt;

    private String valueType;
    private Double value;
    private Double last7Day;
    private String listed;
}
