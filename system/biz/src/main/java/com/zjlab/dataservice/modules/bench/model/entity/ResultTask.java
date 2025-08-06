package com.zjlab.dataservice.modules.bench.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultTask {
    private int modelId;
    private double score;
    private List<BenchScore> children;
}
