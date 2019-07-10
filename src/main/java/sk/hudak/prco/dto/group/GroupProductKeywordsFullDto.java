package sk.hudak.prco.dto.group;

import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.dto.DtoAble;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class GroupProductKeywordsFullDto implements DtoAble {

    private GroupIdNameDto groupIdNameDto;

    private List<String[]> keyWords;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append(this.getClass().getName()).append("[\n")
                .append(" groupIdNameDto=[").append(groupIdNameDto).append("]\n")
                .append(" keyWords=[").append("\n");
        keyWords.stream().forEach(value -> sb.append("   " + Arrays.asList(value)).append("\n"));
        sb.append(" ]").append("\n");
        sb.append("]");
        return sb.toString();
    }
}
