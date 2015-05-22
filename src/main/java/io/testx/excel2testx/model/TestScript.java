package io.testx.excel2testx.model;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gantcho
 */
public class TestScript {
    private final List<TestScriptStep> steps = new ArrayList<>();

    public TestScript addStep(TestScriptStep function) {
        this.steps.add(function);
        return this;
    }
    
    @Override
    public String toString() {
        return "{\"steps\": [" + Joiner.on(",").join(steps) + "]}";
    }
}