package com.jansmerecki.dto;

import java.util.ArrayList;
import java.util.List;

public class TopologyNode {
    private String macAddress;
    private List<TopologyNode> children =new ArrayList<>();

    public TopologyNode() {}

    public TopologyNode(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public List<TopologyNode> getChildren() {
        return children;
    }

    public void setChildren(List<TopologyNode> children) {
        this.children = children;
    }
}
