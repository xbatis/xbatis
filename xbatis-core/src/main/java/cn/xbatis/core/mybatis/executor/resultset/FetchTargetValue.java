package cn.xbatis.core.mybatis.executor.resultset;

import lombok.Data;

@Data
public class FetchTargetValue {

    private Object matchFieldValue;

    private Object target;

    public FetchTargetValue() {

    }

    public FetchTargetValue(Object matchFieldValue, Object target) {
        this.matchFieldValue = matchFieldValue;
        this.target = target;
    }
}
