package hello.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseData {

    private long pageNumber;
    private long pageSize;
    private long totalCount;
    private long totalPage;
    private boolean first;
    private boolean last;
    private Object items;
}
