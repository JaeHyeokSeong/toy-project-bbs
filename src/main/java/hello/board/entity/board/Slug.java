package hello.board.entity.board;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Slug {

    public static String generate(String value) {
        String tmp = value.toLowerCase();
        tmp = tmp.replaceAll("#", " sharp ");
        tmp = tmp.replaceAll(">=", " gte ");
        tmp = tmp.replaceAll("<=", " lte ");
        tmp = tmp.replaceAll(">", " gt ");
        tmp = tmp.replaceAll("<", " lt ");
        tmp = tmp.replaceAll("[^0-9A-Za-z가-힣ㄱ-ㅎㅏ-ㅣ]+", " ").trim();
        log.info("tmp={}", tmp);
        return tmp.replaceAll("\\s", "-");
    }
}
