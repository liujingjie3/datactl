package com.zjlab.dataservice.modules.dataset.controller;

import com.zjlab.dataservice.common.api.vo.Result;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.MarkStatisticsRankDto;
import com.zjlab.dataservice.modules.dataset.model.vo.board.BoardStatisticsVo;
import com.zjlab.dataservice.modules.dataset.model.vo.board.MarkStatisticsRankVo;
import com.zjlab.dataservice.modules.dataset.model.vo.board.PeriodStatisticsVo;
import com.zjlab.dataservice.modules.dataset.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
public class BoardController {

    @Resource
    private BoardService boardService;

    @PostMapping("statistics")
    public Result<BoardStatisticsVo> statistics(){
        BoardStatisticsVo statistics = boardService.statistics();
        return Result.OK(statistics);
    }

    @PostMapping("statistics/rank")
    public Result<List<MarkStatisticsRankVo>> statisticsRank(@RequestBody(required = false) MarkStatisticsRankDto rankDto){
        List<MarkStatisticsRankVo> markRankVo = boardService.statisticsRank(rankDto);
        return Result.ok(markRankVo);
    }

    @PostMapping("statistics/period")
    public Result<List<PeriodStatisticsVo>> statisticsPeriod(){
        List<PeriodStatisticsVo> markStatisticsPeriod = boardService.statisticsPeriod();
        return Result.ok(markStatisticsPeriod);
    }

    @PostMapping("statistics/daily/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        boardService.exportDailyExcel(response);
    }
}
