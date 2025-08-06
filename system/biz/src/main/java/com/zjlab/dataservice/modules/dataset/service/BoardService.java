package com.zjlab.dataservice.modules.dataset.service;

import com.zjlab.dataservice.modules.dataset.model.dto.mark.MarkStatisticsRankDto;
import com.zjlab.dataservice.modules.dataset.model.vo.board.BoardStatisticsVo;
import com.zjlab.dataservice.modules.dataset.model.vo.board.MarkStatisticsRankVo;
import com.zjlab.dataservice.modules.dataset.model.vo.board.PeriodStatisticsVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface BoardService {

    BoardStatisticsVo statistics();

    List<MarkStatisticsRankVo> statisticsRank(MarkStatisticsRankDto rankDto);

    List<PeriodStatisticsVo> statisticsPeriod();

    void exportDailyExcel(HttpServletResponse response) throws IOException;
}
