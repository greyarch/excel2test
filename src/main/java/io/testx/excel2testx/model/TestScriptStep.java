package io.testx.excel2testx.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.json.simple.JSONValue;

/**
 *
 * @author gantcho
 */
public class TestScriptStep {

    private static final Logger LOG = Logger.getLogger(TestScriptStep.class.getName());

    private String name;
    private Map<String, String> arguments;
    private Map<String, String> meta;

    public TestScriptStep(String name, Map<String, String> arguments, Map<String, String> meta) {
        this.name = name;
        this.arguments = arguments;
        this.meta = meta;
    }

    public TestScriptStep(String name) {
        this(name, new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArgument(String arg) {
        return arguments.get(arg);
    }

    public TestScriptStep addArgument(String name, String value) {
        arguments.put(name, value);
        return this;
    }

    public TestScriptStep addMetaItem(String name, String value) {
        meta.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        return "{" + "\"name\":\"" + name + "\", \"meta\":" + JSONValue.toJSONString(meta) + ", \"arguments\":" + JSONValue.toJSONString(arguments) + "}";
    }
}
