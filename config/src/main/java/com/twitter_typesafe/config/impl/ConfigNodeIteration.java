package com.twitter_typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfigNodeIteration extends AbstractConfigNode {
    final private ArrayList<AbstractConfigNode> preIterableChildren;
    final private ArrayList<AbstractConfigNode> postIterableChildren;
    final private AbstractConfigNodeValue body;

    final private ConfigNodeSingleToken substitution;
    final private ConfigNodeComplexValue array;

    ConfigNodeIteration(Collection<AbstractConfigNode> preIterableChildren,
                        Collection<AbstractConfigNode> postIterableChildren,
                        AbstractConfigNodeValue body,
                        ConfigNodeSingleToken substitution) {
        this.preIterableChildren = new ArrayList<AbstractConfigNode>(preIterableChildren);
        this.postIterableChildren = new ArrayList<AbstractConfigNode>(postIterableChildren);
        this.body = body;
        this.substitution = substitution;
        this.array = null;
    }

    ConfigNodeIteration(Collection<AbstractConfigNode> preIterableChildren,
                        Collection<AbstractConfigNode> postIterableChildren,
                        AbstractConfigNodeValue body,
                        ConfigNodeComplexValue array) {
        this.preIterableChildren = new ArrayList<AbstractConfigNode>(preIterableChildren);
        this.postIterableChildren = new ArrayList<AbstractConfigNode>(postIterableChildren);
        this.body = body;
        this.array = array;
        this.substitution = null;
    }

    public AbstractConfigNodeValue body() { return body; }

    @Override
    protected Collection<Token> tokens() {
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (AbstractConfigNode child : preIterableChildren) {
            tokens.addAll(child.tokens());
        }
        if (this.substitution != null) {
            tokens.add(this.substitution.token);
        } else {
            tokens.addAll(this.array.tokens());
        }
        for (AbstractConfigNode child : postIterableChildren) {
            tokens.addAll(child.tokens());
        }
        tokens.addAll(body.tokens());
        return tokens;
    }

}
