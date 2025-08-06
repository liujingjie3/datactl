package com.zjlab.dataservice.modules.bench.controller;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.bench.model.entity.BenchCapability;
import com.zjlab.dataservice.modules.bench.service.BenchCapabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bench/capability")
public class BenchCapabilityController {

    @Autowired
    private BenchCapabilityService benchCapabilityService;

    @GetMapping("/all")
    public PageResult<BenchCapability> getAllBenchCapabilities() {
        return benchCapabilityService.qryBenchAllCapability();
    }

}
