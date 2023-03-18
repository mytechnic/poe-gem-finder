import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class SkillGemPriceRepository {
    private List<GemGroup> skillGemGroupList;

    public SkillGemPriceRepository() {
        this.skillGemGroupList = new ArrayList<>();
    }

    public void append(SkillGem skillGem) {
        int skillGemIndex = getSkillGemIndex(skillGem);
        if (skillGemIndex == -1) {
            skillGemGroupList.add(new GemGroup(skillGem));
        } else {
            skillGemGroupList.get(skillGemIndex).append(skillGem);
        }
    }

    public int getSkillGemIndex(SkillGem skillGem) {
        String name = getBaseName(skillGem.getName());
        for (int i = 0; i < skillGemGroupList.size(); i++) {
            if (skillGemGroupList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Getter
    @ToString
    public static class GemGroup {
        private final String name;
        private final List<PriceGroup> priceGroupList;

        public GemGroup(SkillGem skillGem) {
            name = getBaseName(skillGem.getName());
            priceGroupList = new ArrayList<>();
            priceGroupList.add(new PriceGroup(skillGem));
        }

        public void append(SkillGem skillGem) {
            int priceGroupIndex = getPriceGroupIndex(skillGem);
            if (priceGroupIndex == -1) {
                priceGroupList.add(new PriceGroup(skillGem));
            } else {
                priceGroupList.get(priceGroupIndex).append(skillGem);
            }
        }

        private int getPriceGroupIndex(SkillGem skillGem) {
            for (int i = 0; i < priceGroupList.size(); i++) {
                PriceGroup row = priceGroupList.get(i);
                if (Objects.equals(row.getLevel(), skillGem.getLevel())
                        && row.getQuality().equals(skillGem.getQuality())
                        && row.isCorrupt() == skillGem.isCorrupt()) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Getter
    @ToString
    public static class PriceGroup {
        private final Integer level;
        private final String quality;
        private final boolean isCorrupt;

        private final List<Price> priceList;

        public PriceGroup(SkillGem skillGem) {
            level = skillGem.getLevel();
            quality = skillGem.getQuality();
            isCorrupt = skillGem.isCorrupt();
            priceList = new ArrayList<>();
            priceList.add(new Price(skillGem));
        }

        public void append(SkillGem skillGem) {
            priceList.add(new Price(skillGem));
        }
    }

    @Getter
    @ToString
    public static class Price {
        private final String tag;
        private final Double value;

        public Price(SkillGem skillGem) {
            tag = getBaseTag(skillGem.getName());
            value = skillGem.getValue();
        }
    }

    private static String getBaseTag(String name) {
        if (name.startsWith("Awakened")) {
            return "각성";
        } else if (name.startsWith("Divergent")) {
            return "상이";
        } else if (name.startsWith("Anomalous")) {
            return "기묘";
        } else if (name.startsWith("Phantasmal")) {
            return "몽환";
        } else {
            return "기본";
        }
    }

    private static String getBaseName(String name) {
        name = name.replace("Awakened ", "");
        name = name.replace("Divergent ", "");
        name = name.replace("Anomalous ", "");
        name = name.replace("Phantasmal ", "");
        return name;
    }
}
