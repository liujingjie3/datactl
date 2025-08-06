package com.zjlab.dataservice.modules.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjlab.dataservice.common.api.page.PageResult;
import com.zjlab.dataservice.modules.dataset.model.dto.mark.*;
import com.zjlab.dataservice.modules.dataset.model.po.DatasetMarkFilePo;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MarkFileDetailVo;
import com.zjlab.dataservice.modules.dataset.model.vo.mark.MetadataVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MarkService extends IService<DatasetMarkFilePo> {

    PageResult<MarkFileDetailVo> qryMarkList(MarkListDto markListDto);

    void filter(MarkFilterDto filterDto);

    Integer assignMarkFile(MarkAssignDto markDistributeDto);

    Integer assignMetadata(MetadataAssignDto metadataAssignDto);

    void addMark(MarkAddDto markAddDto);
    void addMarkBatch(MarkAddBatchDto markAddBatchDto);

    void editMark(MarkEditDto markEditDto);

    void resetMark(MarkResetDto markResetDto);

    void abandonMark(MarkAbandonDto abandonDto) throws Exception;

    void abandonMetadata(MetadataAbandonDto abandonDto) throws Exception;

    void checkMark(MarkCheckDto checkDto) throws Exception;

    void checkMarkClass(MarkCheckClassDto checkClassDto) throws Exception;

    void review(MarkReviewDto reviewDto);
    void reviewBatch(MarkReviewBatchDto reviewBatchDto);

    void reviewMetadata(MetadataReviewDto metadataReviewDto);
    void reviewBatchMetadata(MetadataReviewBatchDto metadataReviewBatchDto);

    void exportExcel(HttpServletResponse response, ExportExcelDto exportExcelDto) throws IOException;
//    void exportExcelMetadata(HttpServletResponse response, ExportExcelDto exportExcelDto) throws IOException;

    PageResult<MetadataVo> qryMetadataList(MetadataListDto metadataListDto);

    void filterMetadata(MetadataFilterDto metadataFilterDto);

    void segmentMark(MarkSegmentDto markSegmentDto) throws Exception;
}
