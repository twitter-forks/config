package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;

import java.util.Map;

public class ConfigConditional {

    private SubstitutionExpression left;
    private AbstractConfigValue right;
    private SimpleConfigObject body;

    ConfigConditional(SubstitutionExpression left, AbstractConfigValue right, SimpleConfigObject body) {
        this.left = left;
        this.right = right;
        this.body = body;

        if (this.left.optional()) {
            throw new ConfigException.BugOrBroken("Substitutions in conditional expressions cannot be optional");
        }
    }

    public SimpleConfigObject resolve(Map<String, AbstractConfigValue> values) {
        AbstractConfigValue val = values.get(left.path().first());
        if (val == null) {
            throw new ConfigException.BugOrBroken("Could not resolve substitution " + this.left.toString() + " in conditional expression");
        }
        if (val.equals(this.right)) {
            return this.body;
        } else {
            return null;
        }
    }
}